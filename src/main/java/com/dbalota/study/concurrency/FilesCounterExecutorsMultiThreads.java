package com.dbalota.study.concurrency;

import java.io.File;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterExecutorsMultiThreads implements FilesCounter, Runnable {

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static Deque<Future> futures = new ConcurrentLinkedDeque<>();

    private Map<String, AtomicInteger> files = new ConcurrentHashMap<>();
    private File rootFolder;

    public FilesCounterExecutorsMultiThreads(Map<String, AtomicInteger> files, File rootFolder) {
        this.files = files;
        this.rootFolder = rootFolder;
    }

    public FilesCounterExecutorsMultiThreads(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void searchFiles() {
        futures.add(executorService.submit(new FilesCounterExecutorsMultiThreads(files, rootFolder)));

        while (!futures.isEmpty()) {
            try {
                futures.pollLast().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }

    @Override
    public Map<String, Integer> getFiles() {
        Map<String, Integer> newFilesMap = new HashMap<>();
        for (Map.Entry<String, AtomicInteger> m : files.entrySet()) {
            newFilesMap.put(m.getKey(), m.getValue().get());
        }

        return newFilesMap;
    }

    @Override
    public void run() {
        if (rootFolder.list() != null) {
            for (File f : rootFolder.listFiles()) {
                if (files.containsKey(f.getName())) {
                    AtomicInteger n = files.get(f.getName());
                    n.incrementAndGet();
                    files.put(f.getName(), n);
                } else {
                    files.put(f.getName(), new AtomicInteger(1));
                }
                if (f.isDirectory()) {
                    futures.add(executorService.submit(new FilesCounterExecutorsMultiThreads(files, f)));
                }
            }
        }
    }
}
