package com.company;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FirstReducer {
    private static final BlockingDeque[] buffer = Transfer.buffer;
    private static final List<Set<String>> reduceResult = new CopyOnWriteArrayList<>();

    public List<Set<String>> reduce() {
        int tasks = 4;
        CountDownLatch countDownLatch = new CountDownLatch(tasks);
        Lock lock = new ReentrantLock();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < tasks; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    while(!buffer[finalI].isEmpty()){
                        Map<Set<String>, Integer> map = (Map<Set<String>, Integer>) buffer[finalI].take();
                        for (Map.Entry entry : map.entrySet()) {
                            Set<String> items = (Set<String>) entry.getKey();
                            lock.lock();
                            if(!reduceResult.contains(items)){
                                reduceResult.add(items);
                                System.out.println("Add set to the first reducer's result: "+Util.setToString(items));
                            }
                            lock.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

        return reduceResult;
    }
}
