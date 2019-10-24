package apps.nocturnuslabs.scribe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        login_register_btn.setOnClickListener(this)
        login_login_btn.setOnClickListener(this)
        login_gsign_in_button.setOnClickListener(this)

        //Google sign-in code
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.login_register_btn -> startActivity(Intent(this, RegisterActivity::class.java))
            R.id.login_login_btn -> loginwithAccount(
                login_email_edittext.text.toString(),
                login_pass_edittext.text.toString()
            )
            R.id.login_gsign_in_button -> gSignIn()
        }

    }

    private fun gSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,101)
    }

    private fun loginwithAccount(email: String, password: String) {
        if(checkFields(email,password)){

            showProgressDialog(R.string.login_account)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    hideProgressDialog()
                    if (!it.isSuccessful) {
                        Toast.makeText(this, "Couldn't log you in!", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    } else {
                        //Show MainActivity
                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
        }else
            Toast.makeText(this,"Please fill all the fields!",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
            when(requestCode){

                101 -> try {
                    val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account!!)

                }catch(e:ApiException){
                    //Do something
                }
            }
    }

    private fun firebaseAuthWithGoogle(account:GoogleSignInAccount){
        showProgressDialog(R.string.login_account)

        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){
                hideProgressDialog()
                if(!it.isSuccessful){
                    Snackbar.make(login_layout, "Couldn't log you in!",Snackbar.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }else{
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                    intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("GOOGLE_ACCOUNT", account)
                    startActivity(intent)
                    finish()
                }
            }

    }
}
