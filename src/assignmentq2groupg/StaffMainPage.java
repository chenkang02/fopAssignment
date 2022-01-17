/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentq2groupg;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author KWT
 */
public class StaffMainPage extends SQLConnector {
    // This method prompts user to enter a instruction number
    public static void runMainPage(String username){
        Scanner sc = new Scanner(System.in); 
        System.out.println("1.Modify module\n2.View all modules\n3.View registered student\n4.Log out\n-1. Quit");
        System.out.println();
        System.out.print("What do you want to do today? ");
        String instructionNum = sc.nextLine();
        while (!isValidInstruction(instructionNum)) {
            System.out.print("Please enter a valid instruction: ");
            instructionNum = sc.nextLine();
        }
        carrySpecificInstruction(instructionNum, username);
    }
    // Check whether valid instruction number or not
    public static boolean isValidInstruction(String instructionNum) {
        if (instructionNum.equals("-1")
                || instructionNum.equals("1")
                || instructionNum.equals("2")
                || instructionNum.equals("3")
                || instructionNum.equals("4")) {
            return true;
        }
        return false;
    }
    // The programme starts branching to respective area according to the instruction number entered 
    public static void carrySpecificInstruction(String instructionNum, String username) {
        Scanner sc = new Scanner(System.in);
        switch (instructionNum) {
            case "1":
                modifyModule(username);
                break;
            case "2":
                CourseSearchingController.viewAllModules();
                break;
            case "3":
                viewAllRegisteredStudent();
                break;
            case "4":
                logout(username);
                System.exit(0);
            case "-1":
                System.exit(0);
        }
        System.out.print("\nPress any character to return to main page: ");
        sc.nextLine();
        runMainPage(username);
        System.exit(0);
    }
    // This method will do a confirmation for logout option
    public static void logout(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Are you sure you want to logout? (Press Y to confirm or other character to cancel process) ");
        String input = sc.nextLine().toUpperCase();
        if (input.equals("Y")) {
            System.out.println("Logout successful.");
            LoginPage.runLoginPage();
        } else {
            System.out.println("Logout unsuccessful.");
            StaffMainPage.runMainPage(username);
        }
    }
    // This method allow user to make change with the module
    public static void modifyModule(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("What do you wish to do?");
        System.out.println("\n1.Create new module\n2.Delete existing module.\n3. Edit module\n");
        String input = sc.nextLine();
        switch (input) {
            case "1":
                createModule(username);
                break;
            case "2":
                deleteModule(username);
                break;
            case "3":
                editModule(username);
                break;
            default:
                System.out.println("Invalid instruction number. Please try again.");
                modifyModule(username);
                System.exit(0);
        }
        System.out.print("\nInput any character to return: ");
        sc.nextLine();
        runMainPage(username);
        System.exit(0);
    }
    // This method allow user to create a new module
    public static void createModule(String username) {
        Scanner sc = new Scanner(System.in);
        try {
            Connection con = getSQLConnection();
            System.out.print("Enter the module code of the course you wish to add:");
            String moduleCode = sc.nextLine();
            System.out.print("Enter the module name of the course you wish to add:");
            String moduleName = sc.nextLine();
            System.out.print("Enter the number of occurrence you wish to add: ");
            int occurrence = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter the number of students for a class: ");
            int target = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter the type of activity for the class (ONL/LEC): ");
            String activity = sc.nextLine();
            System.out.print("Enter the name of the lecturer: ");
            String tutor = sc.nextLine();
            System.out.print("Enter the credit hour for the class: ");
            int creditHour = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter the day of the week for the class: ");
            String week = sc.nextLine();
            System.out.print("Enter the starting time of the class in 24 hours format:");
            int startingTime = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter the end time of the class:");
            int endTime = sc.nextInt();
            sc.nextLine();
            int duration = endTime - startingTime;
            int time1 = 0;
            int time2 = 0;
            int time3 = 0;
            if(duration == 2){
                time1 = startingTime;
                time2 = startingTime + 1;
                time3 = endTime;
            }
            else if(duration == 1){
                time1 = startingTime; 
                time2 = endTime;
            }
            PreparedStatement insert = con.prepareStatement("INSERT INTO raw (ModuleCode, ModuleName, Occurrence, Target, Activity, Tutor, credithour, Week, TIME1, TIME2, TIME3) VALUES"
                    + "(\'"+moduleCode+"\',\'"+moduleName+"\',"+occurrence+","+target+",\'"+activity+"\',\'"+tutor+"\',"+creditHour+",'MONDAY',"+time1+","+time2+","+time3+")");
            insert.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Module created successfully.");   
        }
    }
    // This method allow user to delete module
    public static void deleteModule(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input the course code of the course you wish to delete: ");
        String moduleCode = sc.nextLine();
        CourseSearchingController.searchCourse(moduleCode);
        System.out.print("Insert the occurrence of the module you wish to delete: ");
        String occurrence = sc.nextLine();
        while (!occurrence.equals("1") && !occurrence.equals("2") 
                && !occurrence.equals("3") && !occurrence.equals("4") 
                && !occurrence.equals("5") && !occurrence.equals("6") 
                && !occurrence.equals("7") && !occurrence.equals("8")
                && !occurrence.equals("9") && !occurrence.equals("10")
                && !occurrence.equals("11") && !occurrence.equals("12")
                && !occurrence.equals("13") && !occurrence.equals("14")) {
            System.out.print("Please inserts valid occurrence: ");
            occurrence = sc.nextLine();
        }
        System.out.print("Are you sure you want to delete this module? Any changes cannot be undone. (Y/N): ");
        String answer = sc.nextLine();
        try {
            Connection con = getSQLConnection();
            if (answer.equalsIgnoreCase("y")) {
                PreparedStatement delete = con.prepareStatement("DELETE FROM raw WHERE ModuleCode = \'"+moduleCode+"\' And occurrence = "+occurrence+""); 
                delete.executeUpdate();
            }    
        } catch(Exception e) {
            System.out.println("Failed to delete module.");
        } finally {
            System.out.println("Module is deleted successfully.");
        }
    }
    // This method allow user to view all registered student for certain course
    public static void viewAllRegisteredStudent(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter the module code of the course you wish to inspect: ");
        String courseCode = sc.nextLine();
        System.out.print("Please enter the occurrence of the course: ");
        int occurrence = sc.nextInt();
        try {
            Connection con = getSQLConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM "+courseCode+" WHERE occurrence = "+occurrence+"");
            ResultSet results = statement.executeQuery();
            int counter = 1;
            System.out.println("Registered student:");    
            for (int i = 0; i < 70; i++) {
                System.out.print("-");
             }
            System.out.println("");
            System.out.printf("%-5s%-17s%-17s%-25s", "ID", "Matric Number", "Name", "Email");
            System.out.println();
            while (results.next()) {
                String matricNumber = results.getString("matricNumber");
                String name = results.getString("name");
                String email = results.getString("email");
                System.out.printf("%-5d%-17s%-17s%-25s",counter, matricNumber, name, email);
                System.out.println();
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    // This method allow user to edit existing module
    public static void editModule(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input the module code of the module you wishes to edit: ");
        String moduleCode = sc.nextLine();
        System.out.println("Please select the properties you wish to edit");
        System.out.println("\n1.Module Name\n2.Properties of module\n");
        String option = sc.nextLine();
        switch (option) {
            case "1": 
                changeNameOfCourse(moduleCode, username);
                break;
            case "2":
                editProperties(moduleCode);
                break;
            default:
                System.out.println("Invalid properties. Please try again.");
                runMainPage(username);
                System.exit(0);
        }       
    }
    // This method allow user to rename a course
    public static void changeNameOfCourse(String moduleCode, String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input the new name of the course: ");
        String newName = sc.nextLine();
        try {
            Connection con = getSQLConnection();
            boolean exist = CourseSearchingController.isCourseExist(moduleCode);
            if (exist) {           
                PreparedStatement change = con.prepareStatement("UPDATE raw "
                    + " SET ModuleName = \'"+newName+"\' WHERE ModuleCode = \'"+moduleCode+"\'");
                change.executeUpdate();
            } else {
                System.out.println("Module does not exist. Please try again.");
                runMainPage(username);
                System.exit(0);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }
    // This method allow user to edit properties of specific course and occurrence
    public static void editProperties(String moduleCode) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input the occurrence of the module you wishes to edit: ");
        int occurrence = sc.nextInt();
        sc.nextLine();
        System.out.println("Input the properties you wishes to edit:"
                + "\n(MAVName/Target/Activity/Tutor/credithour/Week/TIME1/TIME2/TIME3)");
        String properties = sc.nextLine();
        System.out.print("Input the new "+properties+":");
        String newProperties = sc.nextLine();
        try {
            Connection con = getSQLConnection();        
            String statement = "UPDATE RAW SET "+properties+"= \'"+newProperties+"\' WHERE ModuleCode = \'"+moduleCode+"\' and occurrence = "+occurrence+"";
            PreparedStatement edit = con.prepareStatement(statement);
            edit.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
