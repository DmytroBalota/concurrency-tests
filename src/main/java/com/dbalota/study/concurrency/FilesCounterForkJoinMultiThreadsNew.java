package com.dbalota.study.concurrency;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterForkJoinMultiThreadsNew extends RecursiveAction implements FilesCounter {



    private Map<String, Integer> files = new ConcurrentHashMap<>();
    private File rootFolder;

    public FilesCounterForkJoinMultiThreadsNew(Map<String, Integer> files, File rootFolder) {
        this.files = files;
        this.rootFolder = rootFolder;
    }

    public FilesCounterForkJoinMultiThreadsNew(File rootFolder) {
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
        List<FilesCounterForkJoinMultiThreadsNew> subTasks = new LinkedList<>();
        if (rootFolder.list() != null) {
            for (File f : rootFolder.listFiles()) {
                files.put(f.getName(), files.containsKey(f.getName()) ? files.get(f.getName()) + 1 : 1);
                if (f.isDirectory()) {
                    FilesCounterForkJoinMultiThreadsNew folderProcessor = new FilesCounterForkJoinMultiThreadsNew(files, f);
                    folderProcessor.fork();
                    subTasks.add(folderProcessor);
                }
            }
        }
        for (FilesCounterForkJoinMultiThreadsNew task : subTasks) {
            task.join();
        }
    }
}
