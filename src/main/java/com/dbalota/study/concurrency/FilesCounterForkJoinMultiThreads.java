package com.dbalota.study.concurrency;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterForkJoinMultiThreads extends RecursiveAction implements FilesCounter {



    private Map<String, Integer> files = new ConcurrentHashMap<>();
    private File rootFolder;

    public FilesCounterForkJoinMultiThreads(Map<String, Integer> files, File rootFolder) {
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
        return files;
    }

    @Override
    protected void compute() {
        if (rootFolder.list() != null) {
            for (File f : rootFolder.listFiles()) {
                //fileNames.add(f.getName());
                files.put(f.getName(), files.containsKey(f.getName()) ? files.get(f.getName()) + 1 : 1);
                if (f.isDirectory()) {
                    invokeAll(new FilesCounterForkJoinMultiThreads(files, f));
                }
            }
        }
    }
}
