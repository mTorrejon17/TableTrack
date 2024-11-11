package com.pedrodev.tabletrack

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivitySettingsTeamBinding

class SettingsTeamActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsTeamBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val userID = auth.currentUser?.uid

        userID?.let {
            db.collection("restaurants").whereEqualTo("adminID", userID).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot) {
                            val restaurantName = document.getString("name")
                            val restaurantCode = document.id

                            binding.restaurantName.text = restaurantName
                            binding.restaurantCode.text = restaurantCode
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    binding.root.alert(getString(R.string.failed_database))
                    binding.progressBar.visibility = View.GONE
                }
        }

        binding.back.setOnClickListener {
            this.moveTo(SettingsActivity::class.java)
            finish()
        }
    }
}