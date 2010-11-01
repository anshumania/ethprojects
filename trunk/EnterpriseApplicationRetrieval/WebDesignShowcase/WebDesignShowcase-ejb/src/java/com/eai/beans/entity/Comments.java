/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ANSHUMAN
 */
@Entity
@Table(name = "comments")
@NamedQueries({
    @NamedQuery(name = "Comments.findAll", query = "SELECT c FROM Comments c"),
    @NamedQuery(name = "Comments.findById", query = "SELECT c FROM Comments c WHERE c.id = :id"),
    @NamedQuery(name = "Comments.findByUserId", query = "SELECT c FROM Comments c WHERE c.userId = :userId"),
    @NamedQuery(name = "Comments.findByDesignId", query = "SELECT c FROM Comments c WHERE c.designId = :designId"),
	@NamedQuery(name = "Comments.findByUserIdAndDesignId", query = "SELECT c FROM Comments c WHERE c.userId = :userId AND c.designId = :designId"),
    @NamedQuery(name = "Comments.getLatestComment", query = "SELECT c FROM Comments c ORDER BY c.id DESC"),
    @NamedQuery(name = "Comments.findByComment", query = "SELECT c FROM Comments c WHERE c.comment = :comment")})
public class Comments implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "user_id")
    private Long userId;
    @Basic(optional = false)
    @Column(name = "design_id")
    private int designId;
    @Basic(optional = false)
    @Column(name = "comment")
    private String comment;

    public Comments() {
    }

    public Comments(Long id) {
        this.id = id;
    }

    public Comments(Long id, Long userId, int designId, String comment) {
        this.id = id;
        this.userId = userId;
        this.designId = designId;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getDesignId() {
        return designId;
    }

    public void setDesignId(int designId) {
        this.designId = designId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comments)) {
            return false;
        }
        Comments other = (Comments) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.eai.beans.entity.Comments[id=" + id + "]";
    }

}
