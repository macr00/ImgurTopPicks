package com.imgurtoppicks.app

import android.app.Application
import com.imgurtoppicks.di.AppComponent
import com.imgurtoppicks.di.DaggerAppComponent
import com.imgurtoppicks.di.DaggerComponentProvider

class ImgurTopPicksApp : Application(), DaggerComponentProvider {

    override val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appContext(applicationContext)
            .build()
    }

}