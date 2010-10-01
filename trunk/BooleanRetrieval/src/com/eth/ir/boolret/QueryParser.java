/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class QueryParser {

    TreeMap<String,PostingList> index = new TreeMap<String,PostingList>();
    HashMap<Integer,String> docIndex = new HashMap<Integer,String>();
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

   public void readDocIndex(String indexFile)
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
            HashMap<Integer,String> docIndex = (HashMap<Integer,String>)x;
            System.out.println("docIndex "  + docIndex.size());
            this.docIndex = docIndex;


            for (Map.Entry<Integer,String> entry : docIndex.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            System.out.println("docId = " + key + " DocName =" + value);
            }

            p.close();
            System.out.println("DocIndexread from file ==> " + indexFile);
        } catch (Exception e) {
            System.out.println("Can't write output file.");
            e.printStackTrace();
        }
    }



    public void doAQuery()
    {
        String testQuery = "BORDER and ISRAEL and SYRIA";
        ArrayList<PostingList> pla = new ArrayList<PostingList>();
        String terms[] = testQuery.split("and");
      
        for(String term : terms)
        {
            PostingList pl = index.get(term.trim());
            pla.add(pl);
        }

        String result = new String();
        System.out.println(" pla.size " + pla.size());

        for(int i=0,j=1;i<pla.size();i++,j++)
        {
            
            LinkedList<PostingListNode> plnis = (LinkedList<PostingListNode>)(pla.get(i)).getPostingList();
            LinkedList<PostingListNode> plnjs = (LinkedList<PostingListNode>)(pla.get(j)).getPostingList();
            System.out.println(" plnis.size = " + plnis.size() + " plnjs.size = " + plnjs.size());

            System.out.println("plnis=" + plnis);
            System.out.println("plnjs=" + plnjs);

//            if(plnis.size() > plnjs.size())
            for(PostingListNode plni : plnis)
            {
                for(PostingListNode plnj : plnjs)
                {
                    if(plni.getDocId() == plnj.getDocId())
                    {

                        result+= ", " + docIndex.get(plni.getDocId());
                       // break;
                    }
                }
            }
//            else
//                for(PostingListNode plnj : plnjs)
//            {
//                for(PostingListNode plni : plnis)
//                {
//                    if(plni.getDocId() == plnj.getDocId())
//                    {
//
//                        result+= ", " + docIndex.get(plnj.getDocId());
//                    break;
//                    }
//                }
//            }
            if(j==pla.size()-1) break;

        }
        System.out.println(" result " + result);

        


    }
   
    public static void main(String args[])
    {
        QueryParser x = new QueryParser();
        String indexFile = "F://ETH//Projects//InformationRetrieval//BooleanRetrieval//Docs//index";
        String docIndexFile = "F://ETH//Projects//InformationRetrieval//BooleanRetrieval//Docs//dirIndex";

        x.readIndex(indexFile);
        x.readDocIndex(docIndexFile);
        x.doAQuery();


    }
}
