package com.eth.ir.boolret;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class FileTokenizer {

    TreeMap<String, Integer> index;
    HashMap<Integer, String> docIndex;

    FileTokenizer() {
        index = new TreeMap<String, Integer>(); //maps term to docId
        docIndex = new HashMap<Integer, String>(); //maps docId to document title
    }

    public void tokenizeFile(File file) {
        FileReader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(in);

            int next = st.nextToken();

            while (next != StreamTokenizer.TT_EOF) {
                switch (next) {

                    case StreamTokenizer.TT_NUMBER:
//                        System.out.println("nextn = " + st.nval);
                        Double val = st.nval;
                        if (!index.containsKey(val.toString())) {
                            index.put(val.toString(), new Integer(1));
                        } else {
                            Integer count = (Integer) index.get(val.toString());
                            index.put(val.toString(), count + 1);
                        }
                        break;

                    case StreamTokenizer.TT_WORD:
                        // StreamTokenizer sometimes reads in words with ','s together.
                        // this is to split it up exactly as we want it.
                        String stoken = st.sval;
                        if (stoken.contains(",")) {
                            String x[] = stoken.split(",");
                            for (String y : x) {
                                if (!y.trim().equals("")) {
//                                    System.out.println("y=" + y);
                                    if (!index.containsKey(y)) {
                                        index.put(y, new Integer(1));
                                    } else {
                                        Integer count = (Integer) index.get(y);
                                        index.put(y, count + 1);
                                    }
                                }
                            }
                        } else {
                            // System.out.println("nextw = " + st.sval);
                            if (!index.containsKey(st.sval)) {
                                index.put(st.sval, new Integer(1));
                            } else {
                                Integer count = (Integer) index.get(st.sval);
                                index.put(st.sval, count + 1);
                            }
                        }
                        break;
                    default:
                        break;
                }

                //fetch the next token.
                next = st.nextToken();
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileTokenizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void fetchFilesInDirectory(String dir) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
        Integer n = 0;
        for (File file : files) {
            System.out.println("indexing " + file.getName());
            docIndex.put(++n, file.getName());
            tokenizeFile(file);
        }
    }

    public void printIndex() {
        System.out.println(" size of index " + index.size());
        for (Map.Entry<String, Integer> entry : index.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("key = " + key + ", value = " + value);
        }
    }

    public static void main(String args[]) {
        String dir = "C://Users//ghff//Documents//ETH//Fall 2010//Information Retrieval//Dataset//Docs";
        FileTokenizer tkz = new FileTokenizer();
        tkz.fetchFilesInDirectory(dir);
        tkz.printIndex();
    }
}
