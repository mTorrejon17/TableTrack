package com.pedrodev.tabletrack

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivitySettingsProfileBinding

class SettingsProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val userID = auth.currentUser?.uid

        userID?.let { id ->
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val email = document.getString("email")

                        binding.profileUser.text = username
                        binding.profileEmail.text = email

                        binding.progressBarProfile.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    binding.root.alert(getString(R.string.failed_database))
                    binding.progressBarProfile.visibility = View.GONE
                }
        }

        binding.back.setOnClickListener {
            this.moveTo(SettingsActivity::class.java)
            finish()
        }

        binding.profilePasswordLayout.setOnClickListener {
            val email = binding.profileEmail.text.toString()
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                binding.root.alert(getString(R.string.link_password))
            }
            it.isClickable = false
        }
    }

}