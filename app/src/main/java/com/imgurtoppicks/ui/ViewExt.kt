package com.imgurtoppicks.ui

import android.view.View

fun View.visibleOrGone(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}