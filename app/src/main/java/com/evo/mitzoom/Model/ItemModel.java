package com.evo.mitzoom.Model;

public class ItemModel {
    private final String id;
    private final String namaItem;
    private final int gambarItem;

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
