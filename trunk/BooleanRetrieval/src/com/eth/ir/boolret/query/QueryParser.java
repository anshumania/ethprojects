package com.eth.ir.boolret.query;

import com.eth.ir.boolret.Bundle;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        //System.out.println(query);
        Query q = new Query(query);
        String operator = q.getOperator();

        if(q.hasProximityQueries()) {
            //NOTE: just does the first proximity search for now
            ProximityQuery pq = q.getProximityQueries().get(0);
            TreeSet<String> result = (TreeSet) getCurrentQueryDictionary().doProximityQuery(pq.getFirstTerm(), pq.getSecondTerm(), -pq.getDistance(), pq.getDistance());
            for (String id : result) {
                System.out.println(id);
            }
        } else if (q.hasPhrases()) {
            //NOTE: just does the first phrase for now
            TreeSet<String> result = (TreeSet) getCurrentQueryDictionary().doPhraseQuery(q.getPhrases().get(0));
            for (String id : result) {
                System.out.println(id);
            }
        } else if (operator.equalsIgnoreCase(Query.AndOperator)) {
            TreeSet<String> result = (TreeSet) getCurrentQueryDictionary().doANDQuery(q);

            for (String id : result) {
                System.out.println(id);
            }

            getCurrentQueryDictionary().collectANDStatistics(q);

        } else if (operator.equalsIgnoreCase(Query.OrOperator)) {
            getCurrentQueryDictionary().doORQuery(q);

        } else if (operator.equalsIgnoreCase(Query.NotOperator)) {
            getCurrentQueryDictionary().doNOTQuery(q);

        } else {
            //TODO - throw InvalidQueryOperatorException
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
        String query = "FORCE /2 NUCLEAR";
        queryParser.executeQuery(query);
    }

    public static void phraseTest() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Testing Phrase Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        String query = "\"NUCLEAR STRIKE\"";
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
        //QueryParser.phraseTest();
        QueryParser.proximityTest();
    }
}
