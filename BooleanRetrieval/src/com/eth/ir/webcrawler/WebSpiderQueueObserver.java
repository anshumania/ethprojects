/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.webcrawler;

import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author ANSHUMAN
 */
public class WebSpiderQueueObserver implements Observer {

    int MAX_THREADS = 15;
    private Executor exec ;
    static int threadId =0 ;

    public WebSpiderQueueObserver() {

        
        exec = Executors.newFixedThreadPool(MAX_THREADS);
    }



    public void update(Observable o, Object arg) {
        
        if( arg instanceof Integer)
            return;
//        System.out.println("received update");
        Runnable webCrawler = new WebCrawler(arg,threadId++);
        
        exec.execute(webCrawler);
    }

}


