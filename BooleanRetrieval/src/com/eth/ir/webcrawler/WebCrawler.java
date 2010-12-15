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
import java.util.HashMap;
import java.util.HashSet;
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
public class WebCrawler {

    public static void main(String args[]) {
        String initial = "http://www.ethz.ch";
//        String initial = "http://www.rektorat.ethz.ch";
        Queue<String> sites = new ArrayDeque<String>();
        sites.add(initial);
        //for duplicates
        Set<String> sitesSet = new HashSet<String>();
        Map<String, Set<String>> sitesGraph = new HashMap<String, Set<String>>();

        while (!sites.isEmpty() && sitesSet.size() < 10 )//sitesGraph.size() < 100)
        {

            String urlStr = sites.poll();
            System.out.println("-----------------------");
            System.out.println("processing : " + urlStr);
            try {

                URL url = new URL(urlStr);
                URLConnection site = url.openConnection();
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
                    
//                    System.out.println(w);
                    w=w.substring("href=\"".length(),w.length()-1);
//                    System.out.println(w);
                   
//                    if(!Character.isLetter(w.charAt(w.length()-1)))
//                    {
//                     String actualUrl=w.substring(0,w.lastIndexOf('.'));
//                     String extension = w.substring(w.lastIndexOf('.'),w.length());
//                     String ext = ".";
//                     for(char c : extension.toCharArray())
//                     {
//                         ext += Character.isLetter(c) ? c : "";
//                     }
//
//                     w = actualUrl + ext ;
//                    }
                    
                    if (!sitesSet.contains(w) && acceptable(w)) {

                        sites.add(w);
                        sitesSet.add(w);
                        setForThisSite.add(w);
                    }
                }
                System.out.println("found " + setForThisSite.size() + " links in " + urlStr);
                System.out.println("current site queue size = " + sites.size());
                System.out.println("current sitegraph size = " + sitesGraph.size());
                System.out.println("current sites set size = " + sitesSet.size());
                System.out.println(setForThisSite);
                // add set to this current url
                sitesGraph.put(urlStr, setForThisSite);
                is.close();

                System.out.println("-----------------------");

            } catch (IOException ex) {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.INFO, "Could not open {0}", urlStr);
            }
        }
        System.out.println(sitesGraph.size());

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
        if (match(url, "(\\w+\\.)*(ethz.ch)(\\/w+)*") == 1 ) return true;
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

}
