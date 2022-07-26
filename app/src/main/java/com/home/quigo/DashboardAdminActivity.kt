package com.home.quigo

import cdandroid.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.home.quigo.databinding.ActivityDashboardAdminBinding
import com.home.quigo.databinding.ActivityMainBinding

class DashboardAdminActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityDashboardAdminBinding
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutbtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //not logged in, goto main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            //logged in, get and show user info
            val email = firebaseUser.email
            //set to textview on toolbar
            binding.subtitleTv.text =email
        }
    }
}