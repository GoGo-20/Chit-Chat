package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private var _emailText: EditText? = null
    private var _passwordText: EditText? = null
    private var _loginButton: Button? = null
    private var _backToRegistrationLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }

        mAuth = FirebaseAuth.getInstance()

        _loginButton = findViewById<Button>(R.id.already_member_login)
        _backToRegistrationLink = findViewById<TextView>(R.id.back_to_registeration)
        _passwordText = findViewById<EditText>(R.id.password_remember)
        _emailText = findViewById<EditText>(R.id.email_remember)

        _loginButton!!.setOnClickListener { login() }

        _backToRegistrationLink!!.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }

    }


    private fun login() {

        if (!validate()) {
            onLoginFailed()
            return
        }

        _loginButton!!.isEnabled = false



        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()


               mAuth.signInWithEmailAndPassword(email, password)
                   .addOnCompleteListener { task ->
                       if(task.isSuccessful) {
                           onLoginSuccess()
                       }
                       else{
                           Toast.makeText(baseContext, "Error message:" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                       }
               }
    }


    private fun onLoginSuccess() {
        _loginButton!!.isEnabled = true
        startActivity(Intent(this, MainActivity::class.java))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        finish()
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()

        _loginButton!!.isEnabled = true
    }


    private fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

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

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
    }
}