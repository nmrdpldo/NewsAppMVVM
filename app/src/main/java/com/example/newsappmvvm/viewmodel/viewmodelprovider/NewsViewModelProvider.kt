package com.example.newsappmvvm.viewmodel.viewmodelprovider

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.viewmodel.NewsViewModel

class NewsViewModelProvider(val app : Application, val repository: NewsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,repository) as T
    }
}