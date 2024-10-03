package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.SessionManager;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;


    private Button login,forgotPassword,register,loginGoogle;
    private final Double version = 1.00;
    TextInputLayout phoneLoginL, passwordLoginL;
    FirebaseAuth auth;
    ProgressBar progressBar;
    DataSnapshot snapshot;


    private GoogleSignInClient mGoogleSignInClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*createRequest();*/


        auth = FirebaseAuth.getInstance();


        /*------------------------------Hooks start---------------------------------------*/
        forgotPassword = findViewById(R.id.forgotPassword);
        login = findViewById(R.id.Login);
        register= findViewById(R.id.Register);
        /*loginGoogle = findViewById(R.id.login_google);*/
        phoneLoginL = findViewById(R.id.phoneLoginL);
        passwordLoginL = findViewById(R.id.passwordLoginL);
        progressBar = findViewById(R.id.progress_barL);

        /*------------------------------Hooks end---------------------------------------*/


        /*------------------------------Variables---------------------------------------*/



        /*------------------------------Variables end---------------------------------------*/
        progressBar.setVisibility(View.GONE);
        //Login Button Functionality
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                processLogin();
            }
        });

       /*loginGoogle.setOnClickListener(new View.OnClickListener() {
         @Override
           public void onClick(View view) {
            signIn();
           }
        });*/


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });




    }


    @Override
    public void onStart() {
        super.onStart();

        if(new SessionManager(LoginActivity.this).checkLogin()){
            startActivity(new Intent(LoginActivity.this, ServiceListActivity.class));
            finish();
        }
    }




    public void processLogin(){
        progressBar.setVisibility(View.VISIBLE);
        String phone = phoneLoginL.getEditText().getText().toString();
        String password = passwordLoginL.getEditText().getText().toString();

        if(!validatePhone() || !validatePassword()){
            progressBar.setVisibility(View.GONE);
            return;
        }


        DatabaseReference DBref =  FirebaseDatabase.getInstance().getReference("mechanics");



        Query checkUser = DBref.orderByChild("phone").equalTo(phone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot resultList) {

                if(resultList.exists()){
                    
                    
                    for(DataSnapshot snap : resultList.getChildren()){
                        
                        snapshot = snap;
                        break;
                    }
                    
                    
                    phoneLoginL.setError(null);
                    phoneLoginL.setErrorEnabled(false);


                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if(passwordFromDB.equals(password)){

                        passwordLoginL.setError(null);
                        passwordLoginL.setErrorEnabled(false);

                        String lastNameFromDB = snapshot.child("lastName").getValue(String.class);
                        String phoneFromDB = snapshot.child("phone").getValue(String.class);
                        String firstNameFromDB = snapshot.child("firstName").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);
                        String mechIDFromDB = snapshot.child("mechanicID").getValue(String.class);

                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.createLoginSession(version.toString(),firstNameFromDB,lastNameFromDB,emailFromDB,phoneFromDB,passwordFromDB,mechIDFromDB);

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, ServiceListActivity.class));
                        finish();




                    } else{
                        progressBar.setVisibility(View.GONE);
                        passwordLoginL.setError("Wrong Password");
                        passwordLoginL.requestFocus();


                    }




                }else{
                    progressBar.setVisibility(View.GONE);
                    phoneLoginL.setError("No such user exist");
                    phoneLoginL.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });







    }






































    private Boolean validatePhone(){
        String phone = phoneLoginL.getEditText().getText().toString();

        if(phone.isEmpty()) {
            phoneLoginL.setError("Field cannot be empty");
            return false;
        }else{
            phoneLoginL.setError(null);
            phoneLoginL.setErrorEnabled(false);
            return true;
        }
    }



    public Boolean validatePassword(){
        String password = passwordLoginL.getEditText().getText().toString();
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
            passwordLoginL.setError("atleast 5 characters");
            return false;
        }else{
            passwordLoginL.setError(null);
            return true;
        }

    }










    //GOOGLE Authentication



   /* public void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
    }*/

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }







}