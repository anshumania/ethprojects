package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Church
 */
public class PartOfSpeech {
    public final static String PART_OF_SPEECH_FILE = "resources/English_Words_Types.txt";
    private static Set<String> nouns;

    public static boolean isNoun(String test) {
        return nouns.contains(test.toLowerCase());
    }

    public static void readPartOfSpeechFile(String fileName) {
        nouns = new HashSet<String>();
        InputStream fileStream = PartOfSpeech.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
        String line;
        try {
            //read in first line (header)
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] words = line.split("[\t]", 4);
                if(words[2].trim().equalsIgnoreCase("NoC")) {
                    nouns.add(words[1].trim());
                }
            }
            Logger.getLogger(PartOfSpeech.class.getName()).log(Level.INFO, "Part-of-speech Resolver initiated");
        } catch (IOException ex) {
            Logger.getLogger(PartOfSpeech.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
