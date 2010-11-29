/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.clustering.datastructure;

import com.eth.ir.clustering.KMeansClustering.kClass;
import java.util.Map;

/**
 * This data structure represents the
 * posting list as required by the kmeans algorithm
 * that is as
 *
 * @author ANSHUMAN
 */
public class KMeansDS {

    public kClass getKclass() {
        return kclass;
    }

    public void setKclass(kClass kclass) {
        this.kclass = kclass;
    }

    public Map<String, Double> getTermTfIdfMap() {
        return termTfIdfMap;
    }

    public void setTermTfIdfMap(Map<String, Double> termTfIdfMap) {
        this.termTfIdfMap = termTfIdfMap;
    }

    int clusterId   ;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
    kClass kclass;
    Map<String,Double> termTfIdfMap ;

    @Override
    public String toString()
    {
        return "[cluster=" + getClusterId() + "Map="+ getTermTfIdfMap()+"]";
    }

}
