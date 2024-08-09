package com.Employee.API.response;



public class ManagerChangeResponseDTO {
    private String message;

    public ManagerChangeResponseDTO() {}

    public ManagerChangeResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
