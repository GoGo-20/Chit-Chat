package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers : DatabaseReference
    private var firebaseUserID: String = ""

    private var firebaseUser : FirebaseUser? = null

    private var _usernameText: EditText? = null
    private var _emailText: EditText? = null
    private var _passwordText: EditText? = null
    private var _registerButton: Button? = null
    private var _alreadyHaveAccountLink: TextView? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        _registerButton = findViewById<Button>(R.id.register_button)
        _alreadyHaveAccountLink = findViewById<TextView>(R.id.already_have_account_text_view)
        _passwordText = findViewById<EditText>(R.id.password_edittext_register)
        _emailText = findViewById<EditText>(R.id.email_edittext_resgister)
        _usernameText = findViewById<EditText>(R.id.username_edittext_register)


        _registerButton!!.setOnClickListener{
            register()
        }

        _alreadyHaveAccountLink!!.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()


    }



    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null) {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun register() {

        if (!validate()) {
            onRegisterFailed()
            return
        }

        val username = _usernameText!!.text.toString()
        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        _registerButton!!.isEnabled = false

        mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->

                        if(task.isSuccessful){
                            firebaseUserID = mAuth.currentUser!!.uid
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)
                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = firebaseUserID
                            userHashMap["username"] = username
                            userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chitchat-659b9.appspot.com/o/profile.png?alt=media&token=73a29794-60f2-4b89-bc86-3b201e841b05"
                            userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chitchat-659b9.appspot.com/o/cover.jpg?alt=media&token=f78f19ed-7b8e-45a7-a38e-19f45b51fa9e"
                            userHashMap["status"] = "offline"
                            userHashMap["search"] = username.toLowerCase()
                            userHashMap["facebook"] = "https://m.facebook.com"
                            userHashMap["instagram"] = "https://m.instagram.com"
                            userHashMap["snapchat"] = "https://m.snapchat.com"

                            refUsers.updateChildren(userHashMap)
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful)
                                        onRegisterSuccess()
                                }
                        }

                        else{
                            Toast.makeText(baseContext, "Error message:" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }

                }
    }

    private fun onRegisterSuccess() {

        _registerButton!!.isEnabled = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onRegisterFailed() {
        Toast.makeText(baseContext, "Register failed" , Toast.LENGTH_LONG).show()

        _registerButton!!.isEnabled = true

    }

    private fun validate(): Boolean {

        var valid = true

        val username = _usernameText!!.text.toString()
        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        if (username.isEmpty() || username.length < 3) {
            _usernameText!!.error = "at least 3 characters"
            valid = false
        } else {
            _usernameText!!.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "enter a valid email address"
            valid = false
        } else {
            _emailText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText!!.error = null
        }

        return valid
    }

}