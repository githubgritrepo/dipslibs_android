package com.evo.mitzoom.API;

public class Server {

    public static final String BASE_URL_API = "http://103.140.91.46:6514/";
    public static final String BASE_URL_OCR_API = "http://ocr-dips.grit.id/";
    //public static final String BASE_URL_API = "http://45.127.133.162:3000/"; //prod
    //public static final String BASE_URL_API = "https://next-backup.vercel.app/";
    //public static final String BASE_URL_API = "http://192.168.10.198:4000/";

    public static ApiService getAPIService() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }
    public static ApiService getAPIServiceoCR() {
        return Client.getClientUnsafe(BASE_URL_OCR_API).create(ApiService.class);
    }

    /* public static ApiService getAPIFirebase() {
        return Client.getClientUnsafe(BASE_URL_API_FIREBASE).create(ApiService.class);
    }*/

}
