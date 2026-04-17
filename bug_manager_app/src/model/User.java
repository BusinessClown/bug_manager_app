package model;

public class User {

    private long id;
    private String username, fullname, email, password, jobTitle;
    private boolean admin;

    public User() { super(); }

    public User(long id, String username, String fullname, String email,
                String password, boolean admin, String jobTitle) {
        this.id       = id;
        this.username = username;
        this.fullname = fullname;
        this.email    = email;
        this.password = password;
        this.admin    = admin;
        this.jobTitle = jobTitle;
    }

    public long    getId()                  { return id; }
    public void    setId(long id)           { this.id = id; }
    public String  getUsername()            { return username; }
    public void    setUsername(String u)    { this.username = u; }
    public String  getFullname()            { return fullname; }
    public void    setFullname(String f)    { this.fullname = f; }
    public String  getEmail()               { return email; }
    public void    setEmail(String e)       { this.email = e; }
    public String  getPassword()            { return password; }
    public void    setPassword(String p)    { this.password = p; }
    public boolean isAdmin()                { return admin; }
    public void    setAdmin(boolean a)      { this.admin = a; }
    public String  getJobTitle()            { return jobTitle; }
    public void    setJobTitle(String j)    { this.jobTitle = j; }
}
