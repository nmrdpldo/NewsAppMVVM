package com.example.newsappmvvm

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsappmvvm.databinding.ActivityMainBinding
import com.example.newsappmvvm.db.ArticleDatabase
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.viewmodel.NewsViewModel
import com.example.newsappmvvm.viewmodel.viewmodelprovider.NewsViewModelProvider
import com.example.mvvm.utils.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding> () {
    override fun getLayoutID(): Int {
        return R.layout.activity_main
    }

    lateinit var viewModel: NewsViewModel

    override fun initUI() {
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProvider = NewsViewModelProvider(application,newsRepository)
        viewModel = ViewModelProvider(this, viewModelProvider).get(NewsViewModel::class.java)

        findNavController(R.id.newsFragment).setGraph(R.navigation.news_nav_graph)
        binding.bottomNavigationView.setupWithNavController(supportFragmentManager.findFragmentById(R.id.newsFragment)!!.findNavController())

        findNavController(R.id.newsFragment).addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.breakingNewsFragment){
                binding.toolbarTitle.text = "Breaking News"
            }
            if(destination.id == R.id.savedNewsFragment){
                binding.toolbarTitle.text = "Saved news"
            }
            if(destination.id == R.id.searchNewsFragment){
                binding.toolbarTitle.text = "Search News"
            }
            if(destination.id == R.id.articleFragment){
                binding.toolbarTitle.text = ""
            }
        })

    }
}