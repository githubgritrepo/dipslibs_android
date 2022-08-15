package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

public class frag_data_pekerjaan extends Fragment {
    private Context context;
    private Button btnProses, btnKembali;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private byte[] KTP, NPWP, TTD;
    private String[] jumlahKaryawan_, pekerjaan_, bidangUsaha_, jabatan_;
    private AutoCompleteTextView jumlahKaryawan, pekerjaan, bidangUsaha, jabatan;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_data_pekerjaan, container, false);
        btnProses = view.findViewById(R.id.btnProses_Pekerjaan);
        btnKembali = view.findViewById(R.id.btnKembali_Pekerjaan);
        jumlahKaryawan = view.findViewById(R.id.act_JumlahKaryawan);
        pekerjaan = view.findViewById(R.id.act_pekerjaan);
        bidangUsaha = view.findViewById(R.id.act_bidangUsaha);
        jabatan = view.findViewById(R.id.act_jabatan);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        jumlahKaryawan_ = new String[]{"< 25 Orang", "25 - 50 Orang", "> 50 Orang"};
        pekerjaan_ = new String[]{"Pegawai Negeri","TNI/POLRI","Wiraswasta","Pegawai Swasta", "Profesional", "Pensiunan", "Ibu Rumah Tangga", "Pelajar/Mahasiswa", "Dosen/Guru"};
        bidangUsaha_ = new String[]{"Kontruksi", "Real Estate/Property", "Kesehatan", "Perkebunan", "Keuangan/Bank/Non Bank", "Transportasi", "Industri", "Pertambangan", "Perdangan"};
        jabatan_ = new String[]{"Komisaris", "Direktur", "Manajer", "Kepala Divisi", "Kepala Bagian", "Kepala Seksi", "Staff"};

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new frag_data_keuangan();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new frag_address_opening();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

        ArrayAdapter<String> jk = new ArrayAdapter<String>(context,R.layout.list_item, jumlahKaryawan_);
        jumlahKaryawan.setAdapter(jk);

        ArrayAdapter<String> pkj = new ArrayAdapter<String>(context,R.layout.list_item, pekerjaan_);
        pekerjaan.setAdapter(pkj);

        ArrayAdapter<String> bdgush = new ArrayAdapter<String>(context,R.layout.list_item, bidangUsaha_);
        bidangUsaha.setAdapter(bdgush);

        ArrayAdapter<String> jbtn = new ArrayAdapter<String>(context,R.layout.list_item, jabatan_);
        jabatan.setAdapter(jbtn);
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
