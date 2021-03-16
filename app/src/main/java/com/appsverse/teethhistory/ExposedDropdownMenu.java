package com.appsverse.teethhistory;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class ExposedDropdownMenu extends MaterialAutoCompleteTextView {
    public ExposedDropdownMenu(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    //fix bug with lost list when orientation changed
    @Override
    public boolean getFreezesText() {
        return false;
    }
}
