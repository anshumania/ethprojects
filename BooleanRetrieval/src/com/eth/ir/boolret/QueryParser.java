/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.String;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class QueryParser {

    TreeMap<String,PostingList> index = new TreeMap<String,PostingList>();
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

            for (Map.Entry<String, PostingList> entry : index.entrySet()) {
            String key = entry.getKey();
            PostingList value = entry.getValue();
            System.out.println("key = " + key + " PostingListSize =" + value.getPostingList().size());
            }

            p.close();
            System.out.println("Inverted index read from file ==> " + indexFile);
        } catch (Exception e) {
            System.out.println("Can't write output file.");
            e.printStackTrace();
        }
    }

    public void doAQuery()
    {
        String testQuery = "BORDER and ISRAEL and SYRIA";
        ArrayList pla = new ArrayList<PostingList>();
        String terms[] = testQuery.split("and");
        int i = 0;
        for(String term : terms)
        {
            pla[i] = new PostingList();
            PostingList pl = index.get(term);
            pla[i] = pl;
        }

    }
   
    public static void main(String args[])
    {
        QueryParser x = new QueryParser();
        String indexFile = "F://ETH//Projects//InformationRetrieval//BooleanRetrieval//Docs//index";
        x.readIndex(indexFile);


    }
}
