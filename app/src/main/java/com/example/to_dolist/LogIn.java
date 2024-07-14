package com.example.to_dolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {



    EditText email,password;
    LottieAnimationView lottieAnimationView;

    TextView log_in,register;
    FirebaseAuth firebaseAuth ;

    public static String TAG = "RequesError";
    ImageView hight_pass;
    ScrollView lagin_layout;

    TextView forger_pass;

    public static SharedPreferences sharedPreferences ;
    public static SharedPreferences.Editor editor;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        lottieAnimationView = findViewById(R.id.animationView);
        log_in = findViewById(R.id.log_in);
        register = findViewById(R.id.register);
        hight_pass = findViewById( R.id.hight_pass);
        lagin_layout = findViewById(R.id.lagin_layout);
        forger_pass = findViewById(R.id.forger_pass);

        firebaseAuth = FirebaseAuth.getInstance();
        lagin_layout.setVisibility(View.GONE);



        sharedPreferences= getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String check = sharedPreferences.getString("checkverify","hhh");

        if (check.contains("verified")){
            startActivity(new Intent(LogIn.this,Home.class));
        }else {
            lagin_layout.setVisibility(View.VISIBLE);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this,Register.class));
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

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_email = email.getText().toString();
                String s_pass = password.getText().toString();

                if (TextUtils.isEmpty(s_email)){
                    Toast.makeText(LogIn.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    email.setError("enter your email");
                    email.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(s_email).matches()){
                    Toast.makeText(LogIn.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    email.setError("Valied email is Required");
                    email.requestFocus();
                }else if (TextUtils.isEmpty(s_pass)){
                    Toast.makeText(LogIn.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    password.setError("password your Required");
                    password.requestFocus();
                }else {
                    lottieAnimationView.setVisibility(View.VISIBLE);

                    LogINMethod(s_email,s_pass);
                }
            }
        });


    }

    private void LogINMethod(String sEmail, String sPass) {

        firebaseAuth.signInWithEmailAndPassword(sEmail,sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


               if (task.isSuccessful()){
                   FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                   if (firebaseUser.isEmailVerified()){
                       editor.putString("checkverify","verified");
                       editor.apply();
                       startActivity(new Intent(LogIn.this,Home.class));
                   }else {
                       firebaseUser.sendEmailVerification();
                       firebaseAuth.signOut();
                       ShowAlertDialog();
                   }

                   lottieAnimationView.setVisibility(View.GONE);


               }else {
                   lottieAnimationView.setVisibility(View.GONE);

                   try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                        if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                            Toast.makeText(LogIn.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                            password.setError("Invalid Password");
                            password.requestFocus();
                        } else {
                            Toast.makeText(LogIn.this, "Invalid credentials, please check and re-enter.", Toast.LENGTH_SHORT).show();
                            email.setError("Invalid credentials, please check and re-enter.");
                            email.requestFocus();
                        }
                    }catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(LogIn.this, "User does not exist. Register again.", Toast.LENGTH_SHORT).show();
                        email.setError("User does not exist. Register again.");
                        email.requestFocus();
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage()); // Use Log.e for errors
                        Toast.makeText(LogIn.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    lottieAnimationView.setVisibility(View.GONE);

                }
            }
        });
    }

    private void ShowAlertDialog() {

        new AlertDialog.Builder(LogIn.this)
                .setTitle("Email Not Verified")
                .setMessage("please verify your Email address. You can not login without verify")
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }
}