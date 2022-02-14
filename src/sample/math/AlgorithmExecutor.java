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


    private static final String TEMPLATE_BEGIN = "import sample.math.BaseScript;"
        + "class Template extends BaseScript{\n"
        + "    boolean doFilter(){\n"
        + "        return ";

    private static final String TEMPLATE_END = ";\n"
        + "    }\n"
        + "}\n";

}
