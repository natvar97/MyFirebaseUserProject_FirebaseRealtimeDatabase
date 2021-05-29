package com.indialone.myfirebaseuserproject

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.indialone.myfirebaseuserproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseDbInstance: FirebaseDatabase
    private lateinit var firebaseDb: DatabaseReference
    private lateinit var mBinding: ActivityMainBinding
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        firebaseDbInstance = FirebaseDatabase.getInstance()

        firebaseDb = firebaseDbInstance.getReference(Constants.USERS)

        firebaseDbInstance.getReference(Constants.APP_TITLE_NODE).setValue(Constants.APP_TITLE)

        firebaseDbInstance.getReference(Constants.APP_TITLE_NODE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val title = snapshot.getValue(String::class.java)
                    supportActionBar!!.title = title
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("tag - error", error.message)
                }
            })

        mBinding.btnSave.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btn_save -> {
                    val name = mBinding.etName.text.toString()
                    val email = mBinding.etEmail.text.toString()
                    if (TextUtils.isEmpty(userId)) {
                        createUser(name, email)
                    } else {
                        updateUser(name, email)
                    }
                }
            }
        }
    }

    private fun createUser(name: String, email: String) {
        userId = firebaseDb.push().key
        val user = User(name, email)
        firebaseDb.child(userId!!).setValue(user)
        addUserChangeListener()
    }

    private fun updateUser(name: String, email: String) {
        if (!TextUtils.isEmpty(name)) {
            firebaseDb.child(userId!!).child(Constants.NAME).setValue(name)
        }
        if (!TextUtils.isEmpty(email)) {
            firebaseDb.child(userId!!).child(Constants.EMAIL).setValue(email)
        }
        addUserChangeListener()
    }

    private fun addUserChangeListener() {
        firebaseDb.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                mBinding.tvResult.text = "name : ${user!!.name} \nemail : ${user.email}"
                mBinding.etEmail.setText("")
                mBinding.etName.setText("")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("tag-error" , error.message)
            }

        })
    }

}