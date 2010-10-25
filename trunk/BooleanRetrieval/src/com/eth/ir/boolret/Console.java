package com.eth.ir.boolret;

import com.eth.ir.boolret.query.QueryDictionary;
import com.eth.ir.boolret.query.QueryParser;
import com.eth.ir.boolret.query.StemmedQueryDictionary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Church
 */
public class Console {


    public static void main(String args[]) {
        BufferedReader userIn;
        QueryParser queryParser = new QueryParser();
        HashMap<String, String> indexFilenames= new HashMap<String, String>();
        indexFilenames.put("BASIC", Bundle.INDEX_FILE);
        indexFilenames.put("STOPWORD", Bundle.INDEX_FILE + Bundle.STOPWORD);
        indexFilenames.put("STEM", Bundle.INDEX_FILE + Bundle.PORTERSTEM);
        
        try {
            userIn = new BufferedReader(new InputStreamReader(System.in));

            while(true) {
                String index = "";
                String query = "";
                String spellNum = "";

                while (!index.equalsIgnoreCase("BASIC") && !index.equalsIgnoreCase("STOPWORD") && !index.equalsIgnoreCase("STEM")
                        && !index.equalsIgnoreCase("exit") && !index.equalsIgnoreCase("restart")) {
                    System.out.print("Load (basic) index, (stopword) index, or (stem)med index?  ");
                    index = userIn.readLine();
                }

                if (index.equals("exit")) { break; }
                if (index.equals("restart")) { continue; }

                if(index.equalsIgnoreCase("STEM")) {
                    queryParser.setCurrentQueryDictionary(new StemmedQueryDictionary());
                } else {
                    queryParser.setCurrentQueryDictionary(new QueryDictionary());
                }

                System.out.println("Loading index...");
                String filename = indexFilenames.get(index.toUpperCase());
                queryParser.readIndex(QueryParser.class.getResource("../" + Bundle.DOCS_DIR + "/" + indexFilenames.get(index.toUpperCase())).getFile());
                System.out.println("Finished loading index.");
                
                while(true) {
                    query = "";
                    while (query.isEmpty()) {
                        System.out.print("Enter query:  ");
                        query = userIn.readLine();
                    }

                    if (query.equals("exit")) { break; }
                    if (query.equals("restart")) { break; }
                    if (query.equalsIgnoreCase("SPELL")) {
                        spellNum = "";
                        while (spellNum.isEmpty()) {
                            System.out.print("How many spelling errors should we find?  ");
                            spellNum = userIn.readLine();
                        }

                        if (spellNum.equals("exit")) { break; }
                        if (spellNum.equals("restart")) { break; }
                        
                        queryParser.findSpellingErrors(Integer.parseInt(spellNum));
                    } else {
                        queryParser.executeQuery(query, false);
                    }
                }

                if (query.equals("exit") || spellNum.equals("exit")) { break; }
                if (query.equals("restart") || spellNum.equals("restart")) { continue; }

            }            

        }
        catch (IOException ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
