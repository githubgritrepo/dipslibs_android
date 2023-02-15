package com.evo.mitzoom.Model;

public class PortfolioModel {
    private final String id;
    private final String namaPortfolio;
    private final String nominalPortfolio;
    private final int gambarPortfolio;
    private final String linkIcon;

    public PortfolioModel (String id, String namaPortfolio, String nominalPortfolio,int gambarPortfolio, String icon){
        this.id = id;
        this.namaPortfolio = namaPortfolio;
        this.nominalPortfolio = nominalPortfolio;
        this.gambarPortfolio = gambarPortfolio;
        this.linkIcon = icon;
    }

    public String getId() {
        return id;
    }

    public String getNamaPortfolio() {
        return namaPortfolio;
    }

    public String getNominalPortfolio() {
        return nominalPortfolio;
    }

    public int getGambarPortfolio() {
        return gambarPortfolio;
    }

    public String getLinkIcon() {
        return linkIcon;
    }

}
