package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class FileIndexer {

    // the inverted index with a dictionary of terms<String> and posting lists <PostingList>
    TreeMap<String, PostingList> index;
    final static String DOCS_DIR = "resources/Docs";
    final static String INDEX_FILE = "index";
    final static String COMMA = ",";
    final static String EMPTY = "";

    FileIndexer() {
        index = new TreeMap<String, PostingList>();
    }

//    public PostingList createNewPostingList() {
//        return new PostingList();
//    }
    public void addToDictionary(String docId, String key) {

        //the key is not a stopword
        
        if (!StopWords.isStopWord(key.trim())) {
             // the index does not contain this term
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
                pl.setNumPostings(new Integer(1));
                index.put(key.trim(), pl);
            } // the index contains this term
            else {
                // fetch the postinglist pl
                // retrieve the postinglistnode pln for given docid
                // increment frequency of pln

                PostingList pl = index.get(key);
                // this term is in the same docId. increment it for this docId only
                // for future use - storing the frequency of the word within that document
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
                    pl.setNumPostings(pl.getNumPostings() + 1);
                    index.put(key.trim(), pl);
                }
            }
        }
 
 
     
    }

    /**
     * This method utilizes the StreamTokenizer class
     * to isolate words.
     * Characters in the ASCII set 33-47 and 58-64
     * are deemed ordinary.
     *
     * @param file - the file to be tokenized
     */
    public void tokenizeFile(File file) {

        String docId = file.getName();
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);
            // get rid of the '\' ';' and so on ..
            st.ordinaryChars(33, 47);
            st.ordinaryChars(58, 64);


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
                        if (stoken.contains(COMMA)) {
                            String xtokens[] = stoken.split(COMMA);
                            for (String xtoken : xtokens) {
                                if (!xtoken.trim().equals(EMPTY)) {
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
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Retrieves the documents in the specified resource directory
     * and tokenizes the documents
     *
     * @param dir - the resource directory with all the documents
     */
    public void tokenizeAllFilesInDirectory(String dir) {

        File directory = new File(dir);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (!file.isHidden()) // hack for getting rid of svn files
            {
                if(!file.getName().contains(INDEX_FILE))
                    tokenizeFile(file);
                else
                    file.delete();
                
            }
        }
    }

    /**
     * Prints the index to System.out
     */
    public void printIndex() {
        System.out.println(" size of index " + index.size());
        for (Map.Entry<String, PostingList> entry : index.entrySet()) {
            String key = entry.getKey();
            PostingList value = entry.getValue();
            System.out.println("key = " + key + " PostingList =" + value.getPostingList().toString());
        }
    }

    /**
     * Prints the following statistics about the index:
     *   - Size
     *   - Number of matches
     *   - Length of longest posting list
     *   - Length of shortest posting list
     */
    public void printIndexStats() {
        Integer indexSize = this.index.size();  //TODO - is this the right measure of size?  or # terms/# documents?
        Integer numMatches = 0;
        String longestPostingListTerm = "";
        ArrayList<String> shortestPostingListTerms = new ArrayList<String>();
        Integer longestPostingList = 0;
        Integer shortestPostingList = Integer.MAX_VALUE;
        Integer postingListSize = 0;
        Integer numTerms = this.index.size();

        for (Map.Entry<String, PostingList> entry : this.index.entrySet()) {
            //System.out.println(entry.getKey() + " : " + entry.getValue().getFrequency());
            //postingListSize = entry.getValue().getFrequency();
            postingListSize = entry.getValue().getPostingList().size();
            numMatches += postingListSize;

            if (postingListSize > longestPostingList) {
                longestPostingList = postingListSize;
                longestPostingListTerm = entry.getKey();
            }
            if (postingListSize == shortestPostingList) {
                shortestPostingListTerms.add(entry.getKey());
            } else if (postingListSize < shortestPostingList) {
                shortestPostingList = postingListSize;
                shortestPostingListTerms.clear();
                shortestPostingListTerms.add(entry.getKey());
            }
        }


        System.out.println("---------------------");
        System.out.println("INDEX STATISTICS");
        System.out.println("---------------------");
        System.out.println("Size of index = " + indexSize + " terms");
        System.out.println("Number of 1s in matrix = " + numMatches);
        System.out.println("Longest Posting List = " + longestPostingList + " ['" + longestPostingListTerm + "']");
        System.out.println("Shortest Posting List = " + shortestPostingList + " [Occurs " + shortestPostingListTerms.size() + " times]");
        /*        for(String term : shortestPostingListTerms) {
        System.out.print("'" + term + "'");
        }
        System.out.println("]");*/
    }

    // serialize the index
    public void serializeToFile(String outputFile) {

        FileOutputStream ostream = null;
        ObjectOutputStream p = null;
        try {
            ostream = new FileOutputStream(outputFile);
            p = new ObjectOutputStream(ostream);
        } catch (Exception e) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, "Cannot open output file", outputFile);
            e.printStackTrace();
        }
        try {
            //write the index to the file
            p.writeObject(this.index);
            p.flush();
            p.close();
            Logger.getLogger(FileIndexer.class.getName()).log(Level.INFO, "Inverted index written to file", outputFile);
        } catch (Exception e) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, "Cannot write to output file", outputFile);
            e.printStackTrace();
        }
    }

    public void deleteIndex()
    {
        String fileName = FileIndexer.class.getResource(DOCS_DIR).getFile() + "/" + INDEX_FILE;

    }
    public static void main(String args[]) {

        FileIndexer tkz = new FileIndexer();
        // initiate stopWords
        StopWords.readStopWordsFile(StopWords.STOPWORDFILE);
        
        // read all the documents in the director and tokenize
        tkz.tokenizeAllFilesInDirectory(FileIndexer.class.getResource(DOCS_DIR).getFile());
        // print the statistics
        tkz.printIndexStats();
        // serialize the index
        tkz.serializeToFile(FileIndexer.class.getResource(DOCS_DIR).getFile() + "/" + INDEX_FILE);
        tkz.printIndex();

    }
}
