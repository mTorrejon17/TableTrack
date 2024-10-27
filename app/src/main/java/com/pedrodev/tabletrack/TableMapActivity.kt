package com.pedrodev.tabletrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivityTableMapBinding

class TableMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTableMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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
    } // FIN OnCreate

}