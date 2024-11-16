package com.pedrodev.tabletrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.databinding.ActivitySelectRoleBinding

class CreateRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


    }
}