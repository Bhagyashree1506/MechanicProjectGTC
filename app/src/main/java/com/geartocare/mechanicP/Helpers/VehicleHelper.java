package com.geartocare.mechanicP.Helpers;

public class VehicleHelper {
    String Company,Model,VehicleNo;



    public VehicleHelper(String company, String model, String vehicleNo) {
        Company = company;
        Model = model;
        VehicleNo = vehicleNo;


    }



    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }
}
