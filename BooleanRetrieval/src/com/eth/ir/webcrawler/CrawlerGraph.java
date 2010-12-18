/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.webcrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ANSHUMAN
 */
public class CrawlerGraph {

    int V;
    int E;

    Map<Integer,List<Integer>> edge;

//    List<Integer>[] edge;
//    final Object lock;

    public CrawlerGraph()
    {
        edge = new ConcurrentHashMap<Integer, List<Integer>>();
    }

    public void addV()
    {
        
//        edge[++V] = new ArrayList<Integer>();
        edge.put(++V, new ArrayList());
        
    }

    public void addE(int x, int y)
    {
//        edge[x].add(y);
        edge.get(x).add(y);
        E++;
        
    }

    public int getV()
    {
        return V;
    }

    public int getE()
    {
        return E;
    }


    public static void main(String args[])
    {

    }
}

    