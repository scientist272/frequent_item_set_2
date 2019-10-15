package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SecondMapper {
    private static final BlockingDeque<Map<Set<String>,Integer>> buffer = Transfer.bufferForSecondPass;
    private static final Map<Set<String>,Integer> itemSet = new ConcurrentHashMap<>();
    public void map(ArrayList[] buckets,List<Set<String>> frequentItems,int threshold){
        final int tasks = 4;
        ExecutorService executorService = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(tasks);
        Lock lock = new ReentrantLock();
        for (int i = 0; i < tasks; i++) {
            int perSize = buckets.length / tasks;
            int finalI = i;
            executorService.execute(() -> {
                int index = finalI * perSize;
                for (int j = index; j < index+perSize ; j++) {
                    List<String> bucket = buckets[j];
                    for(Set<String> items:frequentItems){
                        int notEqual=0;
                        for(String item:items){
                            if(!bucket.contains(item))
                                notEqual++;
                            if(notEqual>0)
                                break;
                        }
                        if(notEqual==0){
                            lock.lock();
                            itemSet.put(items,itemSet.getOrDefault(items,0)+1);
                            lock.unlock();
                        }
                    }
                }
                countDownLatch.countDown();
            });
        }
        //等待所有任务完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        for (Map.Entry entry:itemSet.entrySet()){
            if((int)entry.getValue()<threshold){
                itemSet.remove(entry.getKey());
            }
        }
        //将汇总的结果传输给缓冲区
        try {
            buffer.put(itemSet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

