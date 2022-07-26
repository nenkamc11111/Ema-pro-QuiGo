package com.home.quigo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.home.quigo.databinding.ActivityLoginBinding
import com.home.quigo.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {

    //View Binding
    private  lateinit var binding: ActivityLoginBinding
    //Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    //progress Dialog
    private  lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //Init Progress dialog, will show while logging user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        //Handle click, not have account?
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //handle click, login
        binding.loginBtn.setOnClickListener{
           /*
           * 1) Input data
           * 2) Validate data
           * 3) Login - Firebase Auth
           * 4) check user type - Firebase Auth and open dashboard accordingly */
           validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        // 1) Input Data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // 2) validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern...
            Toast.makeText(this, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty() || password.length< 4){
            //Empty password...
            Toast.makeText(this, "Enter A valid Password...", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }


    }

    private fun loginUser() {
       // 3) Login - Firebase Auth

        //show progress
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        //
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //login success
                checkUser()
            }
            .addOnFailureListener{e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        // 4) check user type - Firebase Auth and open dashboard accordingly */

        progressDialog.setMessage("Checking User ...")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type e.g user or admin
                    val userType = snapshot.child("userType").value
                    if(userType == "user"){
                         //simple user, open user dashboard
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                    else if(userType == "admin"){
                        //Admin, open admin dashboard
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}