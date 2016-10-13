package com.benjaminearley.bokchoy

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.benjaminearley.bokchoy.util.SignInScreenIntent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_lists.*

class ListsActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    companion object {
        val RC_SIGN_IN = 0
        val REQUEST_INVITE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lists)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        fab?.setOnClickListener {
            AddListBottomSheetFragment().show(supportFragmentManager, AddListBottomSheetFragment.TAG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.login_toast), Toast.LENGTH_LONG).show()
                (supportFragmentManager.findFragmentById(R.id.listsFragment) as? ListsActivityFragment)?.setupFirebaseAdapter()
            } else {
                Toast.makeText(this, getString(R.string.login_failed_toast), Toast.LENGTH_LONG).show()
                finish()
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.invite_sent_toast), Toast.LENGTH_LONG).show()
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lists, menu)
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

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        checkLogin(firebaseAuth.currentUser)
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

    fun checkLogin(user: FirebaseUser?) {
        user ?: run {
            startActivityForResult(
                    SignInScreenIntent,
                    RC_SIGN_IN)
        }
    }

}
