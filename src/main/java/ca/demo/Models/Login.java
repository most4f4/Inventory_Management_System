package ca.demo.Models;

import java.util.HashMap;
import java.util.Map;

public class Login {
    private String userName;
    private String password;

    private static final Map<String, String> dataBase = new HashMap<>();

    static {
        dataBase.put("user1", "pass1");
        dataBase.put("user2", "pass2");
    }

    public Login(String username, String password) {
        this.userName = username.toLowerCase();
        this.password = password.toLowerCase();
    }

    public boolean validate() {
        return dataBase.containsKey(userName) && dataBase.get(userName).equals(password);
    }
}
