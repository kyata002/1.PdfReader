package com.rate.control;

public interface OnCallback {

    void onMaybeLater();

    void onSubmit(String review);

    void onRate();

}
