package com.imgurtoppicks.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import com.imgurtoppicks.domain.RxSchedulers
import com.imgurtoppicks.domain.search.SearchParams
import com.imgurtoppicks.domain.search.SearchTopPicsUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchTopPicsViewModel
@Inject constructor(
    private val schedulers: RxSchedulers,
    private val searchUseCase: SearchTopPicsUseCase
) : ViewModel() {

    private var state = SearchTopPicsViewState()
    private val statePublisher = BehaviorSubject.create<SearchTopPicsViewState>()
    private val queryPublisher = PublishSubject.create<SearchTopPicsUiEvent.Query>()
    private val togglePublisher = PublishSubject.create<SearchTopPicsUiEvent.Toggle>()
    private val destroyed = PublishSubject.create<Unit>()

    private val uiEvents: Observable<SearchTopPicsUiEvent> =
        Observable.merge(queryPublisher, togglePublisher)

    private val toggleTransformer =
        ObservableTransformer<SearchTopPicsUiEvent.Toggle, StateReducer<SearchTopPicsViewState>> { toggleEvent ->
            toggleEvent.map { event -> reduceWith(event.isToggled) }
        }

    private val queryTransformer =
        ObservableTransformer<SearchTopPicsUiEvent.Query, StateReducer<SearchTopPicsViewState>> { queryEvent ->
            queryEvent
                .filter { event -> event.query.isNotEmpty() }
                .switchIfEmpty { Observable.empty<Any>() }
                .map { SearchParams(it.query) }
                .switchMap { params -> searchUseCase.execute(params) }
                .doOnEach { Log.d("VM", "UseCase results $it") }
                .map { status -> reduceWith(status) }
        }

    private val uiEventToStateTransformer =
        ObservableTransformer<SearchTopPicsUiEvent, StateReducer<SearchTopPicsViewState>> { events ->
            events.publish { event ->
                Observable.merge(
                    event.ofType(SearchTopPicsUiEvent.Query::class.java).compose(queryTransformer),
                    event.ofType(SearchTopPicsUiEvent.Toggle::class.java).compose(toggleTransformer)
                )
            }
        }

    init {
        uiEvents
            .compose(uiEventToStateTransformer)
            .scan(state) { state, reduce -> reduce(state)}
            .takeUntil(destroyed)
            .subscribe(statePublisher)
    }

    fun viewStateObservable(): Observable<SearchTopPicsViewState> = statePublisher
        .doOnNext { state = it }
        .startWith(state)
        .distinctUntilChanged()
        .hide()

    fun observeSearchQueryChanges(queryObservable: Observable<CharSequence>) {
        queryObservable
            .debounce(250, TimeUnit.MILLISECONDS, schedulers.computation)
            .map { chars -> SearchTopPicsUiEvent.Query(chars.toString()) }
            .onErrorResumeNext(Observable.empty())
            .distinctUntilChanged()
            .subscribe(queryPublisher)
    }

    fun observeToggleChanges(toggleObservable: Observable<Boolean>) {
        toggleObservable
            .map { isToggled -> SearchTopPicsUiEvent.Toggle(isToggled) }
            .subscribe(togglePublisher)
    }

    fun clearedObservable(): Observable<Unit>  = destroyed.hide()

    override fun onCleared() {
        destroyed.onNext(Unit)
        super.onCleared()
    }
}

