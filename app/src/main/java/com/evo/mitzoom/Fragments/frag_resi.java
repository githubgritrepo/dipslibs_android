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

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class frag_resi extends Fragment {
    private String [] rekening_sumber, rekening_sumber2;
    private Button btnOK;
    private SessionManager sessions;
    private Context context;
    private TextView tv_NoFormulir, tv_NoReferensi, tv_Tanggal, tv_Jam, tv_RekeningSumberDana, tv_NamaPemilikRekening, tv_JenisLayanan, tv_BankPenerima, tv_NamaPenerima, tv_PenerimaManfaat, tv_JenisPenduduk, tv_Berita, tv_BiayaAdmin, tv_NominalTransaksi;
    private String Jam,TanggalSekarang,RekeningSumber, NamaBank, RekPenerima, NamaPenerima, Nominal, JenisLayanan, PenerimaManfaat,JenisPenduduk,Berita;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_resi, container, false);
        tv_NoFormulir = view.findViewById(R.id.No_Formulir);
        tv_NoReferensi = view.findViewById(R.id.No_Referensi);
        tv_Tanggal = view.findViewById(R.id.Tanggal);
        tv_Jam = view.findViewById(R.id.Jam);
        tv_RekeningSumberDana = view.findViewById(R.id.Rekening_Sumber_Dana);
        tv_NamaPemilikRekening = view.findViewById(R.id.Nama_Pemilik_Rekening);
        tv_JenisLayanan = view.findViewById(R.id.Jenis_Layanan);
        tv_BankPenerima = view.findViewById(R.id.Bank_Penerima);
        tv_NamaPenerima = view.findViewById(R.id.Nama_Penerima);
        tv_PenerimaManfaat = view.findViewById(R.id.Penerima_Manfaat);
        tv_JenisPenduduk = view.findViewById(R.id.Jenis_Penduduk);
        tv_Berita = view.findViewById(R.id.Berita_Resi);
        tv_BiayaAdmin = view.findViewById(R.id.BiayaAdmin);
        tv_NominalTransaksi = view.findViewById(R.id.Nominal_Transaksi);
        btnOK = view.findViewById(R.id.btn_done_resi);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle terima = getArguments();
        RekeningSumber = terima.getString("rekeningSumber");
        JenisLayanan = terima.getString("jenisLayanan");
        NamaBank = terima.getString("namaBank");
        NamaPenerima = terima.getString("namaPenerima");
        PenerimaManfaat = terima.getString("penerimaManfaat");
        JenisPenduduk = terima.getString("jenisPenduduk");
        Berita = terima.getString("berita");
        Nominal = terima.getString("nominal");
        RekPenerima = terima.getString("rekPenerima");

        rekening_sumber = RekeningSumber.split("\n");
        String NamaRekening = rekening_sumber[0];
        String Rekening_dan_Pemilik = rekening_sumber[1];
        String saldo = rekening_sumber[2];

        rekening_sumber2 = Rekening_dan_Pemilik.split(" - ");
        String rekening = rekening_sumber2[0];
        String pemilik_rekening = rekening_sumber2[1];
        Log.d("cek","Rekening Sumber Dana = "+rekening);
        Log.d("cek","Nama Pemilik Rekening = "+pemilik_rekening);

        Calendar c = Calendar.getInstance();
        System.out.println("Current Time =>"+c.getTime());
        SimpleDateFormat tanggal_sekarang = new SimpleDateFormat("dd MMMM yyy");
        SimpleDateFormat jam_sekarang = new SimpleDateFormat("HH:mm");
        TanggalSekarang = tanggal_sekarang.format(c.getTime());
        Jam = jam_sekarang.format(c.getTime());

        tv_Tanggal.setText(TanggalSekarang);
        tv_Jam.setText(Jam+" WIB");
        tv_RekeningSumberDana.setText(rekening);
        tv_NamaPemilikRekening.setText(pemilik_rekening);
        tv_JenisLayanan.setText(JenisLayanan);
        tv_BankPenerima.setText(NamaBank);
        tv_PenerimaManfaat.setText(PenerimaManfaat);
        tv_JenisPenduduk.setText(JenisPenduduk);
        tv_Berita.setText(Berita);
        tv_BiayaAdmin.setText("Rp2.500");
        tv_NominalTransaksi.setText("Rp"+Nominal);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessions.saveIsCust(false);
                getFragmentPage(new frag_portfolio());
            }
        });

    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
