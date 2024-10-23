package com.pedrodev.tabletrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityLoginBinding
import com.pedrodev.tabletrack.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.okTerms.setOnClickListener {
            Functions.saveBoolean(this, "temp_data", "accepted_terms", true)
            this.moveTo(SignUpActivity::class.java)
            finish()
        }

        binding.back.setOnClickListener {
            this.moveTo(SignUpActivity::class.java)
            finish()
        }
    }
}