package com.example.base_clean_architecture.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.base_clean_architecture.R
import com.example.base_clean_architecture.databinding.ActivityMainBinding
import com.example.base_clean_architecture.extension.isTablet
import com.example.base_clean_architecture.extension.triggerRebirth
import com.example.base_clean_architecture.ui.home.HomeFragment
import com.example.base_clean_architecture.ui.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var currentNavId = NAV_ID_NONE
    private val mainViewModel: MainViewModel by viewModels()
    private var mainFragment: MainFragment = MainFragment.newInstance()
    private var activeFragment: Fragment = mainFragment
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("----MainActivity----")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = if (isTablet()) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        try {
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fcv_nav_host_fragment) as NavHostFragment
        } catch (e: Exception) {
            Timber.e("navHostFragment: $e")
            triggerRebirth()
        }

        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Whatever you want
                // when back pressed
                println("Back button pressed")
                finish()
            }
        })
    }


    companion object {
        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
        private const val timeDelay: Long = 500
        private const val timeShowToast: Long = 4000
        private const val NAV_ID_NONE = -1
        private const val BACK_PRESS_COOLDOWN_PERIOD: Long = 500 // 0.5 seconds
    }
}