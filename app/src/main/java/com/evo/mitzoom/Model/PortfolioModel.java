package com.evo.mitzoom.Model;

public class PortfolioModel {
    private String id;
    private String namaPortfolio;
    private String nominalPortfolio;
    private int gambarPortfolio;

    public PortfolioModel (String id, String namaPortfolio, String nominalPortfolio,int gambarPortfolio){
        this.id = id;
        this.namaPortfolio = namaPortfolio;
        this.nominalPortfolio = nominalPortfolio;
        this.gambarPortfolio = gambarPortfolio;
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
}
