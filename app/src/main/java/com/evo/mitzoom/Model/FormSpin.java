package com.evo.mitzoom.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FormSpin {

    private int id;
    private String code;
    private String name;
    private String nameEng;

    public FormSpin(int id, String code, String name, String nameEng) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.nameEng = nameEng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof FormSpin){
            FormSpin c = (FormSpin ) obj;
            return c.getName().equals(name) && c.getId() == id;
        }

        return false;
    }
}
