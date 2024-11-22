package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityTableListBinding

class TableListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTableListBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val userID = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.recyclerViewTables.layoutManager = LinearLayoutManager(this)

        db.collection("users").document(userID.toString()).get()
            .addOnSuccessListener { userDocument ->
                val restaurantID = userDocument.getString("memberOf")
                if (restaurantID != null) {
                    db.collection("restaurants").document(restaurantID)
                        .collection("rooms").get()
                        .addOnSuccessListener {
                            if (!it.isEmpty) {
                                val roomID = it.documents[0].id
                                db.collection("restaurants").document(restaurantID)
                                    .collection("rooms").document(roomID).get()
                                    .addOnSuccessListener {
                                        val roomName = it.getString("name")
                                        binding.title.text = "$roomName"

                                        tablesData(restaurantID)
                                    }
                            }
                        }
                }
            }

        binding.optionsMenu.setOnClickListener {
            val optionsMenu = PopupMenu(this, binding.optionsMenu)

            optionsMenu.menuInflater.inflate(R.menu.table_view_menu, optionsMenu.menu)
            optionsMenu.menu.findItem(R.id.option_change_view).title =
                getString(R.string.option_change_view_map)
            optionsMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option_change_view -> {
                        this.moveTo(TableMapActivity::class.java)
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
    }

    private fun tablesData(restaurantID: String) {
        db.collection("restaurants").document(restaurantID)
            .collection("rooms")
            .get()
            .addOnSuccessListener { roomsSnapshot ->
                if (!roomsSnapshot.isEmpty) {
                    val roomID = roomsSnapshot.documents[0].id
                    db.collection("restaurants").document(restaurantID)
                        .collection("rooms").document(roomID)
                        .collection("tables")
                        .get()
                        .addOnSuccessListener { tablesSnapshot ->
                            val tables = mutableListOf<Table>()
                            for (document in tablesSnapshot) {
                                val number = document.getString("number") ?: ""
                                val isAvailable = document.getBoolean("isAvailable") ?: true
                                tables.add(Table(number, isAvailable))
                            }

                            val tableAdapter = TableAdapter(tables)
                            binding.recyclerViewTables.adapter = tableAdapter
                        }
                }
            }
    }

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
}