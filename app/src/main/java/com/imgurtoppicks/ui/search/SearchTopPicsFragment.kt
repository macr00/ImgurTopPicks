package com.imgurtoppicks.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.imgurtoppicks.R
import com.imgurtoppicks.di.injector
import com.imgurtoppicks.ui.visibleOrGone
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.search_top_pics_fragment.*

class SearchTopPicsFragment : Fragment(), SearchTopPicsView {

    companion object {
        fun newInstance() = SearchTopPicsFragment()
    }

    private val adapter = SearchTopPicsAdapter()
    private lateinit var toggle: ToggleButton
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    // Must only be accessed once activity created
    private val viewModel: SearchTopPicsViewModel by lazy {
        ViewModelProvider(
            this,
            activity!!.injector.viewModelFactory()
        ).get(SearchTopPicsViewModel::class.java)
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

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.observeToggleChanges(toggle.checkedChanges())
        viewModel.viewStateObservable()
            .doOnEach { Log.d("Fragment", it.toString()) }
            .takeUntil(viewModel.clearedObservable())
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
        searchView = menu.findItem(R.id.search_item).actionView as SearchView
        viewModel.observeSearchQueryChanges(searchView.queryTextChanges().skipInitialValue())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun displayResults(results: List<SearchGalleryListItem>) {
        Log.d("Display", results.toString())
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
        Snackbar.make(container_layout, R.string.error, Snackbar.LENGTH_LONG).show()
    }
}
