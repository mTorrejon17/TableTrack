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

            var validRegister = true
            it.isClickable = false

            if (username.isEmpty()) {
                //binding.root.alert(getString(R.string.please_all))
                binding.createUsername.error = getString(R.string.required)
                validRegister = false
            }
            if (email.isEmpty()) {
                //binding.root.alert(getString(R.string.please_all))
                binding.createEmail.error = getString(R.string.required)
                validRegister = false
            }
            if (password.isEmpty()) {
                //binding.root.alert(getString(R.string.please_all))
                binding.createPassword.error = getString(R.string.required)
                validRegister = false
            }
            if (!validRegister) {
                binding.root.alert(getString(R.string.please_all))
                it.isClickable = true
                return@setOnClickListener
            }
            if (!Functions.checkValidEmail(email) &&
                !Functions.checkValidDomain(email))  {
                //binding.root.alert(getString(R.string.please_email))
                binding.createEmail.error = getString(R.string.please_email)
                it.isClickable = true
                return@setOnClickListener
            }
            if (password.length < 6) {
                //binding.root.alert(getString(R.string.please_password))
                binding.createPassword.error = getString(R.string.please_password)
                it.isClickable = true
                return@setOnClickListener
            }
            if (!termsChecked) {
                //binding.root.alert(getString(R.string.please_terms))
                binding.checkboxTerms.error = getString(R.string.please_terms)
                it.isClickable = true
                return@setOnClickListener
            }
            closeKeyboard()
            // TODA LA AUTENTICACIÓN DEL USUARIO Y LO DE LA BASE DE DATOS
            // SE MOVIÓ A SelectRoleActivity
            this.moveTo(SelectRoleActivity::class.java)
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