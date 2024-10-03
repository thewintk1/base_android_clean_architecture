package com.example.base_clean_architecture.ui.home

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.base_clean_architecture.common.base.BaseFragment
import com.example.base_clean_architecture.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    companion object {
        fun newInstance(
        ): HomeFragment {
            return HomeFragment()

        }
    }

    override fun initializeViews() {
        binding?.apply {
            btnStart.setOnClickListener {
                viewModel.fetchData()
            }

            btnStop.setOnClickListener {
                viewModel.cancelJob()
            }

            viewModel.data.observe(viewLifecycleOwner) {
                tvResult.text = it.toString()
            }
        }
    }

}