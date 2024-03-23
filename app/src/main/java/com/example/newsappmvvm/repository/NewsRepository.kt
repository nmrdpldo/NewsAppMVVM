package com.example.newsappmvvm.repository

import com.example.newsappmvvm.api.ApiService
import com.example.newsappmvvm.db.ArticleDao
import com.example.newsappmvvm.db.ArticleDatabase
import com.example.newsappmvvm.model.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val api : ApiService
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber : Int) =
        api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String, pageNumber : Int) =
        api.searchNews(searchQuery,pageNumber)

    fun getSavedArticles() = articleDao.getAllArticles()

    suspend fun upsertArticle(article : Article) =
        articleDao.upsert(article)

    suspend fun deleteArticle(article: Article) =
        articleDao.deleteArticle(article)
}