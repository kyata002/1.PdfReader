package com.common.control.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.common.control.R;
import com.common.control.interfaces.PermissionCallback;
import com.common.control.utils.PermissionUtils;


public class PermissionNormalDialog extends AppCompatActivity {

    private static final String NOT_SHOW_AGAIN = "not_show_again";

    private boolean isStartSettingPermission;
    private String[] permissions;


    public static void show(Activity context, String... permissions) {
        Intent intent = new Intent(context, PermissionNormalDialog.class);
        intent.putExtra("data", permissions);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_normal);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isStartSettingPermission) {
            if (PermissionUtils.permissionGranted(this, permissions) && PermissionUtils.instance().getPermissionCallback() != null) {
                PermissionUtils.instance().getPermissionCallback().onPermissionGranted();
                finish();
                this.isStartSettingPermission = false;
            }
        }
    }

    private void initViews() {
        permissions = getIntent().getStringArrayExtra("data");
        PermissionUtils.instance().requestPermission(this, permissions);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionCallback callBack = PermissionUtils.instance().getPermissionCallback();
        if (callBack != null) {
            if (PermissionUtils.permissionGranted(this, permissions)) {
                callBack.onPermissionGranted();
            } else {
                callBack.onPermissionDenied();
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!PermissionUtils.permissionGranted(this, permissions)) {
            try {
                PermissionUtils.instance().getPermissionCallback().onPermissionAborted();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PermissionUtils.instance().setPermissionCallback(null);
    }
}
