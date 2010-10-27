package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Tim Church
 */
//@Entity
//@Table(name = "users")
//@NamedQueries({
//    @NamedQuery(name = "UsersE.findAll", query = "SELECT u FROM UsersE u"),
//    @NamedQuery(name = "UsersE.findById", query = "SELECT u FROM UsersE u WHERE u.id = :id"),
//    @NamedQuery(name = "UsersE.findByUsername", query = "SELECT u FROM UsersE u WHERE u.username = :username"),
//    @NamedQuery(name = "UsersE.findByPassword", query = "SELECT u FROM UsersE u WHERE u.password = :password"),
//    @NamedQuery(name = "UsersE.findByFirstname", query = "SELECT u FROM UsersE u WHERE u.firstname = :firstname"),
//    @NamedQuery(name = "UsersE.findByLastname", query = "SELECT u FROM UsersE u WHERE u.lastname = :lastname"),
//    @NamedQuery(name = "UsersE.findByEmail", query = "SELECT u FROM UsersE u WHERE u.email = :email")})
public class UsersE implements Serializable {
//    private static final long serialVersionUID = 1L;
//    @Id
//    @Basic(optional = false)
//    @Column(name = "id")
//    private Integer id;
//    @Basic(optional = false)
//    @Column(name = "username")
//    private String username;
//    @Basic(optional = false)
//    @Column(name = "password")
//    private String password;
//    @Basic(optional = false)
//    @Column(name = "firstname")
//    private String firstname;
//    @Basic(optional = false)
//    @Column(name = "lastname")
//    private String lastname;
//    @Basic(optional = false)
//    @Column(name = "email")
//    private String email;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
//    private Collection<DesignsEE> designsCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
//    private Collection<CommentsE> commentsCollection;
//
//    public UsersE() {
//    }
//
//    public UsersE(Integer id) {
//        this.id = id;
//    }
//
//    public UsersE(Integer id, String username, String password, String firstname, String lastname, String email) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//        this.firstname = firstname;
//        this.lastname = lastname;
//        this.email = email;
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
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getFirstname() {
//        return firstname;
//    }
//
//    public void setFirstname(String firstname) {
//        this.firstname = firstname;
//    }
//
//    public String getLastname() {
//        return lastname;
//    }
//
//    public void setLastname(String lastname) {
//        this.lastname = lastname;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public Collection<DesignsEE> getDesignsCollection() {
//        return designsCollection;
//    }
//
//    public void setDesignsCollection(Collection<DesignsEE> designsCollection) {
//        this.designsCollection = designsCollection;
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
//        if (!(object instanceof UsersE)) {
//            return false;
//        }
//        UsersE other = (UsersE) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "entity.UsersE[id=" + id + "]";
//    }

}
