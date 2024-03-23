package com.example.newsappmvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsappmvvm.model.Article

@TypeConverters(Converter::class)
@Database(
    entities = [Article::class],
    version = 1
)
abstract class ArticleDatabase : RoomDatabase (){

    abstract fun getArticleDao() : ArticleDao
}