package com.example.newsappmvvm.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsappmvvm.NewsApplication
import com.example.newsappmvvm.model.Article
import com.example.newsappmvvm.model.NewsResponse
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel (app : Application, private val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPageNum = 1
    var breakingNewsResponse : NewsResponse? = null

    val searchNewsData : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPageNum = 1
    var searchNewsResponse : NewsResponse? = null

    fun getBreakingNews () = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews("us",breakingNewsPageNum)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t : Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Failure"))
            }
        }
    }

    fun searchNews (searchQuery : String) = viewModelScope.launch {
        searchNewsData.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery,searchNewsPageNum)
                searchNewsData.postValue(handleSearchNewsResponse(response))
            }else{
                searchNewsData.postValue(Resource.Error("No internet connection"))
            }
        }catch (t : Throwable) {
            when (t) {
                is IOException -> searchNewsData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsData.postValue(Resource.Error("Conversion Failure"))
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