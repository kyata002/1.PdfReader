package com.helper.permission;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.permission.managerstorage.R;

public class PlPermissionDialog extends AppCompatActivity implements View.OnClickListener {

    private static final String NOT_SHOW_AGAIN = "not_show_again";

    private boolean isStartSettingPermission;

    public static void show(Activity context) {
        context.startActivity(new Intent(context, PlPermissionDialog.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pl_activity_permission);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isStartSettingPermission) {
            if (PlPermissionUtils.isStoragePermissionGranted(this) && PlPermissionUtils.instance().getPermissionCallback() != null) {
                PlPermissionUtils.instance().getPermissionCallback().onPermissionGranted();
                finish();
                this.isStartSettingPermission = false;
            }
        }
    }

    private void initViews() {
        findViewById(R.id.bt_deny).setOnClickListener(this);
        findViewById(R.id.bt_grant).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_deny) {
            finish();
            return;
        }
        if (id == R.id.bt_grant) {
            PlPermissionUtils.instance().requestPermission(this);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PlPermissionUtils.instance().onActivityResult(this, requestCode);
        if (PlPermissionUtils.isStoragePermissionGranted(this)) {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PlPermissionUtils.RQC_REQUEST_PERMISSION_ANDROID_BELOW) {
            return;
        }

        PlPermissionPrefData instance = PlPermissionPrefData.instance(this);
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                continue;
            }
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                if (permission.equals(WRITE_EXTERNAL_STORAGE) && PlPermissionUtils.instance().getPermissionCallback() != null) {
                    PlPermissionUtils.instance().getPermissionCallback().onPermissionGranted();
                    finish();
                    return;
                }
            } else if (permission.equals(WRITE_EXTERNAL_STORAGE)) {
                instance.putBoolean(NOT_SHOW_AGAIN, true);
            }
        }

        if (instance.getBoolean(NOT_SHOW_AGAIN, false)) {
            PlPermissionUtils.showDialogPermission(this);
            isStartSettingPermission = true;
            return;
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!PlPermissionUtils.isStoragePermissionGranted(this)) {
            try {
                PlPermissionUtils.instance().getPermissionCallback().onPermissionAborted();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
