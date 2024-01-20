package com.example.newsappmvvm.ui

import android.view.View
import android.webkit.WebViewClient
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.databinding.FragmentArticleWebviewBinding
import com.example.newsappmvvm.model.Article
import com.example.newsappmvvm.utils.BaseFragment
import com.example.newsappmvvm.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : BaseFragment<FragmentArticleWebviewBinding>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_article_webview
    }

    lateinit var viewModel : NewsViewModel

    override fun initUI() {
        viewModel = (activity as MainActivity).viewModel
        val article = arguments?.let {
           it.getSerializable("article") as Article
        }

        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            if(article?.url != null && !article.url.equals("https://removed.com", ignoreCase = true)){
                loadUrl(article.url)
                binding.saveArticleBtn.visibility = View.VISIBLE
            }else{
                binding.saveArticleBtn.visibility = View.INVISIBLE
            }
        }

        binding.saveArticleBtn.setOnClickListener {
            if(article != null){
                viewModel.saveArticle(article)
                Snackbar.make(it,"Article Saved Successfully!", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}