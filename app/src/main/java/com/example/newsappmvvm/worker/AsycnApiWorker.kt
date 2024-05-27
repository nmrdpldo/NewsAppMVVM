package com.example.newsappmvvm.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsappmvvm.API_KEY
import com.example.newsappmvvm.api.ApiService
import com.example.newsappmvvm.repository.NewsRepository
import com.google.android.material.snackbar.Snackbar
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AsycnApiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val apiService: ApiService,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {
        return try{
            val response = apiService.getBreakingNews("ph",1, API_KEY)
            if(response.isSuccessful){
                response.body()?.let {
                    //newsRepository.getBreakingNews("ph",1) // this code should be where you update the local database, if database is empty add all if, exist change all
                    Log.d("WORKMANAGER TEST", "doWork: Api Request Performed")
                }
                Result.success()
            }else{
                Result.retry()
            }
        }catch (e : Exception){
            Result.retry()
        }
    }
}