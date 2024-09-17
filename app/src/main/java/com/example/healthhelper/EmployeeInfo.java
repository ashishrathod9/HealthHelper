package com.example.healthhelper;

public class EmployeeInfo {
    private String employeeName;
    private String employeeContactNumber;
    private String employeeAddress;

    // Default constructor required for calls to DataSnapshot.getValue(EmployeeInfo.class)
    public EmployeeInfo() {
    }

    public EmployeeInfo(String employeeName, String employeeContactNumber, String employeeAddress) {
        this.employeeName = employeeName;
        this.employeeContactNumber = employeeContactNumber;
        this.employeeAddress = employeeAddress;
    }

    // Getters and setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeContactNumber() {
        return employeeContactNumber;
    }

    public void setEmployeeContactNumber(String employeeContactNumber) {
        this.employeeContactNumber = employeeContactNumber;
    }

    public String getEmployeeAddress() {
        return employeeAddress;
    }

    public void setEmployeeAddress(String employeeAddress) {
        this.employeeAddress = employeeAddress;
    }


}