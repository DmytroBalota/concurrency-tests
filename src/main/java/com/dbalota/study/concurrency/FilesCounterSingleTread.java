package com.dbalota.study.concurrency;

import java.io.File;
import java.util.*;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class FilesCounterSingleTread implements FilesCounter {
    private Map<String, Integer> files = new HashMap<String, Integer>();
    private File rootFolder;

    public FilesCounterSingleTread(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void searchFiles() {
        count(rootFolder);
    }

    @Override
    public Map<String, Integer> getFiles() {
        return files;
    }

    ///TODO: without recursion
    private void count(File rootFolder) {
        try {
            if (rootFolder.list() != null) {
                for (File f : rootFolder.listFiles()) {
                    //fileNames.add(f.getName());
                    files.put(f.getName(), files.containsKey(f.getName()) ? files.get(f.getName()) + 1 : 1);
                    if (f.isDirectory()) {
                        count(f);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(rootFolder);
        }
    }

    //works longer then with recursion
    public void countWithoutRecursion(File rootFolder) {
        Deque<File> q = new LinkedList<>();
        q.offer(rootFolder);
        while (!q.isEmpty()) {
            File[] listFiles = rootFolder.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    files.put(f.getName(), files.containsKey(f.getName()) ? files.get(f.getName()) + 1 : 1);
                    if (f.isDirectory()) {
                        q.offer(f);
                    }
                }
            }
            rootFolder = q.pop();
        }
    }

}
