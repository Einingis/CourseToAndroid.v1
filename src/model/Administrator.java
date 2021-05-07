package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Administrator extends User implements Serializable {

    private int id;
    private boolean admin;
    private String phoneNumber;
    private ArrayList<Course> myCreatedCourses = new ArrayList<>();

    public Administrator(
            int id,
            boolean admin,
            String login,
            String psw,
            String email,
            String phoneNumber,
            ArrayList<Course> myCreatedCourses) {
        super(id, admin, login, psw, email);
        this.phoneNumber = phoneNumber;
        this.myCreatedCourses = myCreatedCourses;
    }

    public Administrator(String login, String psw, String email, String phoneNumber) {
        super(login, psw, email);

        this.phoneNumber = phoneNumber;
    }

    public Administrator(int id, boolean admin, String login, String psw, String email, String phoneNumber) {
        super(login, psw, email);
        this.id = id;
        this.admin = admin;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<Course> getMyCreatedCourses() {
        return myCreatedCourses;
    }

    public void setMyCreatedCourses(ArrayList<Course> myCreatedCourses) {
        this.myCreatedCourses = myCreatedCourses;
    }

    @Override
    public void greetUser() {
        System.out.println("Hello admin");
    }

    @Override
    public String toString() {
        return "Login name " + getLogin() + " \nemail adress " + getEmail() + " \nphone number " + phoneNumber + "\npassword " + getPsw();
    }
}

