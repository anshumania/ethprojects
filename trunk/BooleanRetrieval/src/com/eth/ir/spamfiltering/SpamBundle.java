/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.spamfiltering;

/**
 *
 * @author ANSHUMAN
 */
public class SpamBundle {

     // the directory with all the documents
    public final static String DOC_CORPUS_DIR_t = "resources/Spam_Filtering_Dataset/test";
    public final static String DOC_CORPUS_DIR = "resources/Spam_Filtering_Dataset/Ling-SpamCorpus";
    public final static String CLUSTER_DIR = "part1";
    public final static String CLUSTER_DIR_t = "part2";
    // the spam identifier
    public final static String SPAM_ID = "spmsg";
    public final static String QUERY_2_DIR = "resources/Queries2";
    public final static String INDEX_FILE = "index";
    public final static String STOPWORD   = "_sw";
    public final static String PORTERSTEM = "_pStem";
    public final static String STEMMEDMAP = "stemmedMap";
    public final static String DOCUMENT_LENGTHS_FILE = "doc_lengths";
    public final static String RELEVANCY_LISTS_FILE = "resources/RelevancyLists.txt";
    public final static String COMMA = ",";
    public final static String EMPTY = "";
    public final static int DOUBLE_QUOTE = 34;
    public final static String BACKSLASH = "/";
    public final static String WORDNETHOME = "C:\\Program Files\\WordNet\\2.1";
    //public final static String WORDNETHOME = "C:\\Program Files (x86)\\WordNet\\2.1";
    //public final static String PATHTODICT  = WORDNETHOME + File.separator + "dict";
    public final static String LOCAL_EXPANSION = "local";
    public final static String GLOBAL_EXPANSION = "global";
    public final static String NO_EXPANSION = "";
}
