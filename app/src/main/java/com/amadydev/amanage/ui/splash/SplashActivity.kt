package com.amadydev.amanage.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amadydev.amanage.R
import com.amadydev.amanage.ui.MainActivity
import com.amadydev.amanage.ui.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splashViewModel.isUserLoggedIn()

        Handler(Looper.getMainLooper()).postDelayed({
            splashViewModel.splashState.observe(this) {
                when (it) {
                    is SplashViewModel.SplashState.Logged ->
                        startActivity(Intent(this, MainActivity::class.java))
                    is SplashViewModel.SplashState.NotLogged ->
                        startActivity(Intent(this, IntroActivity::class.java))
                }
            }
        }, 2500)
    }
}