package com.dbalota.study.concurrency;

import java.util.*;

/**
 * Created by Dmytro_Balota on 3/16/2016.
 */
public interface FilesCounter {
    void searchFiles();

    Map<String, Integer> getFiles();

    default Map<String, Integer> getTop10Files() {
        List<Map.Entry<String, Integer>> entries = new LinkedList(getFiles().entrySet());
        Collections.sort(entries, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        Deque deque = (Deque) entries;

        Map result = new LinkedHashMap();
        int i = 0;
        Map.Entry entry;
        while ((entry = (Map.Entry<String, Integer>) deque.pollLast()) != null && i < 10) {
            result.put(entry.getKey(), entry.getValue());
            i++;
        }
        return result;
    }
}
