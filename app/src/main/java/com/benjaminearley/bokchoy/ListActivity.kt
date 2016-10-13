package com.benjaminearley.bokchoy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.benjaminearley.bokchoy.model.ListItem
import com.benjaminearley.bokchoy.util.Keys
import com.benjaminearley.bokchoy.util.SignInScreenIntent
import com.benjaminearley.bokchoy.util.animateTitleChange
import com.benjaminearley.bokchoy.util.colorThemeMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    companion object {

        val TITLE = "title"
        val COLOR = "color"
        val KEY = "key"

        fun startListActivity(context: Context, title: String, color: String, key: String) {
            context.startActivity(
                    Intent(context, ListActivity::class.java)
                            .putExtra(TITLE, title)
                            .putExtra(COLOR, color)
                            .putExtra(KEY, key)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(colorThemeMap[intent.getStringExtra(COLOR)]!!)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        toolbar.title = intent.getStringExtra(TITLE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        FirebaseDatabase.getInstance().reference.child(Keys.LISTS_CHILD).child(intent.getStringExtra(KEY)).child(Keys.LISTS_TITLE).addValueEventListener(object : ValueEventListener {

            var shouldAnimateTitle = false

            override fun onCancelled(databaseError: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (shouldAnimateTitle) {
                    animateTitleChange(this@ListActivity, toolbar, dataSnapshot?.getValue(String::class.java) ?: "")
                } else {
                    shouldAnimateTitle = true
                }
            }
        })

        fab?.setOnClickListener {
            val listItem = ListItem(false, "")
            FirebaseDatabase
                    .getInstance()
                    .reference
                    .child(Keys.LISTS_CHILD)
                    .child(intent.getStringExtra(KEY))
                    .child(Keys.LIST_CHILD)
                    .push()
                    .setValue(listItem)
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        checkLogin(firebaseAuth.currentUser)
    }

    fun checkLogin(user: FirebaseUser?) {
        user ?: run {
            startActivityForResult(
                    SignInScreenIntent,
                    ListsActivity.RC_SIGN_IN)
        }
    }
}
