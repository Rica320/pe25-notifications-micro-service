package pt.up.fe.pe25.authentication;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import java.util.List;

@Entity
@Table(name = "user")
@UserDefinition
public class User extends PanacheEntity {
    @Username
    public String username;
    @Password
    protected String password;
    @Roles
    @ElementCollection
    protected List<String> role;

    /**
     * Adds a new user to the database
     * @param username the username
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role a list of roles
     */
    public static void add(String username, String password, List<String> role) {
        User user = new User();
        user.username = username;
        user.setPassword(BcryptUtil.bcryptHash(password));
        user.setRole(role);
        user.persist();
    }

    /**
     * Finds a user by username
     * @param username the username
     * @return the user
     */
    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    /**
     * Finds a user by role
     * @param role the role
     * @return a list of users
     */
    public static List<User> findByRole(String role) {
        return find("role", role).list();
    }

    /**
     * sets User's password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * sets User's roles
     * @param role
     */
    public void setRole(List<String> role) {
        this.role = role;
    }
}
