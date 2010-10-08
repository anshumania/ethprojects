/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class StopWords {

    final static String STOPWORDFILE = "resources/English_Stopwords.txt";
    private static Set<String> stopWords;

    public static Set<String> getStopWords() {
        return stopWords;
    }

    public static boolean isStopWord(String test)
    {
        return stopWords.contains(test.toLowerCase()) ;
    }

    
    public static void readStopWordsFile(String fileName)
    {
        stopWords = new HashSet<String>();
        InputStream fileStream = StopWords.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
        String stopWord;
        try {
            while ((stopWord = br.readLine()) != null) {
               stopWords.add(stopWord.trim());
            }
            Logger.getLogger(StopWords.class.getName()).log(Level.INFO,"StopWords initiated");
        } catch (IOException ex) {
            Logger.getLogger(StopWords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String args[])
    {
      StopWords.readStopWordsFile(STOPWORDFILE);
    }

}
