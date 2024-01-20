package com.example.newsappmvvm.repository

import com.example.newsappmvvm.api.retrofit.RetrofitInstance
import com.example.newsappmvvm.db.ArticleDatabase
import com.example.newsappmvvm.model.Article

class NewsRepository(val db : ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber : Int) =
        RetrofitInstance.Api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String, pageNumber : Int) =
        RetrofitInstance.Api.searchNews(searchQuery,pageNumber)

    fun getSavedArticles() = db.getArticleDao().getAllArticles()

    suspend fun upsertArticle(article : Article) =
        db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)
}