package com.pedrodev.tabletrack

import android.content.Intent
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
                                    db.collection("restaurants").document(restaurantID)
                                        .collection("rooms").get()
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
                        db.collection("users").document(userID.toString()).get()
                            .addOnSuccessListener { userDoc ->
                                val restaurantID = userDoc.getString("memberOf")
                                if (restaurantID != null) {
                                    db.collection("restaurants").document(restaurantID)
                                        .collection("rooms").get()
                                        .addOnSuccessListener { roomDoc ->
                                            if (roomDoc.isEmpty) {
                                                binding.root.alert("No hay sala creada todavía")
                                            } else {
                                                this.moveTo(CreateTableActivity::class.java)
                                            }
                                        }
                                }
                            }
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
                val roomName = it.getString("name")
                val rowCount = it.getLong("rows")?.toInt() ?: 3
                val colCount = it.getLong("columns")?.toInt() ?: 3
                binding.gridLayout.rowCount = rowCount
                binding.gridLayout.columnCount = colCount
                binding.title.text = "$roomName"

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

    private fun addTable(row: Int, col: Int, text: String, size: Int, status: Status, restaurantID: String, roomID: String) {
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

            setOnClickListener {
                val tableOptions = PopupMenu(this@TableMapActivity, it)
                tableOptions.menuInflater.inflate(R.menu.table_options, tableOptions.menu)
                val currentStatus = tableOptions.menu.findItem(R.id.table_options_status)

                if (status == Status.AVAILABLE) {
                    currentStatus.title = "Disponible"
                } else if (status == Status.UNAVAILABLE) {
                    currentStatus.title = "Ocupado"
                }

                tableOptions.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.table_options_status -> {
                            db.collection("restaurants").document(restaurantID)
                                .collection("rooms").document(roomID)
                                .collection("tables").whereEqualTo("number", text)
                                .get()
                                .addOnSuccessListener { tableDoc ->
                                    if (!tableDoc.isEmpty) {
                                        val table = tableDoc.documents[0]
                                        val statusNow = table.getBoolean("isAvailable") ?: true
                                        val nextStatus = !statusNow

                                        db.collection("restaurants").document(restaurantID)
                                            .collection("rooms").document(roomID)
                                            .collection("tables").document(table.id)
                                            .update("isAvailable", nextStatus)
                                            .addOnSuccessListener {
                                                background = ContextCompat.getDrawable(
                                                    this@TableMapActivity,
                                                    if (nextStatus) R.drawable.vector_table_green
                                                    else R.drawable.vector_table_red
                                                )
                                            }
                                    }
                                }
                            true
                        }
                        R.id.table_options_edit -> {
                            val intent = Intent(this@TableMapActivity, EditTablesActivity::class.java).apply {
                                putExtra("restaurantID", restaurantID)
                                putExtra("roomID", roomID)
                                putExtra("tableNumber", text)
                                putExtra("coordRow", row)
                                putExtra("coordCol", col)
                            }
                            startActivity(intent)
                            true
                        }
                        R.id.table_options_delete -> {
                            val tableButton = it as Button
                            db.collection("restaurants").document(restaurantID)
                                .collection("rooms").document(roomID)
                                .collection("tables").whereEqualTo("number", text)
                                .get()
                                .addOnSuccessListener { tableDoc ->
                                    if (!tableDoc.isEmpty) {
                                        val table = tableDoc.documents[0]

                                        db.collection("restaurants").document(restaurantID)
                                            .collection("rooms").document(roomID)
                                            .collection("tables").document(table.id)
                                            .delete()
                                            .addOnSuccessListener {
                                                gridLayout.removeView(tableButton)
                                            }
                                    }
                                }
                            true
                        }
                        else -> false
                    }
                }
                tableOptions.show()
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

                        addTable(row, col, tableNumber.toString(), tableSize, status, restaurantID, roomID)
                    }
                }
            }
    }
}