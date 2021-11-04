package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheLFU {

  private final int capacity;

  private final Map<Integer, Rate> meta = new HashMap<>();

  private final Map<Integer, Entry> cache = new HashMap<>();

  private final List<Entry> log = new ArrayList();

  public CacheLFU(int capacity) {
    this.capacity = capacity;
  }

  public void put(int key, Entry value) {
//    System.out.println("Write " + key);
    Entry v = cache.get(key);
    if (v == null) {
      if (meta.size() >= capacity) {
        Integer k = getKeyOfLessRequested();
        System.out.println("Eviction ------- " + k);
        // add entry to log
        log.add(cache.remove(k));
        meta.remove(k);
      }
      meta.put(key, new Rate(key, 1, System.nanoTime()));
    } else {
      Rate rate = meta.get(key);
      rate.countOfRequests += 1;
      rate.lastTime = System.nanoTime();
    }
    cache.put(key, value);
  }

  public Entry get(Integer key) {
//    System.out.println("Read " + key);
    Entry value = cache.get(key);
    if (value != null) {
      Rate rate = meta.get(key);
      rate.countOfRequests += 1;
      rate.lastTime = System.nanoTime();
      return value;
    }
    return null;
  }

  private Integer getKeyOfLessRequested() {
    Rate lessRequested = Collections.min(meta.values());
    return lessRequested.key;
  }

  public int getCapacity() {
    return capacity;
  }

  public List<Entry> getLog() {
    return log;
  }
}

class Entry {
  private String data;

  public Entry(String data) {
    this.data = data;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return data;
  }
}

class Rate implements Comparable<Rate> {

  Integer key;
  Integer countOfRequests;
  Long lastTime;

  public Rate(Integer key, Integer countOfRequests, Long lastTime) {
    this.key = key;
    this.countOfRequests = countOfRequests;
    this.lastTime = lastTime;
  }

  @Override
  public int compareTo(Rate o) {
    int r = countOfRequests.compareTo(o.countOfRequests);
    return r != 0 ? r : lastTime.compareTo(o.lastTime);
  }
}
