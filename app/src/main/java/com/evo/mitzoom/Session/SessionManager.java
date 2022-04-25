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

    public String getLANG() {
        return pref.getString(KEY_LANG,null);
    }
    public String getRTGS() {
        return pref.getString(KEY_RTGS,null);
    }

}
