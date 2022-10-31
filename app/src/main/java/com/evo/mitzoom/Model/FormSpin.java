package com.evo.mitzoom.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FormSpin {

    private int id;
    private String code;
    private String name;

    public FormSpin(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof FormSpin){
            FormSpin c = (FormSpin ) obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }

        return false;
    }
}
