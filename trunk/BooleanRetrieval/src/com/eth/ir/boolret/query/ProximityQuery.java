package com.eth.ir.boolret.query;

/**
 *
 * @author Tim Church
 */
class ProximityQuery {
    private String firstTerm;
    private String secondTerm;
    private Integer distance;

    public ProximityQuery(String firstTerm, String secondTerm, Integer distance) {
        this.distance = distance;
        this.firstTerm = firstTerm;
        this.secondTerm = secondTerm;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getFirstTerm() {
        return firstTerm;
    }

    public String getSecondTerm() {
        return secondTerm;
    }
}
