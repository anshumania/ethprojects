package com.eth.ir.clustering;

/**
 *
 * @author Tim Church
 */
public class ClusterMeasure {
    private Double randIndex;
    private Double purity;

    public ClusterMeasure(Double randIndex, Double purity) {
        this.randIndex = randIndex;
        this.purity = purity;
    }

    public Double getPurity() {
        return purity;
    }

    public Double getRandIndex() {
        return randIndex;
    }

}
