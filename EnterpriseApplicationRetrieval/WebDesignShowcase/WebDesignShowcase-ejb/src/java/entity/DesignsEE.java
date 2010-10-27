package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Tim Church
 */
//@Entity
//@Table(name = "designs")
//@NamedQueries({
//    @NamedQuery(name = "DesignsEE.findAll", query = "SELECT d FROM DesignsEE d"),
//    @NamedQuery(name = "DesignsEE.findById", query = "SELECT d FROM DesignsEE d WHERE d.id = :id"),
//    @NamedQuery(name = "DesignsEE.findByTitle", query = "SELECT d FROM DesignsEE d WHERE d.title = :title"),
//    @NamedQuery(name = "DesignsEE.findByUrl", query = "SELECT d FROM DesignsEE d WHERE d.url = :url"),
//    @NamedQuery(name = "DesignsEE.findByImageUrl", query = "SELECT d FROM DesignsEE d WHERE d.imageUrl = :imageUrl")})
public class DesignsEE implements Serializable {
//    private static final long serialVersionUID = 1L;
//    @Id
//    @Basic(optional = false)
//    @Column(name = "id")
//    private Integer id;
//    @Basic(optional = false)
//    @Column(name = "title")
//    private String title;
//    @Basic(optional = false)
//    @Column(name = "url")
//    private String url;
//    @Basic(optional = false)
//    @Column(name = "image_url")
//    private String imageUrl;
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    @ManyToOne(optional = false)
//    private Users users;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designs")
//    private Collection<CommentsE> commentsCollection;
//
//    public DesignsEE() {
//    }
//
//    public DesignsEE(Integer id) {
//        this.id = id;
//    }
//
//    public DesignsEE(Integer id, String title, String url, String imageUrl) {
//        this.id = id;
//        this.title = title;
//        this.url = url;
//        this.imageUrl = imageUrl;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public Users getUsers() {
//        return users;
//    }
//
//    public void setUsers(Users users) {
//        this.users = users;
//    }
//
//    public Collection<CommentsE> getCommentsCollection() {
//        return commentsCollection;
//    }
//
//    public void setCommentsCollection(Collection<CommentsE> commentsCollection) {
//        this.commentsCollection = commentsCollection;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof DesignsEE)) {
//            return false;
//        }
//        DesignsEE other = (DesignsEE) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "entity.DesignsEE[id=" + id + "]";
//    }

}
