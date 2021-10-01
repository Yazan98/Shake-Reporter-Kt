package com.yazantarifi.reporter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yazantarifi.android.reporter.ReporterInterceptor
import com.yazantarifi.android.reporter.ShakeReporter
import com.yazantarifi.android.reporter.ShakeSensorListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.lang.NullPointerException
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ShakeReporter.startSensorListener(this, ShakeSensorListener(this))
        crashButton?.setOnClickListener {
            throw NullPointerException("Null Exception Happend")
        }

        requestItem?.setOnClickListener {
            executeRequest()
        }
    }

    private fun executeRequest() {
        create().getUserInfo().enqueue(object: Callback<Response<Void>> {
            override fun onResponse(
                call: Call<Response<Void>>,
                response: Response<Response<Void>>
            ) {
                println("III :: Response")
            }

            override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                println("III :: Error : ${t?.message}")
            }
        })
    }

    private fun create() : ApiRequests {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.github.com/")
            .client(OkHttpClient.Builder().addInterceptor(ReporterInterceptor()).build())
            .build()
        return retrofit.create(ApiRequests::class.java)
    }

    interface ApiRequests {
        @GET("users/Yazan98")
        fun getUserInfo(): Call<Response<Void>>
    }

}
