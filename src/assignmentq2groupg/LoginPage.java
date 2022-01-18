/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package assignmentq2groupg;

import static assignmentq2groupg.SQLConnector.getSQLConnection;
import static assignmentq2groupg.UserController.isValidPassword;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author KWT
 */

public class LoginPage {
    // This method run login page and accept correct instruction number from user
    public static void runLoginPage() {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("|                    Welcome to Group G module registration platform                    |");
        System.out.println("|---------------------------------------------------------------------------------------|");
        System.out.println("|NOTE: You can press \"0\" to exit or \"-1\" to return to login page if you see \"*\".        |");
        System.out.println("|1. Login to an existing account.                                                       |");
        System.out.println("|2. Sign up a new account.                                                              |");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.print("*Press any number stated above to continue: ");
        String instructionNum = sc.nextLine();
        while (!isValidInstruction(instructionNum)) {
            System.out.print("*Please enter a valid instruction: ");
            instructionNum = sc.nextLine();
        }
        carrySpecificInstruction(instructionNum);
    }
    
    // This method is used to check whether the instruction entered is valid or not
    public static boolean isValidInstruction(String instructionNum) {
        if (instructionNum.equals("-1")
                || instructionNum.equals("0")
                || instructionNum.equals("1")
                || instructionNum.equals("2")) {
            return true;
        }
        return false;
    }
    // The programme starts branching to respective area according to the instruction number entered 
    public static void carrySpecificInstruction(String instructionNum) {
        boolean mainSwitch = true;
        while(mainSwitch){
        switch (instructionNum) {
            case "-1":
                runLoginPage();
                break;
            case "0":
                System.exit(0);
            case "1":
                enterCredential();
                break;
            case "2":
                createNewAccount();
                break;
        }
        }
    }
    // This method requires user to enter correct username or matric number
    public static void enterCredential() {
        Scanner sc = new Scanner(System.in);
        String role = getCorrectRole();
        if (role.equals("staff")) {
            System.out.print("*Please enter your username: ");
            String username = sc.nextLine();
            if (username.equals("-1") || username.equals("0")) carrySpecificInstruction(username);
            else if (UserController.isUsernameExist(username)) enterCorrectPasswordToEnterMainPage(username, role);
            else {
                System.out.println("The username doesn't exists. Please create an account and login again.");
                runLoginPage();
                System.exit(0);
            }
        } else {
            System.out.print("*Please enter your matric number: ");
            String matricNum = sc.next().toUpperCase();
            if (matricNum.equals("-1") || matricNum.equals("0")) carrySpecificInstruction(matricNum);
            else if (UserController.isMatricNumExist(matricNum)) enterCorrectPasswordToEnterMainPage(matricNum, role);
            else {
                System.out.println("The matric number doesn't exists. Please create an account and login again.");
                runLoginPage();
                System.exit(0);
            }
        }
    }
    // This method will trigger the creation of new account based on their input role
    public static void createNewAccount() {
        String role = getCorrectRole();
        if (role.equals("staff")) UserController.createStaffAccount();
        else UserController.createStudentAccount();
    }
    // This method prompts user to enter staff or student
    public static String getCorrectRole() {
        Scanner sc = new Scanner(System.in);
        String role = "";
        int i = 0;
        while ( i < 5 && !role.equals("staff") && !role.equals("student")) {
            if (i == 0) System.out.print("Please enter your role(staff or student): ");
            else System.out.print("Enter staff or student only: ");
            role = sc.nextLine().toLowerCase();
            i++;
        }
        if (!role.equals("staff") && !role.equals("student")) {
            System.out.println("You have tried too many times. Please login again.");
            runLoginPage();
            System.exit(0);
        }
        return role;
    }
    // This method prompts user to enter password and will check whether correct password or not
    public static void enterCorrectPasswordToEnterMainPage(String personalID, String role) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter your password: ");
        String password = sc.nextLine();
        int chance = 3;
        boolean isCorrectPassword = false;
        if (role.equals("staff")) isCorrectPassword = UserController.isCorrectStaffPassword(personalID, password);
        else isCorrectPassword = UserController.isCorrectStudentPassword(personalID, password);
        while (!isCorrectPassword && chance > 0) {
            System.out.println("Password incorrect, you have " + chance + " more chances.");
            System.out.print("Please re-enter your password: ");
            password = sc.nextLine();
            chance--;
            if (role.equals("staff")) isCorrectPassword = UserController.isCorrectStaffPassword(personalID, password);
            else isCorrectPassword = UserController.isCorrectStudentPassword(personalID, password);
        }    
        if (isCorrectPassword) {
            System.out.println("Succesfully login.");
            System.out.println("--------------------------------------------------------------");
            System.out.println("Welcome " + personalID + " !");
            System.out.println("--------------------------------------------------------------");
            if (role.equalsIgnoreCase("staff")) StaffMainPage.runMainPage(personalID);
            else StudentMainPage.runMainPage(personalID);
        } else {
            System.out.println("Please reset your password, you have no more chances.");
            resetPassword(personalID, role);
            runLoginPage();
        }
    }
    // This method can reset password
    public static void resetPassword(String matricNumber, String role) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Your password must contain 1 uppercase character, 1 lowercase character, 1 special character, 1 digit,\nand is between 8 to 20 characters long. \nPlease enter your new password:");
        String password1 = sc.nextLine();
        boolean valid = isValidPassword(password1);
        while(!valid){
            System.out.println("Your password must contain 1 uppercase character, 1 lowercase character, 1 special character, 1 digit,\nand is between 8 to 20 characters long.");
            System.out.print("Please reenter a valid password:");
            password1 = sc.nextLine();
            valid = isValidPassword(password1);
        }
        System.out.print("Please reenter your new password: ");
        String password2= sc.nextLine();
        String oldPassword = "";
        String newPassword = "";
        if(password1.equals(password2)){
            newPassword = password2;
            }
        
        if(role.equalsIgnoreCase("student")){
            try{
                Connection con = getSQLConnection();

                PreparedStatement select = con.prepareStatement("SELECT * FROM userdata WHERE matricNumber = \'"+matricNumber+"\'");
                ResultSet data = select.executeQuery();
                while(data.next()){
                    oldPassword = data.getString("password");
                }
            if(newPassword.equals(oldPassword)){
                System.out.println("Sorry, new password cannot be old password");
                System.out.println("Please try again.");
            }
            else{
                    PreparedStatement change = con.prepareStatement("UPDATE userdata SET password = \'"+newPassword+"\' WHERE matricNumber = \'"+matricNumber+"\'");
                    change.executeUpdate();
                    System.out.println("Password changed successfully.");
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
        else if(role.equalsIgnoreCase("staff")) {
            try{
                Connection con = getSQLConnection();
                PreparedStatement select = con.prepareStatement("SELECT * FROM staffData WHERE username = \'"+matricNumber+"\'");
                ResultSet data = select.executeQuery();
                while(data.next()){
                    oldPassword = data.getString("password");
                }
                
                if(newPassword.equals(oldPassword)){
                System.out.println("Sorry, new password cannot be old password");
                }
                else{
                    PreparedStatement change = con.prepareStatement("UPDATE staffData SET password = \'"+newPassword+"\' WHERE username = \'"+matricNumber+"\'");
                    change.executeUpdate();
                    System.out.println("Password successfully changed.");
                }
          
            }catch(Exception e){
                System.out.println(e);
                
            }
        }                
    }
}
