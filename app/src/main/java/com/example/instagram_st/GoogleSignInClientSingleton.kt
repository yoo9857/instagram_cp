package com.example.instagram_st
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
class GoogleSignInClientSingleton {
    companion object {
        fun getClient(context: Context): Intent {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("792011073431-g9md87uvpjb6a9o7ohmptg0ofse1pdc0.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            return googleSignInClient.signInIntent
        }
    }
}