package com.lovebus.entity;

import android.widget.Button;

import java.io.Serializable;

import bus.android.com.lovebus.R;

public class User implements Serializable{
    private static final long serialVersionUID=548624851698452674L;

    private boolean is_login;
    private String account;
    private String phone;
    private String password;
    private String nickname;
    private String city;
    private String head_image;
    public User(boolean is_login,String account, String phone, String password, String nickname, String city, String head_image) {
        this.is_login=is_login;
        this.account = account;
        this.phone = phone;
        this.password = password;
        this.nickname = nickname;
        this.city = city;
        this.head_image = head_image;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public boolean isIs_login() {
        return is_login;
    }

    public void setIs_login(boolean is_login) {
        this.is_login = is_login;
    }
}
