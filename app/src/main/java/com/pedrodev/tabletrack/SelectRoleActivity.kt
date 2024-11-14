package com.pedrodev.tabletrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Tasks
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
    private var roleInt: Int? = null
    private var roleString: String? = null
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
            binding.back.isClickable = false
            binding.linearLayout.visibility = View.GONE
            binding.linearLayoutNameCode.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            val db = FirebaseFirestore.getInstance()
            val user = auth.currentUser
            val userID = user?.uid
            var validCode = false

            val task = when (roleInt) {
                1 -> {
                    null
                }
                2, 3 -> {
                    val restaurantCode = binding.editTextNameOrCode.text.toString().trim()
                    db.collection("restaurants").document(restaurantCode).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                Log.e("TAG", "ERROR. El código $restaurantCode no está registrado.")
                                binding.root.alert("Código ingresado no existe.")
                                binding.linearLayout.visibility = View.VISIBLE
                                binding.linearLayoutNameCode.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                validCode = false
                            } else {
                                validCode = true
                            }
                        }
                        .addOnFailureListener {
                            Log.e("TAG", "FAILURELISTENER")
                            validCode = false
                        }
                }
                else -> null
            }

            task?.let {
                Tasks.whenAllComplete(task).addOnCompleteListener {
                    if (!validCode) {
                        Log.d("TAG", "Se detectó un error y se detuvo la ejecución.")
                        return@addOnCompleteListener
                    }
                    Log.d("TAG", "EL CODIGO SIGUIO DESPUES DEL RETURN")
                }
            }



            /*

            val userData = hashMapOf(
                "username" to username,
                "email" to email,
                "role" to roleString,
                "timeCreation" to Timestamp.now()
            )

            if (roleInt == 1) {
                val randomCode = generateRandomCodeRestaurant()
                val restaurantName = binding.editTextNameOrCode.text.toString().trim()

                db.collection("restaurants").document(randomCode).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val restaurantData = hashMapOf(
                                "name" to restaurantName,
                                "adminID" to userID,
                                "timeCreation" to Timestamp.now()
                            )



                        } else {

                        }
                    }

                db.collection("users").document(userID.toString()).set(userData)
                    .addOnSuccessListener {
                        Log.d("TAG", "Admin $username")
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "Usuario $username NO asociado con restaurantes")
                    }


            } else {
                val restaurantCode = binding.editTextNameOrCode.text.toString().trim()

                db.collection("users").document(userID.toString()).set(userData)
                    .addOnSuccessListener {
                        Log.d("TAG", "Usuario $username creado y asociado con éxito a restaurante $restaurantCode")
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "Usuario $username NO asociado con restaurantes")
                    }

                db.collection("restaurants").document(restaurantCode).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            binding.root.alert("Código ingresado no existe.")
                             return@addOnSuccessListener
                        } else {
                           db.collection("users").document(userID.toString())
                               .set()
                        }

                    }
            }

            db.collection("users").document(userID.toString())
                .update("restaurantID", restaurantCode)
                .addOnSuccessListener {
                    Log.d("TAG",
                        "usuario asociado con restaurante" +
                                "user: $username | restaurantecode: $restaurantCode")

                    this.moveTo(TableMapActivity::class.java)
                }
                .addOnFailureListener {
                    Log.d("ERROR",
                        "falló asociar usuario y restaurant")
                }

            Log.e("TAG", "El programa siguió")




             */



            /*
            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val userID = user?.uid

                        val userData = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "role" to roleString,
                            "restaurantID" to randomCode,
                            "timeCreation" to Timestamp.now()
                        )
                        // AQUÍ FALTA QUE SE UNA A UN RESTAURANTE EXISTENTE (when 2, 3)
                        when (roleInt) {
                            1 -> {
                                Log.d("ERROR", "ROL ES ADMINISTRADOOOOOOOOOR")
                                val restaurantData = hashMapOf(
                                    "name" to nameOrCode,
                                    "adminID" to userID,
                                    "timeCreation" to Timestamp.now()
                                )

                                db.collection("restaurants").document(randomCode).set(restaurantData)
                                    .addOnSuccessListener {
                                        Log.d("TAG",
                                            "FUNCIONÓ, name: $nameOrCode | code: $randomCode ")
                                    }
                                    .addOnFailureListener {
                                        Log.d("ERROR",
                                            "falló agregar restaurant a db")
                                    }
                            }
                            else -> {
                                Log.d("ERROR", "CUALQUIER OTRO ROOOOOOOOL")
                                randomCode = nameOrCode

                                db.collection("restaurants").document(randomCode).get()
                                    .addOnSuccessListener {
                                        if (it.exists()) {
                                            validRestaurant = true
                                            Log.d("ERROR", "RESTAURANTE SI EXISTEEEEE")
                                            db.collection("users").document(userID.toString())
                                                .update("restaurantID", randomCode)
                                                .addOnSuccessListener {
                                                    Log.d("TAG",
                                                        "usuario asociado con restaurante" +
                                                                "user: $username | restaurantecode: $nameOrCode")
                                                }
                                                .addOnFailureListener {
                                                    Log.d("ERROR",
                                                        "falló asociar usuario y restaurant")
                                                }
                                        } else {
                                            Log.d("ERROR", "NO EXISTE RESTAURANTEEEEEE")
                                            validRestaurant = false
                                            binding.linearLayout.visibility = View.VISIBLE
                                            binding.linearLayoutNameCode.visibility = View.VISIBLE
                                            binding.progressBar.visibility = View.GONE
                                            binding.back.isClickable = true

                                            binding.root.alert("No existe restaurante con ese código. Intente de nuevo")
                                            return@addOnSuccessListener
                                        }
                                    }
                            }
                        }

                        if (!validRestaurant) {
                            Log.d("ERROR", "VALID RESTAURANT FAAAAAAAAAAALSE")
                            return@addOnCompleteListener
                        }

                        userID?.let {
                            db.collection("users").document(it).set(userData)
                                .addOnSuccessListener {
                                    Log.d("ERROR", "USERID LET SUCCESS")
                                    this.moveTo(TableMapActivity::class.java)
                                    Functions.clearData(this, "temp_data")
                                    finish()
                                }
                                .addOnFailureListener {
                                    Log.d("ERROR", "USERID LET FAILURE")
                                    binding.root.alert(getString(R.string.failed_database))
                                    binding.buttonSignUp.isClickable = true
                                }
                        }
                        Log.d("ERROR", "ultimo validRestaurant = $validRestaurant")

                    } else {
                        binding.root.alert(getString(R.string.failed_sign_up))
                        binding.buttonSignUp.isClickable = true

                    }
                }

             */
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


    private fun addUserToDB() {

    }

    private fun addRestaurantToDB() {

    }

    private fun userAuthFirebase() {

    }


}