package com.pedrodev.tabletrack

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.closeKeyboard
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        auth = FirebaseAuth.getInstance()

        binding.progressBar.visibility = View.GONE

        binding.okForgotPassword.setOnClickListener {
            val email = binding.forgotEmail.text.toString().trim()

            closeKeyboard()
            binding.okForgotPassword.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            it.isClickable = false

            if (email.isEmpty()) {
                //binding.root.alert(getString(R.string.please_all))
                binding.forgotEmail.error = getString(R.string.required)
                binding.progressBar.visibility = View.GONE
                binding.okForgotPassword.visibility = View.VISIBLE
                it.isClickable = true
                return@setOnClickListener
            }
            if (!Functions.checkValidEmail(email) &&
                !Functions.checkValidDomain(email))  {
                //binding.root.alert(getString(R.string.please_email))
                binding.forgotEmail.error = getString(R.string.please_email)
                binding.progressBar.visibility = View.GONE
                binding.okForgotPassword.visibility = View.VISIBLE
                it.isClickable = true
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                binding.root.alert(getString(R.string.link_password))
                binding.okForgotPassword.isClickable = true
                binding.forgotEmail.setText("")
                binding.progressBar.visibility = View.GONE
                binding.okForgotPassword.visibility = View.VISIBLE
            }.addOnFailureListener {
                binding.root.alert(getString(R.string.please_email))
                binding.progressBar.visibility = View.GONE
                binding.okForgotPassword.visibility = View.VISIBLE
            }
        }

        binding.back.setOnClickListener {
            this.moveTo(LoginActivity::class.java)
            finish()
        }

    }
}