package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivitySingBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class singActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        auth.currentUser
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){res->
            val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null ){
                        firebaseGoogleAuth(account.idToken!!)
                    }
            } catch (e: ApiException){
                    Log.d("Log", "надо поменять ключ (если на другом пк запускаем)")
            }
            }
        binding.button.setOnClickListener(){
           signInWithGoogle()
        }
        checkAuth()
        }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          //  .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return  GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle(){
        val signInClient = getClient()
            launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseGoogleAuth(idToken: String){
       val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener(){
            if(it.isSuccessful){
                Log.d("Log", "Успешно")
                checkAuth()
            }
            else{
                Log.d("Log", "ошибка")
            }
        }
    }
    private fun checkAuth() {
        if(auth.currentUser != null){
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}


