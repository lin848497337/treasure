package sample.view.emotion;

import sample.view.tools.TrickerMap;

public class CollectorContext {
    TrickerMap<Integer, Integer> lianbanCounterMap = new TrickerMap<>();
    private int index;

    public void cleanUp(){
        lianbanCounterMap.tricker();
    }

    public int getLianBanTimes(int stockInfo){
        return lianbanCounterMap.get(stockInfo, 0);
    }

    public int recent(int sotckInfoId){
        return lianbanCounterMap.recent(sotckInfoId);
    }

    public void addZhangTingTimes(int stockInfo){
        lianbanCounterMap.put(stockInfo, getLianBanTimes(stockInfo) + 1);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
