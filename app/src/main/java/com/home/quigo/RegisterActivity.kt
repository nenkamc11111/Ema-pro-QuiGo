package com.home.quigo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.home.quigo.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //View Binding
    private  lateinit var binding: ActivityRegisterBinding
    //Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    //progress Dialog
    private  lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //Init Progress dialog, will show while creating account | Register user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        //Handle back Button click
        binding.backBtn.setOnClickListener{
            onBackPressed() //goto previous screen
        }

        //Handle Register Button click
        binding.registerBtn.setOnClickListener{
            /*Steps
            * 1) Input Data
            * 2) Validate Data
            * 3) Create Account - Firebase Auth
            * 4) Save User Info - Firebase Realtime DB*/
            validateData()
        }
    }

    private var name =""
    private var email =""
    private var password =""

    private fun validateData() {
        // 1) Input Data
        name= binding.nameEt.text.toString().trim()
        email= binding.emailEt.text.toString().trim()
        password= binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        // 2) Validate Data
        if(name.isEmpty()){
            //empty name...
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern...
            Toast.makeText(this, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            //Empty password...
            Toast.makeText(this, "Enter A valid Password...", Toast.LENGTH_SHORT).show()
        }
        else if(cPassword.isEmpty()){
            //Empty password...
            Toast.makeText(this, "Confirm Password...", Toast.LENGTH_SHORT).show()
        }
        else if(password != cPassword){
            //Empty password...
            Toast.makeText(this, "Passwords do not Match...", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        // 3) Create Account - Firebase Auth

        //show Progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //Account created, now user info in db
                updateUserInfo()
            }
            .addOnFailureListener{e->
                //Failed Creating Account
                progressDialog.dismiss()
                Toast.makeText(this, "Failure during Account creation...${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
       // 4) Save User Info - Firebase Realtime DB

        progressDialog.setMessage("Saving User info...")

        //timestamp
        val timestamp = System.currentTimeMillis()

        //get current user uid
        val uid =firebaseAuth.uid

        //setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" //later in profile editing
        hashMap["userType"] = "user" // possible values user/admin
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //User info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener{e->
                //Failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}