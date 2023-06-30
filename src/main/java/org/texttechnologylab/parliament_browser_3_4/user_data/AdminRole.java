package org.texttechnologylab.parliament_browser_3_4.user_data;

/**
 * @author Kenan Khauto
 */
public class AdminRole implements Role {

    private String roleName;

    /**
     * constructor
     * @param roleName as a string
     */
    public AdminRole(String roleName) {
        setRoleName(roleName);
    }

    @Override
    public void setRoleName(String name) {
        this.roleName = name;
    }

    @Override
    public String getRoleName() {
        return this.roleName;
    }


}
