package com.pedrodev.tabletrack

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityCreateRoomBinding

class CreateRoomActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityCreateRoomBinding
    private var auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser
    private val userID = user?.uid
    var roomName: String = ""
    var columns = 3
    var rows = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val spinner: Spinner = binding.spinnerRoomSize
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_room_size,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.okCreateRoom.setOnClickListener {
            roomName = binding.editTextRoomName.text.toString().trim()

            if (roomName.isEmpty()) {
                binding.root.alert("No se ha ingresado un nombre para la sala")
                return@setOnClickListener
            }

            db.collection("users").document(userID.toString()).get()
                .addOnSuccessListener {
                    val restaurantID = it.getString("memberOf")
                    if (restaurantID != null) {
                        val roomData = hashMapOf(
                            "name" to roomName,
                            "columns" to columns,
                            "rows" to rows
                        )

                        db.collection("restaurants").document(restaurantID)
                            .collection("rooms").add(roomData)
                            .addOnSuccessListener {
                                this.moveTo(TableMapActivity::class.java)
                                finish()
                            }
                    }
                }
        }

        binding.back.setOnClickListener {
            this.moveTo(TableMapActivity::class.java)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (pos) {
            0 -> rows = 3
            1 -> rows = 4
            2 -> rows = 5
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}