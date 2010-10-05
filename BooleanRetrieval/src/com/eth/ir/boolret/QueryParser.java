package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class QueryParser {


    //retrieve the index.
    // parse through the index creating treemap of frequecny and dictionary.

    

    TreeMap<String,PostingList> index = new TreeMap<String,PostingList>();
    HashMap<Integer,String> docIndex = new HashMap<Integer,String>();

    public void doORQuery(Query query)
    {
        String terms[] = query.getTerms();
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        HashSet<String> result = new HashSet<String>();
        // PostingList -> LinkedList of PostingListNodes [docId, freq]
        long currentTime = System.nanoTime();
        for(String term : terms)
        {
            PostingList pl = index.get(term);

            for(PostingListNode pln : pl.getPostingList()) {
                result.add(pln.getDocId());
            }
        }
        long executionTime = System.nanoTime() - currentTime;
        //System.out.println("Result = " + result);

        for(String id : result) {
            System.out.println(id);
        }

        System.out.println("The Execution Time in nanoseconds " + executionTime);
    }

    
    public void doNOTQuery(Query query)
    {
        String terms[] = query.getTerms();
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        //HashMap<String,Set<String>> result = new HashMap<String,Set<String>>();
        List<Set<String>> result = new ArrayList<Set<String>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        long currentTime = System.nanoTime();
        for(String term : terms)
        {
            //System.out.println("termsss=" + term);
            PostingList pl = index.get(term);
            solutionSpace.put(term, pl);
            Set<String> docSet = new LinkedHashSet<String>();
            for(PostingListNode pln : pl.getPostingList())
            {
                docSet.add(pln.getDocId());
            }
            result.add(docSet);

        }



        //System.out.println(" result = " + result);
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
        long executionTime = System.nanoTime() - currentTime;

        //System.out.println("Result = " + partialSet);
        for(String id : partialSet) {
            System.out.println(id);
        }
        System.out.println("The Execution Time in nanoseconds = " + executionTime);
    }

    public void collectStatistics(Query query)
    {
        
        String terms[] = query.getTerms();
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        TreeMap<Integer,ArrayList<Set<String>>> result = new TreeMap<Integer,ArrayList<Set<String>>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]


        for(String term : terms)
        {
            PostingList pl = index.get(term);
            solutionSpace.put(term, pl);
            HashSet<String> docSet = new HashSet<String>();
            for(PostingListNode pln : pl.getPostingList()) {
                docSet.add(pln.getDocId());
            }
            if(result.containsKey(pl.getPostingList().size()))
            {
                ArrayList<Set<String>> temp = result.get(pl.getPostingList().size());
                temp.add(docSet);
                result.put(pl.getPostingList().size(), temp);
            }
            else
            {
                ArrayList<Set<String>> temp = new ArrayList<Set<String>>();
                temp.add(docSet);
                result.put(pl.getPostingList().size(), temp);
            }
        }

     //   System.out.println("result=" + result);

        long currentTime = System.nanoTime();

        ArrayList<Set<String>> partialSet = new ArrayList<Set<String>>();
        Set<String> firstSet = new HashSet<String>();
        boolean first = true;
        for(Map.Entry<Integer,ArrayList<Set<String>>> iterator : result.entrySet())
        {


               for(Set<String> loopSet : iterator.getValue())
               {
                   if(first)
                   {
                       firstSet = loopSet;
                       first = false;
                   }
                    else
                    firstSet.retainAll(loopSet);
               }
                
        }
        long executionTime = System.nanoTime() - currentTime;
        System.out.println("Execution Time fastest in nanoseconds = " + executionTime);
        //System.out.println("Result = " + partialSet);




       NavigableMap<Integer,ArrayList<Set<String>>> reverse = result.descendingMap();
       // System.out.println("reverse=" + reverse);
        currentTime = System.nanoTime();
        first = true;
        for(Map.Entry<Integer,ArrayList<Set<String>>> iterator : reverse.entrySet())
        {


               for(Set<String> loopSet : iterator.getValue())
               {
                   if(first)
                   {
                       firstSet = loopSet;
                       first = false;
                   }
                    else
                    firstSet.retainAll(loopSet);
               }

        }
       
       executionTime = System.nanoTime() - currentTime;

       System.out.println("Execution Time slowest in nanoseconds = " + executionTime);

     
 
        

    }

    public void doANDQuery(Query query)
    {
        String terms[] = query.getTerms();
        TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        TreeMap<String,Set<String>> result = new TreeMap<String,Set<String>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        for(String term : terms)
        {
            PostingList pl = index.get(term);
            solutionSpace.put(term, pl);
            HashSet<String> docSet = new HashSet<String>();
            for(PostingListNode pln : pl.getPostingList()) {
                docSet.add(pln.getDocId());
            }
            result.put(term.trim(), docSet);
        }



        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for(Map.Entry<String,Set<String>> iterator : result.entrySet())
        {
           if(first) {
               partialSet = iterator.getValue();
               first=false;
            } else {
               partialSet.retainAll(iterator.getValue());
            }
        }



        //System.out.println("Result = " + partialSet);
        for(String id : partialSet) {
            System.out.println(id);
        }
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
            TreeMap<String, PostingList> index = (TreeMap<String, PostingList>) x;
            System.out.println("index " + index.size());
            this.index = index;

            /*
            for (Map.Entry<String, PostingList> entry : index.entrySet()) {
            String key = entry.getKey();
            PostingList value = entry.getValue();
            System.out.println("key = " + key + " PostingListSize =" + value.getPostingList().size());
            }
             */

            p.close();
            System.out.println("Inverted index read from file ==> " + indexFile);
        } catch (Exception e) {
            System.out.println("Can't write output file.");
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
        String terms[] = q.getTerms();

        //System.out.println("Operator = '" + operator + "'");
        //System.out.println(terms);
        
        //Find the PostingList for each term in the query
        ArrayList<PostingList> postingListArray = new ArrayList<PostingList>();
        for (String term : terms) {
            PostingList pl = index.get(term);
            postingListArray.add(pl);
        }
        //System.out.println(" postingListArray.size " + postingListArray.size());

        if(operator.equalsIgnoreCase(Query.AndOperator)) {
            doANDQuery(q);
            collectStatistics(q);

            /*
            //merge each pair of terms with the result of the previous merge
            PostingList resultList = postingListArray.get(0);
            for(PostingList nextPostingList : postingListArray.subList(1, postingListArray.size()-1)) {
                resultList = doANDQuery(resultList, nextPostingList);
            }

            //TESTING
            System.out.print(resultList.getNumPostings() + " matches found:");
            for(PostingListNode pln : resultList.getPostingList()) {
                System.out.print(pln.getDocId() + ", ");
            }
            System.out.println("---");
            */
        } else if(operator.equalsIgnoreCase(Query.OrOperator)) {
            doORQuery(q);
        } else if(operator.equalsIgnoreCase(Query.NotOperator)) {
            doNOTQuery(q);
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
            while(query.trim().equals("")) {
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

    public void executeAllQueriesInDirectory(String dir){
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (File file : files) {
            String query = loadQueryFromFile(file);
            System.out.println(file.getName() + ":" + query);
            executeQuery(query);
            System.out.println("");
        }
    }

    public static void main(String args[]) {
        QueryParser x = new QueryParser();
        String indexFile = "F://ETH//Projects//InformationRetrieval//ethprojects//BooleanRetrieval//index";
        //String indexFile = "C://Users//ghff//Documents//ETH//Fall 2010//Information Retrieval//Dataset//index";
        String queryDir = "F://ETH//Projects//InformationRetrieval//ethprojects//BooleanRetrieval//Queries";
        //String queryDir = "C://Users//ghff//Documents//ETH//Fall 2010//Information Retrieval//Dataset//Queries";

        //Load index from file
        x.readIndex(indexFile);
        //x.readDocIndex(docIndexFile);
        x.executeAllQueriesInDirectory(queryDir);

        /*
        String testQuery = "INDIAN and COMMUNIST and CHINESE";
        String testQuery2 = "BORDER and ISRAEL and SYRIA" ;
        String testQuery3 = "NUCLEAR and WEAPONS and DEVELOPMENT";
        //x.doAndQuery(testQuery4);

        String testQuery5 = "MOSCOW or SUPPORT or AUTONOMY";
        String testQuery6 = "COUNTRY or NEW or JOIN or UNITED or NATIONS";
        //x.doORQuery(testQuery6);

        String testQuery7 = "PROVISIONS not TREATY";
        String testQuery8 = "PREMIER not KHRUSHCHEV";
        x.doNOTQuery(testQuery8);
        */
    }

}
