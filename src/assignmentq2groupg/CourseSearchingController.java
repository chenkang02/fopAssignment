/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentq2groupg;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author KWT
 */
public class CourseSearchingController extends SQLConnector {
    // This method allow user to search for existing course
    public static void searchCourse(String userInput) {
        Scanner sc = new Scanner(System.in);
        String input = userInput.toUpperCase();
        boolean found = isCourseExist(input);
        try {
            String dayOfTheWeek = "";
            int startingTime = 0;
            int endTime = 0;
            if (found) {
                System.out.println("Search result:");
                for(int i = 0; i < 225 ; i++){
                    System.out.printf("%c", '-');
                }
                System.out.println();
                Connection con = getSQLConnection();
                PreparedStatement extract = con.prepareStatement("SELECT * FROM raw WHERE ModuleCode = \'"+input+"\'");
                ResultSet module = extract.executeQuery();
                System.out.printf("%-80s%-15s%-15s%-30s%-55s%n","Module", "Occurrence","Activity", "Day", "Module Tutor");
                while (module.next()) {
                    String moduleName = module.getString("ModuleName");
                    int occurrence = module.getInt("occurrence");
                    String activity = module.getString("Activity");
                    String tutor = module.getString("Tutor");
                    int credithour = module.getInt("credithour");
                    dayOfTheWeek = module.getString("Week");
                    startingTime = module.getInt("TIME1");
                    endTime = module.getInt("TIME2");
                    int time3 = module.getInt("TIME3");
                    boolean timeInvalid = false;
                    if (time3 != 0) endTime = time3;
                    if (startingTime == 0 || endTime == 0) timeInvalid = true;
                    String time = "";
                    if (timeInvalid) time = "-------";
                    else time = startingTime + ":00 - " + endTime + ":00";
                    String formattedModule = userInput + " - " + moduleName;
                    String formattedActivity = "";
                    if (activity.equalsIgnoreCase("lec")) formattedActivity = "Lecture";
                    else if (activity.equalsIgnoreCase("onl")) formattedActivity = "Tutorial";
                    String formattedTime = dayOfTheWeek + " " + time;
                    int creditHour = module.getInt("credithour");
                    System.out.printf("%-80s%-15s%-15s%-30s%-55s%n", formattedModule, occurrence, formattedActivity, formattedTime, tutor);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // This method will check whether the course exists or not
    public static boolean isCourseExist(String userInput) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement search = con.prepareStatement("SELECT * FROM raw WHERE ModuleCode = \'"+userInput+"\'");
            ResultSet results = search.executeQuery();
            if(results.next()){
                return true;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // This method allows user to view all existing modules
    public static void viewAllModules() {
        Scanner sc = new Scanner(System.in);
        try {
            Connection con = getSQLConnection();
            PreparedStatement view = con.prepareStatement("SELECT * FROM raw");
            ResultSet set = view.executeQuery();
            int endTime = 0;
            int startingTime = 0;
            String dayOfTheWeek = "";
            System.out.printf("%-80s%-15s%-15s%-30s%-55s%n","Module", "Occurrence","Activity", "Day", "Module Tutor");
            while (set.next()) {
                String moduleCode = set.getString("ModuleCode");
                String moduleName = set.getString("ModuleName");
                int occurrence = set.getInt("occurrence");
                String activity = set.getString("Activity");
                String tutor = set.getString("Tutor");
                int credithour = set.getInt("credithour");
                dayOfTheWeek = set.getString("Week");
                startingTime = set.getInt("TIME1");
                endTime = set.getInt("TIME2");
                int time3 = set.getInt("TIME3");
                boolean timeInvalid = false;
                if (time3 != 0) endTime = time3;
                if (startingTime == 0 || endTime == 0) timeInvalid = true;
                String time = "";
                if(timeInvalid) time = "-------";
                else time = startingTime + ":00 - " + endTime + ":00";
                String formattedModule = moduleCode + " - " + moduleName;
                String formattedActivity = "";
                if (activity.equalsIgnoreCase("lec")) formattedActivity = "Lecture";
                else if(activity.equalsIgnoreCase("onl")) formattedActivity = "Tutorial";
                String formattedTime = dayOfTheWeek + " " + time;
                int creditHour = set.getInt("credithour");
                System.out.printf("%-80s%-15s%-15s%-30s%-55s%n", formattedModule, occurrence, formattedActivity, formattedTime, tutor);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
