package com.evo.mitzoom.Adapter;

import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

public interface OnClickUploadImageListener {
    void onClickUpload(Uri intent, RecyclerView.ViewHolder viewHolderForAdapterPosition);
}
