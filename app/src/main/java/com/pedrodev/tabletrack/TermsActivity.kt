package com.pedrodev.tabletrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pedrodev.tabletrack.Functions.moveTo
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