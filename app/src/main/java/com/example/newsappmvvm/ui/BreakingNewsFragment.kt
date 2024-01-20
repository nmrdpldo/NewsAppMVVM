package com.example.newsappmvvm.ui

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.PAGE_QUERY_SIZE
import com.example.newsappmvvm.R
import com.example.newsappmvvm.adapter.NewsAdapter
import com.example.newsappmvvm.databinding.FragmentBreakingNewsBinding
import com.example.newsappmvvm.utils.BaseFragment
import com.example.newsappmvvm.utils.Resource
import com.example.newsappmvvm.viewmodel.NewsViewModel

class BreakingNewsFragment : BaseFragment<FragmentBreakingNewsBinding>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_breaking_news
    }

    lateinit var breakingNewsAdapter : NewsAdapter
    lateinit var viewModel : NewsViewModel

    override fun initUI() {
        viewModel = (activity as MainActivity).viewModel
        initBreakingNewsAdapter()

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        breakingNewsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / PAGE_QUERY_SIZE + 2 // added one because the quotient is rounded off then added 1 again because the last page is empty
                        isLastPage = viewModel.breakingNewsPageNum == totalPages
                        if(isLastPage){
                            binding.rvBreakingNews.setPadding(0,0,0,0)
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

        })

    }

    private fun hideProgressBar(){
        binding.progressBarBreakingNews.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.progressBarBreakingNews.visibility = View.VISIBLE
        isLoading = true
    }

    private fun initBreakingNewsAdapter () {
        breakingNewsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = breakingNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onNewsScrollListener)
        }

        breakingNewsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    // handle pagination
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val onNewsScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvBreakingNews.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_QUERY_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
}