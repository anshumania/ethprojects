package com.eai.beans;

import java.io.Serializable;

/**
 *
 * @author ANSHUMAN
 */
public class CommentBean implements Serializable {

    private long userId;
    private int designId;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDesignId() {
        return designId;
    }

    public void setDesignId(int designId) {
        this.designId = designId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User[" + getUserId() + "] commented [" + getComment() + "] on Design[" + getDesignId() + "]";

    }
}
