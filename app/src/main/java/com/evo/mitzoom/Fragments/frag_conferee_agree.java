package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.UserVideoAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioHelper;
import us.zoom.sdk.ZoomVideoSDKAudioRawData;
import us.zoom.sdk.ZoomVideoSDKChatHelper;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKChatMessageDeleteType;
import us.zoom.sdk.ZoomVideoSDKDelegate;
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper;
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus;
import us.zoom.sdk.ZoomVideoSDKLiveTranscriptionHelper;
import us.zoom.sdk.ZoomVideoSDKMultiCameraStreamStatus;
import us.zoom.sdk.ZoomVideoSDKNetworkStatus;
import us.zoom.sdk.ZoomVideoSDKPasswordHandler;
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason;
import us.zoom.sdk.ZoomVideoSDKPhoneStatus;
import us.zoom.sdk.ZoomVideoSDKProxySettingHandler;
import us.zoom.sdk.ZoomVideoSDKRawDataPipe;
import us.zoom.sdk.ZoomVideoSDKRecordingConsentHandler;
import us.zoom.sdk.ZoomVideoSDKRecordingStatus;
import us.zoom.sdk.ZoomVideoSDKSSLCertificateInfo;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoCanvas;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;

public class frag_conferee_agree extends Fragment implements ZoomVideoSDKDelegate {
    private Context context;
    private Button btn_Setuju,btn_tidak;
    private boolean isCust = true;
    private boolean isSwafoto = false;
    private String idDips;
    private SessionManager session;
    protected ZoomVideoSDKSession sessionz;
    protected UserVideoAdapter adapter;
    private Boolean result = true;
    private RabbitMirroring rabbitMirroring;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        isCust = session.getKEY_iSCust();
        isSwafoto = session.getKEY_iSSwafoto();
        rabbitMirroring = new RabbitMirroring(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_start_conferee, container, false);
        btn_Setuju = view.findViewById(R.id.btn_accept);
        btn_tidak = view.findViewById(R.id.btn_not);
        sessionz = ZoomVideoSDK.getInstance().getSession();
        ZoomVideoSDK.getInstance().addListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        btn_tidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flagAgree = session.getFlagConfAgree();
                if (flagAgree == false) {
                    SweetAlertDialog dialogNotAgree = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
                    dialogNotAgree.setContentText(getString(R.string.waiting_conf));
                    dialogNotAgree.setCancelable(true);
                    dialogNotAgree.setConfirmText("OK");
                    dialogNotAgree.show();
                    return;
                }
                EndCall();
                /*SweetAlertDialog dialogEnd = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
                dialogEnd.setContentText(getString(R.string.leave_message));
                dialogEnd.setCancelable(true);
                dialogEnd.setConfirmText(getString(R.string.leave_leave_text));
                dialogEnd.setConfirmButtonBackgroundColor(context.getResources().getColor(R.color.zm_v1_red_A100));
                dialogEnd.setCancelText(getString(R.string.cancel));
                dialogEnd.setCancelButtonBackgroundColor(context.getResources().getColor(R.color.Blue));
                dialogEnd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialogEnd.dismissWithAnimation();
                        releaseResource();
                        int ret = ZoomVideoSDK.getInstance().leaveSession(false);
                        session.clearPartData();
                        MirroringEnd();
                        OutApps();
                    }
                });
                dialogEnd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialogEnd.dismissWithAnimation();
                    }
                });
                dialogEnd.show();*/
            }
        });
        btn_Setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flagAgree = session.getFlagConfAgree();
                if (flagAgree == false) {
                    SweetAlertDialog dialogNotAgree = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
                    dialogNotAgree.setContentText(getString(R.string.waiting_conf));
                    dialogNotAgree.setCancelable(true);
                    dialogNotAgree.setConfirmText("OK");
                    dialogNotAgree.show();
                    return;
                }
                cekData();
                /*BaseMeetingActivity.btnFile.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                BaseMeetingActivity.btnFile.setClickable(true);*/

                //BaseMeetingActivity.btnChat.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.oval_background_10dp,null));
                BaseMeetingActivity.btnChat.setClickable(true);
            }
        });
    }

    private void EndCall(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.headline_endcall));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.end_call));
        sweetAlertDialog.setCancelText(getResources().getString(R.string.no));
        sweetAlertDialog.show();
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                releaseResource();
                ZoomVideoSDK.getInstance().leaveSession(false);
                session.clearPartData();
                MirroringEnd();
                OutApps();
            }
        });
        Button btnCancel = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.cancel_button);
        btnCancel.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }

    private void MirroringEnd(){
        String idDips = session.getKEY_IdDips();
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(true);
            jsons.put("idDips",idDips);
            jsons.put("code",99);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void releaseResource() {
        unSubscribe();
    }
    protected void unSubscribe() {

    }
    private void cekData(){
        if (isCust) {
            //Jika muka terdaftar maka langsung menuju ke portfolio
            session.clearCIF();
            getFragmentPage(new frag_portfolio_new());
            rabbitMirroring.MirroringSendEndpoint(14);
        }
        else{
            rabbitMirroring.MirroringSendEndpoint(2);
            getFragmentPage(new frag_list_produk());
        }
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
        ((Activity)context).finish();
        //System.exit(0);
    }
    private void Mirroring(int codeEvent,int nextCode,boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nextCode);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",codeEvent);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }

    @Override
    public void onSessionJoin() {

    }

    @Override
    public void onSessionLeave() {


    }

    @Override
    public void onError(int errorCode) {

    }

    @Override
    public void onUserJoin(ZoomVideoSDKUserHelper userHelper, List<ZoomVideoSDKUser> userList) {

    }

    @Override
    public void onUserLeave(ZoomVideoSDKUserHelper userHelper, List<ZoomVideoSDKUser> userList) {

    }

    @Override
    public void onUserVideoStatusChanged(ZoomVideoSDKVideoHelper videoHelper, List<ZoomVideoSDKUser> userList) {

    }

    @Override
    public void onUserAudioStatusChanged(ZoomVideoSDKAudioHelper audioHelper, List<ZoomVideoSDKUser> userList) {

    }

    @Override
    public void onUserShareStatusChanged(ZoomVideoSDKShareHelper shareHelper, ZoomVideoSDKUser userInfo, ZoomVideoSDKShareStatus status) {

    }

    @Override
    public void onLiveStreamStatusChanged(ZoomVideoSDKLiveStreamHelper liveStreamHelper, ZoomVideoSDKLiveStreamStatus status) {

    }

    @Override
    public void onChatNewMessageNotify(ZoomVideoSDKChatHelper chatHelper, ZoomVideoSDKChatMessage messageItem) {

    }

    @Override
    public void onChatDeleteMessageNotify(ZoomVideoSDKChatHelper chatHelper, String msgID, ZoomVideoSDKChatMessageDeleteType deleteBy) {

    }

    @Override
    public void onUserHostChanged(ZoomVideoSDKUserHelper userHelper, ZoomVideoSDKUser userInfo) {

    }

    @Override
    public void onUserManagerChanged(ZoomVideoSDKUser user) {

    }

    @Override
    public void onUserNameChanged(ZoomVideoSDKUser user) {

    }

    @Override
    public void onUserActiveAudioChanged(ZoomVideoSDKAudioHelper audioHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onSessionNeedPassword(ZoomVideoSDKPasswordHandler handler) {

    }

    @Override
    public void onSessionPasswordWrong(ZoomVideoSDKPasswordHandler handler) {

    }

    @Override
    public void onMixedAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData) {

    }

    @Override
    public void onOneWayAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData, ZoomVideoSDKUser user) {

    }

    @Override
    public void onShareAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData) {

    }

    @Override
    public void onCommandReceived(ZoomVideoSDKUser sender, String strCmd) {

    }

    @Override
    public void onCommandChannelConnectResult(boolean isSuccess) {

    }

    @Override
    public void onCloudRecordingStatus(ZoomVideoSDKRecordingStatus status, ZoomVideoSDKRecordingConsentHandler handler) {

    }

    @Override
    public void onHostAskUnmute() {

    }

    @Override
    public void onInviteByPhoneStatus(ZoomVideoSDKPhoneStatus status, ZoomVideoSDKPhoneFailedReason reason) {

    }

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKRawDataPipe videoPipe) {

    }

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKVideoCanvas canvas) {

    }

    @Override
    public void onLiveTranscriptionStatus(ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionStatus status) {

    }

    @Override
    public void onLiveTranscriptionMsgReceived(String ltMsg, ZoomVideoSDKUser pUser, ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionOperationType type) {

    }

    @Override
    public void onLiveTranscriptionMsgError(ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage spokenLanguage, ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage transcriptLanguage) {

    }

    @Override
    public void onProxySettingNotification(ZoomVideoSDKProxySettingHandler handler) {

    }

    @Override
    public void onSSLCertVerifiedFailNotification(ZoomVideoSDKSSLCertificateInfo info) {

    }

    @Override
    public void onCameraControlRequestResult(ZoomVideoSDKUser user, boolean isApproved) {

    }

    @Override
    public void onUserVideoNetworkStatusChanged(ZoomVideoSDKNetworkStatus status, ZoomVideoSDKUser user) {

    }

    @Override
    public void onUserRecordingConsent(ZoomVideoSDKUser user) {

    }
}
