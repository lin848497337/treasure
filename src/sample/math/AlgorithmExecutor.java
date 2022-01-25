package sample.math;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.StringUtils;
import sample.model.DailyIndex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AlgorithmExecutor {



    LoadingCache<String, Class> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, Class>() {
        @Override
        public Class load(String s) throws Exception {
            GroovyClassLoader classLoader = new GroovyClassLoader();
            String newRule = TEMPLATE_BEGIN + s + TEMPLATE_END;
            Class groovyClass = classLoader.parseClass(newRule);
            return groovyClass;
        }
    });

    public List<Integer> execute(String rule, Map<Integer, List<DailyIndex>> params)
        throws IllegalAccessException, InstantiationException, ExecutionException {
        if (StringUtils.isBlank(rule)){
            return Collections.EMPTY_LIST;
        }
        List<Integer> stockList = Lists.newArrayList();
        Class groovyClass = cache.get(rule);

        for (Map.Entry<Integer, List<DailyIndex>> e : params.entrySet()) {
            if (e.getValue().size() < 10){
                continue;
            }
            GroovyObject object = (GroovyObject) groovyClass.newInstance();
            Boolean methodResult = (Boolean) object.invokeMethod("filter", e.getValue());
            if (methodResult) {
                stockList.add(e.getKey());
            }
        }
        return stockList;
    }


    private static final String TEMPLATE_BEGIN = "package sample.math\n"
        + "\n"
        + "import sample.model.DailyIndex\n"
        + "\n"
        + "import java.math.RoundingMode\n"
        + "\n"
        + "/**\n"
        + " * 定义\n"
        + " * MA5(CD)\n"
        + " * MA10(CD)\n"
        + " * MA20(CD)\n"
        + " * CLOSE(CD)\n"
        + " * OPEN(CD)\n"
        + " * HIGHT(CD)\n"
        + " * LOW(CD)\n"
        + " * VOL(CD)\n"
        + " * CURRENT_DATE:d\n"
        + " *\n"
        + " * MAX(BEGIN, END, EXP)\n"
        + " * MIN(BEGIN, END, EXP)\n"
        + " */\n"
        + "class Template {\n"
        + "\n"
        + "    private int CURRENT_DATE = 0;\n"
        + "    private int CD = 0;\n"
        + "    private List<DailyIndex> dailyIndices;\n"
        + "\n"
        + "    boolean filter(List<DailyIndex> dailyIndices){\n"
        + "        this.dailyIndices = dailyIndices;\n"
        + "        CURRENT_DATE = dailyIndices.size() - 1;\n"
        + "        CD = CURRENT_DATE;\n"
        + "        return doFilter();\n"
        + "    }\n"
        + "\n"
        + "    boolean doFilter(){\n"
        + "        // gen code\n"
        + "        return  ";

    private static final String TEMPLATE_END = "}\n"
        + "\n"
        + "    double ROSE(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getClosingPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).multiply(100).doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    double MA5(int offset){\n"
        + "        return _MA(offset, 5);\n"
        + "    }\n"
        + "\n"
        + "    double MA10(int offset){\n"
        + "        return _MA(offset, 10);\n"
        + "    }\n"
        + "\n"
        + "    double MA20(int offset){\n"
        + "        return _MA(offset, 20);\n"
        + "    }\n"
        + "\n"
        + "    double _MA(int offset , int days){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        int size = 0;\n"
        + "        BigDecimal total = new BigDecimal(0);\n"
        + "        for (int i = realOffset ; i-- ; i>=0){\n"
        + "            size++;\n"
        + "            DailyIndex dailyIndex = dailyIndices.get(i);\n"
        + "            total = total.add(dailyIndex.getClosingPrice());\n"
        + "            if (size == days){\n"
        + "                break;\n"
        + "            }\n"
        + "        }\n"
        + "        return total.divide(new BigDecimal(size), 2, RoundingMode.HALF_UP).doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    double CLOSE(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getClosingPrice().doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    double OPEN(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getOpeningPrice().doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    double HIGH(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getHighestPrice().doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    double LOW(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getLowestPrice().doubleValue();\n"
        + "    }\n"
        + "\n"
        + "    long VOL(int offset){\n"
        + "        int realOffset = CURRENT_DATE + offset;\n"
        + "        DailyIndex dailyIndex = dailyIndices.get(realOffset);\n"
        + "        return dailyIndex.getTradingVolume();\n"
        + "    }\n"
        + "\n"
        + "}\n";

}
