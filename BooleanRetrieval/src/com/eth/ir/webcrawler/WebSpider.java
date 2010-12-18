/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.webcrawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Does the actual work of crawling
 * @author ANSHUMAN
 */
public class WebSpider {

    final WebSpiderQueue webSpiderQueue;
    final WebSpiderQueueObserver webSpiderQObserver;
    final static int MAX_CAPACITY = 500;
    final String INITIAL_SITE = "http://www.ethz.ch";
    final Set<String> webSpiderURLSet;
    private static AtomicInteger dummyValue = new AtomicInteger(15);
//    static  volatile AtomicIntegerCount dummyValue = 5;
    static final Object lock = new Object();
    static int V = 0;
    final Map<String, Integer> webSpiderMap;
    final CrawlerGraph webSpiderGraph;
    long startTime;
    int tEdges = 0;
    public WebSpider() {
        webSpiderQueue = new WebSpiderQueue(this);
        webSpiderQObserver = new WebSpiderQueueObserver();
        webSpiderQueue.addObserver(webSpiderQObserver);
        webSpiderURLSet = Collections.synchronizedSet(new HashSet<String>(100));
        webSpiderMap = new ConcurrentHashMap<String, Integer>();
        webSpiderGraph = new CrawlerGraph();
    }

    public String getURLfromWebSpiderQ() {

        return webSpiderQueue.poll();
    }

    public int getURLSetSize() {
        synchronized (webSpiderURLSet) {
            return webSpiderURLSet.size();
        }
    }

    public void addEdgesToGraph(String urlV, Set<String> urls) {
        int inE = webSpiderMap.get(urlV);
        tEdges += urls.size();
//        System.out.println("tEdges being added = " + urls.size());
        for (String urlE : urls) {
            int outE = webSpiderMap.get(urlE);
            webSpiderGraph.addE(inE, outE);
        }
    }

    public Set<String> getSetIntersection(Set<String> urlSet) {
        synchronized (webSpiderURLSet) {
            urlSet.removeAll(webSpiderURLSet);
            return urlSet;
        }
    }

    public Set<String> addURLSettoSpiderURLSet(Set<String> urlSet) {
        synchronized (webSpiderURLSet) {
            Set<String> added = new HashSet<String>();
            for (String str : urlSet) {
                if (webSpiderURLSet.size() < MAX_CAPACITY) {
                    webSpiderURLSet.add(str);
                    added.add(str);
                    webSpiderMap.put(str, ++V);
                    webSpiderGraph.addV();
                }
            }
            return added;
        }

    }

    public boolean addURLsToWebSpiderQ(Set<String> urlSet) {

        return webSpiderQueue.addSet(urlSet);
    }

    public void addURLToWebSpiderQ(String url) {

        webSpiderQueue.add(url);

    }

    public synchronized void issueCrawlingTerminationRequest() {
        webSpiderQueue.deleteObservers();
        System.out.println("Termination Request issued");
        calculateStats();

    }

    public void calculateStats() {
        System.out.println("total sites collected = " + webSpiderURLSet.size());
        System.out.println("total sites in map = " + webSpiderMap.size());
        System.out.println("time taken = " + (System.currentTimeMillis() - startTime));
        System.out.println("total number of nodes in Graph = " + webSpiderGraph.getV());
        System.out.println("total number of edges in Graph = " + webSpiderGraph.getE());
        System.out.println("total number of edges in Graph = " + tEdges);
        Map<Integer, String> mostCitedETH = new HashMap<Integer, String>();
        Map<Integer, String> mostCitedExternal = new HashMap<Integer, String>();
        for (Map.Entry<String, Integer> iter : webSpiderMap.entrySet()) {
            String url = iter.getKey();
            Integer urlID = iter.getValue();
            boolean eth = (URLSelector.match(url, "(\\w+\\.)*(ethz.ch)(\\/w+)*") == 1);
            int edges = webSpiderGraph.edge.get(urlID).size();
            if (eth) {
                if (mostCitedETH.size() < 10) {
                    mostCitedETH.put(edges, url);
                } else {
                    int smallest = getSmallestKeyInMap(mostCitedETH);
                    if (edges > smallest) {
                        mostCitedETH.remove(smallest);
                        mostCitedETH.put(edges, url);
                    }
                }
            } else {
                if (mostCitedExternal.size() < 10) {
                    mostCitedExternal.put(edges, url);
                }
                int smallest = getSmallestKeyInMap(mostCitedExternal);
                if (edges > smallest) {
                    mostCitedExternal.remove(smallest);
                    mostCitedExternal.put(edges, url);
                }
            }
        }
        System.out.println("ETH top 10");
        for(Map.Entry<Integer,String> iter : mostCitedETH.entrySet())
        {
            System.out.println(" Site["+ iter.getValue() + "] with " + iter.getKey() + " citations ");
        }
        System.out.println("External top 10");
        for(Map.Entry<Integer,String> iter : mostCitedExternal.entrySet())
        {
            System.out.println(" Site["+ iter.getValue() + "] with " + iter.getKey() + " citations ");
        }


        System.exit(0);
    }

    public int getSmallestKeyInMap(Map<Integer, String> map) {
        List<Integer> tList = new ArrayList<Integer>();
        Integer[] intArr = new Integer[map.keySet().size()];
        Collections.addAll(tList, map.keySet().toArray(intArr));
        Collections.sort(tList);
        int smallest = tList.get(0);
        tList = null;
        return smallest;
    }

    public static int getDummyValue() {


        return dummyValue.decrementAndGet();

    }

    public static void main(String args[]) {
        WebSpider spider = new WebSpider();
        spider.startTime = System.currentTimeMillis();
        spider.webSpiderQueue.addInitialURL(spider.INITIAL_SITE);
    }
}
