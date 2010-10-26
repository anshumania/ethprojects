package com.eth.ir.boolret.query;

import com.eth.ir.boolret.Bundle;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
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

    public void readDocumentLengthsFile(String documentLengthsFile) {
        FileInputStream istream = null;
        ObjectInputStream p = null;
        try {
            istream = new FileInputStream(documentLengthsFile);
            p = new ObjectInputStream(istream);
        } catch (Exception e) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, "Can't open document lengths file {0}", documentLengthsFile);
            e.printStackTrace();
        }
        try {
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Reading document lengths {0}", documentLengthsFile);
            Object x = p.readObject();
            HashMap<String, Integer> documentLengths = (HashMap<String, Integer>) x;
            getCurrentQueryDictionary().setDocumentLengths(documentLengths);

            p.close();
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Document lengths read from file {0}", documentLengthsFile);

        } catch (Exception e) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, "Document lengths cannot be read from file {0}", documentLengthsFile);
            e.printStackTrace();
        }
    }


    public Map<String, HashSet<String>> readRelevancyListsFile(String fileName) {
        HashMap<String, HashSet<String>> relevancyLists = new HashMap<String, HashSet<String>>();
        InputStream fileStream = QueryParser.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
        String relevancyList;
        try {
            while ((relevancyList = br.readLine()) != null) {
                //stopWords.add(stopWord.trim());
                String[] tokens = relevancyList.split("\\s+"); //split on whitespace
                String key = "Q" + tokens[0];
                HashSet<String> relevantDocs = new HashSet<String>();
                for(int x=1; x < tokens.length; x++) {
                    String docId = "doc" + tokens[x];
                    relevantDocs.add(docId);
                }
                relevancyLists.put(key, relevantDocs);
            }
            Logger.getLogger(QueryParser.class.getName()).log(Level.INFO,"Relevancy Lists loaded");
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return relevancyLists;
    }

    /**
     *
     * @param query The query to execute.  May contain whitespace at front and/or back.
     */
    public void executeQuery(String query, Boolean isVectorQuery) {
        System.out.println("[QUERY]  " + query);
        Query q = new Query(query, isVectorQuery);
        String operator = q.getOperator();
        Set<String> result = null;
        ArrayList<TreeSet<String>> phraseResults = new ArrayList<TreeSet<String>>();

        if(operator == null) {
            System.out.println("Invalid Query");
            return;
        } else if(operator.equalsIgnoreCase(Query.VectorOperator)) {
            result = (LinkedHashSet) getCurrentQueryDictionary().doVectorQuery(q.getTerms());

        } else if(operator.equalsIgnoreCase(Query.PhraseOperator)) {
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

        }

        if(result.size() > 0) {
            if(isVectorQuery) {
                int count=0;
                System.out.println("Top 20 results:");
                for(String id : result) {
                    if(count != 0) System.out.print(",");
                    System.out.print(id);
                    if(count >= 20) break;
                    count++;
                }
                System.out.print("\n");
            } else {
                System.out.println(result.size() + " matches:");
                for (String id : result) {
                    System.out.println(id);
                }
            }
        } else {
            System.out.println("No matches found.");
        }

    }

    public LinkedHashSet<String> executeVectorQuery(String query) {
        Query q = new Query(query, true);
        String operator = q.getOperator();
        LinkedHashSet<String> result = null;

        if(operator == null) {
            System.out.println("Invalid Query");
            return result;
        } else if(operator.equalsIgnoreCase(Query.VectorOperator)) {
            result = (LinkedHashSet) getCurrentQueryDictionary().doVectorQuery(q.getTerms());
        }

        return result;
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

    public void executeAllQueriesInDirectory(String dir, Boolean isVectorQuery) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.isHidden()) // hack to escape svn files
            {
                String query = loadQueryFromFile(file);
                System.out.println(file.getName() + ":" + query);
                executeQuery(query, isVectorQuery);
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
        queryParser.executeQuery(query, false);
    }

    public static void phraseTest() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Testing Phrase Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        String query = "\"DELEGATES MEETING\"";
        queryParser.executeQuery(query, false);
    }

    public static void phase1() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 1 of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile(), false);
    }

    public static void phase2_StopWords() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 2 - StopWords of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.STOPWORD).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile(), false);
    }

    public static void phase2_Stemming() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Phase 2 - Stemming of Query Parsing");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new StemmedQueryDictionary());
        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.PORTERSTEM).getFile());
        // Execute all the queries in the directory
        queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_DIR).getFile(), false);
    }

    public static void project2_vectorSpaceModel() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Project2 Phase1  - Query Parsing Vector Space Modelling");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());
        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE).getFile());
        queryParser.readDocumentLengthsFile(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.DOCUMENT_LENGTHS_FILE).getFile());
        Map<String, HashSet<String>> relevancyLists = queryParser.readRelevancyListsFile("../" + Bundle.RELEVANCY_LISTS_FILE);

        // Execute all the queries in the directory
        //queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), true);
        queryParser.precisionRecallGraphForAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), relevancyLists);
    }
    public static void project2_vectorSpaceModel_StopWords() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Project2 Phase1  - Query Parsing Vector Space Modelling");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());
        //stop words
        queryParser.getCurrentQueryDictionary().setStopWordMode(true);
        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.STOPWORD).getFile());
        queryParser.readDocumentLengthsFile(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.DOCUMENT_LENGTHS_FILE).getFile());
        Map<String, HashSet<String>> relevancyLists = queryParser.readRelevancyListsFile("../" + Bundle.RELEVANCY_LISTS_FILE);

        // Execute all the queries in the directory
        //queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), true);
        queryParser.precisionRecallGraphForAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), relevancyLists);
    }
    public static void project2_vectorSpaceModel_StopStemmedWords() {
        Logger.getLogger(QueryParser.class.getName()).log(Level.INFO, "Project2 Phase1  - Query Parsing Vector Space Modelling");
        QueryParser queryParser = new QueryParser();
        queryParser.setCurrentQueryDictionary(new QueryDictionary());
        //stop words
        queryParser.getCurrentQueryDictionary().setStopWordMode(true);
        //stemmed words
        queryParser.getCurrentQueryDictionary().setStemmedWordMode(true);

        //Load index from file
        queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.INDEX_FILE + Bundle.PORTERSTEM).getFile());
        queryParser.readDocumentLengthsFile(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + Bundle.DOCUMENT_LENGTHS_FILE).getFile());
        Map<String, HashSet<String>> relevancyLists = queryParser.readRelevancyListsFile("../" + Bundle.RELEVANCY_LISTS_FILE);

        // Execute all the queries in the directory
        //queryParser.executeAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), true);
        queryParser.precisionRecallGraphForAllQueriesInDirectory(QueryParser.class.getResource("../" + Bundle.QUERY_2_DIR).getFile(), relevancyLists);
    }



    public static void main(String args[]) {

        //QueryParser.phase1();
        //QueryParser.phase2_StopWords();
        //QueryParser.phase2_Stemming();
        //QueryParser.phraseTest();
        //QueryParser.proximityTest();
//        QueryParser.project2_vectorSpaceModel();
//        QueryParser.project2_vectorSpaceModel_StopWords();
        QueryParser.project2_vectorSpaceModel_StopStemmedWords();
    }

    //Ugly brute force attempt at finding words in the index that are actually 2 words with a space
    public void findSpellingErrors(int numToFind) {
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
                                System.out.println(word + "  [" + substring1 + "][" + substring2 + "]");

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
        System.out.println(numFound + " spelling errors found.");
    }


    private void printPrecisionRecallTable(LinkedHashSet<String> queryResults, Set<String> relevancyList) {
        Double totalRelevant = new Double(relevancyList.size());
        Double numRelevantFound = new Double(0);
        Double currentPosition = new Double(1); //start at 1 to avoid divide by 0 problems
        ArrayList<Double> recall = new ArrayList<Double>();
        ArrayList<Double> precision = new ArrayList<Double>();

        for(String queryResult : queryResults) {
            if(relevancyList.contains(queryResult)) {
                numRelevantFound++;
            }
            Double currentRecall = new Double(numRelevantFound / totalRelevant);
            Double currentPrecision = new Double(numRelevantFound / currentPosition);

            recall.add(currentRecall);
            precision.add(currentPrecision);

            //for testing
            System.out.println(queryResult + " | " + currentRecall + " | " + currentPrecision);

            if(numRelevantFound.equals(totalRelevant)) {
                break;
            }
            currentPosition++;
        }
        System.out.println("");
    }




    private void precisionRecallGraphForAllQueriesInDirectory(String dir, Map<String, HashSet<String>> relevancyLists) {
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        Boolean isVectorQuery = true;
        File directory = new File(dir);
        File[] files = directory.listFiles();
        TreeMap<Double, ArrayList<Double>> results = new TreeMap<Double, ArrayList<Double>>();
        for(Double d=new Double(0); d <= 1.0; d = Double.valueOf(oneDecimal.format(d + 0.1))) {
            results.put(d, new ArrayList<Double>());
        }

        for (File file : files) {
            if (!file.isHidden()) // hack to escape svn files
            {
                String queryId = file.getName();
                String query = loadQueryFromFile(file);
                System.out.println(queryId + ":" + query);
                LinkedHashSet<String> queryResults = executeVectorQuery(query);

                Map<Double, Double> iprt = getInterpolatedPrecisionRecallTable(queryResults, relevancyLists.get(queryId));
                for(Entry<Double,Double> e : iprt.entrySet()) {
                    results.get(e.getKey()).add(e.getValue());
                }
            }
        }

        //take average of all interpolated precision/recall tables
        for(Entry<Double, ArrayList<Double>> result : results.entrySet()) {
            Double sum = new Double(0);

            for(Double d : result.getValue()) {
                sum += d;
            }
            Double average = sum / new Double(result.getValue().size());
            System.out.println(result.getKey() + "," + average);
        }
    }

    private Map<Double, Double> getInterpolatedPrecisionRecallTable(LinkedHashSet<String> queryResults, Set<String> relevancyList) {
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        Double totalRelevant = new Double(relevancyList.size());
        Double numRelevantFound = new Double(0);
        Double currentPosition = new Double(1); //start at 1 to avoid divide by 0 problems
        ArrayList<Double> recall = new ArrayList<Double>();
        ArrayList<Double> precision = new ArrayList<Double>();
        System.out.println("getInterpolatedPrecisionRecallTable");
//        System.out.println("releva ncyList.size="+relevancyList.size());
//        System.out.println("queryResults.size="+queryResults.size());
//        System.out.println("estimated precision="+(double)(relevancyList.size()/queryResults.size()));
        for(String queryResult : queryResults) {
            if(relevancyList.contains(queryResult)) {
                numRelevantFound++;
            }
//            System.out.println("numRelevantFound=" + numRelevantFound);
            Double currentRecall = new Double(numRelevantFound / totalRelevant);
            Double currentPrecision = new Double(numRelevantFound / currentPosition);
//            System.out.println("currentPrecesion="+currentPrecision);
//            System.out.println("currentPosition="+currentPosition);
            recall.add(currentRecall);
            precision.add(currentPrecision);

            //for testing
            System.out.println(queryResult + " | " + currentRecall + " | " + currentPrecision + " | " + currentPosition);


            if(numRelevantFound.equals(totalRelevant)) {
                break;
            }
            currentPosition++;
        }
        //System.out.println("");

        //create interpolated precision/recall table
        Map<Double, Double> interpolatedPrecisionRecallTable = new TreeMap<Double, Double>();
        int currentIndex = recall.size() - 1;
        Double highestPrecision = new Double(0);
        for(Double recallLevel = 1.0; recallLevel >= 0; recallLevel = Double.valueOf(oneDecimal.format(recallLevel - 0.1))) {
            while(currentIndex >= 0 && recall.get(currentIndex) >= recallLevel) {
                if(precision.get(currentIndex) > highestPrecision) {
                    highestPrecision = precision.get(currentIndex);
                }
                currentIndex--;
            }
            interpolatedPrecisionRecallTable.put(recallLevel, highestPrecision);
        }
        System.out.println(" interpolatedPrecisionRecallTable=" + interpolatedPrecisionRecallTable);
        
        return interpolatedPrecisionRecallTable;
    }

}
