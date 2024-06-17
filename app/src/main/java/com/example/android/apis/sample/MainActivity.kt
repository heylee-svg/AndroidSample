package com.example.android.apis.sample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.android.apis.sample.databinding.ServiceMainLayoutBinding
import com.example.android.apis.sample.service.Binding
import com.example.android.apis.sample.service.BindingOptions
import com.example.android.apis.sample.service.Controller
import com.example.android.apis.sample.service.JobWorkServiceActivity
import com.example.android.apis.sample.service.MessengerServiceActivities



class MainActivity : AppCompatActivity() {
    lateinit var binding: ServiceMainLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ServiceMainLayoutBinding.inflate(layoutInflater)
        binding.binding.setOnClickListener {
            startActivity(Intent(this, Binding::class.java))
        }
        binding.bingOptions.setOnClickListener {
            startActivity(Intent(this, BindingOptions::class.java))
        }
        binding.controller.setOnClickListener {
            startActivity(Intent(this, Controller::class.java))
        }
        binding.jobWorkService.setOnClickListener {
            startActivity(Intent(this, JobWorkServiceActivity::class.java))
        }
        binding.messageServiceActivities.setOnClickListener {
            startActivity(Intent(this, MessengerServiceActivities::class.java))
        }
        setContentView(binding.root)
    }
}