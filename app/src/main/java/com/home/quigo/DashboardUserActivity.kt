package com.home.quigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.home.quigo.databinding.ActivityDashboardAdminBinding
import com.home.quigo.databinding.ActivityDashboardUserBinding


class DashboardUserActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardUserBinding
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_dashboard_user)

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutbtn.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //not logged in, user can stay in dashboard without login too
            binding.subtitleTv.text = "Not Logged In"
        }
        else{
            //logged in, get and show user info
            val email = firebaseUser.email
            //set to textview on toolbar
            binding.subtitleTv.text =email
        }
    }

}