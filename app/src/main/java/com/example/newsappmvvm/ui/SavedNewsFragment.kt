package com.example.newsappmvvm.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.adapter.NewsAdapter
import com.example.newsappmvvm.databinding.FragmentSavedNewsBinding
import com.example.newsappmvvm.utils.BaseFragment
import com.example.newsappmvvm.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : BaseFragment<FragmentSavedNewsBinding>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_saved_news
    }

    lateinit var viewModel : NewsViewModel
    lateinit var savedNewsAdapter : NewsAdapter

    override fun initUI() {
        viewModel = (activity as MainActivity).viewModel
        initSavedNewsAdapter()


        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer { articles ->
            savedNewsAdapter.differ.submitList(articles)
            if(articles.isEmpty()){
                binding.emptyNewsTxt.visibility = View.VISIBLE
            }else{
                binding.emptyNewsTxt.visibility = View.INVISIBLE
            }

        })
    }

    private fun initSavedNewsAdapter () {
        savedNewsAdapter = NewsAdapter()
        binding.savednewsRv.apply {
            adapter = savedNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        savedNewsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = savedNewsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(binding.root,"Successfully Deleted Article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.savednewsRv)
    }
}