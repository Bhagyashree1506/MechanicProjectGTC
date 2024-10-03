package com.geartocare.mechanicP.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.geartocare.mechanicP.R;

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context) {
        super(context);

        WindowManager.LayoutParams wlmp = getWindow().getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);

        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(
                R.layout.custom_progress, null);
        setContentView(view);
    }
}
