package com.horoftech.ratatouille.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.horoftech.ratatouille.databinding.ProgressBinding;

import java.util.Objects;

public class OpenProgress {
    Context context;
    AlertDialog dialog;
    public OpenProgress(Context context){
        this.context = context;
    }


    public OpenProgress showProgress(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProgressBinding binding = ProgressBinding.inflate(inflater);
        builder.setView(binding.getRoot());
        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();


        return this;
    }

    public void dismissProgress(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

}