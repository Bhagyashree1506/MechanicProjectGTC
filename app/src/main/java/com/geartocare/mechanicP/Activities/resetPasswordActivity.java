package com.geartocare.mechanicP.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.mechanicP.R;

public class resetPasswordActivity extends AppCompatActivity {

    Button resetPassword;
    String phone,password,confirmPassword;
    TextInputLayout passwordL,confirmPasswordL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        /*------------------------------Hooks start---------------------------------------*/
        passwordL = findViewById(R.id.passwordResetL);
        resetPassword = findViewById(R.id.ResetPasswordBtn);
        confirmPasswordL = findViewById(R.id.confirmPasswordResetL);
        /*------------------------------Hooks Ends---------------------------------------*/

        /*------------------------------Variables---------------------------------------*/
        phone = getIntent().getStringExtra("phone");
        /*------------------------------Variables---------------------------------------*/




        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!validatePassword()){
                    return;
                }


                DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users");


                if(passwordL.getEditText().getText().toString().equals(confirmPasswordL.getEditText().getText().toString())){
                    confirmPasswordL.setError(null);
                    DBref.child(phone).child("password").setValue(confirmPasswordL.getEditText().getText().toString());

                    Toast.makeText(resetPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    /*startActivity(new Intent(resetPasswordActivity.this,LoginActivity.class));*/
                    finish();

                }else {
                    confirmPasswordL.setError("Both password should be same");
                    
                    confirmPasswordL.requestFocus();

                }



            }
        });



    }


    public Boolean validatePassword(){
        String password = passwordL.getEditText().getText().toString();

        if(password.length()<5){
            passwordL.setError("atleast 5 characters");
            passwordL.requestFocus();
            return false;
        }else{
            passwordL.setError(null);
            return true;
        }

    }







}