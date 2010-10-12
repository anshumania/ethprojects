package com.eth.ir.boolret.query;

import com.eth.ir.boolret.Bundle;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class QueryParser {

    QueryDictionary currentQueryDictionary;

    public QueryDictionary getCurrentQueryDictionary() {
        return currentQueryDictionary;
    }

    public void setCurrentQueryDictionary(QueryDictionary currentQueryDictionary) {
        this.currentQueryDictionary = currentQueryDictionary;
    }

    public void readIndex(String indexFile) {
        FileInputStream istream = null;
        ObjectInputStream p = null;
        try {
            istream = new FileInputStream(indexFile);
            p = new ObjectInputStream(istream);
        } catch (Exception e) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, "Can't open index file {0}", indexFile);
            e.printStackTrace();
        }
        try {
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Reading index {0}", indexFile);
            Object x = p.readObject();
            TreeMap<String, PostingList> index = (TreeMap<String, PostingList>) x;
            //System.out.println("index="+index);
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Size of index {0}", index.size());
            //this.index = index;
            getCurrentQueryDictionary().setIndex(index);

            p.close();
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Inverted index read from file {0}", indexFile);

        } catch (Exception e) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, "Inverted index cannot be read from file {0}", indexFile);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param query The query to execute.  May contain whitespace at front and/or back.
     */
    public void executeQuery(String query) {
        System.out.println("[QUERY]  " + query);
        Query q = new Query(query);
        String operator = q.getOperator();
        TreeSet<String> result = null;
        ArrayList<TreeSet<String>> phraseResults = new ArrayList<TreeSet<String>>();

        if(operator == null) {
            System.out.println("Invalid Query");
            return;
        }

        if (operator.equalsIgnoreCase(Query.PhraseOperator)) {
            result = (TreeSet) getCurrentQueryDictionary().doPhraseQuery(q.getTerms());

        } else if(operator.equalsIgnoreCase(Query.ProximityOperator)) {
            result = (TreeSet) getCurrentQueryDictionary().doProximityQuery(q.getTerms(), q.getProximity());

        } else if (operator.equalsIgnoreCase(Query.AndOperator)) {
            result = (TreeSet) getCurrentQueryDictionary().doANDQuery(q);

            getCurrentQueryDictionary().collectANDStatistics(q);

        } else if (operator.equalsIgnoreCase(Query.OrOperator)) {
            result = (TreeSet) getCurrentQueryDictionary().doORQuery(q);

        } else if (operator.equalsIgnoreCase(Query.NotOperator)) {
            result = (TreeSet) getCurrentQueryDictionary().doNOTQuery(q);

        } else {
            //TODO - throw InvalidQueryOperatorException
        }

        if(result.size() > 0) {
            System.out.println(result.size() + " matches:");
            for (String id : result) {
                System.out.println(id);
            }
        } else {
            System.out.println("No matches found.");
        }
    }

    /**
     *
     * @param file File containing a single query string on a single line in the file
     * @return The query string from the file
     */
    public String loadQueryFromFile(File file) {
        String query = "";
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            //skip any blank lines
            while (query.trim().equals("")) {
                query = in.readLine().trim();
            }
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return query;
    }

    public void executeAllQueriesInDirectory(String dir) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.isHidden()) // hack to escape svn files
            {
                String query = loadQueryFromFile(file);
                System.out.println(file.getName() + ":" + query);
                executeQuery(query);
                System.out.println("");
            }
        }
    }

    public static void proximityTest() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Testing Proximity Queries");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        String query = "/9 DELEGATES MEETING";
        queryParser.executeQuery(query);
    }

    public static void phraseTest() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Testing Phrase Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        String query = "\"DELEGATES MEETING\"";
        queryParser.executeQuery(query);
    }

    public static void phase1() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 1 of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile());
    }

    public static void phase2_StopWords() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 2 - StopWords of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.STOPWORD).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile());
    }

    public static void phase2_Stemming() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 2 - Stemming of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new StemmedQueryDictionary());
        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.PORTERSTEM).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile());
    }

    public static void main(String args[]) {

        //QueryParser.phase1();
        //QueryParser.phase2_StopWords();
        //QueryParser.phase2_Stemming();
        QueryParser.phraseTest();
        QueryParser.proximityTest();
    }

    //Ugly brute force attempt at finding words in the index that are actually 2 words with a space
    public void findSpellingErrors(int numToFind) {
        //ArrayList<String> found = new ArrayList<String>();
        int numFound = 0;

        TreeMap<String, PostingList> index = this.getCurrentQueryDictionary().getIndex();
        Set<String> dictionary = index.keySet();
        //iterate over all the words in the dictionary
        for(String word : dictionary) {
            int wordCount = index.get(word).getNumPostings();
            //iterate over all substrings, check each half to see if it is two valid words
            for(int wordIndex=1; wordIndex < word.length(); wordIndex++) {
                String substring1 = word.substring(0, wordIndex);
                if(substring1.length() > 3 && dictionary.contains(substring1)) {
                    int substring1Count = index.get(substring1).getNumPostings();
                    //consider the substring a "more valid" word if it appears in the index more than the full word
                    if(substring1Count > wordCount) {
                        String substring2 = word.substring(wordIndex);
                        if(substring2.length() > 3 && dictionary.contains(substring2)) {
                            int substring2Count = index.get(substring2).getNumPostings();
                            //consider the substring a "more valid" word if it appears in the index more than the full word
                            if(substring2Count > wordCount) {
                                System.out.println(word + " [" + substring1 + "][" + substring2 + "]");
                                //found.add(word);
                                numFound++;
                                if(numToFind != 0 && numFound >= numToFind) {
                                    System.out.println(numFound + " spelling errors found.");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
