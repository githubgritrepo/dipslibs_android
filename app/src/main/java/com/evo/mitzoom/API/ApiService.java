package com.evo.mitzoom.API;

import com.evo.mitzoom.Constants.MyConstants;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: "+ MyConstants.CONTENT_TYPE)
    @POST("api/capture/identify")
    Call<CaptureIdentify> CaptureIdentify(@Body JsonCaptureIdentify body);

}
