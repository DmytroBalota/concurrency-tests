package com.dbalota.study.concurrency;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterForkJoinMultiThreads extends RecursiveAction implements FilesCounter {



    private Map<String, AtomicInteger> files = new ConcurrentHashMap<>();
    private File rootFolder;

    public FilesCounterForkJoinMultiThreads(Map<String, AtomicInteger> files, File rootFolder) {
        this.files = files;
        this.rootFolder = rootFolder;
    }

    public FilesCounterForkJoinMultiThreads(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void searchFiles() {
        ForkJoinPool p = new ForkJoinPool(4);
        p.invoke(this);
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
    protected void compute() {
        List<FilesCounterForkJoinMultiThreads> subTasks = new LinkedList<>();
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
                    FilesCounterForkJoinMultiThreads folderProcessor = new FilesCounterForkJoinMultiThreads(files, f);
                    folderProcessor.fork();
                    subTasks.add(folderProcessor);
                }
            }
        }
        for (FilesCounterForkJoinMultiThreads task : subTasks) {
            task.join();
        }
    }
}
