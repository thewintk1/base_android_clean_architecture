package com.example.base_clean_architecture.common.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

typealias BI<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding>(private val bi: BI<VB>) :
    AppCompatActivity() {
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bi(layoutInflater)
        setContentView(binding.root)
        initView()

    }

    abstract fun initView()
}
