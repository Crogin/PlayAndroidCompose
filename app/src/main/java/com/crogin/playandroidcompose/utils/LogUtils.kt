package com.btpj.lib_base.utils

import android.util.Log

object LogUtil {
    private const val TAG = "LogUtil"

    fun d(msg: String) {
        Log.d(TAG, msg)
    }
    fun e(msg: String) {
        Log.e(TAG, msg)
    }
    fun i(msg: String) {
        Log.i(TAG, msg)
    }
    fun v(msg: String) {
        Log.v(TAG, msg)
    }
}