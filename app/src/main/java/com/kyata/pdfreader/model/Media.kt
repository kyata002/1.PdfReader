package com.kyata.pdfreader.model

import android.graphics.Bitmap

class Media(
        val id: Long,
        val data: String,
        val image: String,
        val name: String,
        val date: String,
        val size: Long,
        val type: String,
        var thumb: Bitmap?
) {
    var isChecked = false

}