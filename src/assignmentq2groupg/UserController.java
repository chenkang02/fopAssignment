/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentq2groupg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

/**
 *
 * @author KWT
 */
public class UserController extends SQLConnector {
    // Check whether the user's um mail already exists in the database
    public static boolean isStaffMailExist(String umMail) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement search = con.prepareStatement("SELECT umMail FROM staffData");
            ResultSet result = search.executeQuery();
            ArrayList<String> staffsUmMail = new ArrayList<String>();
            while (result.next()) {
                String mail = result.getString("umMail");
                staffsUmMail.add(mail);
            }
            if (staffsUmMail.contains(umMail)) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check whether the user's siswa mail already exists in the database
    public static boolean isStudentMailExist(String siswaMail) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement search = con.prepareStatement("SELECT siswaMail FROM userdata");
            ResultSet result = search.executeQuery();
            ArrayList<String> studentsMail = new ArrayList<String>();
            while (result.next()) {
                String mail = result.getString("siswaMail");
                studentsMail.add(mail);
            }
            if (studentsMail.contains(siswaMail)) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check whether the user's matric number already exists in database
    public static boolean isMatricNumExist(String matricNum) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement search = con.prepareStatement("SELECT matricNumber FROM userdata");
            ResultSet result = search.executeQuery();
            ArrayList<String> studentsMatricNum = new ArrayList<String>();
            while (result.next()) {
                String num = result.getString("matricNumber");
                studentsMatricNum.add(num);
            }
            if (studentsMatricNum.contains(matricNum)) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check whether user's username already exists in database
    public static boolean isUsernameExist(String username) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement search = con.prepareStatement("SELECT * FROM staffData WHERE username = \'"+username+"\' ");  
            ResultSet result = search.executeQuery();
            if (result.next()) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check whether um mail's format is valid or not
    private static boolean isValidMail(String mail, String role) {
        Pattern pattern;
        if (role.equals("staff")) pattern = Pattern.compile(".@um.edu.my");
        else pattern = Pattern.compile(".@siswa.um.edu.my");
        Matcher matcher = pattern.matcher(mail);
        if (matcher.find()) return true;
        else return false;
    }
    // Create a new student student account after detects new input matric number and siswa mail
    public static void createStudentAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please input your siswamail: ");
        String siswaMail = sc.nextLine();
        System.out.print("Please input your matric number: ");
        String matricNum = sc.nextLine().toUpperCase();
        if (isMatricNumExist(matricNum) || !isValidMail(siswaMail, "student") || isStudentMailExist(siswaMail)) 
            System.out.println("Either invalid siswa mail or username, siswa mail already existed. Please proceed to login.");
        else {
             enterStudentInfo(matricNum, siswaMail);
             System.out.println("Account created successfully.");
        }
        LoginPage.runLoginPage();
    }
    // Require user to input some basic student info and store it inside database
    public static void enterStudentInfo(String matricNum, String siswaMail) {
        try {
            Scanner sc = new Scanner(System.in);
            Connection con = getSQLConnection();
            System.out.print("Please input your full name: ");
            String name = sc.nextLine().toUpperCase();
            System.out.print("Please enter your muet band: ");
            String muetBand = sc.nextLine();
            while (!(muetBand.equals("1") || muetBand.equals("2") 
                    || muetBand.equals("3") || muetBand.equals("4") 
                    || muetBand.equals("5") || muetBand.equals("6"))) {
                System.out.print("Band " + muetBand + " doesn't exists. Please enter again: ");
                muetBand = sc.nextLine();
            }
            System.out.print("Please enter your programme's name: ");
            String programme = sc.nextLine();
            String password = createAccountPassword();
            PreparedStatement insert = con.prepareStatement("INSERT INTO userdata (matricNumber, siswaMail, password, muetBand, name) VALUES ('"+matricNum+"', '"+siswaMail+"', '"+password+"', '"+muetBand+"', \'"+name+"\')");
            insert.executeUpdate();  
            createTableForUser(matricNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Create a new staff account after detects new input um mail and username
    public static void createStaffAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter your UM mail: ");
        String umMail = sc.nextLine();
        System.out.print("Please enter your username: ");
        String username = sc.nextLine();
        if (isUsernameExist(username) || !isValidMail(umMail, "staff") || isStaffMailExist(umMail)) 
            System.out.println("Either invalid UM Mail or username, UM mail already existed. Please proceed to login.");
        else {
            enterStaffInfo(umMail, username);
            System.out.println("Account created successfully.");
        }
        LoginPage.runLoginPage();
    }
    // Require user to enter some basic staff info and store it inside database
    public static void enterStaffInfo(String umMail, String username) {
        try {
            Scanner sc = new Scanner(System.in);
            Connection con = getSQLConnection();
            System.out.print("Please enter your full name: ");
            String name = sc.nextLine();
            String password = createAccountPassword();  
            PreparedStatement insert = con.prepareStatement("INSERT INTO staffData (umMail, username, password, fullName) VALUES ('"+umMail+"', '"+username+"', '"+password+"', '"+name+"' )");
            insert.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // Check whether the student password input matches the correct password exists in the database 
    public static boolean isCorrectStudentPassword(String matricNumber, String password){
        String correctPassword = "";
        try {
            Connection con = getSQLConnection();
            PreparedStatement check = con.prepareStatement("SELECT password FROM userdata WHERE matricNumber = \'"+matricNumber+"\'");
            ResultSet results = check.executeQuery();
            while (results.next()) {
                correctPassword = results.getString(1);
            }
            if (password.equals(correctPassword)) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check whether the staff password input matches the correct password exists in the database
    public static boolean isCorrectStaffPassword(String username, String password) {
        String correctPassword = "";
        try {
            Connection con = getSQLConnection();
            PreparedStatement check = con.prepareStatement("SELECT * FROM staffdata WHERE username = \'"+username+"\'");
            ResultSet results = check.executeQuery();
            while(results.next()){
                correctPassword = results.getString("password");
            }
            if (password.equals(correctPassword)) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Create and return the new password
    public static String createAccountPassword() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter your password: ");
        String password1 = sc.nextLine();
        System.out.print("Please re-enter your password: ");
        String password2 = sc.nextLine();
        if (!password1.equals(password2)) {
            System.out.println("Password not matched. Please re-create your account.");
            LoginPage.createNewAccount();
            System.exit(0);
        }
        return password1;
    }
    // Create a student table if it doesn't exist
     public static void createTableForUser(String matricNumber){
        try {
            Connection con = getSQLConnection();
            PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+matricNumber+" (enrollmentID int NOT NULL AUTO_INCREMENT PRIMARY KEY, courseCode varchar(255), ModuleName varchar(255), Lecturer varchar(255), Occurence int, creditHour int, Week varchar(255), Activity varchar(255), TIME1 int, TIME2 int, TIME3 int)");
            create.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
