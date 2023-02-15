package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.ItemServiceGridAdapter;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.util.ArrayList;

import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_new extends Fragment {

    private Context mContext;
    private RabbitMirroring rabbitMirroring;
    private SessionManager sessions;
    private boolean isSessionZoom = false;

    private RecyclerView rv_item;
    private ImageView btnBack;
    private ArrayList<ItemModel> dataItems;
    private ItemServiceGridAdapter gridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_service_new, container, false);

        btnBack = views.findViewById(R.id.btn_back4);
        rv_item = views.findViewById(R.id.rv_item);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addData();

        int spanCount = 3; // columns
        int spacing = 20; // 20px
        boolean includeEdge = true;
        rv_item.setLayoutManager(new GridLayoutManager(mContext,spanCount));
        rv_item.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        gridAdapter = new ItemServiceGridAdapter(dataItems,mContext,rabbitMirroring);
        rv_item.setAdapter(gridAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RabbitMirroring.MirroringSendEndpoint(14);
                getFragmentPage(new frag_portfolio_new());
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

    private void addData(){
        String titleAcc = getResources().getString(R.string.pembukaan_akun);
        titleAcc = titleAcc.replace("Rekening","Akun");
        dataItems = new ArrayList<>();
        dataItems.add(new ItemModel("6",getResources().getString(R.string.DORMANT_ACTIVATION),R.drawable.img_aktivasi_dormant)); //Sudah
        dataItems.add(new ItemModel("10",getResources().getString(R.string.IBMB_REGISTRATION),R.drawable.img_aktivasi_ibmb)); //Sudah
        dataItems.add(new ItemModel("12",getResources().getString(R.string.block_account),R.drawable.img_blokir_rekening)); //Sudah
        dataItems.add(new ItemModel("11",getResources().getString(R.string.CREDIT_FORM),R.drawable.img_formulir_kredit)); //Sudah
        dataItems.add(new ItemModel("1",getResources().getString(R.string.Formulir_Transfer),R.drawable.img_formulir_transfer)); //Sudah
        dataItems.add(new ItemModel("2",getResources().getString(R.string.CUSTOMER_COMPLAIN),R.drawable.img_keluhan_nasabah)); //Sudah
        dataItems.add(new ItemModel("0",titleAcc,R.drawable.img_opening_account)); //Sudah
        dataItems.add(new ItemModel("13",getString(R.string.account_closure),R.drawable.img_penutupan_rekening)); //Sudah
        dataItems.add(new ItemModel("5",getResources().getString(R.string.KYC_UPDATING),R.drawable.img_perbarui_data)); //Sudah
        dataItems.add(new ItemModel("14",getString(R.string.lbpinjaman),R.drawable.img_pinjaman)); //Sudah
        dataItems.add(new ItemModel("9",getResources().getString(R.string.POWER_OF_ATTORNEY),R.drawable.img_surat_kuasa)); //Sudah
        dataItems.add(new ItemModel("3",getResources().getString(R.string.INTERBANK_TRANSACTION),R.drawable.img_transaksi_antar_bank)); //Sudah
        dataItems.add(new ItemModel("8",getString(R.string.atm_transaction),R.drawable.img_kartu_atm)); //Sudah
        dataItems.add(new ItemModel("15",getString(R.string.ibmb_transaction),R.drawable.img_transaksi_ibmb)); //Sudah
        dataItems.add(new ItemModel("4",getString(R.string.inter_bank_transaction),R.drawable.img_transaksi_inter_bank)); //Sudah
        dataItems.add(new ItemModel("16",getString(R.string.credit_transactions),R.drawable.img_transaksi_kredit)); //Sudah
        dataItems.add(new ItemModel("17",getString(R.string.deposit_transactions),R.drawable.img_transaksi_titipan)); //Sudah
        dataItems.add(new ItemModel("7",getResources().getString(R.string.FOREX),R.drawable.img_transaksi_valas)); //Sudah
        dataItems.add(new ItemModel("18",getString(R.string.wealth_transactions),R.drawable.img_transaksi_wealth)); //Sudah
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    
}