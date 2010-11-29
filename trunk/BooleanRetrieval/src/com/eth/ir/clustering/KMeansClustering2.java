/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.clustering;

import com.eth.ir.boolret.FileIndexer;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.clustering.datastructure.KMeansDS;
import com.eth.ir.spamfiltering.SpamBundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Another approach from the algorithm at 
 * http://nlp.stanford.edu/IR-book/html/htmledition/k-means-1.html
 *
 * @author ANSHUMAN
 */
public class KMeansClustering2 {

    public KMeansHelper kmHelper;

    public KMeansClustering2() {
        kmHelper =  new KMeansHelper();
    }


    public Map<String,Integer> selectRandomSeeds(int k)
    {

        Map<String,Integer> centroids = new HashMap<String, Integer>();
        Random randomGenerator = new Random();
        for (int j = 0; j < k; j++) {
            int rand = randomGenerator.nextInt(kmHelper.getDocClassification().size());
            String doc = (String) kmHelper.getDocClassification().keySet().toArray()[rand];
            System.out.println("random=" + doc);
            centroids.put(doc, j);

        }
        return centroids;
    }


    public void kmeans(int k)
    {
        String file = SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR + "/" + SpamBundle.CLUSTER_DIR).getFile();
        Map<String, PostingList> index = FileIndexer.project3_phase2_vectorSpaceModel();
        kmHelper.preprocess(file);
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

        Map<String,Integer> centroidMap = selectRandomSeeds(k);
        Map<Integer,KMeansDS> centroidVectorMap = new HashMap<Integer,KMeansDS>();
        Map<Integer,List<KMeansDS>> clusterMap = new HashMap<Integer,List<KMeansDS>>();
        for(Map.Entry<String,Integer> centroidIter : centroidMap.entrySet())
        {
            String document = centroidIter.getKey();
            Integer clusterId = centroidIter.getValue();
            centroidVectorMap.put(clusterId, kmHelper.getDocumentVectorsMap().get(document));
            clusterMap.put(clusterId, null);
        }

        //stopping criteria
        for(int i=0 ; i<20 ; i++)
        {
            for(int j=0 ; j<k ; j++)
            {
                clusterMap.put(j, new ArrayList<KMeansDS>());


                Map<Double, Integer> euclidianScores = new TreeMap<Double, Integer>();
                for(Map.Entry<String,KMeansDS> iter : kmHelper.getDocumentVectorsMap().entrySet() )

//                for(int n = 0 ; n< kmHelper.getDocClassification().size(); n++ )
                {
                    KMeansDS centroidVector = centroidVectorMap.get(j);
                    KMeansDS documentVector = iter.getValue();

                    Double euclidianDistance = calculateEuclideanDistance(centroidVector,documentVector);
                    euclidianScores.put(euclidianDistance, j);
                }

                Integer minCluster = euclidianScores.get((Double)euclidianScores.keySet().toArray()[0]);
                clusterMap.put(minCluster, null)

            }

        }

            String centroidDocId = centroidIter.getKey();
            KMeansDS kmeansDS = kmHelper.getDocumentVectorsMap().get(centroidDocId);

        
    }


    public Double calculateEuclideanDistance(KMeansDS centroidVector, KMeansDS documentVector)
    {
        Double dimensionDistance = 0.0;
        for(Map.Entry<String,Double> iter : centroidVector.getTermTfIdfMap().entrySet())
        {
            String dimension = iter.getKey();


            if(documentVector.getTermTfIdfMap().containsKey(dimension))
            {
                 Double documentMagnitude = documentVector.getTermTfIdfMap().get(dimension);
                 Double centroidDMagnitude = iter.getValue();
                 dimensionDistance += (centroidDMagnitude - documentMagnitude) * (centroidDMagnitude - documentMagnitude);
            }

        }
        return Math.sqrt(dimensionDistance);
        //euclidianScores.put(euclidieanDistance, centroid);
    }


}
