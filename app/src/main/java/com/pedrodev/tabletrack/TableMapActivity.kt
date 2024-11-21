package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityTableMapBinding
import kotlinx.coroutines.tasks.await

class TableMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTableMapBinding
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser
    private val userID = user?.uid
    private lateinit var gridLayout: GridLayout
    private val tableSize = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableMapBinding.inflate(layoutInflater)
        gridLayout = binding.gridLayout
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        auth = Firebase.auth

        binding.gridLayout.visibility = View.GONE
        binding.fabTables.visibility = View.GONE

        restaurantData()

        binding.optionsMenu.setOnClickListener {
            val optionsMenu = PopupMenu(this, binding.optionsMenu)
            optionsMenu.menuInflater.inflate(R.menu.table_view_menu, optionsMenu.menu)
            optionsMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option_edit_tables -> {
                        binding.root.alert("SELECCIONADO: Editar mesas")
                        true
                    }
                    R.id.option_change_view -> {
                        this.moveTo(TableListActivity::class.java)
                        finish()
                        true
                    }
                    R.id.option_settings -> {
                        this.moveTo(SettingsActivity::class.java)
                        finish()
                        true
                    }
                    R.id.option_logout -> {
                        Firebase.auth.signOut()
                        this.moveTo(LoginActivity::class.java)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            optionsMenu.show()
        }

        binding.fabTables.setOnClickListener {
            val fabOptions = PopupMenu(this, binding.fabTables)
            fabOptions.menuInflater.inflate(R.menu.fab_add_menu, fabOptions.menu)

            fabOptions.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.fab_add_room -> {
                        db.collection("users").document(userID.toString()).get()
                            .addOnSuccessListener { userDoc ->
                                val restaurantID = userDoc.getString("memberOf")
                                if (restaurantID != null) {
                                    db.collection("restaurants").document(restaurantID).collection("rooms").get()
                                        .addOnSuccessListener { documents ->
                                            val restaurantEmpty = (documents.isEmpty)
                                            if (restaurantEmpty) {
                                                this.moveTo(CreateRoomActivity::class.java)
                                            } else {
                                                binding.root.alert("Límite de 1 sala por restaurante.")
                                            }
                                        }
                                }
                            }
                        true
                    }
                    R.id.fab_add_table -> {
                        true
                    }
                    else -> false
                }
            }
            fabOptions.show()
        }



    } // FIN OnCreate

    override fun onStart() {
        super.onStart()
        db.collection("users").document(userID.toString()).get()
            .addOnSuccessListener { userDocument ->
                val userRole = userDocument.getString("role")
                when (userRole) {
                    "admin" -> {
                        binding.fabTables.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.fabTables.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "error OnStart TableListActivity")
            }
    }

    private fun restaurantData() {
        db.collection("users").document(userID.toString()).get()
            .addOnSuccessListener { userDocument ->
                val restaurantID = userDocument.getString("memberOf")
                if (restaurantID != null) {
                    db.collection("restaurants").document(restaurantID).get()
                        .addOnSuccessListener { restDocument ->
                            val restaurantName = restDocument.getString("name")
                            if (restaurantName != null) {
                                binding.title.text = restaurantName
                            }
                        }
                    db.collection("restaurants").document(restaurantID).collection("rooms").get()
                        .addOnSuccessListener {
                            if (!it.isEmpty) {
                                val roomID = it.documents[0].id
                                roomData(restaurantID, roomID)
                                binding.gridLayout.visibility = View.VISIBLE
                            } else {
                                binding.gridLayout.visibility = View.GONE
                            }
                        }
                }
            }
    }

    private fun roomData(restaurantID: String, roomID: String) {
        db.collection("restaurants").document(restaurantID)
            .collection("rooms").document(roomID).get()
            .addOnSuccessListener {
                val rowCount = it.getLong("rows")?.toInt() ?: 3
                val colCount = it.getLong("columns")?.toInt() ?: 3
                binding.gridLayout.rowCount = rowCount
                binding.gridLayout.columnCount = colCount

                updateChanges(restaurantID, roomID)
            }
    }

    // YA NO SE USA ESTA FUNCIÓN, SE CAMBIÓ POR updateChanges()
    /*
    private fun tablesData(restaurantID: String, roomID: String) {
        db.collection("restaurants").document(restaurantID)
            .collection("rooms").document(roomID)
            .collection("tables").get()
            .addOnSuccessListener { tables ->
                for (table in tables) {
                    val row = table.getLong("coordRow")?.toInt() ?:0
                    val col = table.getLong("coordCol")?.toInt() ?:0
                    val tableNumber = table.getString("number") ?:0
                    val isAvailable = table.getBoolean("isAvailable") ?: true
                    val status = if (isAvailable) Status.AVAILABLE else Status.UNAVAILABLE

                    addTable(row, col, tableNumber.toString(), tableSize, status)
                }
            }
    }
     */


    private fun addTable(row: Int, col: Int, text: String, size: Int, status: Status) {
        val button = Button(this).apply {
            this.text = text
            background = ContextCompat.getDrawable(
                this@TableMapActivity,
                if (status == Status.AVAILABLE) {
                    R.drawable.vector_table_green
                } else {
                    R.drawable.vector_table_red
                }
            )

            layoutParams = GridLayout.LayoutParams().apply {
                width = Functions.dpToPx(size, this@TableMapActivity)
                height = Functions.dpToPx(size, this@TableMapActivity)
                rowSpec = GridLayout.spec(row)
                columnSpec = GridLayout.spec(col)
                setMargins(Functions.dpToPx(4, this@TableMapActivity),
                    Functions.dpToPx(4, this@TableMapActivity),
                    Functions.dpToPx(4, this@TableMapActivity),
                    Functions.dpToPx(4, this@TableMapActivity))
            }
        }
        gridLayout.addView(button)
    }

    private fun updateChanges(restaurantID: String, roomID: String) {
        db.collection("restaurants").document(restaurantID)
            .collection("rooms").document(roomID)
            .collection("tables")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    binding.gridLayout.removeAllViews()
                    for (table in snapshots) {
                        val row = table.getLong("coordRow")?.toInt() ?:0
                        val col = table.getLong("coordCol")?.toInt() ?:0
                        val tableNumber = table.getString("number") ?:0
                        val isAvailable = table.getBoolean("isAvailable") ?: true
                        val status = if (isAvailable) Status.AVAILABLE else Status.UNAVAILABLE

                        addTable(row, col, tableNumber.toString(), tableSize, status)
                    }

                }
            }

    }
}