package com.benjaminearley.bokchoy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.benjaminearley.bokchoy.model.ListItem
import com.benjaminearley.bokchoy.util.Keys
import com.benjaminearley.bokchoy.util.SignInScreenIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    companion object {

        val TITLE = "listTitle"
        val KEY = "listKey"

        fun startListActivity(context: Context, title: String, key: String) {
            context.startActivity(
                    Intent(context, ListActivity::class.java).putExtra(TITLE, title).putExtra(KEY, key)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = intent.getStringExtra(TITLE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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
            finish()
        }
    }
}
