package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pedrodev.tabletrack.Functions.alert
import com.pedrodev.tabletrack.Functions.closeKeyboard
import com.pedrodev.tabletrack.Functions.moveTo
import com.pedrodev.tabletrack.databinding.ActivitySelectRoleBinding
import kotlin.random.Random

class SelectRoleActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var auth: FirebaseAuth
    private var roleInt: Int? = null
    private var roleString: String? = null
    private var randomCodeLength = 6
    var restaurantName = ""
    var restaurantCode = ""
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
            closeKeyboard()
            binding.back.isClickable = false
            binding.linearLayout.visibility = View.GONE
            binding.linearLayoutNameCode.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            val db = FirebaseFirestore.getInstance()
            var stopFunction = false

            if (roleInt != 1) {
                restaurantCode = binding.editTextNameOrCode.text.toString().trim()
                val validCodeTask = db.collection("restaurants").document(restaurantCode).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            stopFunction = false
                        } else {
                            stopFunction = true
                        }
                    }

                Tasks.whenAllComplete(validCodeTask)
                    .addOnCompleteListener {
                        if (stopFunction) {
                            binding.root.alert("Código ingresado no existe.")
                            return@addOnCompleteListener
                        }
                    }
            }

            val authTask = auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userID = user?.uid
                    } else {
                        binding.root.alert(getString(R.string.failed_sign_up))
                        stopFunction = true
                    }
                }

            Tasks.whenAllComplete(authTask)
                .addOnCompleteListener {
                    if (stopFunction) {
                        val user = auth.currentUser
                        user?.delete()

                        Log.e("ERROR", "EL CÓDIGO INGRESADO FALLÓOOO")
                        binding.back.isClickable = true
                        binding.linearLayout.visibility = View.VISIBLE
                        binding.linearLayoutNameCode.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        return@addOnCompleteListener
                    } else {
                        val user = auth.currentUser
                        val userID = user?.uid
                        when (roleInt) {
                            1 -> {
                                restaurantName = binding.editTextNameOrCode.text.toString().trim()
                                restaurantCode = generateRandomCodeRestaurant()
                                val userData = hashMapOf(
                                    "username" to username,
                                    "email" to email,
                                    "role" to roleString,
                                    "memberOf" to restaurantCode,
                                    "timeCreation" to Timestamp.now()
                                )
                                val restaurantData = hashMapOf(
                                    "name" to restaurantName,
                                    "adminID" to userID,
                                    "timeCreation" to Timestamp.now()
                                )

                                db.collection("restaurants").document(restaurantCode).set(restaurantData)
                                    .addOnSuccessListener {
                                        Log.d("TAG",
                                            "RESTAURANTE AGREGADO A DB, name: $restaurantName | code: $restaurantCode ")
                                    }
                                    .addOnFailureListener {
                                        Log.d("ERROR",
                                            "FALLÓ agregar restaurant a db")
                                    }

                                db.collection("users").document(userID.toString()).set(userData)
                                    .addOnSuccessListener {
                                        Log.d("TAG",
                                            "ADMIN AGREGADO A DB, name: $username | member of: $restaurantCode ")
                                    }
                                    .addOnFailureListener {
                                        Log.d("ERROR",
                                            "FALLÓ agregar admin a db")
                                    }
                            }
                            2, 3 -> {
                                restaurantCode = binding.editTextNameOrCode.text.toString().trim()
                                db.collection("restaurants").document(restaurantCode).get()
                                    .addOnSuccessListener { document ->
                                        if (!document.exists()) {
                                            Log.e("ERROR", "ERROOOR El código $restaurantCode no está registrado")
                                            binding.root.alert("Código ingresado no existe.")
                                            binding.linearLayout.visibility = View.VISIBLE
                                            binding.linearLayoutNameCode.visibility = View.VISIBLE
                                            binding.progressBar.visibility = View.GONE
                                        } else {
                                            val userData = hashMapOf(
                                                "username" to username,
                                                "email" to email,
                                                "role" to roleString,
                                                "memberOf" to restaurantCode,
                                                "timeCreation" to Timestamp.now()
                                            )

                                            db.collection("users").document(userID.toString()).set(userData)
                                                .addOnSuccessListener {
                                                    Log.d("TAG", "USER $username rol $roleString añadido correctamente a la db")
                                                }
                                                .addOnFailureListener {
                                                    Log.e("ERROR", "FALLÓ agregar a user $username a la db")
                                                }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.e("TAG", "FAILURELISTENER")
                                    }
                            }
                        }
                        Functions.clearData(this, "temp_data")
                        this.moveTo(TableMapActivity::class.java)
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        binding.back.isClickable = true
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        binding.linearLayoutNameCode.visibility = View.VISIBLE
        roleInt = pos
        when (pos) {
            1 -> { // ADMINISTRACIÓN
                binding.textNameOrCode.text = getString(R.string.text_restaurant_name)
                binding.editTextNameOrCode.hint = getString(R.string.hint_restaurant_name)
                roleString = "admin"
            }
            2 -> { // MESEROS
                binding.textNameOrCode.text = getString(R.string.text_restaurant_code)
                binding.editTextNameOrCode.hint = getString(R.string.hint_restaurant_code)
                roleString = "waiter"
            }
            3 -> {  // RECEPCIÓN
                binding.textNameOrCode.text = getString(R.string.text_restaurant_code)
                binding.editTextNameOrCode.hint = getString(R.string.hint_restaurant_code)
                roleString = "receptionist"
            }
            else -> binding.linearLayoutNameCode.visibility = View.GONE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // aquí hasta ahora no iría nada, pero cuando se borra la función tira un error xd
    }

    private fun generateRandomCodeRestaurant() = (1..randomCodeLength)
        .map { Random.nextInt(0, charactersInCode.size).let { charactersInCode[it]} }
        .joinToString("")
    // esto genera el código para el restaurante de 6 caracteres

    private fun createRandomRestaurantID() {
        val db = FirebaseFirestore.getInstance()
        val randomCode = generateRandomCodeRestaurant()

        db.collection("restaurants").document(randomCode).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val restaurantData = hashMapOf(
                        "name" to restaurantName,
                        "timeCreation" to Timestamp.now()
                    )

                    db.collection("restaurants").document(randomCode).set(restaurantData)
                        .addOnSuccessListener {
                            Log.d("TAG", "Restaurante CREADO. $restaurantName código $restaurantCode")
                        }
                        .addOnFailureListener {
                            Log.e("ERROR", "Creación Restaurante FALLIDA.")
                        }
                } else {
                    createRandomRestaurantID()
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "Error en uniqueRandomCode()!!!!!!!!")
            }
    }
}