package com.geartocare.mechanicP.Helpers;

public class UserHelperClass {
    String lastName;
    String firstName;
    String phone;
    String email;
    String password;





    public UserHelperClass(String lastName, String firstName, String phone, String email, String password) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.password = password;

    }



    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
