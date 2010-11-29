/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.clustering;

import com.eth.ir.boolret.FileIndexer;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.clustering.datastructure.KMeansDS;
import com.eth.ir.spamfiltering.SpamBundle;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class KMeansClustering {

    KMeansHelper kmHelper;
//    String[] centroids;
    Map<String, Integer> centroids;

    public enum kClass {

        SPAM, NOTSPAM
    };

    public KMeansClustering() {
        kmHelper = new KMeansHelper();

    }

    public void selectK(int k) {
//        centroids = new String[k];
        centroids = new HashMap<String, Integer>();
        Random randomGenerator = new Random();
        for (int j = 0; j < k; j++) {
            int rand = randomGenerator.nextInt(kmHelper.getDocClassification().size());
            String doc = (String) kmHelper.getDocClassification().keySet().toArray()[rand];
            System.out.println("random=" + doc);
            centroids.put(doc, j);
//            centroids[j] = doc;

        }
    }

    public void trainKNN(int k) {
//         Train - KNN(C,D)
//         1. D' <- Preprocess(C,D)
//         2. k  <- select-K(C,D)
//         return D',k


        //pre-processing already done
        selectK(k);


    }

    public void applyKNN() {
//         Apply - KNN(C,D',k,d)
//         1. Sk <- computerNearestNeighbours(D',k,d)
//         2. for each cj in C
//         3. do score(c,d) <- Sigma(d' in Sk(d)) Ic(d')cos(v(d'),v(d))
//         4. return arg max
//         where Sk(d) is the set of d's nearest k neighbors and
//         Ic(d') = 1 iff d' is in class c and 0 otherwise.


        // for each document in the documentVectors
        // for each k vector (centroid)
        // compute the euclidean distance of the document Vectors with the k centroid

        // a Map of clusters and docs in that cluster
        Map<Integer, List<String>> clusterDocs = new HashMap<Integer, List<String>>();
        // the documents
        Map<String, KMeansDS> documentVectors = kmHelper.getDocumentVectorsMap();

        // for every document vector
        for (Map.Entry<String, KMeansDS> iterator : documentVectors.entrySet()) {
            // for auto sorting by scores
            Map<Double, String> euclidianScores = new TreeMap<Double, String>();
            // for every centroid < in the first case we have chosen randomly from the documentVectors>
            for (Map.Entry<String, Integer> centIter : centroids.entrySet()) {
                // fetch the ith centroid
                String centroid = centIter.getKey();
                // fetch the centroid vector from the documentVectors
                Map<String, Double> centroidVector = documentVectors.get(centroid).getTermTfIdfMap();
                // get the document for that document vector
                String document = iterator.getKey();
                // its the centroid -- not required i guess
//                if (document.equals(centroid)) {
//                    continue;
//                }
                // get the document vector
                Map<String, Double> documentVector = iterator.getValue().getTermTfIdfMap();
                Double dimensionDistance = 0.0;
                // for every vector dimension in the centroid vector
                // calculate the euclidean distance of the document vectors dimension
                for (Map.Entry<String, Double> iter : centroidVector.entrySet()) {
                    String term = iter.getKey();
                    // if the document vector has a magnitude in that dimension
                    if (documentVector.containsKey(term)) {
                        Double documentDimension = documentVector.get(term);
                        Double centroidDimension = iter.getValue();
                        dimensionDistance += (documentDimension - centroidDimension) * (documentDimension - centroidDimension);
                    }
                }
                // euclidian distance
                Double euclidieanDistance = Math.sqrt(dimensionDistance);
                euclidianScores.put(euclidieanDistance, centroid);

            }
            System.out.println("euclidiean-" + euclidianScores );
            // the first element of the treeMap is the cluster
            
            String clusterToWhichThisDocumentBelongs = euclidianScores.get((Double) euclidianScores.keySet().toArray()[0]);
            System.out.println("belongsto =" + clusterToWhichThisDocumentBelongs);
            // now assign this document to the top cluster
            KMeansDS kmeansDS = iterator.getValue();
            kmeansDS.setClusterId(centroids.get(clusterToWhichThisDocumentBelongs));
            iterator.setValue(kmeansDS);
            // add this document to this cluster
            List<String> listOfDocuments = clusterDocs.get(centroids.get(clusterToWhichThisDocumentBelongs));
            if (null == listOfDocuments) {
                listOfDocuments = new ArrayList<String>();
            }
            listOfDocuments.add(iterator.getKey());
            clusterDocs.put(centroids.get(clusterToWhichThisDocumentBelongs), listOfDocuments);
        }

//        System.out.println(documentVectors);
        System.out.println("clusterDocs=" + clusterDocs);

        // now search for means in each cluster to get a new centroid
        // and then iterate 19 times
        
        for (int i = 0; i < 19; i++)
        {
            // a map of cluster id and its vector representation
            Map<Integer, KMeansDS> centroidVectors = new HashMap<Integer, KMeansDS>();
            // for every cluster
            for (Map.Entry<Integer, List<String>> iterator : clusterDocs.entrySet()) {
                Integer clusterId = iterator.getKey();
                // get the documents in that cluster
                List<String> documents = iterator.getValue();
                // initialize the vector representation of the new centroid
                // i dont think the re-using the same cluster id causes any difference
                KMeansDS kMeansDS = new KMeansDS();
                kMeansDS.setClusterId(clusterId);
                kMeansDS.setTermTfIdfMap(new HashMap<String, Double>());
                // for every term we need to know its frequency in order to calculate the mean
                Map<String, Integer> termFrequency = new HashMap<String, Integer>();
                // for every document in that cluster
                for (String document : documents) {
                    // fetch the vector representation for that document
                    Map<String, Double> termTfIdfForDocument = documentVectors.get(document).getTermTfIdfMap();
                    // for every dimension <term> add up all the magnitudes <tf.idf>
                    // and keep count of the frequency of each term
                    for (Map.Entry<String, Double> iter : termTfIdfForDocument.entrySet()) {
                        // the term
                        String term = iter.getKey();
                        // its tf.idf weight
                        Double tfidfw = iter.getValue();
                        // if the vector representation has that dimension <term>
                        // then add up the corresponding magnitude <tf.idf>
                        if (kMeansDS.getTermTfIdfMap().containsKey(term)) {
                            Double preValue = kMeansDS.getTermTfIdfMap().get(term);
                            kMeansDS.getTermTfIdfMap().put(term, preValue + tfidfw);
                            //count of the term frequency
//                            int preFreq = termFrequency.get(term);
//                            termFrequency.put(term, preFreq + 1);
                        } else {
                            kMeansDS.getTermTfIdfMap().put(term, tfidfw );
//                            termFrequency.put(term, 1);
                        }

                    }
                }
                // for all terms (in termFrequency) adjust the value
                // of the vector representation of centroid with the mean of
                // the magnitude <tf.idf> in that dimension<term>
                // ** dropping as now using document.size() for mean
                for (Map.Entry<String, Integer> iter1 : termFrequency.entrySet()) {
                    String term = iter1.getKey();
                    Double tfIdfw = kMeansDS.getTermTfIdfMap().get(term);
//                    Integer totalWeightForThisTerm = iter1.getValue();
//                    Double meanForThisTerm = tfIdfw / totalWeightForThisTerm;
                     Double meanForThisTerm = tfIdfw / documents.size();
                    kMeansDS.getTermTfIdfMap().put(term, meanForThisTerm);
                }

                // new centroid for this cluster
                centroidVectors.put(clusterId, kMeansDS);
            }

            // now repeat find the euclidian distance with respect to the new centroids
            // reset the cluster docs < we will have new documents w.r.t the new centroids
            clusterDocs.clear();
            // for all documents
            for (Map.Entry<String, KMeansDS> iterator : documentVectors.entrySet()) {
                // for auto sorting by scores
                Map<Double, Integer> euclidianScores = new TreeMap<Double, Integer>();
                // for all centroids
                for (Map.Entry<Integer, KMeansDS> centIter : centroidVectors.entrySet()) {
                    // fetch the ith centroid
                    Integer centroid = centIter.getKey();
                    // get the vector representation of that centroid
                    Map<String, Double> centroidVector = centIter.getValue().getTermTfIdfMap();
                    // get the document
                    String document = iterator.getKey();
                    // the document vector
                    Map<String, Double> documentVector = iterator.getValue().getTermTfIdfMap();
                    Double dimensionDistance = 0.0;
                    // for every dimension of the centroid
                    for (Map.Entry<String, Double> iter : centroidVector.entrySet()) {
                        String term = iter.getKey();
                        if (documentVector.containsKey(term)) {
                            Double documentDimension = documentVector.get(term);
                            Double centroidDimension = iter.getValue();
                            dimensionDistance += (documentDimension - centroidDimension) * (documentDimension - centroidDimension);
                        }
                    }
                    Double euclidieanDistance = Math.sqrt(dimensionDistance);
                    euclidianScores.put(euclidieanDistance, centroid);

                }
//                System.out.println("euclidian run - " + euclidianScores);
                // the first element of the treeMap
                Integer clusterToWhichThisDocumentBelongs = euclidianScores.get((Double) euclidianScores.keySet().toArray()[0]);
//                System.out.println("belongs run - " + clusterToWhichThisDocumentBelongs );
                // get the vector representation of this document
                KMeansDS kmeansDS = iterator.getValue();
                // update its cluster
                kmeansDS.setClusterId(clusterToWhichThisDocumentBelongs);
                iterator.setValue(kmeansDS);
                List<String> listOfDocuments = clusterDocs.get(clusterToWhichThisDocumentBelongs);
                if (null == listOfDocuments) {
                    listOfDocuments = new ArrayList<String>();
                }
                listOfDocuments.add(iterator.getKey());
                clusterDocs.put(clusterToWhichThisDocumentBelongs, listOfDocuments);
            }
        }

        System.out.println("clusterDocs2=" + clusterDocs);



//        System.out.println("centroids" + centroidVectors);


    }

    public void kmeans() {
        // vary k from 2-10
        // read lingspam corpus directory and plot tf-idf vectors of doc
        // choose random (k? centroids)
        // assume convergence at 20 iterations
        // calcuate purity and random index
        // plot graph

        // Train - KNN(C,D)
        // 1. D' <- Preprocess(C,D)
        // 2. k  <- select-K(C,D)
        // return D',k
        // Apply - KNN(C,D',k,d)
        // 1. Sk <- computerNearestNeighbours(D',k,d)
        // 2. for each cj in C
        // 3. do score(c,d) <- Sigma(d' in Sk(d)) Ic(d')cos(v(d'),v(d))
        // 4. return arg max

        String file = SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR + "/" + SpamBundle.CLUSTER_DIR).getFile();
        Map<String, PostingList> index = FileIndexer.project3_phase2_vectorSpaceModel();
        kmHelper.preprocess(file);
        kmHelper.transposeDStoClassificationDS(index);


//        for(int k=2 ; k<=10 ; k++)
        int k =4;
        {
            trainKNN(k);
            applyKNN();

        }




    }

    public static void main(String args[]) {

        KMeansClustering km = new KMeansClustering();
        km.kmeans();


//         Map<String,PostingList> index = FileIndexer.project3_phase2_vectorSpaceModel();
//         System.out.println("index = " + index.size());
//         KMeansClustering km = new KMeansClustering();
//         km.kmeans(index);
    }
}
