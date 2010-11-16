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
@Table(name = "designs")
@NamedQueries({
    @NamedQuery(name = "Designs.findAll", query = "SELECT d FROM Designs d ORDER BY d.id DESC"),
    @NamedQuery(name = "Designs.findById", query = "SELECT d FROM Designs d WHERE d.id = :id"),
    @NamedQuery(name = "Designs.findByUserId", query = "SELECT d FROM Designs d WHERE d.userId = :userId ORDER BY d.id DESC"),
    @NamedQuery(name = "Designs.findByTitle", query = "SELECT d FROM Designs d WHERE d.title = :title"),
    @NamedQuery(name = "Designs.findByUrl", query = "SELECT d FROM Designs d WHERE d.url = :url"),
    @NamedQuery(name = "Designs.findByImageUrl", query = "SELECT d FROM Designs d WHERE d.imageUrl = :imageUrl")})
public class Designs implements Serializable {
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
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @Column(name = "image_url")
    private String imageUrl;

    public Designs() {
    }

    public Designs(Long id) {
        this.id = id;
    }

    public Designs(Long id, long userId, String title, String url, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        if (!(object instanceof Designs)) {
            return false;
        }
        Designs other = (Designs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.eai.beans.entity.Designs[id=" + id + "]";
    }

}
