package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.R;

import org.jetbrains.annotations.NotNull;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText phoneForgot;
    TextInputLayout phoneForgotL;
    Button sendCode;
    ProgressBar progressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        /*------------------------------Hooks start---------------------------------------*/
        phoneForgot = findViewById(R.id.phoneForgot);
        phoneForgotL = findViewById(R.id.phoneForgotL);
        progressBar = findViewById(R.id.progress_barForgotL);
        progressBar.setVisibility(View.GONE);
        sendCode = findViewById(R.id.resetPassword);
        /*------------------------------Hooks Ends---------------------------------------*/


        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processForgotPassword();
            }
        });




    }



    public void processForgotPassword(){
        progressBar.setVisibility(View.VISIBLE);
        String phone = phoneForgot.getText().toString();

        if(!validatePhone()){
            progressBar.setVisibility(View.GONE);
            return;
        }


        DatabaseReference DBref =  FirebaseDatabase.getInstance().getReference("users");



        Query checkUser = DBref.orderByChild("phone").equalTo(phone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyPhoneNo.class);
                    intent.putExtra("purpose","resetPassword");
                    intent.putExtra("phone",phone);


                    startActivity(intent);
                    finish();



                }else{
                    progressBar.setVisibility(View.GONE);
                    phoneForgotL.setError("No such user exist");
                    phoneForgotL.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });







    }



    private Boolean validatePhone(){
        String phone = phoneForgotL.getEditText().getText().toString();

        if(phone.isEmpty()) {
            phoneForgotL.setError("Field cannot be empty");
            return false;
        }else{
            phoneForgotL.setError(null);
            phoneForgotL.setErrorEnabled(false);
            return true;
        }
    }
}