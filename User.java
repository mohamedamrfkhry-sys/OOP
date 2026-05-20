public abstract class User {
    private final String userId;
    private final String userName;
    private String password;
    private String name;
    private String email;
    private boolean Logged;

    public User(String userId, String userName, String password, String name, String email) {
        this.userId = userId;
        this.userName = userName;
        setPassword(password);
        this.name = name;
        this.email = email;
    }

    public String getEmail() {return email;}
    public String getName() {return name;}
    public String getUserId() {return userId;}
    public String getUserName() {return userName;}

    public String getPassword() {return password;}

    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}

    public void setPassword(String password) {
        if (password.length() < 6) {System.out.println("Min pass length is 6 characters");}
        else this.password = password;}

    public boolean login(String password) {
        if (this.password.equals(password)) {
            System.out.println("Login was successful");
            Logged = true;
            return true;
        }
        else
            System.out.println("Login Failed due to incorrect password");
        return false;
    }

    public void logout() {
        Logged = false;
        System.out.println(getUserName() + " just logged off");
    }

    public void updateProfile(String name, String email) {
        setName(name);
        setEmail(email);
    }
}



