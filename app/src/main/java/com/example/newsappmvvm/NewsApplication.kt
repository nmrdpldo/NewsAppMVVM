package com.example.newsappmvvm

import android.app.Application
import com.example.newsappmvvm.worker.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application() {
    @Inject
    lateinit var workScheduler: WorkScheduler

    override fun onCreate() {
        super.onCreate()
        workScheduler.schedulePeriodicApiUpdate()
    }
}