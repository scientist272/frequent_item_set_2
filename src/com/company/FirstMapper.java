package com.company;

import java.util.*;
import java.util.concurrent.*;

public class FirstMapper {
    private static final BlockingDeque[] buffer = Transfer.buffer;

    public void map(ArrayList[] buckets, int threshold) {
        final int tasks = 4;
        ExecutorService executorService = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(tasks);
        for (int i = 0; i < tasks; i++) {
            int perSize = buckets.length / tasks;
            int finalI = i;
            int perThreshold = threshold / tasks;
            executorService.execute(() -> {
                int index = finalI * perSize;
                List<Set<String>> beginningCandidate = new ArrayList<>();
                //生成k=1的项集
                for (int j = 1; j < 10001; j++) {
                    Set<String> set = new HashSet<>();
                    set.add(String.valueOf(j));
                    beginningCandidate.add(set);
                }
                Map<Set<String>,Integer> originKeyValues = getOneRoundItems(beginningCandidate,index,perThreshold,perSize,buckets);
                List<Set<String>> lastCandidate = generateCandidateSet(originKeyValues);
                Map<Set<String>,Integer> lastKeyValues = originKeyValues;
                //循环至k项集不再是frequent
                while(true){
                    Map<Set<String>,Integer> keyValues = getOneRoundItems(lastCandidate,index,perThreshold,perSize,buckets);
                    if(keyValues.size()==0){
                        break;
                    }
                    lastCandidate = generateCandidateSet(keyValues);
                    lastKeyValues = keyValues;
                }
                try {
                    buffer[finalI].put(lastKeyValues);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        //等待所有任务完成再关闭线程池
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    //frequent Items筛选
    private Map<Set<String>,Integer> getOneRoundItems(List<Set<String>> candidate,int index,int perThreshold,int perSize,ArrayList[] buckets){
        Map<Set<String>, Integer> keyValues = new ConcurrentHashMap<>();
        for (int j = index; j < index + perSize; j++) {
            //每一个桶
            List<String> bucket = buckets[j];
            //判断是否包含每一个集合
            for(Set<String> items:candidate){
                int notEqual=0;
                for(String item:items){
                    if(!bucket.contains(item))
                        notEqual++;
                    if(notEqual>0)
                        break;
                }
                if(notEqual==0){
                    keyValues.put(items,keyValues.getOrDefault(items,0)+1);
                }
            }
        }
        for (Map.Entry entry : keyValues.entrySet()) {
            if ((int) entry.getValue() < perThreshold) {
                keyValues.remove(entry.getKey());
            }
        }
        return keyValues;
    }

    //用于生成下一步的候选的set
    private List<Set<String>> generateCandidateSet(Map<Set<String>,Integer> keyValues){
        List<Set<String>> candidateSet = new CopyOnWriteArrayList<>();
        List<Set<String>> resultSet = new CopyOnWriteArrayList<>();
        for (Map.Entry entry:keyValues.entrySet()){
            candidateSet.add((Set<String>) entry.getKey());
        }
        for (int i = 0; i < candidateSet.size() ; i++) {
            Set<String> itemSet1 = candidateSet.get(i);
            for (int j = i+1; j < candidateSet.size() ; j++) {
                int notEqual = 0;
                Set<String> itemSet2 = candidateSet.get(j);
                for (String item:itemSet1){
                    if(!itemSet2.contains(item)){
                        notEqual++;
                    }
                    if (notEqual>1){
                        break;
                    }
                }
                if(notEqual<=1){
                    Set<String> set = new HashSet<>();
                    set.addAll(itemSet1);
                    set.addAll(itemSet2);
                    if(!resultSet.contains(set))
                        resultSet.add(set);
                }
            }
        }
        return resultSet;
    }

}
