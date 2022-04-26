package com.evo.mitzoom.Model;

public class TypeServiceItem {
    private String headline, content;

    public TypeServiceItem (String headline, String content){
        this.headline = headline;
        this.content = content;
    }

    public String getHeadline() {
        return headline;
    }

    public String getContent() {
        return content;
    }
}
