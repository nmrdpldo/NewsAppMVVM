package com.example.newsappmvvm.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappmvvm.NewsApplication
import com.example.newsappmvvm.model.Article
import com.example.newsappmvvm.model.NewsResponse
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    app : Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val _breakingNews : MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.Loading())
    val breakingNews = _breakingNews.asStateFlow()
    var breakingNewsPageNum = 1
    var breakingNewsResponse : NewsResponse? = null

    val _searchNewsData : MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.Loading())
    val searchNewsData = _searchNewsData.asStateFlow()
    val isInitialLoad = mutableStateOf(true)
    var searchNewsPageNum = 1
    var searchNewsResponse : NewsResponse? = null

    init {
        getBreakingNews()
    }

    fun getBreakingNews () = viewModelScope.launch {
        _breakingNews.value = Resource.Loading()
        try{
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews("us",breakingNewsPageNum)
                _breakingNews.value = handleBreakingNewsResponse(response)
            }else{
                _breakingNews.value = Resource.Error("No internet connection")
            }
        }catch (t : Throwable) {
            when (t) {
                is IOException -> _breakingNews.value = Resource.Error("Network Failure")
                else -> _breakingNews.value = Resource.Error("Conversion Failure")
            }
        }
    }

    fun searchNews (searchQuery : String) = viewModelScope.launch {
        _searchNewsData.value = Resource.Loading()
        try{
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery,searchNewsPageNum)
                _searchNewsData.value = handleSearchNewsResponse(response)
            }else{
                _searchNewsData.value = Resource.Error("No internet connection")
            }
        }catch (t : Throwable) {
            when (t) {
                is IOException -> _searchNewsData.value = Resource.Error("Network Failure")
                else -> _searchNewsData.value = Resource.Error("Conversion Failure")
            }
        }
    }

    fun handleBreakingNewsResponse (response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                breakingNewsPageNum++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else {
                    val oldNewsArticles = breakingNewsResponse?.articles
                    val newNewsArticles = resultResponse.articles
                    oldNewsArticles?.addAll(newNewsArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun handleSearchNewsResponse (response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                searchNewsPageNum++
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else {
                    val oldNewsArticles = searchNewsResponse?.articles
                    val newNewsArticles = resultResponse.articles
                    oldNewsArticles?.addAll(newNewsArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getSavedArticles() = newsRepository.getSavedArticles()

    fun saveArticle(article : Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //code below is here
        }else{
            // perform this if api level is below 23
            connectivityManager.activeNetworkInfo?.run{
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }

        }*/

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}