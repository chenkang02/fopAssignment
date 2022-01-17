/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentq2groupg;

import java.util.Scanner;

/**
 *
 * @author KWT
 */
public class StudentMainPage {
    // This method will run main page and accept instruction number from user
    public static void runMainPage(String matricNumber){
        Scanner sc = new Scanner(System.in);
        System.out.println("1. View my timetable");
        System.out.println("2. View or register my courses");
        System.out.println("3. Search for specified modules");
        System.out.println("4. View all modules");
        System.out.println("5. Logout");
        System.out.println("-1. Quit");
        System.out.println();
        System.out.print("What do you want to do today? ");
        String instructionNum = sc.nextLine();
        while (!isValidInstruction(instructionNum)) {
            System.out.print("Please enter a valid instruction: ");
            instructionNum = sc.nextLine();
        }
        carrySpecificInstruction(instructionNum, matricNumber);
    }
    // Check whether valid instruction number or not
    public static boolean isValidInstruction(String instructionNum) {
        if (instructionNum.equals("-1")
                || instructionNum.equals("1")
                || instructionNum.equals("2")
                || instructionNum.equals("3")
                || instructionNum.equals("4")
                || instructionNum.equals("5")) {
            return true;
        }
        return false;
    }
    // The programme starts branching to respective area according to the instruction number entered 
    public static void carrySpecificInstruction(String instructionNum, String matricNumber) {
        Scanner sc = new Scanner(System.in);
        switch (instructionNum) {
            case "1":
                ViewTimetableController.viewTimetable(matricNumber);
                break;
            case "2":
                CourseRegistrationController.viewRegisteredModule(matricNumber);
                CourseRegistrationController.runCourseRegistrationPage(matricNumber);
                break;
            case "3":
                System.out.print("Input search: ");
                String userInput = sc.nextLine();
                CourseSearchingController.searchCourse(userInput);
                break;
            case "4":
                CourseSearchingController.viewAllModules();
                break;
            case "5":
                logout(matricNumber);
                System.exit(0);
            case "-1":
                System.exit(0);
        }
        System.out.print("\nPress any character to return to main page: ");
        sc.nextLine();
        runMainPage(matricNumber);
        System.exit(0);
    }
    // This method will do a confirmation for logout option
    public static void logout(String matricNumber) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Are you sure you want to logout? (Press Y to confirm or other character to cancel process) ");
        String input = sc.nextLine().toUpperCase();
        if (input.equals("Y")) {
            System.out.println("Logout successful.");
            LoginPage.runLoginPage();
        } else {
            System.out.println("Logout unsuccessful.");
            StudentMainPage.runMainPage(matricNumber);
        }
    }
}

