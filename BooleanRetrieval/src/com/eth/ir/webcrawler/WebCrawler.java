/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.webcrawler;

import com.sun.crypto.provider.DESCipher;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class WebCrawler implements Runnable {

    WebSpider webSpider;
    int webCrawlerId;

    WebCrawler(Object webSpider,int webCrawlerId)
    {
        // attach this crawler to a webspider
        this.webCrawlerId = webCrawlerId;
        this.webSpider = (WebSpider)webSpider;
    }

    /** functional flow crawlWebsite()
     *      establish connection
     *      read stream
     *      fetch urls
     *      do acceptance test
     *      insert in queue
     **/

    public void crawl()
    {
        String urlStr = webSpider.getURLfromWebSpiderQ();
//        System.out.println( Thread.currentThread().getName() + " crawlerid=" + webCrawlerId + " polled -" + urlStr);
        if(null == urlStr)
        {
            try {
                Thread.sleep(2000);
                urlStr = webSpider.getURLfromWebSpiderQ();
                if (null == urlStr) {
                    webSpider.issueCrawlingTerminationRequest();
                    return;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        int size = webSpider.getURLSetSize();
        
//        if(webSpider.getURLSetSize() < WebSpider.MAX_CAPACITY)
        {
//            System.out.println("size="+size);

        //ignore urls outside of eth
//        if(!(URLSelector.match(urlStr, "(\\w+\\.)*(ethz.ch)(\\/w+)*") == 1))
//            return;

        URLSelector selector = new URLSelector();
        String stream = selector.fetchStreamAsStringromURL(urlStr);
        if(null == stream)
        {
            System.out.println("could not open " + urlStr);
            return;
        }
        Set<String> urls = selector.fetchURLsFromString(stream);
        urls = webSpider.getSetIntersection(urls);
        urls = webSpider.addURLSettoSpiderURLSet(urls);
        if(!urls.isEmpty())
        {
            if(webSpider.getURLSetSize() < WebSpider.MAX_CAPACITY)
                webSpider.addURLsToWebSpiderQ(urls);
            webSpider.addEdgesToGraph(urlStr,urls);
        }
        
        }
    }

    public void crawl2() {
//        try {
            String urlStr = webSpider.getURLfromWebSpiderQ();
            int vals = WebSpider.getDummyValue();
//            System.out.println( Thread.currentThread().getName() + "polled -" + urlStr);
            if(null == urlStr)
            {
            try {
                Thread.sleep(10000);
                // try again
                urlStr = webSpider.getURLfromWebSpiderQ();
                if(null == urlStr)
                {
//                    System.out.println("now urlStr is null");
                    webSpider.issueCrawlingTerminationRequest();
                    return;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
//
//            if(vals == 0)
//            {
//                System.out.println("now vals is 0 ; simply return");
//                return;
//            }
            for(int i=0;i<vals;i++)
                webSpider.addURLToWebSpiderQ("web["+webCrawlerId+"]some["+i+"]");

//            System.out.println("added for thread " + webCrawlerId);
            


//            URL url = new URL(urlStr);
//            URLConnection site = url.openConnection();
//            site.setConnectTimeout(1000);
//            site.setAllowUserInteraction(false);
//            InputStream is = site.getInputStream();
//            Scanner scanner = new Scanner(is);
//        } catch (IOException ex) {
//            Logger.getLogger(WebSpider.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void run() {
//        throw new UnsupportedOperationException("Not supported yet.");
        crawl();
//        System.out.println("returning Thread [" + webCrawlerId + "]");
//        System.out.println("returning [" + Thread.currentThread().getName() + "]");
        
    }
}
