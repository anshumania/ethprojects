package com.eth.ir.clustering;

import com.eth.ir.boolret.FileIndexer;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.clustering.datastructure.KMeansDS;
import com.eth.ir.spamfiltering.SpamBundle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Another approach from the algorithm at 
 * http://nlp.stanford.edu/IR-book/html/htmledition/k-means-1.html
 *
 * @author ANSHUMAN
 */
public class KMeansClustering2 {

    public KMeansHelper kmHelper;

    public KMeansClustering2() {
        kmHelper = new KMeansHelper();
    }

    public Map<String, Integer> selectRandomSeeds(int k) {
        Map<String, Integer> centroids = new HashMap<String, Integer>();
        Random randomGenerator = new Random();
        for (int j = 0; j < k; j++) {
            int rand = randomGenerator.nextInt(kmHelper.getDocClassification().size());
            String doc = (String) kmHelper.getDocClassification().keySet().toArray()[rand];
            System.out.println("random=" + doc);
            centroids.put(doc, j);
        }
        System.out.println("randomCentroids size=" + centroids.size());
        return centroids;
    }

    public ClusterMeasure kmeans(int k) {
        String file = SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR + "/" + SpamBundle.CLUSTER_DIR).getFile();
        System.out.println("file=" + file);
        TreeMap<String, PostingList> index = (TreeMap) FileIndexer.project3_phase2_vectorSpaceModel();
        kmHelper.preprocess(file);
        kmHelper.normalize(index);
        kmHelper.transposeDStoClassificationDS(index);

        // http://nlp.stanford.edu/IR-book/html/htmledition/k-means-1.html
        //    K-Means({x1,...xn},K)
        //            (s1,s2...sk) <- selectRandomSeeds({x1,..xn},K)
        //            for(k <- 1 to K)
        //            do Mk <- Sk
        //            while stopping criterion has not been met
        //              do for k <- 1 to k
        //                 do wk <- {}
        //                 for n <- 1 to N
        //                 do j <- argMinj' | Mj'- xn|    - Stuck here ! i really think the iteration over N
        //                    wj <- wj U xn               - should be above the iteration the k above it
        //                 for k <- 1 to K
        //                 do Mk <- 1/|wk|Sigma x such that x belongs to wk
        //              return { M1,...Mk }

        Map<String, Integer> centroidMap = selectRandomSeeds(k);
        Map<Integer, KMeansDS> centroidVectorMap = new HashMap<Integer, KMeansDS>();  //vector of the centroid for each cluster
        Map<Integer, List<KMeansDS>> clusterMap = new HashMap<Integer, List<KMeansDS>>(); //list of vectors in each cluster

        for (Map.Entry<String, Integer> centroidIter : centroidMap.entrySet()) {
            String document = centroidIter.getKey();
            Integer clusterId = centroidIter.getValue();
            centroidVectorMap.put(clusterId, kmHelper.getDocumentVectorsMap().get(document));
            clusterMap.put(clusterId, new ArrayList<KMeansDS>());

        }

        //print initial centroids
        for (Map.Entry<Integer, KMeansDS> other : centroidVectorMap.entrySet()) {
            System.out.println("inital centroids=" + other.getKey() + " doc-" + other.getValue().getDocumentName());
        }

