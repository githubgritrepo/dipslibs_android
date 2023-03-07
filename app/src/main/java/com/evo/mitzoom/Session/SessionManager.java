package com.evo.mitzoom.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // Shared Preferences
    private final SharedPreferences pref;

    // Editor for Shared preferences
    private final SharedPreferences.Editor editor;

    // Context
    private final Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "PREF_DiPS";
    // All Shared Preferences Keys
    public static final String KEY_LANG = "LANGUAGE";
    public static final String KEY_RTGS = "RTGS";
    public static final String KEY_IdDips = "IdDips";
    public static final String KEY_KTP = "KTP";
    public static final String KEY_SWAFOTO = "SWAFOTO";
    public static final String KEY_NPWP = "NPWP";
    public static final String KEY_TTD = "TTD";
    public static final String KEY_CHAT = "CHAT";
    public static final String KEY_iSCust = "isCust";
    public static final String KEY_iSSwafoto = "isSwafoto";
    public static final String KEY_FLAG_UPDOC = "FLAG_UPLOADDOC";
    public static final String KEY_MEDIA = "MEDIA";
    public static final String KEY_CAMERA = "CAMERA";
    public static final String KEY_CIF = "CIF";
    public static final String KEY_NOCIF = "NOCIF";
    public static final String KEY_CONF_AGREE = "CONF_AGREE";
    public static final String KEY_NASABAH = "NASABAH";
    public static final String KEY_REKTABUNGAN = "TABUNGANNASABAH";
    public static final String KEY_ADVANCE_AI_LICENSE = "ADVANCE_AI_LICENSE";
    public static final String KEY_ADVANCE_AI_EXPIREDTIME = "ADVANCE_AI_EXPIREDTIME";
    public static final String KEY_AUTHTOKEN = "AUTHTOKEN";
    public static final String KEY_EXCHANGETOKEN = "EXCHANGETOKEN";
    public static final String KEY_CSID = "CSID";
    public static final String KEY_FLOW = "FLOW";
    public static final String KEY_FORMCODE = "FORMCODE";
    public static final String KEY_OCR = "OCR";
    public static final String KEY_IDSCHEDULE = "IDSCHEDULE";
    public static final String KEY_FORMREQ = "FORMREQ";
    public static final String KEY_FORMREQMIRRORING = "FORMREQMIRRORING";
    public static final String KEY_noComplaint = "NOCOMPLAINT";
    public static final String KEY_NAMANASABAH = "NAMANASABAH";
    public static final String KEY_SESSIONID_DIPS = "SESSIONID_DIPS";
    public static final String KEY_SCHEDULE_DATE = "SCHEDULE_DATE";
    public static final String KEY_SCHEDULE_TIME = "SCHEDULE_TIME";

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

    public void saveSessionIdDips(String data){
        editor.putString(KEY_SESSIONID_DIPS,data);
        editor.commit();
    }

    public void saveKTP(String data){
        editor.putString(KEY_KTP, data);
        editor.commit();
    }
    public void saveSWAFOTO(String data){
        editor.putString(KEY_SWAFOTO, data);
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
    public void saveIsSwafoto(boolean data) {
        editor.putBoolean(KEY_iSSwafoto,data);
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

    public void saveFlagConfAgree(boolean data) {
        editor.putBoolean(KEY_CONF_AGREE,data);
        editor.commit();
    }

    public void saveAuthAdvanceAI(String license, long expiredTimes) {
        editor.putString(KEY_ADVANCE_AI_LICENSE,license);
        editor.putLong(KEY_ADVANCE_AI_EXPIREDTIME, expiredTimes);
        editor.commit();
    }

    public void saveNasabah(String data) {
        editor.putString(KEY_NASABAH,data);
        editor.commit();
    }
    public void saveRekNasabah(String data) {
        editor.putString(KEY_REKTABUNGAN,data);
        editor.commit();
    }

    public void saveAuthToken(String data) {
        editor.putString(KEY_AUTHTOKEN,data);
        editor.commit();
    }

    public void saveExchangeToken(String data) {
        editor.putString(KEY_EXCHANGETOKEN,data);
        editor.commit();
    }

    public void saveCSID(String data) {
        editor.putString(KEY_CSID,data);
        editor.commit();
    }

    public void saveFormCOde(int data){
        editor.putInt(KEY_FORMCODE,data);
        editor.commit();
    }

    public void saveOCR(String data) {
        editor.putString(KEY_OCR,data);
        editor.commit();
    }

    public void saveIDSchedule(int data){
        editor.putInt(KEY_IDSCHEDULE,data);
        editor.commit();
    }

    public void saveFormReq(String data) {
        editor.putString(KEY_FORMREQ,data);
        editor.commit();
    }

    public void saveFormReqMirroring(String data) {
        editor.putString(KEY_FORMREQMIRRORING,data);
        editor.commit();
    }

    public void saveNoCIF(String data) {
        editor.putString(KEY_NOCIF,data);
        editor.commit();
    }

    public void saveNoComplaint(String data) {
        editor.putString(KEY_noComplaint,data);
        editor.commit();
    }

    public void saveNasabahName(String data) {
        editor.putString(KEY_NAMANASABAH,data);
        editor.commit();
    }

    public void saveScheduledDate(String data) {
        editor.putString(KEY_SCHEDULE_DATE,data);
        editor.commit();
    }

    public void saveScheduledTime(String data) {
        editor.putString(KEY_SCHEDULE_TIME,data);
        editor.commit();
    }

    public void clearPartData(){
        editor.putString(KEY_RTGS,null);
        editor.putString(KEY_CIF,null);
        editor.putString(KEY_CHAT,null);
        editor.putString(KEY_KTP,null);
        editor.putString(KEY_SWAFOTO,null);
        editor.putString(KEY_NPWP,null);
        editor.putString(KEY_TTD,null);
        editor.putBoolean(KEY_iSCust,false);
        editor.putBoolean(KEY_iSSwafoto,false);
        editor.putString(KEY_OCR,null);
        editor.putInt(KEY_FORMCODE,0);
        editor.putString(KEY_FORMREQ,null);
        editor.putString(KEY_FORMREQMIRRORING,null);
        editor.putString(KEY_noComplaint,null);
        editor.commit();
    }

    public void clearCIF() {
        editor.putString(KEY_CIF,null);
        editor.putString(KEY_KTP,null);
        editor.putString(KEY_SWAFOTO,null);
        editor.putString(KEY_NPWP,null);
        editor.putString(KEY_TTD,null);
        editor.putBoolean(KEY_iSCust,false);
        editor.putString(KEY_OCR,null);
        editor.putBoolean(KEY_iSSwafoto,false);
        editor.putInt(KEY_FORMCODE,0);
        editor.putInt(KEY_FLOW,0);
        editor.putString(KEY_FORMREQ,null);
        editor.putString(KEY_FORMREQMIRRORING,null);
        editor.putString(KEY_noComplaint,null);
        editor.commit();
    }

    public void clearIdDiPSCSID(){
        editor.putString(KEY_IdDips,null);
        editor.putString(KEY_SESSIONID_DIPS,null);
        editor.putString(KEY_CSID,null);
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
    public String getNasabah() {
        return pref.getString(KEY_NASABAH,null);
    }
    public String getRekNasabah() {
        return pref.getString(KEY_REKTABUNGAN,null);
    }
    public String getKEY_IdDips (){
        return pref.getString(KEY_IdDips,null);
    }
    public String getSessionIdDips (){
        return pref.getString(KEY_SESSIONID_DIPS,null);
    }
    public String getKEY_KTP (){
        return pref.getString(KEY_KTP,null);
    }
    public String getKEY_SWAFOTO (){
        return pref.getString(KEY_SWAFOTO,null);
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
    public boolean getKEY_iSSwafoto (){
        return pref.getBoolean(KEY_iSSwafoto,false);
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
    public boolean getFlagConfAgree (){
        return pref.getBoolean(KEY_CONF_AGREE,false);
    }
    public String getAuthAdvanceAI() {
        return pref.getString(KEY_ADVANCE_AI_LICENSE,null);
    }
    public long getExpiredTimeAdvanceAI() { return pref.getLong(KEY_ADVANCE_AI_EXPIREDTIME,0);}
    public String getAuthToken() {
        return pref.getString(KEY_AUTHTOKEN,null);
    }
    public String getExchangeToken() {
        return pref.getString(KEY_EXCHANGETOKEN,null);
    }
    public String getCSID() {
        return pref.getString(KEY_CSID,null);
    }
    public int getFormCode (){
        return pref.getInt(KEY_FORMCODE,0);
    }
    public String getOCR() {
        return pref.getString(KEY_OCR,null);
    }
    public int getIDSchedule (){
        return pref.getInt(KEY_IDSCHEDULE,0);
    }
    public String getFormReq() {
        return pref.getString(KEY_FORMREQ,null);
    }
    public String getFormReqMirroring() {
        return pref.getString(KEY_FORMREQMIRRORING,null);
    }
    public String getNoCIF() {
        return pref.getString(KEY_NOCIF,null);
    }
    public String getNoComplaint() {
        return pref.getString(KEY_noComplaint,null);
    }
    public String getNasabahName() {
        return pref.getString(KEY_NAMANASABAH,null);
    }
    public String getScheduledDate() {
        return pref.getString(KEY_SCHEDULE_DATE,null);
    }
    public String getScheduledTime() {
        return pref.getString(KEY_SCHEDULE_TIME,null);
    }

}
