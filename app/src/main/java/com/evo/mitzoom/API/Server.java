package com.evo.mitzoom.API;

public class Server {

    /*public static final String BASE_URL_API2 = "https://dipsv2.grit.id:6006/gwnode/"; //ip new
    public static final String BASE_URL_RABBITMQ = "amqp://dips361:dips2022@103.140.90.42:6004"; //rabbit mq*/

    ///========== DEV ========= ////
//    public static final String BASE_URL_API = "https://dips.grit.id:3080/api/"; //ip development
//    public static final String BASE_URL_API2 = "https://dips.grit.id:3080/"; //ip development
//    public static final String BASE_URL_API_RABBITHTTP = "http://202.157.186.65:3030/";


    ///========== PROD ========= ////
    public static final String BASE_URL_API = "https://diops.victoriabank.co.id/gwnode/api/"; //ip bank victoria
    public static final String BASE_URL_API2 = "https://diops.victoriabank.co.id/gwnode/"; //ip bank victoria
    public static final String BASE_URL_API_RABBITHTTP = "https://diops.victoriabank.co.id/client-rabbit/";


    public static final String BASE_URL_ADVANCEAI = "https://api.advance.ai/";

    public static final String BASE_URL_RABBITMQ = "amqp://dips361:dips2022@202.157.186.65:5672"; //rabbit mq
    public static final String RABBITMQ_USERNAME = "dips361";
    public static final String RABBITMQ_PASSWORD = "dips2022";
    public static final String RABBITMQ_IP = "202.157.186.65";
    public static final int RABBITMQ_PORT = 5672;

    public static ApiService getAPIService() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }


    public static ApiService getAPIServiceRabbitHttp() {
        return Client.getClientUnsafe(BASE_URL_API_RABBITHTTP).create(ApiService.class);
    }

    public static ApiService getAPIService2() {
        return Client.getClientUnsafe(BASE_URL_API2).create(ApiService.class);
    }

    public static ApiService getAPIWAITING_PRODUCT() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }

    public static ApiService getAPIServiceAdvanceAI() {
        return Client.getClientUnsafe(BASE_URL_ADVANCEAI).create(ApiService.class);
    }

    /* public static ApiService getAPIFirebase() {
        return Client.getClientUnsafe(BASE_URL_API_FIREBASE).create(ApiService.class);
    }*/

}
