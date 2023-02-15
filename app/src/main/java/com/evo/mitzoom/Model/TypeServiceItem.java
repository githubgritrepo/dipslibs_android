package com.evo.mitzoom.Model;

public class TypeServiceItem {
    private final String headline;
    private final String content;

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
