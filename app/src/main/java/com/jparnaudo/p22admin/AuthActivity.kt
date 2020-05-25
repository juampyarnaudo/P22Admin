package com.jparnaudo.p22admin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jparnaudo.apcentro22.login.ForgotPasswordActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlin.properties.Delegates

class AuthActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressDialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    val usuario = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
// Duermo la app por 1500 milisegundos para que se vea el Splash
        Thread.sleep(1500)
// Agrego el tema actual y quito el Splash
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)


// Agregar analytics de google
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)
        //Setup
        setup()
        session()
        initialise()

        forgotPasswordButton.setOnClickListener {
            goToActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun initialise() {
        //llamamos nuestras vista
        txtEmail = findViewById(R.id.metEmail)
        txtPassword = findViewById(R.id.metPassword)
        progressBar = ProgressDialog(this)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        databaseReference = database.reference.child("Users")
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            updateUserInfoAndGoHome()
        }
    }
    private fun createNewAccount() {

        //Obtenemos los datos de nuestras cajas de texto
        email = txtEmail.text.toString()
        password = txtPassword.text.toString()

//Verificamos que los campos estén llenos
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

/*Antes de iniciar nuestro registro bloqueamos la pantalla o también podemos usar una barra de proceso por lo que progressbar está obsoleto*/

            progressBar.setMessage("Usuario registrado...")
            progressBar.show()

//vamos a dar de alta el usuario con el correo y la contraseña
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {

//Si está en este método quiere decir que todo salio bien en la autenticación

/*Una vez que se dio de alta la cuenta vamos a dar de alta la información en la base de datos*/

/*Vamos a obtener el id del usuario con que accedio con currentUser*/
                    val user:FirebaseUser = auth.currentUser!!
/*Damos de alta la información del usuario enviamos el la referencia para guardarlo en la base de datos  de preferencia enviamos el id para que no se repita*/
                    val currentUserDb = databaseReference.child(user.uid)
//Agregamos el nombre y el apellido dentro de user/id/
//                    currentUserDb.child("firstName").setValue(firstName)
//                    currentUserDb.child("lastName").setValue(lastName)
                    currentUserDb.child("Email").setValue(email)
//Por último nos vamos a la vista home
                    updateUserInfoAndGoHome()

                }.addOnFailureListener{
// si el registro falla se mostrara este mensaje
                    Toast.makeText(this, "Error en la autenticación.",
                        Toast.LENGTH_SHORT).show()
                }

        } else {
            Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUserInfoAndGoHome() {
        //Nos vamos a la actividad home
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//ocultamos el progress
        progressBar.hide()

    }
    private fun setup() {
        title = "Autenticación"
        signInButton.setOnClickListener {
            if (metEmail.text.isNotEmpty() && metPassword.text.isNotEmpty()) {
                progressBar.setMessage("Usuario registrado...")
                progressBar.show()
                auth.createUserWithEmailAndPassword(
                    metEmail.text.toString(), metPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user:FirebaseUser = auth.currentUser!!
                        val currentUserDb = databaseReference.child(user.uid)
                        currentUserDb.child("Email").setValue(txtEmail.text.toString())
                       updateUserInfoAndGoHome()

                    } else {
                        showAlert()
                    }
                }
            }

        }
        logInButton.setOnClickListener {
            if (metEmail.text.isNotEmpty() && metPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    metEmail.text.toString(), metPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        metEmail.setText("")
                        metPassword.setText("")
                        updateUserInfoAndGoHome()

                    } else {
                        showAlert()
                    }
                }
            }
        }
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

//    private fun showHome(email: String, provider: ProviderType, nombre: String) {
//
//        val homeIntent = Intent(this, HomeActivity::class.java).apply {
//            putExtra("email", email)
//            putExtra("provider", provider.name)
//            putExtra("nombre", nombre)
//        }
//        startActivity(homeIntent)
//    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Presione atras otra vez para salir...", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}
