package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author ANSHUMAN
 */
public class PostingListNode implements Serializable {

    private String docId;
    private TreeSet<Integer> positions;

    public Double getTf_idf_weight() {
        return tf_idf_weight;
    }

    public void setTf_idf_weight(Double tf_idf_weight) {
        this.tf_idf_weight = tf_idf_weight;
    }

    // tfidf weight
    // = (1+log(tf<t,d>))*log(N/df<t>)
    private Double tf_idf_weight;

    public PostingListNode(String docId, Integer position) {
        this.docId = docId;
        this.positions = new TreeSet<Integer>();
        this.positions.add(position);
    }

    public PostingListNode(String docId) {
        this.docId = docId;
        this.positions = new TreeSet<Integer>();
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String DocId) {
        this.docId = DocId;
    }

    public Set<Integer> getPositions() {
        return positions;
    }

    public void setPositions(Set<Integer> positions) {
        this.positions = new TreeSet<Integer>(positions);
    }

    public void addPosition(Integer position) {
        this.positions.add(position);
    }

    public Integer getTermFrequencyInDoc() {
        return this.positions.size();
    }

    public Boolean equals(PostingListNode node) {
        return (this.docId).equals(node.getDocId());
    }

    @Override
    public String toString() {
        return "[docId=" + getDocId() + ", positions=" + getPositions() + "]";
    }
}
