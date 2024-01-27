package com.helper.permission;

public interface PlPermissionCallback {
    void onPermissionNotGranted();

    void onPermissionGranted();

    void onPermissionDenied();

    void onPermissionAborted();

}
