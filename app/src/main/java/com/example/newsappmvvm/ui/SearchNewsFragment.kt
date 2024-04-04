package com.example.newsappmvvm.ui

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.PAGE_QUERY_SIZE
import com.example.newsappmvvm.R
import com.example.newsappmvvm.SEARCH_TIME_DELAY
import com.example.newsappmvvm.adapter.NewsAdapter
import com.example.newsappmvvm.databinding.FragmentSearchNewsBinding
import com.example.newsappmvvm.utils.BaseFragment
import com.example.newsappmvvm.utils.Resource
import com.example.newsappmvvm.viewmodel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchNewsFragment : BaseFragment<FragmentSearchNewsBinding>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_search_news
    }

    lateinit var searchNewsAdapter : NewsAdapter
    lateinit var viewModel : NewsViewModel

    override fun initUI() {
        viewModel = (activity as MainActivity).viewModel
        initSearchNewsAdapter()
        initSearch()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchNewsData.collectLatest { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let {newsResponse ->
                                searchNewsAdapter.differ.submitList(newsResponse.articles.toList())
                                val totalPages = newsResponse.totalResults / PAGE_QUERY_SIZE + 2 // added one because the quotient is rounded off then added 1 again because the last page is empty
                                isLastPage = viewModel.searchNewsPageNum == totalPages
                                if(isLastPage){
                                    binding.searchRv.setPadding(0,0,0,0)
                                }

                                if(newsResponse.articles.toList().isEmpty()){
                                    binding.emptySearchTxt.visibility = View.VISIBLE
                                }else{
                                    binding.emptySearchTxt.visibility = View.INVISIBLE
                                }
                            }
                        }
                        is Resource.Error -> {
                            hideProgressBar()
                            response.message?.let{ message ->
                                Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is Resource.Loading -> {
                            if(viewModel.isInitialLoad.value){
                                viewModel.isInitialLoad.value = false
                            }else{
                                showProgressBar()
                            }
                        }

                    }
                }
            }
        }

        /*viewModel.searchNewsData.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        searchNewsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / PAGE_QUERY_SIZE + 2 // added one because the quotient is rounded off then added 1 again because the last page is empty
                        isLastPage = viewModel.searchNewsPageNum == totalPages
                        if(isLastPage){
                            binding.searchRv.setPadding(0,0,0,0)
                        }

                        if(newsResponse.articles.toList().isEmpty()){
                            binding.emptySearchTxt.visibility = View.VISIBLE
                        }else{
                            binding.emptySearchTxt.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let{ message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }

            }

        })*/
    }

    private fun hideProgressBar(){
        binding.progressBarSearch.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.progressBarSearch.visibility = View.VISIBLE
        isLoading = true
    }

    private fun initSearchNewsAdapter () {
        searchNewsAdapter = NewsAdapter()
        binding.searchRv.apply {
            adapter = searchNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onNewsScrollListener)
        }

        searchNewsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun initSearch() {
        var job : Job? = null
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    query?.let {
                        if(query.toString().isNotEmpty()){
                            viewModel.searchNews(query.toString())
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    query?.let {
                        if(query.toString().isNotEmpty()){
                            viewModel.searchNews(query.toString())
                        }
                    }
                }
                return false
            }

        })
    }

    // handle pagination
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val onNewsScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.searchRv.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_QUERY_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(binding.searchView.query.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}
