package com.imgurtoppicks.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.imgurtoppicks.R
import com.imgurtoppicks.di.injector
import com.imgurtoppicks.ui.visibleOrGone
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.search_top_pics_fragment.*

class SearchTopPicsFragment : Fragment(), SearchTopPicsView {

    companion object {
        const val QUERY = "QUERY"
        fun newInstance() = SearchTopPicsFragment()
    }

    private val adapter = SearchTopPicsAdapter()
    private lateinit var toggle: ToggleButton
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private var query: String = ""

    // Must only be accessed once activity created
    private val viewModel: SearchTopPicsViewModel by lazy {
        ViewModelProvider(this, activity!!.injector.viewModelFactory())
            .get(SearchTopPicsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.search_top_pics_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toggle = findViewById(R.id.toggle_btn)
            recyclerView = findViewById(R.id.recycler)
            recyclerView.layoutManager = LinearLayoutManager(view.context)
            recyclerView.adapter = adapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "OnStart")
        viewModel.observeToggleChanges(toggle.checkedChanges())
        viewModel.viewStateObservable()
            .takeUntil(createOnStopObservable())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ state ->
                state.renderWith(this)
            }) { error ->
                displayError(error)
                Log.e("Fragment", error.toString())
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search_item)
        searchView = searchItem.actionView as SearchView
        if (query.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(query, false)
            searchView.clearFocus()
        }
        viewModel.observeSearchQueryChanges(searchView.queryTextChanges().skipInitialValue())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun displayResults(results: List<SearchGalleryListItem>) {
        adapter.refreshItems(results)
    }

    override fun displayLoading(isLoading: Boolean) {
        loading_layout.visibleOrGone(isLoading)
    }

    override fun displayEmpty(isEmpty: Boolean) {
        clear_search_layout.visibleOrGone(isEmpty)
    }

    override fun displayNoResults(noResults: Boolean) {
        no_results_layout.visibleOrGone(noResults)
    }

    override fun displayError(error: Throwable) {
        activity?.let {
            Snackbar.make(container_layout, R.string.error, Snackbar.LENGTH_LONG).apply {
                setTextColor(ContextCompat.getColor(it, R.color.colorAccent))
                show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(QUERY, searchView.query.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        query = savedInstanceState?.getString(QUERY) ?: ""
    }
}

fun Fragment.createOnStopObservable(): Observable<Unit> {
    return Observable.create { emitter ->
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            emitter.onNext(Unit)
            emitter.onComplete()
            return@create
        }
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun emitLifeCycleStopped() {
                Log.d("Lifecycle", "OnStop")
                if (!emitter.isDisposed) {
                    emitter.onNext(Unit)
                    emitter.onComplete()
                }
            }
        })
    }
}