package com.example.newsappmvvm.di

import android.content.Context
import androidx.room.Room
import com.example.newsappmvvm.BASE_URL
import com.example.newsappmvvm.api.ApiService
import com.example.newsappmvvm.db.ArticleDao
import com.example.newsappmvvm.db.ArticleDatabase
import com.example.newsappmvvm.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NewsModule {


    @Singleton
    @Provides
    fun provideApi() : ApiService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)


    }

    @Singleton
    @Provides
    fun providesArticleDatabase(
        @ApplicationContext context: Context
    ): ArticleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesArticleDao(
        articleDatabase: ArticleDatabase
    ) : ArticleDao {
        return articleDatabase.getArticleDao()
    }

    @Singleton
    @Provides
    fun providesNewsRepository(
        articleDao: ArticleDao,
        api : ApiService
    ) : NewsRepository {
        return NewsRepository(
            articleDao,
            api)
    }
}