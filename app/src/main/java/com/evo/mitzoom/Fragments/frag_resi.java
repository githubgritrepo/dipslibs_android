package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_resi extends Fragment {
    private String [] rekening_sumber, rekening_sumber2;
    private Button btnOK;
    private SessionManager sessions;
    private Context context;
    private String idDips;
    private String dataRTGS;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private ArrayList<Integer> layouts = new ArrayList<Integer>();
    private ArrayList<String> dataAccount = new ArrayList<String>();
    private ArrayList<String> dataNoForm = new ArrayList<String>();
    private ArrayList<String> dataBankName = new ArrayList<String>();
    private ArrayList<String> dataAccountReceive = new ArrayList<>();
    private ArrayList<String> dataNameReceive = new ArrayList<>();
    private ArrayList<String> dataNominal = new ArrayList<>();
    private ArrayList<String> dataService = new ArrayList<>();
    private ArrayList<String> dataBenefit = new ArrayList<>();
    private ArrayList<String> dataPopulation = new ArrayList<>();
    private ArrayList<String> dataNews = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        dataRTGS = sessions.getRTGS();
        Log.d("CEK","dataRTGS : "+dataRTGS);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_resi, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btnOK = view.findViewById(R.id.btn_done_resi);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (dataRTGS != null) {
            savedRTGS();
        }
        idDips = sessions.getKEY_IdDips();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sessions.saveIsCust(false);
                Mirroring(true,1,1);
                getFragmentPage(new frag_portfolio());
            }
        });

    }

    private void initPager() {
        if (myViewPagerAdapter == null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mirroringPagerRTGS(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        circleIndicator.setViewPager(pager);
    }

    private void savedRTGS() {
        Log.d("CEK","MASUK savedRTGS");
        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            int idx = 1;
            for (int i = 0; i < len; i++) {
                String dataArr = jsArr.get(i).toString();
                JSONObject dataJs = new JSONObject(dataArr);
                String idForm = dataJs.getString("idForm");
                String sourceAccount = dataJs.getString("sourceAccount");
                String sourceBank = dataJs.getString("sourceBank");
                String sourceTypeService = dataJs.getString("sourceTypeService");
                String sourceBenefit = dataJs.getString("sourceBenefit");
                String sourcePopulation = dataJs.getString("sourcePopulation");
                String rek_penerima = dataJs.getString("rek_penerima");
                String nama_penerima = dataJs.getString("nama_penerima");
                String nominal = dataJs.getString("nominal");
                String berita = dataJs.getString("berita");

                layouts.add(R.layout.content_frag_resi);

                dataNoForm.add(idForm);
                dataAccount.add(sourceAccount);
                dataBankName.add(sourceBank);
                dataAccountReceive.add(rek_penerima);
                dataNameReceive.add(nama_penerima);
                dataNominal.add(nominal);
                dataService.add(sourceTypeService);
                dataBenefit.add(sourceBenefit);
                dataPopulation.add(sourcePopulation);
                dataNews.add(berita);

                initPager();

                if (i == 0) {
                    Mirroring(false,idx,len);
                }

                idx++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void Mirroring(boolean bool, int page, int pageAll){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(page);
            jsonArray.put(pageAll);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",20);
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

    private void mirroringPagerRTGS(int position) {
        int lenL = layouts.size();
        int idx = position + 1;
        try {
            String noFormulir = dataNoForm.get(position);
            if (dataAccount.size() == 0 || (dataAccount.size() == position) || (dataAccount.size() > 0 && dataAccount.get(position).isEmpty())) {
                dataAccount.add("");
            }
            if (dataBankName.size() == 0 || (dataBankName.size() == position) || (dataBankName.size() > 0 && dataBankName.get(position).isEmpty())) {
                dataBankName.add("");
            }
            if (dataAccountReceive.size() == 0 || (dataAccountReceive.size() == position) || (dataAccountReceive.size() > 0 && dataAccountReceive.get(position).isEmpty())) {
                dataAccountReceive.add("");
            }
            if (dataNameReceive.size() == 0 || (dataNameReceive.size() == position) || (dataNameReceive.size() > 0 && dataNameReceive.get(position).isEmpty())) {
                dataNameReceive.add("");
            }
            if (dataNominal.size() == 0 || (dataNominal.size() == position) || (dataNominal.size() > 0 && dataNominal.get(position).isEmpty())) {
                dataNominal.add("0");
            }
            if (dataService.size() == 0 || (dataService.size() == position) || (dataService.size() > 0 && dataService.get(position).isEmpty())) {
                dataService.add("");
            }
            if (dataBenefit.size() == 0 || (dataBenefit.size() == position) || (dataBenefit.size() > 0 && dataBenefit.get(position).isEmpty())) {
                dataBenefit.add("");
            }
            if (dataPopulation.size() == 0 || (dataPopulation.size() == position) || (dataPopulation.size() > 0 && dataPopulation.get(position).isEmpty())) {
                dataPopulation.add("");
            }
            if (dataNews.size() == 0 || (dataNews.size() == position) || (dataNews.size() > 0 && dataNews.get(position).isEmpty())) {
                dataNews.add("");
            }

            /*String SourceAccount = dataAccount.get(position);
            String SumberBank = dataBankName.get(position);
            String JenisLayanan = dataService.get(position);
            String posSourceBenefit = dataBenefit.get(position);
            String posSourcePopulation = dataPopulation.get(position);
            String rek_penerima = dataAccountReceive.get(position);
            String nama_penerima = dataNameReceive.get(position);
            String nominal = dataNominal.get(position);
            String berita = dataNews.get(position);
*/

            Mirroring(false,idx,lenL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts.get(position), container, false);
            container.addView(view);

            iniatilizeElement(view,position);

            return view;
        }

        private void iniatilizeElement(View view, int position) {
            int positionE = position;
            int indexMirror = position + 1;

            TextView tv_NoFormulir = view.findViewById(R.id.No_Formulir);
            TextView tv_NoReferensi = view.findViewById(R.id.No_Referensi);
            TextView tv_Tanggal = view.findViewById(R.id.Tanggal);
            TextView tv_Jam = view.findViewById(R.id.Jam);
            TextView tv_RekeningSumberDana = view.findViewById(R.id.Rekening_Sumber_Dana);
            TextView tv_NamaPemilikRekening = view.findViewById(R.id.Nama_Pemilik_Rekening);
            TextView tv_JenisLayanan = view.findViewById(R.id.Jenis_Layanan);
            TextView tv_BankPenerima = view.findViewById(R.id.Bank_Penerima);
            TextView tv_NamaPenerima = view.findViewById(R.id.Nama_Penerima);
            TextView tv_PenerimaManfaat = view.findViewById(R.id.Penerima_Manfaat);
            TextView tv_JenisPenduduk = view.findViewById(R.id.Jenis_Penduduk);
            TextView tv_Berita = view.findViewById(R.id.Berita_Resi);
            TextView tv_BiayaAdmin = view.findViewById(R.id.BiayaAdmin);
            TextView tv_NominalTransaksi = view.findViewById(R.id.Nominal_Transaksi);

            if (dataNoForm.size() > 0 ) {
                if (positionE < dataNoForm.size()) {
                    tv_NoFormulir.setText(dataNoForm.get(positionE));
                }
            }

            if (dataAccount.size() > 0 ) {
                if (positionE < dataAccount.size()) {
                    String dataRekening = dataAccount.get(positionE).toString();
                    String noRek = "";
                    String namaRek = "";
                    String valueRek = "";
                    if (dataRekening.indexOf("\n") > 0) {
                        String[] sp = dataRekening.split("\n");
                        noRek = sp[0];
                        namaRek = sp[1];
                        valueRek = sp[2];
                    }
                    tv_RekeningSumberDana.setText(noRek);
                    tv_NamaPemilikRekening.setText(namaRek);
                }
            }

            Calendar c = Calendar.getInstance();
            System.out.println("Current Time =>"+c.getTime());
            SimpleDateFormat tanggal_sekarang = new SimpleDateFormat("dd MMMM yyy");
            SimpleDateFormat jam_sekarang = new SimpleDateFormat("HH:mm");
            String TanggalSekarang = tanggal_sekarang.format(c.getTime());
            String Jam = jam_sekarang.format(c.getTime());

            tv_Tanggal.setText(TanggalSekarang);
            tv_Jam.setText(Jam+" WIB");

            if (dataBankName.size() > 0 ) {
                if (positionE < dataBankName.size()) {
                    String NamaBank = dataBankName.get(positionE);
                    String RekPenerima = dataAccountReceive.get(positionE);
                    String NamaPenerima = dataNameReceive.get(positionE);

                    tv_BankPenerima.setText(NamaBank);
                    tv_NamaPenerima.setText(NamaPenerima);
                }
            }

            if (dataService.size() > 0 ) {
                if (positionE < dataService.size()) {
                    tv_JenisLayanan.setText(dataService.get(positionE));
                }
            }

            if (dataBenefit.size() > 0 ) {
                if (positionE < dataBenefit.size()) {
                    tv_PenerimaManfaat.setText(dataBenefit.get(positionE));
                }
            }

            if (dataPopulation.size() > 0 ) {
                if (positionE < dataPopulation.size()) {
                    tv_JenisPenduduk.setText(dataPopulation.get(positionE));
                }
            }

            if (dataNews.size() > 0 ) {
                if (positionE < dataNews.size()) {
                    tv_Berita.setText(dataNews.get(positionE));
                }
            }

            tv_BiayaAdmin.setText("Rp2.500");

            if (dataNominal.size() > 0 ) {
                if (positionE < dataNominal.size()) {
                    tv_NominalTransaksi.setText(getResources().getString(R.string.mata_uang)+" "+dataNominal.get(positionE));
                }
            }

        }

        @Override
        public int getCount() {
            return layouts.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
