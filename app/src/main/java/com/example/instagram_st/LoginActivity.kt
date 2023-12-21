package com.example.instagram_st
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Arrays

class LoginActivity : AppCompatActivity() {
    private lateinit var et_email: EditText
    private lateinit var et_pwd: EditText
    private lateinit var btn_email: Button
    private lateinit var btn_google: Button
    private lateinit var btn_facebook: Button
    private lateinit var callbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // CallbackManager 초기화
        callbackManager = CallbackManager.Factory.create()

        et_email = findViewById(R.id.email_edittext)
        et_pwd = findViewById(R.id.password_edittext)

        btn_email = findViewById(R.id.email_login_button)
        btn_email.setOnClickListener {
            val str_email = et_email.text.toString()
            val str_pwd = et_pwd.text.toString()
            try {
                signAndSignUp(str_email, str_pwd)
            } catch (e: Exception) {
                Toast.makeText(this, "아이디와 비밀번호를 제대로 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_google = findViewById(R.id.google_sign_in_button)
        btn_google.setOnClickListener {
            signInWithGoogle()
        }

        btn_facebook = findViewById(R.id.facebook_login_button)
        btn_facebook.setOnClickListener {
            facebookLogin()
        }


        // ActivityResultLauncher 초기화
        signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        Log.e("TAG", "Google sign-in failed", e)
                    }
                }
            }
    }



    // 회원가입 진행
    private fun signAndSignUp(email: String, pwd: String) {
        mAuth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 회원가입 성공
                    movePage(task.result?.user)
                } else { // 회원가입 실패
                    signInEmail(email, pwd)
                }
            }
    }

    // 로그인
    private fun signInEmail(email: String, pwd: String) {
        mAuth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 로그인 성공
                    movePage(task.result?.user)
                } else { // 로그인 실패
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 페이지 이동
    private fun movePage(user: FirebaseUser?) {
        if (user != null) {
            try {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("MovePage", "Error starting MainActivity: ${e.message}")
            }
        }
    }

    // 구글 로그인
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("792011073431-g9md87uvpjb6a9o7ohmptg0ofse1pdc0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    // 페이스북 로그인

    private fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let { handleFacebookAccessToken(it.accessToken) }
                }

                override fun onCancel() {
                    // 취소 시 처리
                }

                override fun onError(error: FacebookException?) {
                    // 에러 시 처리
                    Log.e("TAG", "Facebook 로그인 에러: ${error?.message}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Facebook 로그인 에러: ${error?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    // 구글 로그인 파이어베이스
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Log.d("TAG", "Signed in with Google: ${user?.displayName}")
                    movePage(user)
                } else {
                    Log.e("TAG", "Google sign-in failed", task.exception)
                    Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 페이스북 액세스 토큰을 사용한 Firebase 로그인
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase 로그인 성공
                    val user = mAuth.currentUser
                    movePage(user)
                } else {
                    // Firebase 로그인 실패
                    Toast.makeText(
                        this@LoginActivity,
                        "Firebase 로그인 실패: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
