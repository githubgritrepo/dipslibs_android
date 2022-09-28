package com.evo.mitzoom.API;

import com.evo.mitzoom.Constants.MyConstants;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/digitech/ocr-ktp")
    Call<JsonObject> ocrKtp(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/capture/identify")
    Call<CaptureIdentify> CaptureIdentify(@Body JsonCaptureIdentify body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/capture/advance")
    Call<CaptureIdentify> CaptureAdvanceAI(@Body JsonCaptureIdentify body);

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

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("/api/form/save")
    Call<JsonObject> saveForm(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/form/verifyOtp")
    Call<JsonObject> VerifyOTP(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/form/resendOtp")
    Call<JsonObject> ResendOTP(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/customer/portofolio/{idDiPS}")
    Call<JsonObject> GetPortofolio(
            @Path("idDiPS") String idDiPS
    );

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("api/form/resume-rtgs/{indexTypeTran}-{etc}-{noFIP}-{isPenduduk}-{namaPenyetor}-{addrPenyetor}-{noHP}-" +
            "{noRek}-{total}-{biaya}-{namaPenerima}-{addrPenerima}-{bankPenerima}-{noRekPenerima}-{berita}-" +
            "{namaTeller}")
    Call<JsonObject> GetResumeTransaction(
            @Path("indexTypeTran") int indexTypeTran,
            @Path("etc") String etc,
            @Path("noFIP") String noFIP,
            @Path("isPenduduk") boolean isPenduduk,
            @Path("namaPenyetor") String namaPenyetor,
            @Path("addrPenyetor") String addrPenyetor,
            @Path("noHP") String noHP,
            @Path("noRek") String noRek,
            @Path("total") String total,
            @Path("biaya") int biaya,
            @Path("namaPenerima") String namaPenerima,
            @Path("addrPenerima") String addrPenerima,
            @Path("bankPenerima") String bankPenerima,
            @Path("noRekPenerima") String noRekPenerima,
            @Path("berita") String berita,
            @Path("namaTeller") String namaTeller
    );


    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("spanduk/publish")
    Call<JsonObject> getSpandukPublish();

    @GET("spanduk/media/{id}")
    Call<ResponseBody> getSpandukMedia(@Path("id") int id);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("publish_product")
    Call<JsonObject> getProductPublish();

    @GET("produk/media/{id}")
    Call<ResponseBody> getProductMedia(@Path("id") int id);

    @GET("percobaan/form-nik")
    Call<ResponseBody> getFormNIK();

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("openapi/liveness/v1/auth-license")
    Call<JsonObject> AuthLicenseLiveness(@Body RequestBody body,
                                         @Header("X-ADVAI-KEY") String authHeader
    );

}
