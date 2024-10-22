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
        enableEdgeToEdge()
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.okTerms.setOnClickListener {
            val sharedPreferences = getSharedPreferences("temp_data", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("accepted_terms", true)
            editor.apply()

            this.moveTo(SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.back.setOnClickListener {
            this.moveTo(SignUpActivity::class.java)
            finish()
        }
    }
}