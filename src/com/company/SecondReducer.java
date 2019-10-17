package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

//第二层reducer，用于输出最终结果
public class SecondReducer {
    private static final BlockingDeque<Map<Set<String>,Integer>> buffer = Transfer.bufferForSecondPass;
    public List<Set<String>> reduce(){
        List<Set<String>> result = new ArrayList<>();
        try {
            Map<Set<String>,Integer> itemSet = buffer.take();
            for(Map.Entry entry:itemSet.entrySet()){
                int support = (int)entry.getValue();
                    Set<String> set = (Set<String>) entry.getKey();
                    result.add(set);
                    System.out.println("After the second reduce,Frequent Items set: "+Util.setToString(set)+", support: "+support);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
