/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
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
        String testQuery4 = "SIGNING and BAN and TREATY";
        x.doAndQuery(testQuery4);
       // x.doOrQuery(testQuery5);

    }

}
