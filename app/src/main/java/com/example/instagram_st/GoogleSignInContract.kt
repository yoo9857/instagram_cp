package com.example.instagram_st
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException


class GoogleSignInContract : ActivityResultContract<Unit, GoogleSignInAccount?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return GoogleSignInClientSingleton.getClient(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        return if (intent != null) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                null
            }
        } else {
            null
        }
    }
}

