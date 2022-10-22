package com.test.remake.activitys
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.SystemClock
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemClock.sleep(1500)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}