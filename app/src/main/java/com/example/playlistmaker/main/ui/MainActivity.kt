package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerActivity -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == navController.graph.startDestinationId) {
                    showExitConfirmationDialog()
                } else {
                    navController.navigateUp()
                }
            }

        })

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.hold)
                .setPopEnterAnim(R.anim.hold)
                .setPopExitAnim(R.anim.fade_out)
                .build()

            when (item.itemId) {
                R.id.searchFragment -> {
                    navController.navigate(R.id.searchFragment, null, navOptions)
                    true
                }
                R.id.libraryFragment -> {
                    navController.navigate(R.id.libraryFragment, null, navOptions)
                    true
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment, null, navOptions)
                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.isVisible = true
    }
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_confirmation)
            .setMessage(R.string.are_you_sure_you_want_to_exit)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    fun animateBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }
}