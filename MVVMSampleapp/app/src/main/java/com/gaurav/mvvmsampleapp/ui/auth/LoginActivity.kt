package com.gaurav.mvvmsampleapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.gaurav.mvvmsampleapp.R
import com.gaurav.mvvmsampleapp.databinding.ActivityLoginBinding
import com.gaurav.mvvmsampleapp.helper.toast

class LoginActivity : AppCompatActivity(), AuthListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val mViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.viewModel = mViewModel
        mViewModel.authListener = this@LoginActivity
    }

    override fun onStarted() {
        toast("Login started")
    }

    override fun onSuccess() {
        toast("Login success")
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}
