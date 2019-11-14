package com.gaurav.walllpaperhub.fragmets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.glide.GlideApp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_settings_default.*
import kotlinx.android.synthetic.main.fragment_settings_default.settings_progress
import kotlinx.android.synthetic.main.fragment_settings_logged_in.*


class SettingsFragment
    : Fragment() {

    companion object{
        const val GOOGLE_SIGN_IN_CODE = 212
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (FirebaseAuth.getInstance().currentUser == null) {
            return inflater.inflate(R.layout.fragment_settings_default, container, false)
        }
        return inflater.inflate(R.layout.fragment_settings_logged_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity as AppCompatActivity, googleSignInOptions)

        if (FirebaseAuth.getInstance().currentUser != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
                GlideApp.with(activity as AppCompatActivity)
                    .load(currentUser?.photoUrl.toString())
                    .into(logged_in_image)
            username.text = currentUser?.displayName
            user_email.text = currentUser?.email

            logout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                googleSignInClient.signOut()

                (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.content_area, SettingsFragment())
                    .commit()
            }
        } else {

            btn_sig_in.setOnClickListener {
                val intent = googleSignInClient.signInIntent
                settings_progress.visibility = View.VISIBLE
                startActivityForResult(intent, GOOGLE_SIGN_IN_CODE)
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseLoginWithGoogle(account)
            } catch (e: ApiException) {
                settings_progress.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseLoginWithGoogle(account: GoogleSignInAccount?) {
        val mAuth = FirebaseAuth.getInstance()
        val authCredential = GoogleAuthProvider.getCredential(account?.idToken, null)

        mAuth.signInWithCredential(authCredential).addOnSuccessListener {
            settings_progress.visibility = View.GONE
            (activity as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_area, SettingsFragment())
                .commit()
        }.addOnFailureListener {
            settings_progress.visibility = View.GONE
            Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }
}