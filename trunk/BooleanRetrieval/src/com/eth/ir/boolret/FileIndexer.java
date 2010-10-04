/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class FileIndexer {

    TreeMap<String, PostingList> index;
    HashMap<Integer, String> docIndex;

    FileIndexer() {
        index = new TreeMap<String, PostingList>();
        docIndex = new HashMap<Integer, String>();
    }

    public PostingList createNewPostingList() {
        return new PostingList();
    }

    public void addToDictionary(String docId, String key) {
//        System.out.println("Adding---" + key);
        if (!index.containsKey(key.trim())) {
            //create a new PostingList pl
            //create a new PostingListNode pln
            // set docid to pln
            // increment frequency of pln
            // add pln to pl
            // add pl to index

            PostingList pl = new PostingList();
            PostingListNode pln = new PostingListNode(docId, new Integer(1));
            pl.getPostingList().add(pln);
            index.put(key.trim(), pl);
        } else {
            // fetch the postinglist pl
            // retrieve the postinglistnode pln for given docid
            // increment frequency of pln
            // System.out.println(" key = " + key + " docId " + docId);
            PostingList pl = index.get(key);
            // this terms is in the same docId. increment it for this docId only
            if (docId.equals(pl.getPostingList().getLast().getDocId())) {
                PostingListNode pln = pl.getPostingList().getLast();
                pln.setFrequencyInDoc(pln.getFrequencyInDoc() + 1);
                index.put(key.trim(), pl);
            } // this term is for a new docId
            // create a new pln and append it
            // initiate the frequency for this docId
            else {
                PostingListNode pln = new PostingListNode(docId, new Integer(1));
                pl.getPostingList().add(pln);
                index.put(key.trim(), pl);
            }


        }

    }

    public void tokenizeFile(String docId, File file) {
        System.out.println("Tokeninzing --- " + docId);
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);

            st.ordinaryChars(33,47);
            st.ordinaryChars(58, 64);
//            st.ordinaryChar('\'');
//            st.ordinaryChar('"');
//            st.ordinaryChar('/');
//            st.ordinaryChar(';');

            int next = st.nextToken();

            while (next != StreamTokenizer.TT_EOF) {
                switch (next) {

                    case StreamTokenizer.TT_NUMBER:
//                        System.out.println("nextn = " + st.nval);
                        Double val = st.nval;
                        addToDictionary(docId, val.toString().trim());

                        break;

                    case StreamTokenizer.TT_WORD:
                        // StreamTokenizer sometimes reads in words with ','s together.
                        // this is to split it up exactly as we want it.
                        String stoken = st.sval;
                        if(stoken.contains("border".toUpperCase()))
                            System.out.println("FOUND1------------");
                        if (stoken.contains(",")) {
                            String xtokens[] = stoken.split(",");
                            for (String xtoken : xtokens) {
                                if (!xtoken.trim().equals("")) {
                                    addToDictionary(docId, xtoken.trim());
                                }
                            }
                        } else {
                            // It is without a ',' and works for us
                            addToDictionary(docId, stoken.trim());
                        }
                        break;
                    default:
                        break;
                }
                //fetch the next token.
                next = st.nextToken();
            }

        } catch (IOException ex) {
            Logger.getLogger(FileTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileTokenizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void fetchFilesInDirectory(String dir) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
     //   Integer docId = 0;
        for (File file : files) {

            //System.out.println("Indexing " + file.getName() + " docId " + docId);
       //     docIndex.put(docId, file.getName());
            if(!file.isHidden())
            tokenizeFile(file.getName(), file);
         //   docId++;
        }
    }

    public void printIndex() {
        System.out.println(" size of index " + index.size());
        for (Map.Entry<String, PostingList> entry : index.entrySet()) {
            String key = entry.getKey();
            PostingList value = entry.getValue();
            System.out.println("key = " + key + " PostingList =" + value.getPostingList().toString());
        }
    }

    // serialize the index
    public void serializeToFile(String outputFile, int i) {
        FileOutputStream ostream = null;
        ObjectOutputStream p = null;
        try {
            ostream = new FileOutputStream(outputFile);
            p = new ObjectOutputStream(ostream);
        } catch (Exception e) {
            System.out.println("Can't open output file.");
            e.printStackTrace();
        }
        try {
            if(i==1)
                p.writeObject(this.index);
            if(i==2)
                p.writeObject(this.docIndex);
            p.flush();
            p.close();
            System.out.println("Inverted index written to file ==> " + outputFile);
        } catch (Exception e) {
            System.out.println("Can't write output file.");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        String dir = "F://ETH//Projects//InformationRetrieval//ethprojects//BooleanRetrieval//Docs";
        String indexFile = "F://ETH//Projects//InformationRetrieval//ethprojects//BooleanRetrieval//Docs//index";
       // String dirIndexFile = "F://ETH//Projects//InformationRetrieval//BooleanRetrieval//Docs//dirIndex";
        FileIndexer tkz = new FileIndexer();
        tkz.fetchFilesInDirectory(dir);
       // System.out.println("what = " + tkz.index.size());
       tkz.printIndex();
       tkz.serializeToFile(indexFile,1);
     //  tkz.serializeToFile(dirIndexFile, 2);


    }
}
