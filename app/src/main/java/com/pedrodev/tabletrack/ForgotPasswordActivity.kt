package com.pedrodev.tabletrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityForgotPasswordBinding
import com.pedrodev.tabletrack.databinding.ActivityLoginBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.okForgotPassword.setOnClickListener {

        }

        binding.back.setOnClickListener {
            this.moveTo(LoginActivity::class.java)
            finish()
        }

    }
}