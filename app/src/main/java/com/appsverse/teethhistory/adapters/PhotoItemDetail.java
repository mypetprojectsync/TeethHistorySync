package com.appsverse.teethhistory.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class PhotoItemDetail extends ItemDetailsLookup.ItemDetails {
    private final int adapterPosition;
    private final long selectionKey;

    public PhotoItemDetail(int adapterPosition, long selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
