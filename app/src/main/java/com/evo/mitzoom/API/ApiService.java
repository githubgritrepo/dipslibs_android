package com.evo.mitzoom.API;

import com.evo.mitzoom.Constants.MyConstants;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/ocr/ktp")
    Call<JsonObject> ocrKtp(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/capture/identify")
    Call<CaptureIdentify> CaptureIdentify(@Body JsonCaptureIdentify body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/zoom/signature")
    Call<JsonObject> Signature(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/queue/tiket")
    Call<JsonObject> Ticket(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/nik/cek")
    Call<JsonObject> CekData(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/document/savebase64")
    Call<JsonObject> SaveImage(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/transaction/conferencing")
    Call<JsonObject> Mirroring(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/outbound/accept")
    Call<JsonObject> acceptCall(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/outbound/reject")
    Call<JsonObject> rejectCall(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/scheduled/save")
    Call<JsonObject> saveSchedule(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/account_opening/createform")
    Call<JsonObject> createAccount(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET
    Call<JsonObject> setBarcode(
            @Url String url
    );


    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/prov/test")
    Call<JsonArray> getProv();

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/kab/test/{id}")
    Call<JsonArray> ardGetKab(
            @Path("id") String id
    );
    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/kec/test/{id}")
    Call<JsonArray> ardGetKec(
            @Path("id") String id
    );
    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/kel/test/{id}")
    Call<JsonArray> ardGetKel(
            @Path("id") String id
    );

}
