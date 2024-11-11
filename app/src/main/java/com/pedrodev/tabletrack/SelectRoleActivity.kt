package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivitySelectRoleBinding
import kotlin.random.Random

class SelectRoleActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var auth: FirebaseAuth
    private var userRole: Int? = null
    private var randomCodeLength = 6
    val charactersInCode : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        auth = Firebase.auth

        binding.progressBar.visibility = View.GONE
        binding.linearLayoutNameCode.visibility = View.GONE

        val username = Functions.getString(this, "temp_data", "username")
        val email = Functions.getString(this, "temp_data", "email")
        val password = Functions.getString(this, "temp_data", "password")

        // ESTO PONE LAS OPCIONES DEL strings.xml EN EL SPINNER
        val spinner: Spinner = binding.spinnerRole
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_role_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        //

        binding.back.setOnClickListener {
            this.moveTo(SignUpActivity::class.java)
        }

        binding.buttonSignUp.setOnClickListener {
            val nameOrCode = binding.editTextNameOrCode.text.toString().trim()
            val randomCode = generateRandomCodeRestaurant()

            binding.back.isClickable = false
            binding.linearLayout.visibility = View.GONE
            binding.linearLayoutNameCode.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        val user = auth.currentUser
                        val userID = user?.uid

                        val userData = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "timeCreation" to Timestamp.now()
                        )
                        // AQUÍ FALTA QUE SE CREE EL RESTAURANTE O QUE SE UNA A UN RESTAURANTE EXISTENTE
                        when (userRole) {
                            1 -> {
                                val restaurantData = hashMapOf(
                                    "name" to nameOrCode,
                                    "adminID" to userID,
                                    "timeCreation" to Timestamp.now()
                                )

                                db.collection("restaurants").document(randomCode).set(restaurantData)
                                    .addOnSuccessListener {
                                        Log.d("TAG", "FUNCIONÓ, name: $nameOrCode | code: $randomCode ")
                                    }
                                    .addOnFailureListener {
                                        Log.d("ERROR", "aaaaaaaaaaaaaa")
                                    }

                            }
                            2, 3 -> {

                            }
                            else -> TODO()
                        }

                        userID?.let {
                            db.collection("users").document(it).set(userData)
                                .addOnSuccessListener {
                                    this.moveTo(TableMapActivity::class.java)
                                    Functions.clearData(this, "temp_data")
                                    finish()
                                }
                                .addOnFailureListener {
                                    binding.root.alert(getString(R.string.failed_database))
                                    binding.buttonSignUp.isClickable = true
                                }
                        }
                    } else {
                        binding.root.alert(getString(R.string.failed_sign_up))
                        binding.buttonSignUp.isClickable = true
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        binding.back.isClickable = true
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (pos) {
            1 -> { // ADMINISTRACIÓN
                binding.textNameOrCode.text = getString(R.string.text_restaurant_name)
                binding.editTextNameOrCode.hint = getString(R.string.hint_restaurant_name)
                binding.linearLayoutNameCode.visibility = View.VISIBLE
            }
            2, 3 -> { // MESEROS Y RECEPCIÓN
                binding.textNameOrCode.text = getString(R.string.text_restaurant_code)
                binding.editTextNameOrCode.hint = getString(R.string.hint_restaurant_code)
                binding.linearLayoutNameCode.visibility = View.VISIBLE
            }
            else -> binding.linearLayoutNameCode.visibility = View.GONE
        }
        userRole = pos
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // aquí hasta ahora no iría nada, pero cuando se borra tira un error xd
    }

    private fun generateRandomCodeRestaurant() = (1..randomCodeLength)
        .map { Random.nextInt(0, charactersInCode.size).let { charactersInCode[it]} }
        .joinToString("")

}