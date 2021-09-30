package com.yazantarifi.reporter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yazantarifi.android.reporter.ShakeReporter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ShakeReporter.startSensorListener(this)
        crashButton?.setOnClickListener {
            throw NullPointerException("Null Exception Happend")
        }
    }

}
