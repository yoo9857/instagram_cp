package com.example.instagram_st
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private lateinit var et_email:EditText
    private lateinit var et_pwd: EditText
    private lateinit var btn_eamil: Button
    private lateinit var btn_google:Button

    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        et_email = findViewById(R.id.email_edittext)
        et_pwd = findViewById(R.id.password_edittext)

        btn_eamil = findViewById(R.id.email_login_button)
        btn_eamil.setOnClickListener{
            val str_email = et_email.text.toString()
            val str_pwd = et_pwd.text.toString()
            try{
                signAndSignUp(str_email, str_pwd)
            } catch (e:java.lang.Exception) {
                Toast.makeText(this, "아이디와 비밀번호를 제대로 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_google = findViewById(R.id.google_sign_in_button)
        btn_google.setOnClickListener {
            signInWithGoogle()
        }
    }

    // 회원가입 진행
    fun signAndSignUp(email:String, pwd:String) {
        mAuth?.createUserWithEmailAndPassword(email, pwd)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 회원가입 성공
                movePage(task.result?.user)
            } else { // 회원가입 실패
                signInEmail(email, pwd)
            }
        }
    }

    // 로그인
    fun signInEmail(email:String, pwd:String) {
        mAuth?.signInWithEmailAndPassword(email, pwd)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 로그인 성공
                movePage(task.result?.user)
            } else { // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 페이지 이동
    fun movePage(user: FirebaseUser?) {
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

    // 구글 로그인 활동 결과 처리
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    e("TAG", "Google sign-in failed", e)
                }
            }
        }

    // 구글 로그인 파이어베이스
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    d("TAG", "Signed in with Google: ${user?.displayName}")
                    movePage(user)
                } else {
                    e("TAG", "Google sign-in failed", task.exception)
                }
            }
    }

}
