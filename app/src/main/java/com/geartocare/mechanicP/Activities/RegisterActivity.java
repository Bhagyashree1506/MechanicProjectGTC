package com.geartocare.mechanicP.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.mechanicP.R;

public class RegisterActivity extends AppCompatActivity {
    Button register,AlreadySignIn;
    FirebaseAuth auth;
    TextInputLayout LastNameRegisterLayout,emailRegisterL, passwordRegisterL,phoneRegisterL,FirstNameRegisterL ;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /*------------------------------Navigation Part Starts---------------------------------------*/
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.Register);
        auth = FirebaseAuth.getInstance();
        emailRegisterL = findViewById(R.id.emailRegsiterL);
        passwordRegisterL = findViewById(R.id.passwordRegisterL);
        AlreadySignIn = findViewById(R.id.AlreadySignIn);
        LastNameRegisterLayout = findViewById(R.id.LastNameRegisterLayout);
        phoneRegisterL = findViewById(R.id.phoneRegisterL);
        FirstNameRegisterL = findViewById(R.id.FirstNameRegisterL);
        /*------------------------------Navigation Part Ends---------------------------------------*/








        register.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                registerUser();
            }

        });



        AlreadySignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

        });


    }




    public void registerUser(){
        FirebaseAuth.getInstance().signOut();


        if(!validateName() || !validateLastName() || !validateEmail() || !validatePhone() || !validatePassword()){
            return;
        }

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        String firstName = FirstNameRegisterL.getEditText().getText().toString();
        String lastname = LastNameRegisterLayout.getEditText().getText().toString();
        String email = emailRegisterL.getEditText().getText().toString();
        String phone = phoneRegisterL.getEditText().getText().toString();
        String password = passwordRegisterL.getEditText().getText().toString();

        Intent intent = new Intent(RegisterActivity.this, VerifyPhoneNo.class);

        intent.putExtra("phone",phone);
        intent.putExtra("email",email);
        intent.putExtra("firstName",capitalize(firstName));
        intent.putExtra("password",password);
        intent.putExtra("lastName",capitalize(lastname));
        intent.putExtra("purpose","register");





        startActivity(
                intent
        );

        finish();



    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    //Methods to VALIDATE CREDENTIALS

    public void validateCredentials(){
        validateName();
        validateLastName();
        validateEmail();
        validatePhone();
        validatePassword();
    }


    private Boolean validateName(){
        String fullname = FirstNameRegisterL.getEditText().getText().toString();


        if(fullname.isEmpty()){
            FirstNameRegisterL.setError("Field cannot be empty");
            return false;
        }else{
            FirstNameRegisterL.setError(null);
            FirstNameRegisterL.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLastName(){
        String lastName = LastNameRegisterLayout.getEditText().getText().toString();


        if(lastName.isEmpty()){
            LastNameRegisterLayout.setError("Field cannot be empty");
            return false;
        }else{
            LastNameRegisterLayout.setError(null);
            LastNameRegisterLayout.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateEmail(){
        String email = emailRegisterL.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";


        if(email.isEmpty()){
            emailRegisterL.setError("Field cannot be empty");
            return false;
        }else if(!email.matches(emailPattern)){
            emailRegisterL.setError("Invalid Email");
            return false;
        }else{
            emailRegisterL.setError(null);
            emailRegisterL.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhone(){
        String phone = phoneRegisterL.getEditText().getText().toString();

        if(phone.isEmpty()) {
            phoneRegisterL.setError("Field cannot be empty");
            return false;
        }else{
            phoneRegisterL.setError(null);
            phoneRegisterL.setErrorEnabled(false);
            return true;
        }
    }

    public Boolean validatePassword(){
        String password = passwordRegisterL.getEditText().getText().toString();
        String passwordMatch ="^"+
                "(?=.*[0-9])"+
                "(?=.*[a-z])"+
                "(?=.*[A-Z])"+
                "(?=.*[@#$%^&+=])"+
                "(?=\\S+$)"+
                ".{4,}"+
                "$";



       /* if(password.isEmpty()){
            passwordRegisterL.setError("Field cannot be empty");
            return false;
        }else */
        if(password.length()<5){
            passwordRegisterL.setError("atleast 5 characters");
            return false;
        }else{
            passwordRegisterL.setError(null);
            return true;
        }

    }




}