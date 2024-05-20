package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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



    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Подтверждение выхода")
            .setMessage("Вы действительно хотите выйти?")
            .setPositiveButton("Да") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}