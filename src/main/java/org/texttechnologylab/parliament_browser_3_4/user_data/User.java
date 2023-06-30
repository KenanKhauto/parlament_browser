package org.texttechnologylab.parliament_browser_3_4.user_data;

import org.bson.Document;

/**
 * @author Kenan Khauto
 * a class for users of the website
 */
public class User {

    private String username;
    private String password;
    private String birthday;
    private String email;
    private Role role;
    private final Document userDoc;


    /**
     * constructor to create a user from a Bson Document
     * @param userDoc a Bson Document
     */
    public User(Document userDoc) {
        this.userDoc = userDoc;
        init();
    }


    /**
     * initialise method to set the attributes
     */
    private void init() {
        setUsername();
        setBirthday();
        setPassword();
        setRole();
        setEmail();
    }

    /**
     * set username
     */
    private void setUsername() {
        this.username = userDoc.getString("username");
    }

    /**
     * set password
     */
    private void setPassword() {
        this.password = userDoc.getString("password");
    }

    /**
     * set birthday
     */
    private void setBirthday() {
        this.birthday = userDoc.getString("birthday");
    }


    /**
     * set role
     */
    private void setRole() {

        if (userDoc.getString("role").equals("admin")) {
            this.role = new AdminRole("admin");
        } else {
            this.role = new UserRole("user");
        }
    }

    /**
     * set email
     */
    private void setEmail() {
        this.email = userDoc.getString("email");
    }


    /**
     * return true if the user is an admin
     * @return true or false, if user is an admin
     */
    public boolean isAdmin() {
        return this.role.getRoleName().equals("admin");
    }

    /**
     * get email
     * @return email as a string
     */
    public String getEmail() {
        return email;
    }

    /**
     * get username
     * @return username as a string
     */
    public String getUsername() {
        return username;
    }

    /**
     * get role
     * @return role as Role
     */

    public Role getRole() {
        return role;
    }


    /**
     * get birthday
     * @return birthday as a string
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * get password as a string
     * @return password as a string
     */
    public String getPassword() {
        return password;
    }

    /**
     * return the Bson doc of the user
     * @return user as a Bson document
     */
    public Document toDocument() {
        return this.userDoc;
    }
}
