package com.imgurtoppicks.di

import com.imgurtoppicks.app.RxSchedulersImpl
import com.imgurtoppicks.domain.RxSchedulers
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    abstract fun bindSchedulers(impl: RxSchedulersImpl): RxSchedulers
}