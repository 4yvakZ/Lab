package security;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {
    private String login;
    private String password;

    public User(String login, String password){
        this.login = login;
        this.password =  password;
    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(User user) {
        return login.equals(user.getLogin())&&password.equals(user.getPassword());
    }

    @Override
    public int compareTo(User o) {
        return login.compareTo(o.getLogin());
    }
}
