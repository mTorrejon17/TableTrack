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

class TableMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTableMapBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val userID = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fabTables.visibility = View.GONE

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
                }
            }

        val gridLayout = binding.gridLayout

        gridLayout.rowCount = 3
        gridLayout.columnCount = 3

        fun addTable(row: Int, col: Int, text: String, size: Int) {
            val button = Button(this).apply {
                this.text = text
                background = ContextCompat.getDrawable(
                    this@TableMapActivity,
                    R.drawable.vector_table_green
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
                    this.alert("botón en fila $row, columna $col")
                }
            }
            gridLayout.addView(button)
        }

        val tableSize = 100
        addTable(0, 0, "botón (0,0)", tableSize)
        addTable(2, 1, "botón (2,1)", tableSize)
        addTable(1, 1, "botón (1,1)", tableSize)
        addTable(0, 2, "botón (0,2)", tableSize)


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
                        this.moveTo(CreateRoomActivity::class.java)
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
}