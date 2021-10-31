package com.abhishek.dividekharcha;

public class friendmodel {

    private String name;
    private String email;

    public friendmodel() {
    }

    public friendmodel(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
