package com.dbalota.study.concurrency;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterExecutorsMultiThreads implements FilesCounter, Runnable {

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static Deque<Future> futures = new ConcurrentLinkedDeque<>();

    private Map<String, Integer> files = new ConcurrentHashMap<>();
    private File rootFolder;

    public FilesCounterExecutorsMultiThreads(Map<String, Integer> files, File rootFolder) {
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
        return files;
    }

    @Override
    public void run() {
        if (rootFolder.list() != null) {
            for (File f : rootFolder.listFiles()) {
                //fileNames.add(f.getName());
                files.put(f.getName(), files.containsKey(f.getName()) ? files.get(f.getName()) + 1 : 1);
                if (f.isDirectory()) {
                    futures.add(executorService.submit(new FilesCounterExecutorsMultiThreads(files, f)));
                }
            }
        }
    }
}
