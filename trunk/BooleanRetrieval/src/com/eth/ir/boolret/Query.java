package com.eth.ir.boolret;

import java.util.ArrayList;

/**
 * Simple Query class to store queries with a single operator
 * @author Tim
 */
public class Query {
    //Constants
    public static final String AndOperator = "AND";
    public static final String OrOperator = "OR";
    public static final String NotOperator = "NOT";

    String [] terms; //array of all terms in the query
    String operator; //Must be one of: 'AND', 'OR', 'NOT'

    /**
     *
     * @param query A query containing a single operator (AND, OR, NOT)
     */
    Query(String query) {
        parseQueryString(query);
    }

    /**
     *
     * @param query A query containing a single operator (AND, OR, NOT)
     */
    private void parseQueryString(String query) {
        //first split by space to find the operator
        String words[] = query.trim().split(" ");
        this.setOperator(words[1]); //operator is always in second position

        //then find all terms and trim whitespace
        //NOTE: this assumes well-formed input with single words separated by a single operator
        int wordCount = 0, numTerms = 0;
        String[] tempTerms = new String[(words.length+1)/2];
        for(String word : words) {
            wordCount++;
            if(wordCount % 2 != 0) {
                tempTerms[numTerms] = word.trim();
                numTerms++;
            }
        }
        this.setTerms(tempTerms);
    }

    public String[] getTerms() {
        return terms;
    }

    public void setTerms(String[] terms) {
        this.terms = terms;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


}
