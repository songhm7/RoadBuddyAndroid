package com.hansung.roadbuddyandroid

import android.util.Log

object Logr {
    fun d(tag: String, msg: String) {
        val maxLogSize = 2500
        for (i in 0 until (msg.length + maxLogSize - 1) / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > msg.length) msg.length else end
            Log.d(tag, msg.substring(start, end))
        }
    }
}