package com.company;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static CacheLFU cache = new CacheLFU(1000);
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Random random = new Random();


    public static void main(String[] args) throws InterruptedException {

        // fill cache
        for (int i = 0; i < cache.getCapacity(); i++) {
            cache.put(i, new Entry("Entry " + i));
        }

        // start parallel read
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                while (true) {
                    int key = random.nextInt(cache.getCapacity());
                    Thread.sleep(1000);
                    cache.get(key);
                }
            });
        }
        // start threat of reading first 100 as most accessed for sure that cache works correctly
        executorService.submit(() -> {
            while (true) {
                Thread.sleep(10);
                cache.get(random.nextInt(100));
            }
        });

        // wait for randomly access
        System.out.println("waiting...");
        Thread.sleep(10000);

        // start parallel write
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                while (true) {
                    int key = getRandomInt();
                    Thread.sleep(random.nextInt(10) * 100);
                    cache.put(key, new Entry("Value" + key));
                }
            });
        }

        while (true) {
            Thread.sleep(5000);
            System.out.println("\nLog size = " + cache.getLog().size() + "\n");
        }
    }

    private static int getRandomInt() {
        int r = random.nextInt();
        int exceptExisting = r > cache.getCapacity() ? r : r + cache.getCapacity();
        if (exceptExisting < 0)
            return getRandomInt();

        return exceptExisting;
    }
}
