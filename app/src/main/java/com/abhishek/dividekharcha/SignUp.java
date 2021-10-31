package com.abhishek.dividekharcha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    TextView tv1;
    EditText signupname,signupemail,signuppwd;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tv1=findViewById(R.id.signintv);
        signupname=findViewById(R.id.name);
        signupemail=findViewById(R.id.usernamelogn);
        signuppwd=findViewById(R.id.passwordsignin1);
        btn=findViewById(R.id.signinlog);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);


        //for the textView to switch to signIn
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,SignIn.class);
                startActivity(intent);
                finish();
            }
        });

        //for the btn to register the new user
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();

            }
        });
    }
    private void Register() {
        String str1 = signupname.getText().toString();
        String str2 = signupemail.getText().toString();
        String str3 = signuppwd.getText().toString();

        if (TextUtils.isEmpty(str1)) {
            Toast.makeText(SignUp.this, "Name field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(str2)) {
            Toast.makeText(SignUp.this, "Email field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(str3)) {
            Toast.makeText(SignUp.this, "Password field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        else if (!ValidEmail(str2)) {
            Toast.makeText(SignUp.this, "Invalid Mail", Toast.LENGTH_LONG).show();
            return;
        }
        else if (str3.length()<6) {
            Toast.makeText(SignUp.this, "Password Length is <6", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(str2,str3).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(SignUp.this, "Sign Up failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private boolean ValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}