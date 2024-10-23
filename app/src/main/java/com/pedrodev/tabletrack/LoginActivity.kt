package com.pedrodev.tabletrack

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.databinding.ActivityLoginBinding
import com.pedrodev.tabletrack.Functions
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.closeKeyboard
import com.pedrodev.tabletrack.Functions.moveTo

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        binding.buttonLogin.setOnClickListener {
            it.isClickable = false

            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            this.moveTo(TableMapActivity::class.java)
                        } else {
                            this.alert(getString(R.string.failed_login))
                        }
                    }
            } else {
                this.alert(getString(R.string.failed_login))
            }
            it.postDelayed({
                it.isClickable = true
            },1000)
            closeKeyboard()
        }

        binding.buttonSignUp.setOnClickListener {
            Functions.clearData(this, "temp_data")
            this.moveTo(SignUpActivity::class.java)
            finish()
        }

        binding.textviewForgotPassword.setOnClickListener{
            this.moveTo(ForgotPasswordActivity::class.java)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            this.moveTo(TableMapActivity::class.java)
        }
    }

}