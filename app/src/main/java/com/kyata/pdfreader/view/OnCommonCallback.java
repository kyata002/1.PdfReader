package com.kyata.pdfreader.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.widget.SeekBar;

abstract public class OnCommonCallback implements Animation.AnimationListener, TextWatcher, SeekBar.OnSeekBarChangeListener {
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
