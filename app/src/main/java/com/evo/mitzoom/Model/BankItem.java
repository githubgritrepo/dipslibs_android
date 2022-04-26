package com.evo.mitzoom.Model;

public class BankItem {
    private String bankName;
    private int flagImage;

    public BankItem(String bankName, int flagImage) {
        this.bankName = bankName;
        this.flagImage = flagImage;
    }

    public String getBankName() {
        return bankName;
    }

    public int getFlagImage() {
        return flagImage;
    }
}
