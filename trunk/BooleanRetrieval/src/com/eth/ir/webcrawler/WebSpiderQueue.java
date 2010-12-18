/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.webcrawler;

import java.util.Observable;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class WebSpiderQueue extends Observable{

    final BlockingQueue<String> webSpiderQ ;
    protected WebSpider webSpider;

    public WebSpiderQueue(WebSpider webSpider)
    {
        this.webSpider = webSpider;
        webSpiderQ = new LinkedBlockingQueue<String>();
    }

    public BlockingQueue<String> getWebSpiderQ() {
        return webSpiderQ;
    }

    public void addInitialURL(String initialURL)
    {
        try {
//            System.out.println("first url" + initialURL);
            webSpiderQ.put(initialURL);
            setChanged();
            notifyObservers(webSpider);
        } catch (InterruptedException ex) {
            Logger.getLogger(WebSpiderQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void add(String url)
    {
        try {
            
            //        System.out.println("adding url" + url);
            webSpiderQ.put(url);
            int size = webSpiderQ.size();
            if (size % 10 == 0) {
//                System.out.println("notifyingChange at size" + size);
                setChanged();
                //notify that the spider q has changed
                notifyObservers(webSpider);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(WebSpiderQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public boolean addSet(Set<String> urlSet)
    {
        
            //        System.out.println("adding url" + url);
            int prevSize = webSpiderQ.size();
            boolean add = webSpiderQ.addAll(urlSet);
            int size = webSpiderQ.size();
            if((size - prevSize) > 10) {
//            if (size % 10 == 0) {
//                System.out.println("notifyingChange at size" + size);
                setChanged();
                //notify that the spider q has changed
                notifyObservers(webSpider);
            }
        
            return add;
    }


    public String poll()
    {

//        try {
            String url = webSpiderQ.poll();
             setChanged();
             notifyObservers(webSpider);
           
            return url;
//        } catch (InterruptedException ex) {
//            Logger.getLogger(WebSpiderQueue.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
    }

}
