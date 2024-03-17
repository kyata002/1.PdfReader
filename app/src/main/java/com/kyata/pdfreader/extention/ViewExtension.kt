package com.documentmaster.documentscan.extention

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.google.firebase.analytics.FirebaseAnalytics

fun View.disableFocus() {
    this.isFocusableInTouchMode = false
    this.isFocusable = false
}

fun View.enableFocus() {
    this.isFocusableInTouchMode = true
    this.isFocusable = true
}

//fun View.showKeyboard(context: Context) {
//    this.requestFocus()
//    val imm: InputMethodManager =
//        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//}

fun Context.showKeyboard(view: View) {
    view.requestFocus()
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

//
fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE

}

fun Context.logEvent(eventName: String) {
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    mFirebaseAnalytics.logEvent(eventName, bundle)
}

