package com.gaurav.mvvmsampleapp.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    var email: String? = null
    var password:String? = null
    var authListener: AuthListener? = null

    fun onLoginClick(view: View){
        authListener?.onStarted()
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            authListener?.onFailure("Invalid email or password")
            return
        }
        authListener?.onSuccess()
    }
}