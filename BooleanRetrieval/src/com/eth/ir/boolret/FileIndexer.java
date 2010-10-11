package com.eth.ir.boolret;

import com.eth.ir.boolret.dictionary.Dictionary;
import com.eth.ir.boolret.dictionary.PorterStemmerDictionary;
import com.eth.ir.boolret.dictionary.StopWordDictionary;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class FileIndexer {
    private Dictionary currentDictionary;

    public Dictionary getCurrentDictionary() {
        return currentDictionary;
    }

    public void setCurrentDictionary(Dictionary currentDictionary) {
        this.currentDictionary = currentDictionary;
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


            int position = 0;
            int next = st.nextToken();
            while (next != StreamTokenizer.TT_EOF) {
                switch (next) {

                    case StreamTokenizer.TT_NUMBER:
                        Double val = st.nval;
                        getCurrentDictionary().addToDictionary(docId, val.toString().trim(), position);
                        position++;
                        break;

                    case StreamTokenizer.TT_WORD:
                        // StreamTokenizer sometimes reads in words with ','s together.
                        // this is to split it up exactly as we want it.
                        String stoken = st.sval;
                        if (stoken.contains(Bundle.COMMA)) {
                            String xtokens[] = stoken.split(Bundle.COMMA);
                            for (String xtoken : xtokens) {
                                if (!xtoken.trim().equals(Bundle.EMPTY)) {
                                    getCurrentDictionary().addToDictionary(docId, xtoken.trim(), position);
                                }
                            }
                        } else {
                            // It is without a ',' and works for us
                            getCurrentDictionary().addToDictionary(docId, stoken.trim(), position);
                        }
                        position++;
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
            if (!file.isHidden() && !file.getName().contains(Bundle.INDEX_FILE)) // hack for getting rid of svn files
            {
               tokenizeFile(file);
            }
        }
    }

    /**
     * Prints the index to System.out
     */
    public void printIndex() {
        System.out.println(" size of index " + getCurrentDictionary().getIndex().size());
        for (Map.Entry<String, PostingList> entry : getCurrentDictionary().getIndex().entrySet()) {
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
    public void printIndexStats(String phase) {
        Integer numMatches = 0;
        String longestPostingListTerm = "";
        ArrayList<String> shortestPostingListTerms = new ArrayList<String>();
        Integer longestPostingList = 0;
        Integer shortestPostingList = Integer.MAX_VALUE;
        Integer postingListSize = 0;
        Integer numTerms = getCurrentDictionary().getIndex().size();//this.index.size();
        Integer numDocs = 425;
        Integer indexSize = numTerms * numDocs;

        
        for (Map.Entry<String, PostingList> entry : getCurrentDictionary().getIndex().entrySet()) {
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
        System.out.println("INDEX STATISTICS [" + phase + "]");
        System.out.println("---------------------");
        System.out.println("Size of term-document matrix = " + indexSize + " (" + numTerms + " terms x " + numDocs + " documents)");
        System.out.println("Number of 1s in matrix = " + numMatches);
        System.out.println("Longest Posting List = " + longestPostingList + " ['" + longestPostingListTerm + "']");
        System.out.println("Shortest Posting List = " + shortestPostingList + " [Occurs " + shortestPostingListTerms.size() + " times]");
        /*
        for(String term : shortestPostingListTerms) {
            System.out.print("'" + term + "'");
        }
        System.out.println("]");
        */
    }

    // serialize the index
    public void serializeToFile(String outputFile) {

        FileOutputStream ostream = null;
        ObjectOutputStream p = null;
        try {
            ostream = new FileOutputStream(outputFile);
            p = new ObjectOutputStream(ostream);
        } catch (Exception e) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, "Cannot open index file {0}", outputFile);
            e.printStackTrace();
        }
        try {
            //write the index to the file
            //this sucks
//            if(getCurrentDictionary() instanceof PorterStemmerDictionary)
//                p.writeObject(((PorterStemmerDictionary)getCurrentDictionary()).getStemmedIndex());
//            else
                p.writeObject(getCurrentDictionary().getIndex());

            p.flush();
            p.close();
            Logger.getLogger(FileIndexer.class.getName()).log(Level.INFO, "Inverted index written to file {0}", outputFile);
        } catch (Exception e) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, "Cannot write to index file {0}", outputFile);
            e.printStackTrace();
        }
    }

    public static void deleteIndex(String index)
    {
        boolean success = new File(index).delete();
        if(success)
            Logger.getLogger(FileIndexer.class.getName()).log(Level.INFO,"Deleted previous index {0}",index);

    }

    public static void phase1()
    {
        FileIndexer tkz = new FileIndexer();

        //create a new dictionary for this phase with its index file
        Dictionary phase1Dictionary = new Dictionary(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile() +
                                                                                    "/" + Bundle.INDEX_FILE);
        // notify the fileIndex as to which dictionary its working on
        tkz.setCurrentDictionary(phase1Dictionary);
        //delete the previous index if any
        FileIndexer.deleteIndex(tkz.getCurrentDictionary().getINDEX_FILE());
        // read all the documents in the director and tokenize
        tkz.tokenizeAllFilesInDirectory(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile());
        // print the statistics
        tkz.printIndexStats("phase1");
        // serialize the index
        tkz.serializeToFile(tkz.getCurrentDictionary().getINDEX_FILE());

    }

    public static void phase2_StopWords()
    {
        FileIndexer tkz = new FileIndexer();
        //create a new dictionary for this phase with its index file
        Dictionary phase2_sw_Dictionary = new StopWordDictionary(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile() + "/" + Bundle.INDEX_FILE + Bundle.STOPWORD);
        // notify the fileIndex as to which dictionary its working on
        tkz.setCurrentDictionary(phase2_sw_Dictionary);
        //delete previous stopword index if any
        FileIndexer.deleteIndex(tkz.getCurrentDictionary().getINDEX_FILE());
        // initiate stopWords
        StopWords.readStopWordsFile(StopWords.STOPWORDFILE);

        // read all the documents in the director and tokenize
        tkz.tokenizeAllFilesInDirectory(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile());
        // print the statistics
        tkz.printIndexStats("phase2_StopWords");
        // serialize the index
        tkz.serializeToFile(tkz.getCurrentDictionary().getINDEX_FILE());
       // tkz.useStopWords = false;


    }
    public static void phase2_Stemming()
    {
        FileIndexer tkz = new FileIndexer();
        //create a new dictionary for this phase with its index file
        Dictionary phase2_pStem_Dictionary = new PorterStemmerDictionary(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile() + "/" + Bundle.INDEX_FILE + Bundle.PORTERSTEM);
        // notify the fileIndex as to which dictionary its working on
        tkz.setCurrentDictionary(phase2_pStem_Dictionary);
        //delete previous stopword index if any
        FileIndexer.deleteIndex(tkz.getCurrentDictionary().getINDEX_FILE());
        // read all the documents in the director and tokenize
        tkz.tokenizeAllFilesInDirectory(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile());
        // print the statistics
        tkz.printIndexStats("phase3_Stemming");
        // serialize the index
        tkz.serializeToFile(tkz.getCurrentDictionary().getINDEX_FILE());
        // serialize the index
       // tkz.serializeToFile(FileIndexer.class.getResource(Bundle.DOCS_DIR).getFile() + "/" + Bundle.STEMMEDMAP);

    }

    public static void main(String args[]) {

     FileIndexer.phase1();
     FileIndexer.phase2_StopWords();
     FileIndexer.phase2_Stemming();


    }
}
