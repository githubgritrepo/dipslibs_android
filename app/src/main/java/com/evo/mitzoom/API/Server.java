package com.evo.mitzoom.API;

public class Server {

    public static final String BASE_URL_API = "http://103.140.91.46:6514/";

    public static ApiService getAPIService() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }

//    public static ApiService getAPIFirebase() {
//        return Client.getClientUnsafe(BASE_URL_API_FIREBASE).create(ApiService.class);
//    }

}
