package com.jparnaudo.p22admin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jparnaudo.apcentro22.login.ForgotPasswordActivity
import com.jparnaudo.p22admin.ui.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressDialog
    private lateinit var databaseReference: FirebaseFirestore
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
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
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
//        databaseReference = database.reference.child("Users")
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
        logInButton.setOnClickListener {
            if (metEmail.text.isNotEmpty() && metPassword.text.isNotEmpty()) {
                val str: String = etPinAdmin.text.toString()
                if (str == "1234"){
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
                }else{
                    toast("ingresar el pin correcto")
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
