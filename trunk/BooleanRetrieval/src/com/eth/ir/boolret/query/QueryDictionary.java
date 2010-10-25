package com.eth.ir.boolret.query;

import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
    protected HashMap<String, Integer> documentLengths = new HashMap<String, Integer>();

    public TreeMap<String, PostingList> getIndex() {
        return index;
    }

    public void setIndex(TreeMap<String, PostingList> index) {
        this.index = index;
    }

    public HashMap<String, Integer> getDocumentLengths() {
        return documentLengths;
    }

    public void setDocumentLengths(HashMap<String, Integer> documentLengths) {
        this.documentLengths = documentLengths;
    }

    public void initiateTime() {
        currentTime = System.nanoTime();
    }

    public void executionTime(String type, String query) {
        Logger.getLogger(QueryDictionary.class.getName()).log(Level.INFO, " Query [ {0} ] {1} Execution Time {2}", new Object[]{query, type, (System.nanoTime() - currentTime)});
    }

    public Set<String> doProximityQuery(String term1, String term2, Integer proximityBefore, Integer proximityAfter) {
        TreeMap<String, PostingList> miniIndex = new TreeMap<String, PostingList>();
        Set<String> resultSet = new TreeSet<String>();

        //first do a normal AND query to find matches
        ArrayList<String> terms = new ArrayList<String>();
        terms.add(term1);
        terms.add(term2);
        Set<String> matches = doANDQuery(new Query(terms, Query.AndOperator));
        if(matches.isEmpty()) {
            //no matches at all, don't check positions
            return resultSet;
        }
        
        //for each term, build the posting list of only matches
        for(String term : terms) {
            PostingList pl = index.get(term);
            //note: no stop word check needed since we have already done an AND
            
            for(String doc : matches) {
                PostingList matchPostingList = new PostingList();
                for (PostingListNode pln : pl.getPostingList()) {
                    if(matches.contains(pln.getDocId())) {
                        matchPostingList.getPostingList().add(pln);
                    }
                }
                miniIndex.put(term, matchPostingList);
            }
        }

        PostingList resultPL =  doProximityQuery(miniIndex.get(term1), miniIndex.get(term2), proximityBefore, proximityAfter);
        
        //put each matching document id into a set
        for(PostingListNode pln : resultPL.getPostingList()) {
            resultSet.add(pln.getDocId());
        }

        return resultSet;
    }

    public PostingList doProximityQuery(PostingList pl1, PostingList pl2, Integer proximityBefore, Integer proximityAfter) {
        
        PostingList resultPL = new PostingList();

        //for each position in each document in pl1, check if there is a positional match with pl2
        PostingListNode pln2;
        ListIterator pl2Iterator = pl2.getPostingList().listIterator();
        for(PostingListNode pln : pl1.getPostingList()) {
            pln2 = (PostingListNode) pl2Iterator.next();
            while(!pln2.getDocId().equals(pln.getDocId())) {
                pln2 = (PostingListNode) pl2Iterator.next();
            }

            Set<Integer> matchingPositions = new TreeSet<Integer>();
            for(Integer position : pln.getPositions()) {
                for(int nextPosition = position+proximityBefore; nextPosition <= position + proximityAfter; nextPosition++) {
                    if(pln2.getPositions().contains(nextPosition)) {
                        //we found a positional match
                        matchingPositions.add(nextPosition);
                    }
                }
            }

            if(!matchingPositions.isEmpty()) {
                PostingListNode newPLN = new PostingListNode(pln.getDocId());
                newPLN.setPositions(matchingPositions);
                resultPL.getPostingList().add(newPLN);
            }
        }

        return resultPL;
    }

    public Set<String> doProximityQuery(ArrayList<String> terms, Integer distance) {
        Set<String> resultSet = new TreeSet<String>();
        TreeMap<String, PostingList> miniIndex = new TreeMap<String, PostingList>();

        //first do a normal AND query to find matches
        Set<String> matches = doANDQuery(new Query(terms, Query.AndOperator));
        if(matches.isEmpty()) {
            //no matches at all, don't check positions
            return resultSet;
        }

        //for each term, build the posting list of only matches
        for(String term : terms) {
            PostingList pl = index.get(term);
            //note: no stop word check needed since we have already done an AND

            for(String doc : matches) {
                PostingList matchPostingList = new PostingList();
                for (PostingListNode pln : pl.getPostingList()) {
                    if(matches.contains(pln.getDocId())) {
                        matchPostingList.getPostingList().add(pln);
                    }
                }
                miniIndex.put(term, matchPostingList);
            }
        }

        //check for proximity matches for each successive pair of terms
        PostingList resultPL = null;
        Boolean isFirst = true;
        for(String term : terms) {
            if(isFirst) {
                resultPL = miniIndex.get(term);
                isFirst = false;
            } else {
                resultPL = doProximityQuery(resultPL, miniIndex.get(term), 0, distance);
            }
        }

        //put each matching document id into a set
        for(PostingListNode pln : resultPL.getPostingList()) {
            resultSet.add(pln.getDocId());
        }

        return resultSet;
    }

    public Set<String> doPhraseQuery(ArrayList<String> phraseTerms) {
        return doProximityQuery(phraseTerms, 1);
    }

    public Set<String> doVectorQuery(ArrayList<String> phraseTerms)
    {
        // for each term calculate its termFrequency
        // could be shifted to be done during creating itself
        // but for logistic reasons keeping it here

        Map<String, Double> scores = new HashMap<String, Double>();

        for(String term : phraseTerms) {
            PostingList pl = index.get(term);

            if(pl != null) {
                // the document frequency = dft
                Integer docFrequency = pl.getPostingList().size();
                // damp the document frequency = log<10>(N/dft)
                Double docFrequencyDamped = Math.log10(425/docFrequency);

                // the term frequency
                Integer termFrequency = countFrequency(term, phraseTerms);
                // damp the frequency
                Double termFrequencyDamped = (1 + Math.log10(termFrequency));

                //calculate the weighted tf-idf weight for the term
                Double tfIdfWeight = docFrequencyDamped * termFrequencyDamped;


                //calculate dot-product of query term weight and document term weight
                for(PostingListNode pln : pl.getPostingList())
                {
                    Double score = tfIdfWeight * pln.getTf_idf_weight();
                    if(scores.containsKey(pln.getDocId())) {
                        Double newScore = score + scores.get(pln.getDocId());
                        scores.put(pln.getDocId(),  newScore);
                    } else {
                        scores.put(pln.getDocId(), score);
                    }

                }
            }
        }

        //normalize for length
        for(Entry<String, Double> entry : scores.entrySet()) {
            entry.setValue(entry.getValue() / getDocumentLengths().get(entry.getKey()));
        }

        //sort by score
        Map<String, Double> sorted_scores = sortByValue(scores);

        
        /* //for testing only
        for(Entry<String, Double> entry : sorted_scores.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
        */

        //save sorted keys into a set
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        result.addAll(sorted_scores.keySet());
        return result;
    }
    

    public Set<String> doANDQuery(Query query) {
        // fetch the terms for this query
        ArrayList<String> terms = query.getTerms();

        //TreeMap<String,PostingList> solutionSpace = new TreeMap<String,PostingList>();
        // A treeMap for all terms and their corresponding docIds
        TreeMap<String, Set<String>> result = new TreeMap<String, Set<String>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        // For each term fetch its corresponding postingList pl and for each
        // postingListnode pln add the corresponding docId to the Set<String>
        // for that term
        for (String term : terms) {
            PostingList pl = index.get(term);
            if (pl != null) // Stop Words implementation
            {
                TreeSet<String> docSet = new TreeSet<String>();
                for (PostingListNode pln : pl.getPostingList()) {
                    docSet.add(pln.getDocId());
                }
                result.put(term.trim(), docSet);
            }
        }

        Set<String> partialSet = new TreeSet<String>();
        boolean first = true;
        for (Map.Entry<String, Set<String>> iterator : result.entrySet()) {
            if (first) {
                partialSet = iterator.getValue();
                first = false;
            } else {
                partialSet.retainAll(iterator.getValue()); // the AND operation
            }
        }

        return partialSet;
        /*
        for (String id : partialSet) {
            System.out.println(id);
        }
        */
    }

    public Set<String> doORQuery(Query query) {
        ArrayList<String> terms = query.getTerms();
        // a hashSet to store the docIds
        TreeSet<String> result = new TreeSet<String>();
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
        /*
        for (String id : result) {
            System.out.println(id);
        }
        */

        executionTime("Average", query.getQueryString());

        return result;
    }

    public Set<String> doNOTQuery(Query query) {
        // Fetch the terms for this query
        ArrayList<String> terms = query.getTerms();
        
        // Maintain a list of Set<String> which will contain all the
        // docIds for a particular term. note we dont need the terms for the
        // not operation
        List<Set<String>> result = new ArrayList<Set<String>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        initiateTime();
        // for each term in the query fetch the PostingList pl for that term
        // and then for each PostingListNode pln add all its docIds to a Set<String>
        // and add this to the result ( List<Set<String>> )
        for (String term : terms) {

            PostingList pl = index.get(term);
            if (pl != null) // this is the STOPWords implementation
            {

                Set<String> docSet = new TreeSet<String>();
                for (PostingListNode pln : pl.getPostingList()) {
                    docSet.add(pln.getDocId());
                }
                result.add(docSet);
            }
        }

        Set<String> partialSet = new TreeSet<String>();
        boolean first = true;
        for (Set<String> iterator : result) {
            if (first) {
                partialSet = iterator;
                first = false;
            } else {
                partialSet.removeAll(iterator); // The NOT Operation
            }
        }

        executionTime("Average", query.getQueryString());

        /*
        //System.out.println("Result = " + partialSet);
        for (String id : partialSet) {
            System.out.println(id);
        }
        */
        return partialSet;
    }

    /**
     * the objective of this function is to order the postingLists
     * by size and then perform the NOT operation on the terms ( ordered by postingLists size)
     * in both increasing and decreasing order.
     * @param query
     */
    public void collectNOTStatistics(Query query) {
        ArrayList<String> terms = query.getTerms();

        // In order to achieve this we create a datastructure TreeMap
        // which has postingList size as key and an ArrayList of Set<docIds>
        // for terms with that postingList size
        TreeMap<Integer, ArrayList<Set<String>>> result = new TreeMap<Integer, ArrayList<Set<String>>>();
        // PostingList -> LinkedList of PostingListNodes [ docId, freq]
        //require this for the "not x and y"

        initiateTime();


        // for each term fetch the postingList pl and for each postingListingNode pln
        // in pl add its docId to a Set<String>.
        // Finally add this entry of pln's size, ArrayList<Set<String>> to the TreeMap

        for (String term : terms) {
            //System.out.println("termsss=" + term);

            PostingList pl = index.get(term);

            if (pl != null) // check for stopwords
            {
                Set<String> docSet = new LinkedHashSet<String>();
                for (PostingListNode pln : pl.getPostingList()) {
                    docSet.add(pln.getDocId());
                }
                ArrayList<Set<String>> temp;
                // check if the result has an entry for this size
                if (!result.containsKey(pl.getPostingList().size())) {
                    temp = new ArrayList<Set<String>>();
                } else {
                    temp = result.get(pl.getPostingList().size());
                }

                temp.add(docSet);
                result.put(pl.getPostingList().size(), temp);
            }
        }


        // Now our dataStructure is ready
        // we perform the NOT operation by increasing order first
        //( which is by default in the TreeMap)
        // Operation are of type X not Y only

        Set<String> partialSet = new HashSet<String>();
        boolean first = true;
        for (Map.Entry<Integer, ArrayList<Set<String>>> iterator : result.entrySet()) {
            for (Set<String> loopSet : iterator.getValue()) {
                if (first) {
                    partialSet = loopSet;
                    first = false;
                } else {
                    partialSet.removeAll(loopSet); // The NOT operation
                }
            }



        }

        executionTime("Slowest", query.getQueryString());

        // now in decreasing order of the postinglist size
        // operation is now of the form  not Y and X

        NavigableMap<Integer, ArrayList<Set<String>>> reverse = result.descendingMap();

        initiateTime();
        first = true;
        for (Map.Entry<Integer, ArrayList<Set<String>>> iterator : reverse.entrySet()) {

            for (Set<String> loopSet : iterator.getValue()) {
                if (first) {
                    partialSet = loopSet;
                    first = false;
                } else {
                    Set<String> temp = loopSet;
                    temp.removeAll(partialSet); // not Y and X
                    partialSet.clear();
                    partialSet.addAll(temp);
                }
            }

        }

        executionTime("Fastest", query.getQueryString());
    }

    /**
     * the objective of this function is to order the postingLists
     * by size and then perform the AND operation on the terms ( ordered by postingLists size)
     * in both increasing and decreasing order.
     * @param query
     */
    public void collectANDStatistics(Query query) {

        ArrayList<String> terms = query.getTerms();

        // In order to achieve this we create a datastructure TreeMap
        // which has postingList size as key and an ArrayList of Set<docIds>
        // for terms with that postingList size
        TreeMap<Integer, ArrayList<Set<String>>> result = new TreeMap<Integer, ArrayList<Set<String>>>();

        // PostingList -> LinkedList of PostingListNodes [ docId, freq]


        for (String term : terms) {
            PostingList pl = index.get(term);
            if (pl != null) // the STOP Words implementation
            {

                HashSet<String> docSet = new HashSet<String>();
                for (PostingListNode pln : pl.getPostingList()) {
                    docSet.add(pln.getDocId());
                }
                ArrayList<Set<String>> temp;
                if (result.containsKey(pl.getPostingList().size())) {
                    temp = result.get(pl.getPostingList().size());
                } else {
                    temp = new ArrayList<Set<String>>();
                }

                temp.add(docSet);
                result.put(pl.getPostingList().size(), temp);
            }
        }

        initiateTime();

        Set<String> firstSet = new HashSet<String>();
        boolean first = true;
        for (Map.Entry<Integer, ArrayList<Set<String>>> iterator : result.entrySet()) {

            for (Set<String> loopSet : iterator.getValue()) {
                if (first) {
                    firstSet = loopSet;
                    first = false;
                } else {
                    firstSet.retainAll(loopSet); // the AND operation
                }
            }

        }
        executionTime("Fastest", query.getQueryString());

        NavigableMap<Integer, ArrayList<Set<String>>> reverse = result.descendingMap();

        initiateTime();
        first = true;
        for (Map.Entry<Integer, ArrayList<Set<String>>> iterator : reverse.entrySet()) {


            for (Set<String> loopSet : iterator.getValue()) {
                if (first) {
                    firstSet = loopSet;
                    first = false;
                } else {
                    firstSet.retainAll(loopSet);  // the AND operation
                }
            }

        }

        executionTime("Slowest", query.getQueryString());
    }

    //returns the number of times term occurs in phraseTerms
    private Integer countFrequency(String term, ArrayList<String> allTerms) {
        Integer count = 0;
        for(String currentTerm : allTerms) {
            if(term.equals(currentTerm)) {
                count ++;
            }
        }
        return count;
    }

    //from: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java/3420912#3420912
    //switched compare order to sort highest to lowest
    static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                  return ((Comparable) ((Map.Entry) (o2)).getValue())
                 .compareTo(((Map.Entry) (o1)).getValue());
             }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
