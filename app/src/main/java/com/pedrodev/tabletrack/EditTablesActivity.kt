package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityEditTablesBinding

class EditTablesActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityEditTablesBinding
    private var auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser
    private val userID = user?.uid
    private lateinit var restaurantID: String
    private lateinit var roomID: String
    private lateinit var tableNumber: String
    private var newNumber: String? = null
    private var newRow: Int? = null
    private var newCol: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTablesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        restaurantID = intent.getStringExtra("restaurantID").toString()
        roomID = intent.getStringExtra("roomID").toString()
        tableNumber = intent.getStringExtra("tableNumber").toString()

        binding.editTextTableNumber.setText(tableNumber)

        db.collection("users").document(userID.toString()).get()
            .addOnSuccessListener {
                val restaurantID = it.getString("memberOf")
                if (restaurantID != null) {
                    db.collection("restaurants").document(restaurantID)
                        .collection("rooms").get()
                        .addOnCompleteListener { roomsTask ->
                            if (roomsTask.isSuccessful) {
                                val roomSnapshot = roomsTask.result
                                if (!roomSnapshot.isEmpty) {
                                    val roomDoc = roomSnapshot.documents[0]
                                    val rows = roomDoc.getLong("rows")?.toInt() ?: 0

                                    if (rows == 3) {
                                        val spinnerColumn = binding.spinnerColumn
                                        spinnerColumn.onItemSelectedListener = this
                                        ArrayAdapter.createFromResource(
                                            this,
                                            R.array.rows_columns_3x3,
                                            android.R.layout.simple_spinner_item
                                        ).also { adapter ->
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerColumn.adapter = adapter
                                        }
                                        val spinnerRow = binding.spinnerRow
                                        spinnerRow.onItemSelectedListener = this
                                        ArrayAdapter.createFromResource(
                                            this,
                                            R.array.rows_columns_3x3,
                                            android.R.layout.simple_spinner_item
                                        ).also { adapter ->
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerRow.adapter = adapter
                                        }
                                    } else if (rows == 4) {
                                        val spinnerColumn = binding.spinnerColumn
                                        spinnerColumn.onItemSelectedListener = this
                                        ArrayAdapter.createFromResource(
                                            this,
                                            R.array.rows_columns_3x4,
                                            android.R.layout.simple_spinner_item
                                        ).also { adapter ->
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerColumn.adapter = adapter
                                        }
                                        val spinnerRow = binding.spinnerRow
                                        spinnerRow.onItemSelectedListener = this
                                        ArrayAdapter.createFromResource(
                                            this,
                                            R.array.rows_columns_3x4,
                                            android.R.layout.simple_spinner_item
                                        ).also { adapter ->
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerRow.adapter = adapter
                                        }
                                    }
                                }
                            }
                        }
                }
            }


        binding.okEditTable.setOnClickListener {
            newNumber = binding.editTextTableNumber.text.toString().trim()

            if (newNumber!!.isEmpty()) {
                binding.root.alert("El número de la mesa no puede estar vacío")
                return@setOnClickListener
            }

            db.collection("restaurants").document(restaurantID)
                .collection("rooms").document(roomID)
                .collection("tables").whereEqualTo("number", newNumber)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty && newNumber != tableNumber) {
                        binding.root.alert("El número ya se está usando, elija otro")
                    } else {
                        db.collection("restaurants").document(restaurantID)
                            .collection("rooms").document(roomID)
                            .collection("tables").whereEqualTo("number", tableNumber)
                            .get()
                            .addOnSuccessListener { tableSnapshot ->
                                if (!tableSnapshot.isEmpty) {
                                    val tableDoc = tableSnapshot.documents[0]
                                    val tableID = tableDoc.id

                                    db.collection("restaurants").document(restaurantID)
                                        .collection("rooms").document(roomID)
                                        .collection("tables").document(tableID)
                                        .update(
                                            mapOf(
                                                "number" to newNumber,
                                                "coordRow" to newRow,
                                                "coordCol" to newCol
                                            )
                                        )

                                    this.moveTo(TableMapActivity::class.java)
                                    finish()
                                }
                            }
                    }
                }
        }

        binding.back.setOnClickListener {
            this.moveTo(TableMapActivity::class.java)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            binding.spinnerRow.id -> {
                newRow = parent.getItemAtPosition(pos).toString().toInt()
            }
            binding.spinnerColumn.id -> {
                newCol = parent.getItemAtPosition(pos).toString().toInt()
            }
        }
        Log.e("ERROR", "LA OPCION SELECCIONADA FUE $newRow  |   $newCol")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}