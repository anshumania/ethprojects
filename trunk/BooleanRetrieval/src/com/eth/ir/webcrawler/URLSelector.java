/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
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
public class URLSelector {

    public String fetchStreamAsStringromURL(String urlStr) {
        InputStream is = null;
        String stream = new String();
        try {
            URL url = new URL(urlStr);
            URLConnection site = url.openConnection();
            site.setConnectTimeout(1000);
            site.setAllowUserInteraction(false);
            is = site.getInputStream();
            Scanner scanner = new Scanner(is);
            stream = scanner.useDelimiter("\\A").next();
        } catch (IOException ex) {
            Logger.getLogger(URLSelector.class.getName()).log(Level.INFO, "Could not open {0}", urlStr);
        } finally {
            try {
                if(!(null == is))
                    is.close();

            } catch (IOException ex) {
                Logger.getLogger(URLSelector.class.getName()).log(Level.INFO, "Could not open {0}", urlStr);
            }
        }
        return stream;
    }

    public Set<String> fetchURLsFromString(String stream) {

        Set<String> urlsInThisStream = new HashSet<String>();
        String regexp = "href=\\\"(.*?)\\\"";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(stream);
        while (matcher.find())
        {
            String url = trimOffHref(matcher.group().trim());
            if (filterURL(url))
                urlsInThisStream.add(url);
        }
        return urlsInThisStream;
    }

    public static String trimOffHref(String hrefURL)
    {
       return hrefURL.substring("href=\"".length(),hrefURL.length()-1);
    }
    public static boolean filterURL(String url)
    {
        if(!url.contains("http://")) return false;
        if (url.length() > 50)      return false;
        
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


}
