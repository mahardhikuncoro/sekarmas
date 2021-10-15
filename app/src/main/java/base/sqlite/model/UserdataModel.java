package base.sqlite.model;

/**
 * Created by benyamin on 5/16/18.
 */

public class UserdataModel {

    private Integer idSqlite;
    private String username;
    private String photoprofile;
    private String fullname;
    private String groupid;
    private String groupname;
    private String branchid;
    private String id;
    private String accesstoken;
    private String tokentype;
    private String expiredin;

    public Integer getIdSqlite() {
        return idSqlite;
    }

    public void setIdSqlite(Integer idSqlite) {
        this.idSqlite = idSqlite;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoprofile() {
        return photoprofile;
    }

    public void setPhotoprofile(String photoprofile) {
        this.photoprofile = photoprofile;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthDate() {
        return groupid;
    }

    public void setBirthDate(String groupid) {
        this.groupid = groupid;
    }

    public String getGender() {
        return groupname;
    }

    public void setGender(String groupname) {
        this.groupname = groupname;
    }

    public String getPhoneNumber() {
        return branchid;
    }

    public void setPhoneNumber(String branchid) {
        this.branchid = branchid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getEmail() {
        return tokentype;
    }

    public void setEmail(String tokentype) {
        this.tokentype = tokentype;
    }

    public String getExpiredin() {
        return expiredin;
    }

    public void setExpiredin(String expiredin) {
        this.expiredin = expiredin;
    }
}
