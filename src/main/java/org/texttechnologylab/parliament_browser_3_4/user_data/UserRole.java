package org.texttechnologylab.parliament_browser_3_4.user_data;


/**
 * @author Kenan Khauto
 */

public class UserRole implements Role {

    String roleName;

    /**
     * constructor
     * @param roleName as a string
     */
    public UserRole(String roleName) {
        setRoleName(roleName);
    }

    @Override
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getRoleName() {

        return this.roleName;
    }

}
