package com.pdfreaderdreamw.pdfreader.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreatePdfDialog extends Dialog {

    private EditText edtName;
    private OnActionCallback callback;

    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }


    public CreatePdfDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_create_pdf);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edtName = findViewById(R.id.edt_name);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy_HHmmss");

        String pdfName = "pdf_" + formatter.format(new Date());
        edtName.setText(pdfName);
/*//        String name = edtName.getText().toString();
//        if (name.contains(".pdf") || name.contains(".PDF")) {
//            tag = ".pdf";
//        }*/
        findViewById(R.id.bt_create).setOnClickListener(v -> {
            if (!edtName.getText().toString().trim().isEmpty()) {
                callback.callback(null, edtName.getText().toString());
                dismiss();
            } else {
                edtName.setError(getContext().getString(R.string.empty));
            }
        });
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());

    }

}
