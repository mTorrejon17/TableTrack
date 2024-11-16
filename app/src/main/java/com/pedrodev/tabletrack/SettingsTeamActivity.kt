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
            db.collection("users").document(userID).get()
                .addOnSuccessListener { userDocument ->
                    val memberOf = userDocument.getString("memberOf")
                    if (memberOf != null) {
                        db.collection("restaurants").document(memberOf).get()
                            .addOnSuccessListener { restaurantDocument ->
                                val restaurantName = restaurantDocument.getString("name")

                                binding.restaurantName.text = restaurantName
                                binding.restaurantCode.text = memberOf

                                binding.progressBar.visibility = View.GONE
                            }
                    }
                }
        }

        binding.back.setOnClickListener {
            this.moveTo(SettingsActivity::class.java)
            finish()
        }
    }
}