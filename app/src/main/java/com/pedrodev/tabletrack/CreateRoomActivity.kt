package com.pedrodev.tabletrack

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.databinding.ActivityCreateRoomBinding
import com.pedrodev.tabletrack.databinding.ActivitySelectRoleBinding

class CreateRoomActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityCreateRoomBinding
    private lateinit var auth: FirebaseAuth
    var roomName: String = ""
    var columns = 3
    var rows = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        auth = Firebase.auth
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
            Functions.saveString(this, "createroom", "roomName", roomName)
            Functions.saveInt(this, "createroom", "rows", rows)
            Functions.saveInt(this, "createroom", "columns", columns)



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
        Unit
    }

}