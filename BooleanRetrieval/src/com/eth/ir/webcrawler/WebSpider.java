/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.webcrawler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Does the actual work of crawling
 * @author ANSHUMAN
 */
public class WebSpider {

    final WebSpiderQueue webSpiderQueue;
    final WebSpiderQueueObserver webSpiderQObserver;
    final static int MAX_CAPACITY = 1000;
    final String INITIAL_SITE = "http://www.ethz.ch";
    final Set<String> webSpiderURLSet;
    private static AtomicInteger dummyValue = new AtomicInteger(15);
//    static  volatile AtomicIntegerCount dummyValue = 5;
    static final Object lock = new Object();

    long startTime;

    public WebSpider() {
        webSpiderQueue = new WebSpiderQueue(this);
        webSpiderQObserver = new WebSpiderQueueObserver();
        webSpiderQueue.addObserver(webSpiderQObserver);
        webSpiderURLSet = Collections.synchronizedSet(new HashSet<String>(100));
    }

    public String getURLfromWebSpiderQ() {

        return webSpiderQueue.poll();
    }

    public int getURLSetSize()
    {
        synchronized(webSpiderURLSet)
        {
            return webSpiderURLSet.size();
        }
    }

    public Set<String> getSetIntersection(Set<String> urlSet)
    {
        synchronized(webSpiderURLSet)
        {
         urlSet.removeAll(webSpiderURLSet);
         return urlSet;
        }
    }

    public Set<String> addURLSettoSpiderURLSet(Set<String> urlSet) {
        synchronized (webSpiderURLSet)
        {
            Set<String> added = new HashSet<String>();
            for(String str : urlSet)
            {
                if(webSpiderURLSet.size() < MAX_CAPACITY)
                {
                    webSpiderURLSet.add(str);
                    added.add(str);
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
        doNextWork();

    }

    public void doNextWork() {
        System.out.println("total sites collected = " + webSpiderURLSet.size());
        System.out.println("time taken = " + (System.currentTimeMillis() - startTime));
        System.exit(0);
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
