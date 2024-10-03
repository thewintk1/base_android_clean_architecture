package com.example.base_clean_architecture.ui.main

import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.base_clean_architecture.R
import com.example.base_clean_architecture.common.base.BaseFragment
import com.example.base_clean_architecture.databinding.FragmentMainBinding
import com.example.base_clean_architecture.ui.MainViewModel
import com.example.base_clean_architecture.ui.home.HomeFragment
import com.example.base_clean_architecture.utils.Constant.Tab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(FragmentMainBinding::inflate) {
    companion object {
        const val NAVID = "NAVID"
        fun newInstance(
            destinationId: String? = Tab.HOME.value
        ): MainFragment {
            return MainFragment().apply {
                arguments = bundleOf(
                    NAVID to destinationId
                )
            }
        }
    }

    override val viewModel: MainViewModel by viewModels()
    private val homeFragment: HomeFragment = HomeFragment.newInstance()
    private val navId get() = arguments?.getString(NAVID) ?: Tab.HOME.value

    private lateinit var ft: FragmentTransaction

    override fun initializeViews() {
        ft = childFragmentManager.beginTransaction()
        ft.apply {
            if (!childFragmentManager.fragments.contains(homeFragment) && navId == Tab.HOME.value) {
                add(R.id.fcv_nav_host_fragment, homeFragment, null)
            }
        }
    }
}