        //stopping criteria
        for (int i = 0; i < 20; i++) {
            System.out.println("run" + (i + 1));

            // clear the cluster documents in the cluster map.
            for (int clusterId = 0; clusterId < k; clusterId++) {
                clusterMap.get(clusterId).clear();
            }

            //for each document, find nearest centroid, assign to cluster
            for (Map.Entry<String, KMeansDS> docVectorMap : kmHelper.getDocumentVectorsMap().entrySet()) {
                //Map<Integer, Double> euclidianScores = new TreeMap<Integer, Double>();
                Map<Double, Integer> euclidianScores = new TreeMap<Double, Integer>();
                KMeansDS documentVector = docVectorMap.getValue();

                //calculate distance for this doc to each cluster centroid
                for (Map.Entry<Integer, KMeansDS> centroidIter : centroidVectorMap.entrySet()) {
                    Integer clusterId = centroidIter.getKey();
                    KMeansDS centroidVector = centroidIter.getValue();
                    Double euclidianDistance = calculateEuclideanDistance(centroidVector, documentVector);
                    //Double euclidianDistance = calculateCosineSimilarity(centroidVector, documentVector);
                    euclidianScores.put(euclidianDistance, clusterId);

                    //assign this doc to the nearest cluster
                    Integer minCluster = euclidianScores.get((Double) euclidianScores.keySet().toArray()[0]);
                    clusterMap.get(minCluster).add(documentVector);

                    /*for (int j = 0; j < k; j++) {
                        System.out.println("currentclustermap=[" + j + "]" + "size" + clusterMap.get(j).size());
                    }*/
                }
            }

            // now compute the new centroids
            System.out.println("computing new centroids current cluster =" + clusterMap.size());
            for (Map.Entry<Integer, List<KMeansDS>> iter : clusterMap.entrySet()) {
                Integer clusterId = iter.getKey();
                KMeansDS newCentroid = computeCentroidVector(clusterId, iter.getValue());
                centroidVectorMap.put(clusterId, newCentroid);
                System.out.println("currentCentroids=" + centroidMap.size());
            }

            //print all docs in cluster
            for (Map.Entry<Integer, List<KMeansDS>> iter : clusterMap.entrySet()) {
                Integer clusterId = iter.getKey();
                StringBuilder out = new StringBuilder();
                Integer numberOfSpam = 0;
                Integer numberOfHam = 0;
                for (KMeansDS docVectors : iter.getValue()) {
                    //if(docVectors.getKclass().equals(KMeansClustering.kClass.NOTSPAM)
                    //spam++;
                    if (docVectors.getDocumentName().contains(SpamBundle.SPAM_ID)) {
                        numberOfSpam++;
                    } else {
                        numberOfHam++;
                    }
                    out.append(docVectors.getDocumentName()).append(",");
                }
                System.out.println("[Cluster=" + clusterId + "{spam=" + numberOfSpam + ";ham=" + numberOfHam + "}");
                System.out.println("[Cluster=" + clusterId + "{docs=" + out + "}");
            }
        }
        
