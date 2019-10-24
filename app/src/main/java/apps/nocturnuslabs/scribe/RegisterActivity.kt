package apps.nocturnuslabs.scribe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        register_register_btn.setOnClickListener(this)
        register_login_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.register_login_btn -> finish()
            R.id.register_register_btn -> {
                createAccount(
                    register_username_edittext.text.toString(),
                    register_email_edittext.text.toString(),
                    register_pass_edittext.text.toString()
            )}
        }
    }

    private fun createAccount(username:String, email: String, password: String) {

        if(checkFields(username,email,password)){
            showProgressDialog(R.string.create_account)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    hideProgressDialog()
                    if (!it.isSuccessful) {
                        Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    } else {
                        //Created User Account
                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
        }else
            Toast.makeText(this,"Please complete all the fields!",Toast.LENGTH_SHORT).show()


    }
}
