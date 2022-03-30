package sample.view.tools;

import java.util.HashMap;
import java.util.Map;

public class TrickerMap<K, V>{

    private int tricker = 0;
    private Map<K, MyNode<V>> kvMap = new HashMap<>();

    private static class MyNode<V>{
        private V value;
        private int tricker;
        private int lastTrikker = Integer.MAX_VALUE;
    }


    public void put(K k, V v){
        MyNode<V> node = new MyNode();
        node.tricker = tricker;
        node.value = v;
        MyNode<V> old = kvMap.put(k, node);
        if (old != null){
            node.lastTrikker = old.tricker;
        }
    }

    public V get(K k, V defaultValue){
        MyNode node = kvMap.get(k);
        if (node != null){
            if (tricker - node.tricker > 1 ){
            }else {
                return (V) node.value;
            }
        }
        return defaultValue;
    }

    public int recent(K k){
        MyNode node = kvMap.get(k);
        if (node != null){
            if (node.lastTrikker != -1){
                return tricker - node.lastTrikker;
            }
        }
        return Integer.MAX_VALUE;
    }

    public void tricker(){
        tricker ++;
    }

}
