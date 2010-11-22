package com.eth.ir.spamfiltering.naiveBayes;

import com.eth.ir.boolret.FileIndexer;
import com.eth.ir.spamfiltering.SpamBundle;
import com.eth.ir.spamfiltering.naiveBayes.NaiveBayes.sClass;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class NaiveBayesHelper {

//    Map<String, Integer> vocabulary;
    // a map of string < the terms > and a Map <sClass, Integer> : which contains the
    // number of occurences of that term for a particular class
    Map<String, Map<sClass, Integer>> vocabulary;
    int spamDocsCount;
    int legitDocsCount;
    int spamTokensCount;
    int legitTokensCount;

    NaiveBayesHelper() {
//        vocabulary = new HashMap<String, Integer>();
        vocabulary = new HashMap<String, Map<sClass, Integer>>();

    }

    public void initializeNBHelper(String documentCorpusDir, String skipDir) {

        vocabulary.clear();

        File directory = new File(documentCorpusDir);
        File[] files = directory.listFiles();
//        System.out.println("files=" + files.length);
        for (File file : files) {
            // hack for getting rid of svn files
            //if (!file.isHidden() && file.isDirectory()){ // && !file.getName().contains(Bundle.INDEX_FILE)) {
            if (!file.isHidden() && file.isDirectory()) {
                if (file.getName().equals(skipDir)) {
                    continue;
                }
//                System.out.println("working on=" + file.getName());
                File[] trainingSet = file.listFiles();
//                System.out.println("trainingset="+ trainingSet.length);
                for (File trainingFile : trainingSet) {
                    if (!trainingFile.isHidden()) {
                        if (trainingFile.getName().contains(SpamBundle.SPAM_ID)) {
                            spamDocsCount++;
                            spamTokensCount += tokenizeFile(trainingFile, sClass.SPAM);
                        } else {
                            legitDocsCount++;
                            legitTokensCount += tokenizeFile(trainingFile, sClass.NOTSPAM);
                        }

                    }
                }
            }
//            break;
        }

    }

    public Map<String, Map<sClass, Integer>> extractVocabulary() {
        return vocabulary;
    }

    public int countDocs() {
        return spamDocsCount + legitDocsCount;
    }

    public int countDocsInClass(sClass sclass) {
        return sclass.equals(sClass.SPAM) ? spamDocsCount : legitDocsCount;
    }

    public int concatenateTextOfALLDocsInClass(sClass sclass) {
        return sclass.equals(sClass.SPAM) ? spamTokensCount : legitTokensCount;
    }

    public List<String> extractTokensFromDocs(String documentCorpusDir, String workDir) {
        File directory = new File(documentCorpusDir + File.separator + workDir);
        File[] files = directory.listFiles();
        List<String> allTokens = new ArrayList<String>();
        for (File tFile : files) {
            if (!tFile.isHidden()) {
                System.out.println("working on testfile=" + tFile);
                List<String> tokensInDoc = extractTokensFromDoc(tFile);
                allTokens.addAll(tokensInDoc);
            }
        }
        return allTokens;
    }

    /**
     * This method utilizes the StreamTokenizer class
     * to isolate words.
     * Characters in the ASCII set 33-47 and 58-64
     * are deemed ordinary.
     *
     * @param file - the file to be tokenized
     */
    public List<String> extractTokensFromDoc(File file) {

        List<String> tokensInFile = new ArrayList<String>();

        String docId = file.getName();
//        System.out.println("docId" + docId);
        int tokenCount = 0;
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);
            // get rid of the '\' ';' and so on ..
            st.ordinaryChars(33, 47);
            st.ordinaryChars(58, 64);



            int next = st.nextToken();
            while (next != StreamTokenizer.TT_EOF) {
                String nextString = FileIndexer.normalizeWord(st, next);

                if (!nextString.isEmpty()) {
                    if (vocabulary.containsKey(nextString)) {
                        tokensInFile.add(nextString);
                    }
                }
                //fetch the next token.
                next = st.nextToken();
            }

        } catch (IOException ex) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tokensInFile;
    }

    /**
     * This method utilizes the StreamTokenizer class
     * to isolate words.
     * Characters in the ASCII set 33-47 and 58-64
     * are deemed ordinary.
     *
     * @param file - the file to be tokenized
     */
    public int tokenizeFile(File file, sClass sclass) {

        String docId = file.getName();
//        System.out.println("docId" + docId);
        int tokenCount = 0;
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);
            // get rid of the '\' ';' and so on ..
            st.ordinaryChars(33, 47);
            st.ordinaryChars(58, 64);



            int next = st.nextToken();
            while (next != StreamTokenizer.TT_EOF) {
                String nextString = FileIndexer.normalizeWord(st, next);

                if (!nextString.isEmpty()) {
                    if (vocabulary.containsKey(nextString)) {

                        Map<sClass, Integer> mapClass = vocabulary.get(nextString);
                        int count = 0;
                        if (mapClass.containsKey(sclass)) {
                            count = (Integer) mapClass.get(sclass);
                        }

                        mapClass.put(sclass, count + 1);
                        vocabulary.put(nextString, mapClass);
                    } else {
                        Map<sClass, Integer> mapClass = new EnumMap<sClass, Integer>(sClass.class);
                        mapClass.put(sclass, 1);
                        vocabulary.put(nextString, mapClass);
                    }
                    tokenCount++;
                }

                //fetch the next token.
                next = st.nextToken();
            }

        } catch (IOException ex) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // close the stream handles
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tokenCount;
    }

    public static void main(String args[]) {
        new NaiveBayesHelper().initializeNBHelper(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile(), "part1");
    }
}
