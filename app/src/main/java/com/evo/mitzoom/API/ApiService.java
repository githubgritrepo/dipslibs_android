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
    @POST("digitech/ocr-ktp")
    Call<JsonObject> ocrKtp(@Body RequestBody body,
                            @Header("Authorization") String authHeader,
                            @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("digitech/ocr-npwp")
    Call<JsonObject> ocrNpwp(@Body RequestBody body,
                             @Header("Authorization") String authHeader,
                             @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("capture/identify")
    Call<CaptureIdentify> CaptureIdentify(@Body JsonCaptureIdentify body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("capture/advance")
    Call<CaptureIdentify> CaptureAdvanceAI(@Body JsonCaptureIdentify body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("auth/customer/auth")
    Call<JsonObject> CaptureAuth(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer/get-by-nik")
    Call<JsonObject> IdentifybyNIK(@Body RequestBody body,
                                   @Header("Authorization") String authHeader);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("auth/customer/exchange")
    Call<JsonObject> ExchangeAuth(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("credential/zoom-signature")
    Call<JsonObject> Signature(@Body RequestBody body,
                               @Header("Authorization") String authHeader,
                               @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("queue/tiket")
    Call<JsonObject> Ticket(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("nik/cek")
    Call<JsonObject> CekData(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer/nik-check")
    Call<JsonObject> CekByNIK(@Body RequestBody body,
                              @Header("Authorization") String authHeader,
                              @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("document/savebase64")
    Call<JsonObject> SaveImage(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("transaction/conferencing")
    Call<JsonObject> Mirroring(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("outbound/accept")
    Call<JsonObject> acceptCall(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("outbound/reject")
    Call<JsonObject> rejectCall(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("schedule/create")
    Call<JsonObject> saveSchedule(@Body RequestBody body,
                                  @Header("Authorization") String authHeader,
                                  @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("schedule/not-available")
    Call<JsonObject> GetCheckSchedule(@Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("time-period")
    Call<JsonObject> GetScheduleTimes(@Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("account_opening/createform")
    Call<JsonObject> createAccount(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("client-rabbit/get-ticket")
    Call<JsonObject> getTicketV2(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET
    Call<JsonObject> setBarcode(
            @Url String url
    );


    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("prov/test")
    Call<JsonArray> getProv();

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("kab/test/{id}")
    Call<JsonArray> ardGetKab(
            @Path("id") String id
    );
    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("kec/test/{id}")
    Call<JsonArray> ardGetKec(
            @Path("id") String id
    );
    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("kel/test/{id}")
    Call<JsonArray> ardGetKel(
            @Path("id") String id
    );

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("form/save")
    Call<JsonObject> saveForm(@Body RequestBody body,
                              @Header("Authorization") String authHeader,
                              @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("form/verifyOtp")
    Call<JsonObject> VerifyOTP(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("form/resendOtp")
    Call<JsonObject> ResendOTP(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("kodepos/get-by-wilayah")
    Call<JsonObject> getKodePos(@Body RequestBody body,
                                @Header("Authorization") String authHeader,
                                @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("middleware/send-otp")
    Call<JsonObject> SendOTP(@Body RequestBody body,
                             @Header("Authorization") String authHeader,
                             @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("middleware/validate-otp")
    Call<JsonObject> ValidateOTP(@Body RequestBody body,
                                 @Header("Authorization") String authHeader,
                                 @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("middleware/inquery-cif-by-nik")
    Call<JsonObject> InquieryCIFbyNIK(@Body RequestBody body,
                                 @Header("Authorization") String authHeader,
                                 @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("customer-portfolio/{NoCIF}")
    Call<JsonObject> GetPortofolio(
            @Path("NoCIF") String NoCIF
    );

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/validasi/data-nasabah")
    Call<JsonObject> validasiDataNasabah(@Body RequestBody body,
                                 @Header("Authorization") String authHeader,
                                 @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/portfolio-data")
    Call<JsonObject> AddDataSelf(@Body RequestBody body,
                                 @Header("Authorization") String authHeader,
                                 @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/employment-data/")
    Call<JsonObject> AddDataWork(@Body RequestBody body,
                                 @Header("Authorization") String authHeader,
                                 @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/financial-data")
    Call<JsonObject> AddDataFinance(@Body RequestBody body,
                                    @Header("Authorization") String authHeader,
                                    @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("list-account/list")
    Call<JsonObject> GetNewPortofolio(@Body RequestBody body,
                                      @Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("form/resume-rtgs/{indexTypeTran}-{etc}-{noFIP}-{isPenduduk}-{namaPenyetor}-{addrPenyetor}-{noHP}-" +
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
    Call<JsonObject> getSpandukPublish(@Header("Authorization") String authHeader,
                                       @Header("exchangeToken") String exchangeToken);

    @GET("spanduk/media/{id}")
    Call<ResponseBody> getSpandukMedia(@Path("id") int id,
                                       @Header("Authorization") String authHeader,
                                       @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("publish_product")
    Call<JsonObject> getProductPublish();

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("list-publish-product/list")
    Call<JsonObject> getNewProductPublish(@Header("Authorization") String authHeader,
                                          @Header("exchangeToken") String exchangeToken);

    @GET("product/media/{id}")
    Call<ResponseBody> getProductMedia(@Path("id") int id,
                                       @Header("Authorization") String authHeader,
                                       @Header("exchangeToken") String exchangeToken);

    @GET("form-builder/list/{formid}")
    Call<JsonObject> getFormBuilder(@Path("formid") int formId,
                                    @Header("Authorization") String authHeader,
                                    @Header("exchangeToken") String exchangeToken);

    @GET("percobaan/form-nik")
    Call<ResponseBody> getFormNIK();

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("openapi/liveness/v1/auth-license")
    Call<JsonObject> AuthLicenseLiveness(@Body RequestBody body,
                                         @Header("X-ADVAI-KEY") String authHeader
    );

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("credentials/credential/advance-licence")
    Call<JsonObject> APIAuthLicenseLiveness(@Body RequestBody body);

    @POST("form-data-swafoto/check")
    Call<JsonObject> swafotoCheck(@Header("Content-Type") String contentType,
                                  @Header("Authorization") String authHeader,
                                  @Header("exchangeToken") String exchangeToken,
                                    @Body RequestBody body);

    @POST("form-data/data-diri")
    Call<JsonObject> formAttachment(@Header("Content-Type") String contentType,
                                    @Header("Authorization") String authHeader,
                                    @Header("exchangeToken") String exchangeToken,
                                    @Body RequestBody body);

    @GET("tnc/list/{idTnc}")
    Call<JsonObject> getTNC(@Path("idTnc") int idTnc,
                            @Header("Authorization") String authHeader,
                            @Header("exchangeToken") String exchangeToken);

    @GET("form-generator/formcif/{idDips}")
    Call<JsonObject> getResiCIF(@Path("idDips") String idDips,
                                @Header("Authorization") String authHeader,
                                @Header("exchangeToken") String exchangeToken);

    @GET("form-generator/pembukaanrekening/{idForm}")
    Call<JsonObject> getResiCIFReady(@Path("idForm") String idForm,
                                     @Header("Authorization") String authHeader,
                                     @Header("exchangeToken") String exchangeToken);

    @GET("form-generator/formkomplain/{noComplaint}")
    Call<JsonObject> getResiComplaint(@Path("noComplaint") String noComplaint,
                                      @Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @GET("form-generator/formkomplain-new/{noComplaint}")
    Call<JsonObject> getNewResiComplaint(@Path("noComplaint") String noComplaint,
                                         @Header("Authorization") String authHeader,
                                         @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("komplain")
    Call<JsonObject> formComplaint(@Body RequestBody body,
                                   @Header("Authorization") String authHeader,
                                   @Header("exchangeToken") String exchangeToken);
    @POST("form-data/komplain-old")
    Call<JsonObject> formComplaintOld(@Header("Content-Type") String contentType,
                                      @Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken,
                                      @Body RequestBody body);

    @POST("form-data-komplain/add-media")
    Call<JsonObject> formComplaintMedia(@Header("Content-Type") String contentType,
                                        @Header("Authorization") String authHeader,
                                        @Header("exchangeToken") String exchangeToken,
                                        @Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("approval/status/{idForm}")
    Call<JsonObject> ApprovalStatus(@Path("idForm") String idForm,
                                    @Header("Authorization") String authHeader,
                                    @Header("exchangeToken") String exchangeToken);

    @GET
    Call<JsonObject> getDynamicUrl(@Url String url,
                                   @Header("Authorization") String authHeader,
                                   @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("digitech/h5-advance")
    Call<JsonObject> H5Advance(@Body RequestBody body);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("rating-agent-v2")
    Call<JsonObject> RateAgent(@Body RequestBody body,
                               @Header("Authorization") String authHeader,
                               @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("rating-app")
    Call<JsonObject> RateApp(@Body RequestBody body,
                             @Header("Authorization") String authHeader,
                             @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/validasi/ibu-kandung")
    Call<JsonObject> validasiIbuKandung(@Body RequestBody body,
                                      @Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/validasi/wajah-nasabah")
    Call<JsonObject> validasiWajahNasabah(@Body RequestBody body,
                                        @Header("Authorization") String authHeader,
                                        @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/validasi/dukcapil-v2")
    Call<JsonObject> validasiDukcapil(@Body RequestBody body,
                                      @Header("Authorization") String authHeader,
                                      @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("customer-portfolio/validasi/dttot-v2")
    Call<JsonObject> validasiDttot(@Body RequestBody body,
                                   @Header("Authorization") String authHeader,
                                   @Header("exchangeToken") String exchangeToken);

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @GET("config/list")
    Call<JsonObject> ConfigList(@Header("Authorization") String authHeader,
                                    @Header("exchangeToken") String exchangeToken);
}
