package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Student extends User implements Serializable {

    private int id;
    private boolean admin;
    private String name;
    private String surname;
    private ArrayList<Course> myEnrolledCourses = new ArrayList<>();

    public Student() {
    }

    public Student(
            String login, String psw, String email, String name, String surname) {
        super(login, psw, email);
        this.name = name;
        this.surname = surname;

    }

    public Student(String login, String psw, String email) {
        super(login, psw, email);

    }

    public Student(
            int id, boolean admin, String login, String psw, String email, String name, String surname) {
        super(login, psw, email);
        this.id = id;
        this.admin = admin;
        this.name = name;
        this.surname = surname;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ArrayList<Course> getMyEnrolledCourses() {
        return myEnrolledCourses;
    }

    public void setMyEnrolledCourses(ArrayList<Course> myEnrolledCourses) {
        this.myEnrolledCourses = myEnrolledCourses;
    }

    @Override
    public void greetUser() {
        System.out.println("Hello student " + name);
    }

    @Override
    public String toString() {
        return "Login name " + getLogin() + " \nemail adress " + getEmail() + " \nname " + getName() + "\nsurname " + getSurname() + "\npassword " + getPsw();
    }
}

