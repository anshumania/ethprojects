/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class QueryParser {


    //retrieve the index.
    // parse through the index creating treemap of frequecny and dictionary.

    

    TreeMap<String,PostingList> index = new TreeMap<String,PostingList>();
    HashMap<Integer,String> docIndex = new HashMap<Integer,String>();

    public void doORQuery(String query)
    {

        String terms[] = query.split("or");
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        HashSet<String> result = new HashSet<String>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        for(String term : terms)
        {
            PostingList pl = index.get(term.trim());

            for(PostingListNode pln : pl.getPostingList())
            {
                result.add(pln.getDocId());
            }

        }


        System.out.println("Result = " + result);
    }

    public void doNOTQuery(String query)
    {
        String terms[] = query.split("not");
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        //HashMap<String,Set<String>> result = new HashMap<String,Set<String>>();
        List<Set<String>> result = new ArrayList<Set<String>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        for(String term : terms)
        {
            System.out.println("termsss=" + term);
            PostingList pl = index.get(term.trim());
            solutionSpace.put(term.trim(), pl);
            Set<String> docSet = new LinkedHashSet<String>();
            for(PostingListNode pln : pl.getPostingList())
            {
                docSet.add(pln.getDocId());
            }
            result.add(docSet);

        }

        System.out.println(" result = " + result);
        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for(Set<String> iterator : result)
        {
           if(first)
           {
               //System.out.println(" first = " + iterator.getKey());
               partialSet = iterator ; //.getValue();
               first=false;
            }
           else
               partialSet.removeAll(iterator);
        }

        System.out.println("Result = " + partialSet);

    }

    public void doAndQuery(String query)
    {
        String terms[] = query.split("and");
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        TreeMap<String,Set<String>> result = new TreeMap<String,Set<String>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        for(String term : terms)
        {
            PostingList pl = index.get(term.trim());
            solutionSpace.put(term.trim(), pl);
            HashSet<String> docSet = new HashSet<String>();
            for(PostingListNode pln : pl.getPostingList())
            {
                docSet.add(pln.getDocId());
            }
            result.put(term.trim(), docSet);
        }

        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for(Map.Entry<String,Set<String>> iterator : result.entrySet())
        {
           if(first)
           {
               partialSet = iterator.getValue();
               first=false;
            }
           else
               partialSet.retainAll(iterator.getValue());
        }

        System.out.println("Result = " + partialSet);

    }


    public void readIndex(String indexFile)
    {
        FileInputStream istream = null;
        ObjectInputStream p = null;
        try {
            istream = new FileInputStream(indexFile);
            p = new ObjectInputStream(istream);
        } catch (Exception e) {
            System.out.println("Can't open output file.");
            e.printStackTrace();
        }
        try {
            Object x = p.readObject();
            TreeMap<String,PostingList> index = (TreeMap<String,PostingList>)x;
            System.out.println("index "  + index.size());
            this.index = index;

//            for (Map.Entry<String, PostingList> entry : index.entrySet()) {
//            String key = entry.getKey();
//            PostingList value = entry.getValue();
//        //    System.out.println("key = " + key + " PostingListSize =" + value.getPostingList().size());
//            }

            p.close();
            System.out.println("Inverted index read from file ==> " + indexFile);
        } catch (Exception e) {
            System.out.println("Can't write output file.");
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        QueryParser x = new QueryParser();
        String indexFile = "F://ETH//Projects//InformationRetrieval//ethprojects//BooleanRetrieval//Docs//index";
        x.readIndex(indexFile);
        String testQuery = "INDIAN and COMMUNIST and CHINESE";
        String testQuery2 = "BORDER and ISRAEL and SYRIA" ;
        String testQuery3 = "NUCLEAR and WEAPONS and DEVELOPMENT";
      //  x.doAndQuery(testQuery4);

        String testQuery5 = "MOSCOW or SUPPORT or AUTONOMY";
        String testQuery6 = "COUNTRY or NEW or JOIN or UNITED or NATIONS";
        //x.doORQuery(testQuery6);

        String testQuery7 = "PROVISIONS not TREATY";
        String testQuery8 = "PREMIER not KHRUSHCHEV";
        x.doNOTQuery(testQuery8);

    }

}
