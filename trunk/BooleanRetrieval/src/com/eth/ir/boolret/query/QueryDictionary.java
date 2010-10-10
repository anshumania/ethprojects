/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.boolret.query;

import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import java.util.ArrayList;
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
public class QueryDictionary {

    // for statistical purposes
    protected long currentTime;
    // the universal index for all types of parsers
    protected TreeMap<String, PostingList> index = new TreeMap<String, PostingList>();

    public void setIndex(TreeMap<String, PostingList> index) {
        this.index = index;
    }

    public void initiateTime() {
        currentTime = System.nanoTime();

    }

    public void executionTime(String type,String query) {
        Logger.getLogger(QueryDictionary.class.getName()).log(Level.INFO, " Query [ {0} ] {1} Execution Time {2}", new Object[]{query,type,(System.nanoTime() - currentTime)});

    }
    

    public void doANDQuery(Query query) {
        // fetch the terms for this query
        String terms[] = query.getTerms();

        //TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        // A treeMap for all terms and their corresponding docIds
        TreeMap<String, Set<String>> result = new TreeMap<String, Set<String>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        // For each term fetch its corresponding postingList pl and for each
        // postingListnode pln add the corresponding docId to the Set<String>
        // for that term
        for (String term : terms) {
            PostingList pl = index.get(term);
            if(pl != null) // Stop Words implementation
            {
            HashSet<String> docSet = new HashSet<String>();
            for (PostingListNode pln : pl.getPostingList()) {
                docSet.add(pln.getDocId());
            }
            result.put(term.trim(), docSet);
            }
        }



        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for (Map.Entry<String, Set<String>> iterator : result.entrySet()) {
            if (first) {
                partialSet = iterator.getValue();
                first = false;
            } else {
                partialSet.retainAll(iterator.getValue()); // the AND operation
            }
        }

        for (String id : partialSet) {
            System.out.println(id);
        }
    }

    public void doORQuery(Query query) {
        String terms[] = query.getTerms();
        // a hashSet to store the docIds
        HashSet<String> result = new HashSet<String>();
        // PostingList -> LinkedList of PostingListNodes [docId, freq]
        initiateTime();

        // for every term fetch the corresponding postinglist for the term
        // from the index and add all its docIds to the hashSet
        for (String term : terms) {
            PostingList pl = index.get(term);
            if (pl != null) // this NULL check is actually just for implementing stopwords
            {
                for (PostingListNode pln : pl.getPostingList()) {
                    result.add(pln.getDocId()); // the OR operation
                }
            }

        }

        for (String id : result) {
            System.out.println(id);
        }

        executionTime("Average",query.getQueryString());

    }


