package com.evo.mitzoom.Model.Request;

import com.google.gson.annotations.SerializedName;

public class JsonCaptureIdentify {

    @SerializedName("image")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
