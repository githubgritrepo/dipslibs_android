package com.evo.mitzoom.API;

import com.evo.mitzoom.Constants.MyConstants;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

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


}
