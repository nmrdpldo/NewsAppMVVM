package com.example.mvvm.utils

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding : DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        initUI()
    }

    fun performDataBinding(){
        binding = DataBindingUtil.setContentView(this, getLayoutID())
    }

    @LayoutRes
    protected abstract fun getLayoutID(): Int

    protected abstract fun initUI()
}