package com.evo.mitzoom.Model;

public class ItemModel {
    private String id;
    private String namaItem;
    private int gambarItem;

    public ItemModel (String id, String namaItem, int gambarItem){
        this.id = id;
        this.namaItem = namaItem;
        this.gambarItem = gambarItem;
    }

    public String getId() {
        return id;
    }

    public String getNamaItem() {
        return namaItem;
    }

    public int getGambarItem() {
        return gambarItem;
    }
}
