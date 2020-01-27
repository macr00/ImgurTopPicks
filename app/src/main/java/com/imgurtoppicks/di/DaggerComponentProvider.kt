package com.imgurtoppicks.di

import android.app.Activity
import com.imgurtoppicks.app.ImgurTopPicksApp

interface DaggerComponentProvider {

    val appComponent: AppComponent
}

val Activity.injector get() = (application as ImgurTopPicksApp).appComponent