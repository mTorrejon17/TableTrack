package com.pedrodev.tabletrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.closeKeyboard
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        auth = Firebase.auth

        val acceptedTerms = Functions.getBoolean(this, "temp_data", "accepted_terms")
        binding.checkboxTerms.isChecked = acceptedTerms

        binding.buttonCreateSignUp.setOnClickListener {
            val username = binding.createUsername.text.toString().trim()
            val email = binding.createEmail.text.toString().trim()
            val password = binding.createPassword.text.toString().trim()
            val termsChecked = binding.checkboxTerms.isChecked

            it.isClickable = false

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                this.alert(getString(R.string.please_all))
                return@setOnClickListener
            }
            if (!Functions.checkValidEmail(email) &&
                !Functions.checkValidDomain(email))  {
                this.alert(getString(R.string.please_email))
                return@setOnClickListener
            }
            if (!termsChecked) {
                this.alert(getString(R.string.please_terms))
                return@setOnClickListener
            }
            if (password.length < 6) {
                this.alert(getString(R.string.please_password))
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        this.moveTo(TableMapActivity::class.java)
                    } else {
                        this.alert(getString(R.string.failed_login))
                        it.isClickable = true
                    }
                }
            closeKeyboard()
            Functions.clearData(this, "temp_data")
        }

        binding.back.setOnClickListener {
            Functions.clearData(this, "temp_data")
            this.moveTo(LoginActivity::class.java)
            finish()
        }

        binding.checkboxTerms.setOnClickListener {
            val username = binding.createUsername.text.toString().trim()
            val email = binding.createEmail.text.toString().trim()
            val password = binding.createPassword.text.toString().trim()

            Functions.saveString(this,"temp_data", "username", username)
            Functions.saveString(this,"temp_data", "email", email)
            Functions.saveString(this,"temp_data", "password", password)

            if (binding.checkboxTerms.isChecked) {
                this.moveTo(TermsActivity::class.java)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            this.moveTo(TableMapActivity::class.java)
        }

        binding.buttonCreateSignUp.isClickable = true

        val username = Functions.getString(this, "temp_data", "username")
        val email = Functions.getString(this, "temp_data", "email")
        val password = Functions.getString(this, "temp_data", "password")
        binding.createUsername.setText(username)
        binding.createEmail.setText(email)
        binding.createPassword.setText(password)
    }

    public override fun onPause() {
        super.onPause()

        val username = binding.createUsername.text.toString().trim()
        val email = binding.createEmail.text.toString().trim()
        val password = binding.createPassword.text.toString().trim()
        Functions.saveString(this, "temp_data", "username", username)
        Functions.saveString(this, "temp_data", "email", email)
        Functions.saveString(this, "temp_data", "password", password)
    }

    public override fun onResume() {
        super.onResume()

        val username = Functions.getString(this, "temp_data", "username")
        val email = Functions.getString(this, "temp_data", "email")
        val password = Functions.getString(this, "temp_data", "password")
        binding.createUsername.setText(username)
        binding.createEmail.setText(email)
        binding.createPassword.setText(password)
    }
}