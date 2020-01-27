package com.imgurtoppicks.domain

import io.reactivex.Observable

interface UseCase<P : Params, T> {
    fun execute(params: P): Observable<T>
}

abstract class BaseUseCase<P : Params, T>(
    internal val schedulers: RxSchedulers
) : UseCase<P, T>