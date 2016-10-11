package com.benjaminearley.bokchoy

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.benjaminearley.bokchoy.model.ListItem
import com.benjaminearley.bokchoy.util.Keys
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*

class ListActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    val RC_SIGN_IN = 0
    val REQUEST_INVITE = 1
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        fab?.setOnClickListener {
            val listItem = ListItem(false, "")
            FirebaseDatabase.getInstance().reference.child(Keys.ITEMS_CHILD).push().setValue(listItem)
        }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Logged In!", Toast.LENGTH_LONG).show()
                (supportFragmentManager.findFragmentById(R.id.listFragment) as? ListActivityFragment)?.setupFirebaseAdapter()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                finish()
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Invite Sent!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logout()
                return true
            }
            R.id.action_invite -> {
                invite()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
            checkLogin(firebaseAuth.currentUser)
    }

    fun checkLogin(user: FirebaseUser?) {
        user ?: run {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.ic_bokchoy)
                            .setTheme(R.style.AppTheme)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                            .build(),
                    RC_SIGN_IN)
        }
    }

    fun logout() {
        AuthUI.getInstance().signOut(this)
    }

    fun invite() {
        val intent = AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build()
        startActivityForResult(intent, REQUEST_INVITE)
    }
}