        //calculate Rand Index and Purity
        Double randIndex = calculateRandIndex(clusterMap);
        Double purity = calculatePurity(clusterMap);
        return new ClusterMeasure(randIndex, purity);
    }

    public Double calculateEuclideanDistance(KMeansDS centroidVector, KMeansDS documentVector) {
        // dxi - cxi
        // dyi
        //        czi
        Double dimensionDistance = 0.0;

        for (Map.Entry<String, Double> diter : documentVector.getTermTfIdfMap().entrySet()) {
            String dDimension = diter.getKey();
            Double dMagnitude = diter.getValue();

            if (centroidVector.getTermTfIdfMap().containsKey(dDimension)) {
                Double cMagnitude = centroidVector.getTermTfIdfMap().get(dDimension);
                dimensionDistance += (dMagnitude - cMagnitude) * (dMagnitude - cMagnitude);

            } else {
                dimensionDistance += dMagnitude * dMagnitude;
            }

        }
        for (Map.Entry<String, Double> citer : centroidVector.getTermTfIdfMap().entrySet()) {
            String cDimension = citer.getKey();
            Double cMagnitude = citer.getValue();

            if (centroidVector.getTermTfIdfMap().containsKey(cDimension)) {// already computed
            } else {
                dimensionDistance += cMagnitude * cMagnitude;
            }
        }


//        for (Map.Entry<String, Double> iter : centroidVector.getTermTfIdfMap().entrySet()) {
//            String dimension = iter.getKey();
//
//
//                Double centroidDMagnitude = iter.getValue();
//            if (documentVector.getTermTfIdfMap().containsKey(dimension)) {
//                Double documentMagnitude = documentVector.getTermTfIdfMap().get(dimension);
////                dimensionDistance += (centroidDMagnitude - documentMagnitude) * (centroidDMagnitude - documentMagnitude);
//                dimensionDistance += (documentMagnitude - centroidDMagnitude) * (documentMagnitude - centroidDMagnitude);
//            }
//            else
//            {
//                dimensionDistance += documentMagnitude*documentMagnitude;
//                dimensionDistance += centroidDMagnitude*centroidDMagnitude;
//
//            }
//
//
//        }
        return Math.sqrt(dimensionDistance);
        //euclidianScores.put(euclidieanDistance, centroid);
    }

    public Double calculateCosineSimilarity(KMeansDS centroidVector, KMeansDS documentVector) {
        Double cosineSimilarity = 0.0;
        Integer centroidVectorLength = centroidVector.getTermTfIdfMap().size();
        for (Map.Entry<String, Double> iter : centroidVector.getTermTfIdfMap().entrySet()) {
            String dimension = iter.getKey();

            if (documentVector.getTermTfIdfMap().containsKey(dimension)) {
                Double documentMagnitude = documentVector.getTermTfIdfMap().get(dimension);
                Double centroidDMagnitude = iter.getValue();
//                dimensionDistance += (centroidDMagnitude - documentMagnitude) * (centroidDMagnitude - documentMagnitude);
                //dimensionDistance += (documentMagnitude - centroidDMagnitude) * (documentMagnitude - centroidDMagnitude);
                cosineSimilarity += (documentMagnitude * centroidDMagnitude);
            }
        }
//        return Math.sqrt(cosineSimilarity);
        return Math.sqrt(cosineSimilarity / centroidVectorLength);
        //euclidianScores.put(euclidieanDistance, centroid);
    }

    private Double calculateRandIndex(Map<Integer, List<KMeansDS>> clusterMap) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Double calculatePurity(Map<Integer, List<KMeansDS>> clusterMap) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private KMeansDS computeCentroidVector(Integer clusterId, List<KMeansDS> documentVectorsInThisCluster) {
        KMeansDS newCentroid = new KMeansDS();
        newCentroid.setDocumentName("centroid");
        newCentroid.setClusterId(clusterId);

        Map<String, Double> centroidVector = new HashMap<String, Double>();
        for (KMeansDS document : documentVectorsInThisCluster) {
            Map<String, Double> documentVector = document.getTermTfIdfMap();
            for (Map.Entry<String, Double> term : documentVector.entrySet()) {
                String dimension = term.getKey();
                Double magnitude = term.getValue();

                if (centroidVector.containsKey(dimension)) {
                    Double prevMagnitude = centroidVector.get(dimension);
                    centroidVector.put(dimension, prevMagnitude + magnitude);
                } else {
                    centroidVector.put(dimension, magnitude);
                }
            }
        }

        // normalize
        for (Map.Entry<String, Double> centroidDimensions : centroidVector.entrySet()) {
            Double magnitude = centroidDimensions.getValue();
            Integer centroidWeight = centroidVector.size();
            centroidDimensions.setValue((magnitude / centroidWeight));
        }

        // update vector representation
        newCentroid.setTermTfIdfMap(centroidVector);
        return newCentroid;
    }

    /**
     * Calculates the average Purity and Rand Index for the given K over num_iterations
     * @param k Number of clusters
     * @param num_iterations Number of iterations from which to take the average
     * @return
     */
    public ClusterMeasure averageKMeans(int k, int num_iterations) {
        ArrayList<Double> randIndexes = new ArrayList<Double>();
        ArrayList<Double> purityScores = new ArrayList<Double>();

        //run K-Means num_iterations times
        for (int i = 0; i < num_iterations; i++) {
            ClusterMeasure result = kmeans(k);
            randIndexes.add(result.getRandIndex());
            purityScores.add(result.getPurity());
        }

        //calculate average Rand Index
        Double randIndexSum = new Double(0);
        for (Double randIndex : randIndexes) {
            randIndexSum += randIndex;
        }
        Double averageRandIndex = randIndexSum / (double) randIndexes.size();

        //calculate average Purity
        Double puritySum = new Double(0);
        for (Double purity : purityScores) {
            randIndexSum += purity;
        }
        Double averagePurity = puritySum / (double) purityScores.size();

        return new ClusterMeasure(averagePurity, averageRandIndex);
    }

    public static void main(String args[]) {
        KMeansClustering2 km = new KMeansClustering2();
        Map<Integer, Double> avgRandIndex = new TreeMap<Integer, Double>();
        Map<Integer, Double> avgPurity = new TreeMap<Integer, Double>();

        //find average purity/rand index over 100 runs for K 2 to 10
        for (int k = 2; k <= 10; k++) {
            ClusterMeasure averages = km.averageKMeans(k, 100);
            avgRandIndex.put(k, averages.getRandIndex());
            avgPurity.put(k, averages.getPurity());
        }

        //output results
        System.out.println("K,Avg Rand Index,Avg Purity");
        for (Map.Entry<Integer, Double> entry : avgRandIndex.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue() + "," + avgPurity.get(entry.getKey()));
        }
    }

    // from http://stackoverflow.com/questions/2864840/treemap-sort-by-value
    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {

                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        return e1.getValue().compareTo(e2.getValue());
                    }
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
