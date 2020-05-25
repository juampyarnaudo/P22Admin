package com.jparnaudo.p22admin

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
//import com.squareup.picasso.Picasso
import java.util.regex.Pattern

fun Activity.toast(message: CharSequence,duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message,duration).show()
fun Activity.toast(resourceId: Int,duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resourceId,duration).show()
fun ViewGroup.inflate(layoutId:Int) = LayoutInflater.from(context).inflate(layoutId,this,false)!!
//fun ImageView.loadByURL(url: String) = Picasso.get().load(url).into(this)
//fun ImageView.loadByResource(resource: Int) = Picasso.get().load(resource).fit().into(this)


inline fun <reified T:Activity> Activity.goToActivity(noinline init:Intent.() -> Unit = {}){
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}
//video 96
fun EditText.validate(validation: (String) -> Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            validation(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}


fun Activity.isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

 fun Activity.isValidPassword(password: String): Boolean {
//        Necesita contener ->>  1 numero   1 minuscula 1 mayuscula y minimo 6 caracteres
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
    val pattern = Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}

fun Activity.isValidConfirmPassword(password: String, confirmpassword: String): Boolean {
    return password == confirmpassword
}