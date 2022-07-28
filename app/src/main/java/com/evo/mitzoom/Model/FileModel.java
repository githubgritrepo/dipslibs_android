package com.evo.mitzoom.Model;

public class FileModel {
    private String id;
    private String namaFile;
    private String url;
    private int colorItem;

    public FileModel (String id, String namaFile, int colorItem, String url){
        this.id = id;
        this.namaFile = namaFile;
        this.colorItem = colorItem;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public int getColorItem() {
        return colorItem;
    }
}