    public void doNOTQuery(Query query) {
        // Fetch the terms for this query
        String terms[] = query.getTerms();
        // Maintain a list of Set<String> which will contain all the
        // docIds for a particular term. note we dont need the terms for the
        // not operation
        List<Set<String>> result = new ArrayList<Set<String>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        initiateTime();
        // for each term in the query fetch the PostingList pl for that term
        // and then for each PostingListNode pln add all its docIds to a Set<String>
        // and add this to the result ( List<Set<String>> )
        for(String term : terms)
        {
            
            PostingList pl = index.get(term);
            if(pl !=null)   // this is the STOPWords implementation
            {
            
            Set<String> docSet = new LinkedHashSet<String>();
            for(PostingListNode pln : pl.getPostingList())
            {
                docSet.add(pln.getDocId());
            }
            result.add(docSet);
            }
        }



        
        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for(Set<String> iterator : result)
        {
           if(first)
           {
               partialSet = iterator ;
               first=false;
            }
           else
               partialSet.removeAll(iterator); // The NOT Operation
        }

        executionTime("Average",query.getQueryString());

        //System.out.println("Result = " + partialSet);
        for(String id : partialSet) {
            System.out.println(id);
        }
        
    }
/**
 * the objective of this function is to order the postingLists
 * by size and then perform the NOT operation on the terms ( ordered by postingLists size)
 * in both increasing and decreasing order.
 * @param query
 */
    public void collectNOTStatistics(Query query)
    {
        String terms[] = query.getTerms();

        // In order to achieve this we create a datastructure TreeMap
        // which has postingList size as key and an ArrayList of Set<docIds>
        // for terms with that postingList size
        TreeMap<Integer,ArrayList<Set<String>>> result = new TreeMap<Integer,ArrayList<Set<String>>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        //require this for the "not x and y"

        initiateTime();


        // for each term fetch the postingList pl and for each postingListingNode pln
        // in pl add its docId to a Set<String>.
        // Finally add this entry of pln's size, ArrayList<Set<String>> to the TreeMap

        for(String term : terms)
        {
            //System.out.println("termsss=" + term);

            PostingList pl = index.get(term);

            if(pl!=null) // check for stopwords
            {
            Set<String> docSet = new LinkedHashSet<String>();
            for(PostingListNode pln : pl.getPostingList())
            {
                docSet.add(pln.getDocId());
            }
            ArrayList<Set<String>> temp;
            // check if the result has an entry for this size
            if(!result.containsKey(pl.getPostingList().size()))
               temp = new ArrayList<Set<String>>();
            else
               temp = result.get(pl.getPostingList().size());
                        
            temp.add(docSet);
            result.put(pl.getPostingList().size(),temp);
            }
        }


        // Now our dataStructure is ready
        // we perform the NOT operation by increasing order first
        //( which is by default in the TreeMap)
        // Operation are of type X not Y only

        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for(Map.Entry<Integer,ArrayList<Set<String>>> iterator : result.entrySet())
        {
           for(Set<String> loopSet : iterator.getValue())
               {
                   if(first)
                   {
                       partialSet = loopSet;
                       first = false;
                   }
                    else
                        partialSet.removeAll(loopSet); // The NOT operation
               }



        }


        executionTime("Slowest",query.getQueryString());

        // now in decreasing order of the postinglist size
        // operation is now of the form  not Y and X

        NavigableMap<Integer,ArrayList<Set<String>>> reverse = result.descendingMap();
       
        initiateTime();
        first = true;
        for(Map.Entry<Integer,ArrayList<Set<String>>> iterator : reverse.entrySet())
        {

               for(Set<String> loopSet : iterator.getValue())
               {
                   if(first)
                   {
                       partialSet = loopSet;
                       first = false;
                   }
                    else
                   {
                    Set<String> temp = loopSet;
                    temp.removeAll(partialSet); // not Y and X
                    partialSet.clear();
                    partialSet.addAll(temp);
                   }
               }

        }

       executionTime("Fastest",query.getQueryString());

    }

    /**
     * the objective of this function is to order the postingLists
     * by size and then perform the AND operation on the terms ( ordered by postingLists size)
     * in both increasing and decreasing order.
     * @param query
     */

 public void collectANDStatistics(Query query)
    {

        String terms[] = query.getTerms();
       
        // In order to achieve this we create a datastructure TreeMap
        // which has postingList size as key and an ArrayList of Set<docIds>
        // for terms with that postingList size
        TreeMap<Integer,ArrayList<Set<String>>> result = new TreeMap<Integer,ArrayList<Set<String>>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]


        for(String term : terms)
        {
            PostingList pl = index.get(term);
            if(pl!=null) // the STOP Words implementation
            {
       
            HashSet<String> docSet = new HashSet<String>();
            for(PostingListNode pln : pl.getPostingList()) {
                docSet.add(pln.getDocId());
            }
            ArrayList<Set<String>> temp;
            if(result.containsKey(pl.getPostingList().size()))
               temp = result.get(pl.getPostingList().size());
            else
               temp = new ArrayList<Set<String>>();

            temp.add(docSet);
            result.put(pl.getPostingList().size(), temp);
            }
        }



        initiateTime();

        
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
                    firstSet.retainAll(loopSet); // the AND operation
               }

        }
        executionTime("Fastest",query.getQueryString());
        
        NavigableMap<Integer,ArrayList<Set<String>>> reverse = result.descendingMap();
       
        initiateTime();
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
                    firstSet.retainAll(loopSet);  // the AND operation
               }

        }

       executionTime("Slowest",query.getQueryString());

    }

}
