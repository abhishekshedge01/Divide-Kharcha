package com.abhishek.dividekharcha;

public class model {
    private String title;
    private String content;
    private String phone;

    public model() {
    }

    public model(String title, String content,String phone){
        this.title = title;
        this.content = content;
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPhone() {return phone; }



}
