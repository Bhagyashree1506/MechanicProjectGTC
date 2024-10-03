package com.geartocare.mechanicP;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    public SharedPreferences.Editor editor;
    Context context;
    public static  final String VERSION = "version";
    public static  final String IS_LOGIN = "IsLoggedIn";
    public static  final String KEY_FIRSTNAME = "firstName";
    public static  final String KEY_lASTNAME = "lastName";
    public static  final String KEY_EMAIL = "email";
    public static  final String KEY_PHONENUMBER = "phoneNumber";
    public static  final String KEY_PASSWORD = "password";
    public static  final String KEY_MECH_ID = "mechID";



    public SessionManager(Context _context){
        context = _context;
        usersSession = _context.getSharedPreferences("usersloginSession",Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }


    public void createLoginSession(String version,String fullName,String lastName,String email,String phone,String password,String mechID){
        editor.putBoolean(IS_LOGIN,true);

        /* Personal*/
        editor.putString(KEY_FIRSTNAME,fullName);
        editor.putString(KEY_lASTNAME,lastName);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PHONENUMBER,phone);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(VERSION,version);
        editor.putString(KEY_MECH_ID,mechID);





        editor.commit();
    }

    public HashMap<String,String> getUsersDetailsFromSessions(){
        HashMap<String,String> userData = new HashMap<String,String>();

        userData.put(KEY_FIRSTNAME,usersSession.getString(KEY_FIRSTNAME,null));
        userData.put(KEY_lASTNAME,usersSession.getString(KEY_lASTNAME,null));
        userData.put(KEY_EMAIL,usersSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PHONENUMBER,usersSession.getString(KEY_PHONENUMBER,null));
        userData.put(KEY_PASSWORD,usersSession.getString(KEY_PASSWORD,null));
        userData.put(KEY_MECH_ID,usersSession.getString(KEY_MECH_ID,null));


        userData.put(VERSION,usersSession.getString(VERSION,null));






        return  userData;
    }






    public Boolean checkLogin(){
        if(usersSession.getBoolean(IS_LOGIN,false)){
                return true;
        }else {
            return false;
        }

    }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }


}
