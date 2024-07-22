package com.snapcat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TriggerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trigger)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}