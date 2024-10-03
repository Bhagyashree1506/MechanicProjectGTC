package com.geartocare.mechanicP.Models;




public class ModelVehicle {
    String VehicleNo,Company,Model;

    private boolean isSelected = false;


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

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}
