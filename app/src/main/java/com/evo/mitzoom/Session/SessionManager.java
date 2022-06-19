package com.evo.mitzoom.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "PREF_DiPS";
    // All Shared Preferences Keys
    public static final String KEY_LANG = "LANGUAGE";
    public static final String KEY_RTGS = "RTGS";
    public static final String KEY_IdDips = "IdDips";
    public static final String KEY_KTP = "KTP";
    public static final String KEY_NPWP = "NPWP";
    public static final String KEY_TTD = "TTD";
    public static final String KEY_CHAT = "CHAT";
    public static final String KEY_iSCust = "isCust";
    public static final String KEY_FLAG_UPDOC = "FLAG_UPLOADDOC";
    public static final String KEY_MEDIA = "MEDIA";
    public static final String KEY_CAMERA = "CAMERA";
    public static final String KEY_CIF = "CIF";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void saveLANG(String data) {
        editor.putString(KEY_LANG,data);
        editor.commit();
    }
    public void saveRTGS(String data) {
        editor.putString(KEY_RTGS,data);
        editor.commit();
    }
    public void saveCIF(String data) {
        editor.putString(KEY_CIF,data);
        editor.commit();
    }
    public void saveChat(String data) {
        editor.putString(KEY_CHAT,data);
        editor.commit();
    }
    public void saveIdDips(String data){
        editor.putString(KEY_IdDips,data);
        editor.commit();
    }
    public void saveKTP(String data){
        editor.putString(KEY_KTP, data);
        editor.commit();
    }
    public void saveNPWP(String data){
        editor.putString(KEY_NPWP,data);
        editor.commit();
    }
    public void saveTTD(String data){
        editor.putString(KEY_TTD,data);
        editor.commit();
    }
    public void saveFlagUpDoc(boolean data){
        editor.putBoolean(KEY_FLAG_UPDOC,data);
        editor.commit();
    }
    public void saveIsCust(boolean data) {
        editor.putBoolean(KEY_iSCust,data);
        editor.commit();
    }
    public void saveMedia(int data){
        editor.putInt(KEY_MEDIA,data);
        editor.commit();
    }

    public void saveCamera(int data){
        editor.putInt(KEY_CAMERA,data);
        editor.commit();
    }

    public void clearData(){
        editor.clear();
        editor.commit();
    }

    public String getLANG() {
        return pref.getString(KEY_LANG,null);
    }
    public String getRTGS() {
        return pref.getString(KEY_RTGS,null);
    }
    public String getCIF() {
        return pref.getString(KEY_CIF,null);
    }
    public String getKEY_IdDips (){
        return pref.getString(KEY_IdDips,null);
    }
    public String getKEY_KTP (){
        return pref.getString(KEY_KTP,null);
    }
    public String getKEY_NPWP (){
        return pref.getString(KEY_NPWP,null);
    }
    public String getKEY_TTD (){
        return pref.getString(KEY_TTD,null);
    }
    public String getKEY_CHAT (){
        return pref.getString(KEY_CHAT,null);
    }
    public boolean getKEY_iSCust (){
        return pref.getBoolean(KEY_iSCust,false);
    }
    public boolean getFlagUpDoc (){
        return pref.getBoolean(KEY_FLAG_UPDOC,false);
    }
    public int getMedia (){
        return pref.getInt(KEY_MEDIA,0);
    }
    public int getCamera (){
        return pref.getInt(KEY_CAMERA,0);
    }

}
