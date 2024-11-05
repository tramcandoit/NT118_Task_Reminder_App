package com.example.mobileapp.Database;

public class User {
    /*user_id (ID người dùng)

    username (Tên người dùng)

    email (Email)

    password (Mật khẩu)*/

    private int user_id;
    private String username;
    private String email;
    private String password;

    public User(){

    }

    public User(int user_id, String username, String email, String password)
    {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public int getUser_id()
    {
        return this.user_id;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return this.password;
    }

}
