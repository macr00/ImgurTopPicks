package com.imgurtoppicks.domain

import io.reactivex.Scheduler

interface RxSchedulers {

    val uiMain: Scheduler

    val io: Scheduler

    val computation: Scheduler
}