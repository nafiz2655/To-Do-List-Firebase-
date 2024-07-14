package com.example.to_dolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText email,password,con_password;

    TextView register,login;
    ImageView hight_pass,hight_con_pass;
    LottieAnimationView animationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.con_password);
        register = findViewById(R.id.register);
        hight_pass = findViewById(R.id.hight_pass);
        hight_con_pass = findViewById(R.id.hight_con_pass);
        animationView = findViewById(R.id.animationView);
        login = findViewById(R.id.login);


        Toast.makeText(this, "register Activity", Toast.LENGTH_SHORT).show();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        hight_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hight_pass.setImageResource(R.drawable.hide);
                }else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hight_pass.setImageResource(R.drawable.view);
                }
            }
        });

        hight_con_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (con_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    con_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hight_con_pass.setImageResource(R.drawable.hide);
                }else {
                    con_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hight_con_pass.setImageResource(R.drawable.view);
                }
            }
        });




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s_email = email.getText().toString();
                String s_password = password.getText().toString();
                String con_pass = con_password.getText().toString();
                if (TextUtils.isEmpty(s_email)){
                    Toast.makeText(Register.this, "Enter Your Email Address", Toast.LENGTH_SHORT).show();
                    email.setError("Enter Email");
                    email.requestFocus();
                    //num,lowe,cou,sim,upe
                }else if (!Patterns.EMAIL_ADDRESS.matcher(s_email).matches()){
                    Toast.makeText( Register.this, "Enter Your  Valied Email Address", Toast.LENGTH_SHORT).show();
                    email.setError("Email");
                    email.requestFocus();
                }else if (TextUtils.isEmpty(s_password)){
                    Toast.makeText(Register.this, "Enter Strong Passwoed", Toast.LENGTH_SHORT).show();
                    password.setError("Password");
                    password.requestFocus();
                    password.setBackgroundResource(R.drawable.error);
                }else if(con_pass.length()==0||!con_pass.contains(s_password)){
                    Toast.makeText(Register.this, "Passwoed Error", Toast.LENGTH_SHORT).show();
                    con_password.setError("Continue Password");
                    con_password.requestFocus();
                    con_password.setBackgroundResource(R.drawable.error);
                }else {
                    animationView.setVisibility(View.VISIBLE);
                    Registration(s_email,s_password);

                }

            }
        });

    }

    private void Registration(String sEmail, String sPassword) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



        firebaseAuth.createUserWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(Register.this,LogIn.class));
                    animationView.setVisibility(View.GONE);
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        password.setError("Your Password too week");
                        password.requestFocus();
                        animationView.setVisibility(View.GONE);


                    }catch (FirebaseAuthInvalidCredentialsException e){
                        email.setError("Your Email Address invalide");
                        email.requestFocus();
                        animationView.setVisibility(View.GONE);

                    }catch (FirebaseAuthUserCollisionException e){
                        email.setError("User is alrady registerd. use anather Email");
                        email.requestFocus();
                        animationView.setVisibility(View.GONE);

                    }catch (Exception e){

                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });




    }


}