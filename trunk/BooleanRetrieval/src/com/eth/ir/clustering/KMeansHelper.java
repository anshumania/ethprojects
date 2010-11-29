/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.clustering;

import com.eth.ir.boolret.FileIndexer;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import com.eth.ir.clustering.KMeansClustering.kClass;
import com.eth.ir.clustering.datastructure.KMeansDS;
import com.eth.ir.spamfiltering.SpamBundle;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ANSHUMAN
 */
public class KMeansHelper {

    // to check which class the document belongs to
    // utilized in apply-knn()
    public Map<String,kClass>  docClassification;

//    public Map<String,Map<String,Double>> documentVectors;
    public Map<String,KMeansDS> documentVectorsMap;

    public Map<String, KMeansDS> getDocumentVectorsMap() {
        return documentVectorsMap;
    }

//    public Map<String, Map<String, Double>> getDocumentVectors() {
//        return documentVectors;
//    }

    public KMeansHelper()
    {
        docClassification = new HashMap<String,kClass>();
//        documentVectors = new HashMap<String, Map<String, Double>>();
        documentVectorsMap = new HashMap<String,KMeansDS>();
    }




    public void preprocess(String documentCorpusDir)
    {
       File directory = new File(documentCorpusDir);
       File[] files = directory.listFiles();
       for (File file : files) {
           if(!file.isHidden())
           {
               file.getAbsolutePath();
               String docId = file.getName();
            //   System.out.println("docid="+docId);
              
               kClass kclass =  (docId.contains(SpamBundle.SPAM_ID)) ? kClass.SPAM : kClass.NOTSPAM;
               docClassification.put(docId, kclass);
//               documentVectors.put(docId, null);
               documentVectorsMap.put(docId, null);
           }

        }

       // System.out.println(docClassification);
        System.out.println(docClassification.size());
    }

    public void transposeDStoClassificationDS(Map<String,PostingList> index)
    {
        for(Map.Entry<String,PostingList> iterator : index.entrySet())
        {
            String term = iterator.getKey();
            PostingList pl = iterator.getValue();
            int check = 0;
            for(PostingListNode pln : pl.getPostingList())
            {
                String docId = pln.getDocId();
                Double tfdfw = pln.getTf_idf_weight();
//                System.out.println("docId = " + docId);
//                if(documentVectors.containsKey(docId))
                if(documentVectorsMap.containsKey(docId))
                {
                  
                    // fetch the term and tf-idf mapping for that document
//                    Map<String,Double> termtfIdfMap = documentVectors.get(docId);
                    KMeansDS kmeans = documentVectorsMap.get(docId);
                    // first time
//                    if(null == termtfIdfMap)
                    if(null == kmeans)
                    {
                        kmeans = new KMeansDS();
                        kmeans.setKclass(null);
                        kmeans.setTermTfIdfMap(new HashMap<String, Double>());
//                        termtfIdfMap = new HashMap<String, Double>();
                    }
                    // check if this document contains this term
                    // this too shud happen only once for every iteration of the index
//                    if(!(termtfIdfMap.containsKey(term)))
                    if(!(kmeans.getTermTfIdfMap().containsKey(term)))
                    {
                        
//                        termtfIdfMap.put(term, tfdfw);
                        kmeans.getTermTfIdfMap().put(term, tfdfw);
//                        documentVectors.put(docId, termtfIdfMap);
                        documentVectorsMap.put(docId, kmeans);
                        check++;
                    }

                }
                else
                    System.out.println("wtf!");
                assert check == 1;
            }
        }

        System.out.println("document=" + documentVectorsMap);
    }


    public Map<String, kClass> getDocClassification() {
        return docClassification;
    }


    public static void main(String args[])
    {
        String file = SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR + "/" + SpamBundle.CLUSTER_DIR).getFile();
        System.out.println("file="+file);
//        System.out.println("3-msg.tx".contains(SpamBundle.SPAM_ID));

        Map<String, PostingList> index = FileIndexer.project3_phase2_vectorSpaceModel();
        System.out.println("index="+index.size());


        KMeansHelper km = new KMeansHelper();
        km.preprocess(file);
        km.transposeDStoClassificationDS(index);
    }

}

