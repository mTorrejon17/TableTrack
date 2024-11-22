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
import com.pedrodev.tabletrack.databinding.ActivityCreateTableBinding

class CreateTableActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityCreateTableBinding
    private var auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser
    private val userID = user?.uid
    var rows = 0
    var tableRow = 0
    var tableColumn = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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

        Log.e("ERROR", "ROWS: $rows")

        binding.back.setOnClickListener {
            this.moveTo(TableMapActivity::class.java)
            finish()
        }

        binding.okCreateTable.setOnClickListener {
            val tableNumber = binding.editTextTableNumber.text.toString().trim()

            if (tableNumber.isEmpty()) {
                binding.root.alert("El número de la mesa no puede estar vacío")
                return@setOnClickListener
            }

            db.collection("users").document(userID.toString()).get()
                .addOnSuccessListener { userDoc ->
                    val restaurantID = userDoc.getString("memberOf")
                    if (restaurantID != null) {
                        db.collection("restaurants").document(restaurantID)
                            .collection("rooms").get()
                            .addOnSuccessListener { roomsDoc ->
                                val roomID = roomsDoc.documents[0].id
                                db.collection("restaurants").document(restaurantID)
                                    .collection("rooms").document(roomID)
                                    .collection("tables").document(tableNumber).get()
                                    .addOnSuccessListener { tableDoc ->
                                        if (tableDoc.exists()) {
                                            binding.root.alert("El número de mesa ya está en uso")
                                        } else {
                                            val tableData = hashMapOf(
                                                "coordRow" to tableRow,
                                                "coordCol" to tableColumn,
                                                "number" to tableNumber,
                                                "isAvailable" to true
                                            )

                                            db.collection("restaurants").document(restaurantID)
                                                .collection("rooms").document(roomID)
                                                .collection("tables").document(tableNumber)
                                                .set(tableData)
                                                .addOnSuccessListener {
                                                    this.moveTo(TableMapActivity::class.java)
                                                    finish()
                                                }
                                        }
                                    }
                            }
                    }
                }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            binding.spinnerRow.id -> {
                tableRow = parent.getItemAtPosition(pos).toString().toInt()
            }
            binding.spinnerColumn.id -> {
                tableColumn = parent.getItemAtPosition(pos).toString().toInt()
            }
        }
        Log.e("ERROR", "LA OPCION SELECCIONADA FUE $tableRow  |   $tableColumn")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}