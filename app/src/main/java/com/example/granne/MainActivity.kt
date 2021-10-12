package com.example.granne

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

    }

    fun startLoginActivity(view: View) {
        Log.d("!Main", "Pressed login button")

        // När man trycker på login knappen på första sidan så ska programmet kolla om
        // switchen "keep me logged in" == true. och då ska man skickas direkt till mainscreen
        // när man trycker på "Login" knappen

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    fun startCreateAccount(view: View) {
        val createAccountIntent = Intent(this, CreateAccountActivity::class.java)
        startActivity(createAccountIntent)
    }
}