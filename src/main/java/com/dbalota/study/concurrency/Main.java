package com.dbalota.study.concurrency;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public class Main {
    private static File root;

    public static void main(String[] args) {
        root = new File("d:/");
        if (args != null && args.length == 1) {
            root = new File(args[0]);
        }


        System.out.println("\n=================== SINGLE THREAD ==========================");
        FilesCounter fc = new FilesCounterSingleTread(root);
        execute(fc);

        System.out.println("\n=================== MULTI THREADS FORK-JOIN==========================");
        fc = new FilesCounterForkJoinMultiThreads(root);
        execute(fc);

        System.out.println("\n=================== MULTI  THREADS EXECUTORS==========================");
        fc = new FilesCounterExecutorsMultiThreads(root);
        execute(fc);
    }

    private static void execute(FilesCounter fc) {
        long startTime = System.currentTimeMillis();
        fc.searchFiles();
        System.out.println("Searching files in milliseconds:" + (System.currentTimeMillis() - startTime));

        System.out.println(String.format("Number of files in directory %s is %s", root.getAbsoluteFile(), fc.getFiles().size()));
        Iterator i = fc.getTop10Files(fc.getFiles()).entrySet().iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }

}
