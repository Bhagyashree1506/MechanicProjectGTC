package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.mechanicP.Helpers.UserHelperClass;
import com.geartocare.mechanicP.R;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNo extends AppCompatActivity {
    TextView codeEnteredByTheUser;
    ProgressBar progressBar;
    Button verify_btn;
    String verificationCodeBySystem;
    FirebaseAuth mAuth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);

        /*------------------------------Hooks start---------------------------------------*/
        verify_btn = findViewById(R.id.verify_btn);
        codeEnteredByTheUser = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);
        /*------------------------------Hooks end---------------------------------------*/



        String phoneNo = getIntent().getStringExtra("phone");

        sendVerificationCodeToUser(phoneNo);


        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(codeEnteredByTheUser.getText().toString().isEmpty()){
                    Toast.makeText(VerifyPhoneNo.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }else{

                    verifyCode(codeEnteredByTheUser.getText().toString());
                }

            }
        });



    }

    private void sendVerificationCodeToUser(String phoneNo) {

        mAuth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        progressBar.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);

        if(getIntent().getStringExtra("purpose").equals("resetPassword")){

           resetPassword(credential);
        }else if(getIntent().getStringExtra("purpose").equals("register")){

            signInTheUserByCredentials(credential);
        }



    }

    private void resetPassword(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String c = codeEnteredByTheUser.getText().toString();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            String phone = getIntent().getStringExtra("phone");

                            Toast.makeText(VerifyPhoneNo.this, "Code Verified", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerifyPhoneNo.this, resetPasswordActivity.class).putExtra("phone",phone));
                            finish();

                        } else {

                            Toast.makeText(VerifyPhoneNo.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseAuth.signOut();


    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String c = codeEnteredByTheUser.getText().toString();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    enterDataToDataBase();



                    Toast.makeText(VerifyPhoneNo.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyPhoneNo.this, LoginActivity.class));
                    finish();

                } else {

                    Toast.makeText(VerifyPhoneNo.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }








    public void enterDataToDataBase(){

        reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Intent carrier = getIntent();

        String firstName = carrier.getStringExtra("firstName");
        String lastname = carrier.getStringExtra("lastName");
        String email = carrier.getStringExtra("email");
        String phone = carrier.getStringExtra("phone");
        String password = carrier.getStringExtra("password");






        UserHelperClass UserHelper =  new UserHelperClass(lastname,firstName,phone,email,password);


        reference.child(phone).setValue(UserHelper);

    }


}