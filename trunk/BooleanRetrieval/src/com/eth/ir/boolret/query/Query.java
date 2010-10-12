package com.eth.ir.boolret.query;

import com.eth.ir.boolret.Bundle;
import com.eth.ir.boolret.FileIndexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Query class to store queries with a single operator
 * @author Tim
 */
public class Query {
    //Constants
    public static final String AndOperator = "AND";
    public static final String OrOperator = "OR";
    public static final String NotOperator = "NOT";

    //String [] terms; //array of all terms in the query
    private ArrayList<String> terms;
    private ArrayList<ArrayList<String>> phrases;
    private ArrayList<ProximityQuery> proximityQueries;

    //TODO - proximity terms and distances
    private String operator; //Must be one of: 'AND', 'OR', 'NOT'
    private String queryString; // the original query string for this Query

    /**
     *
     * @param query A query containing a single operator (AND, OR, NOT)
     */
    public Query(String query) {
        this.terms = new ArrayList<String>();
        this.phrases = new ArrayList<ArrayList<String>>();
        this.proximityQueries = new ArrayList<ProximityQuery>();
        this.queryString = query;
        parseQueryString(query);
    }

    public Query(ArrayList<String> terms, String operatorString) {
        this.terms = terms;
        this.operator = operatorString;
        this.phrases = new ArrayList<ArrayList<String>>();
        this.proximityQueries = new ArrayList<ProximityQuery>();
    }

    private Boolean isOperator(String s) {
        if(s == null) return false;
        
        return s.equalsIgnoreCase(AndOperator) || s.equalsIgnoreCase(OrOperator) || s.equalsIgnoreCase(NotOperator);
    }

    /**
     *
     * @param query A query containing a single operator (AND, OR, NOT)
     */
    private void parseQueryString(String query) {
        BufferedReader in = null;
        StringReader reader = null;
        try {
            reader = new StringReader(query.trim());
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);
            // note:  different than when creating index because we want to allow " and / in searches now
            st.ordinaryChars(34, 47);
            st.ordinaryChars(58, 64);
            st.quoteChar(Bundle.DOUBLE_QUOTE);
            st.wordChars(47, 47);
            st.wordChars(65, 90);
            st.wordChars(97, 122);

            Boolean isProximity = false;
            Integer proximityDistance = 0;
            String lastTerm = null, proximityFirstTerm = null;
            int next = st.nextToken();
            while (next != StreamTokenizer.TT_EOF) {
                if(isProximity) {
                    //last token was a proximity distance, now read in second term
                    String nextTerm = FileIndexer.normalizeString(st.sval);
                    this.proximityQueries.add(new ProximityQuery(proximityFirstTerm, nextTerm, proximityDistance));
                    isProximity = false;
                    proximityFirstTerm = null;
                    proximityDistance = 0;
                    lastTerm = nextTerm;
                } else if(next == Bundle.DOUBLE_QUOTE) {
                    //this is a phrase, read in all at once
                    String phrase = st.sval;
                    this.phrases.add(new ArrayList<String>());
                    for(String word : phrase.split("[ ]")) {
                        String nextTerm = FileIndexer.normalizeString(word);
                        this.phrases.get(this.phrases.size()-1).add(nextTerm);
                    }
                } else if (next == StreamTokenizer.TT_WORD && st.sval.startsWith(Bundle.BACKSLASH)) {
                    //this is a proximity search
                    isProximity = true;
                    proximityDistance = Integer.parseInt(st.sval.substring(1));
                    proximityFirstTerm = lastTerm;
                    this.terms.remove(lastTerm);
                } else {

                    //don't add operators to list of terms
                    if(isOperator(st.sval)) {
                        if(this.operator == null) {
                            this.operator = st.sval;
                        }
                    } else {
                        //tokenize using the same method as during indexing
                        String nextTerm = FileIndexer.normalizeWord(st, next);

                        if(!nextTerm.isEmpty()) {
                            this.terms.add(nextTerm);
                        }

                        lastTerm = nextTerm;
                    }
                }
                
                //fetch the next token.
                next = st.nextToken();
            }

        } catch (IOException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<String> terms) {
        this.terms = terms;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getQueryString() {
        return queryString;
    }

    public ArrayList<ArrayList<String>> getPhrases() {
        return phrases;
    }

    public ArrayList<ProximityQuery> getProximityQueries() {
        return proximityQueries;
    }

    public Boolean hasPhrases() {
        return !this.getPhrases().isEmpty();
    }

    public Boolean hasProximityQueries() {
        return !this.getProximityQueries().isEmpty();
    }
}
