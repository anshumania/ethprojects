/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ANSHUMAN
 */
public class WebCrawlerOld {

    public static void main(String args[]) {
        String initial = "http://www.ethz.ch";
//        String initial = "http://www.rektorat.ethz.ch";
        Queue<String> sites = new ArrayDeque<String>();
        sites.add(initial);
        //for duplicates
        Set<String> sitesSet = new HashSet<String>();
        Map<String, Set<String>> sitesGraph = new HashMap<String, Set<String>>();
        Map<String,Map<String,Integer>> total = new HashMap<String,Map<String,Integer>>();

        long startTime = System.currentTimeMillis();

        while (!sites.isEmpty()  )// && sitesGraph.size() < 50)
        {

            String urlStr = sites.poll();
//            System.out.println("-----------------------");
//            System.out.println("processing : " + urlStr);
            try {

                URL url = new URL(urlStr);
                URLConnection site = url.openConnection();
                site.setConnectTimeout(1000);
                site.setAllowUserInteraction(false);
                InputStream is = site.getInputStream();
                Scanner scanner = new Scanner(is);
                String text = scanner.useDelimiter("\\A").next();

//                System.out.println(text);
//                String regexp = "http://(\\w+\\.)*(ethz.ch)(\\/w+)*";
//                String regexp = "^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";
//                String regexp = "http://(\\w+\\.)*(\\w+)*([\\w\\-\\.]+[^#?\\s]+)";

                String regexp = "href=\\\"(.*?)\\\"";
                Pattern pattern = Pattern.compile(regexp,Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);

                // first add the url to the map
                if (!sitesGraph.containsKey(urlStr)) {
                    sitesGraph.put(urlStr, new HashSet<String>());
                }

                Set setForThisSite = sitesGraph.get(urlStr);

                while (matcher.find()) {
                    String w = matcher.group().trim();

                    w=w.substring("href=\"".length(),w.length()-1);

                    
                    if (acceptable(w)) {

                     if (match(w, "(\\w+\\.)*(ethz.ch)(\\/w+)*") == 1 )
                     {
                        if (sitesSet.size() < 1000)
                        {// only fetch 10,000 sites dont add to queue

                            if(sitesSet.add(w))
                                 sites.add(w);
                        }
                        setForThisSite.add(w);

                        Map<String,Integer> forin =  total.get("in");
                        if(null == forin)
                            forin = new HashMap<String, Integer>();
                        if(!forin.containsKey(w))
                        {
                            forin.put(w, 1);
                        }
                        else
                        {
                            Integer prev = forin.get(w);
                            forin.put(w, prev+1);
                        }
                        total.put("in", forin);


                     }
                     else
                     {
                        Map<String,Integer> forout =  total.get("out");
                        if(null == forout)
                            forout = new HashMap<String, Integer>();
                        if(!forout.containsKey(w))
                        {
                            forout.put(w, 1);
                        }
                        else
                        {
                            Integer prev = forout.get(w);
                            forout.put(w, prev+1);
                        }
                        total.put("out", forout);

                     }

                    }
                }
//                System.out.println("found " + setForThisSite.size() + " links in " + urlStr);
//                System.out.println("current site queue size = " + sites.size());
//                System.out.println("current sitegraph size = " + sitesGraph.size());
                System.out.println("current sites set size = " + sitesSet.size());
//                System.out.println(setForThisSite);
                // add set to this current url
                sitesGraph.put(urlStr, setForThisSite);
                is.close();

//                System.out.println("-----------------------");

            } catch (IOException ex) {
                Logger.getLogger(WebCrawlerOld.class.getName()).log(Level.INFO, "Could not open {0}", urlStr);
            }
        }

        System.out.println("totaltime = " + (System.currentTimeMillis()-startTime));

//        System.out.println(sitesGraph.size());
        Map<String,Integer> outs = total.get("out");
        Map<String,Integer> in = total.get("in");
        Map<String,Integer> sin  = sortByValue(in);
        Map<String,Integer> souts = sortByValue(outs);
        System.out.println("souts " + souts);
        System.out.println("sin " + sin);

        System.out.println("total number of nodes = " + sitesGraph.size());
        int numberOfedges = 0;
        for(Map.Entry<String,Set<String>> iter : sitesGraph.entrySet())
        {
            numberOfedges += iter.getValue().size();
        }
        System.out.println("total number of edges =" + numberOfedges);

        for (int i=0;i<4;i++)
        {
             String maxin = (String)sin.keySet().toArray()[i];
             Integer maxinValue = sin.get(maxin);
             System.out.println("inbound = " + maxin + " value " + maxinValue);
        }
        for( int i=0; i<4 ; i++)
        {
        String maxout = (String)souts.keySet().toArray()[i];
        Integer maxoutValue = souts.get(maxout);
        System.out.println("outbound = " + maxout + " value " + maxoutValue);
        }


    }

    public static boolean acceptable(String url) {
        if(!url.contains("http://")) return false;
        if (url.length() > 50) {
            return false;
        }
        if (url.endsWith("jpg") || url.endsWith("jpeg") || url.endsWith("gif")
                || url.endsWith("xls") || url.endsWith("css") || url.endsWith("pdf")
                || url.endsWith("dtd") || url.endsWith("ppt") || url.endsWith("js")
                || url.endsWith("ps") || url.endsWith("png") || url.endsWith("zexp")) {
            return false;
        }
        if( match(url, "/") > 6 ) return false;
        if (match(url, "\\.") > 5 ) return false;
//        if (match(url, "(\\w+\\.)*(ethz.ch)(\\/w+)*") == 1 ) return true;
        return true;


    }

    public static int match(String url, String regex) {


        String nohttp = url.substring("http://".length(), url.length());
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nohttp);
        int i = 0;
        while (matcher.find()) {
            i++;
        }
        return i;

    }


        public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
