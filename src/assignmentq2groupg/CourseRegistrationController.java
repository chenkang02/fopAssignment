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
/**
 *
 * @author KWT
 */
public class CourseRegistrationController extends SQLConnector {
    // This method run course registration page and accept correct instruction number from user   
    public static void runCourseRegistrationPage(String matricNumber) {
        try {
            Connection con = getSQLConnection();
            Scanner sc = new Scanner(System.in);
            System.out.println("----------------------------------------------------------------");
            System.out.println("                    Module Registration Page                    ");
            System.out.println("----------------------------------------------------------------");
            System.out.println("1. Add module\n2. Drop module\n3. View my course(s)\n4. Return to main page\n\n");
            String choice = "";
            do {
                System.out.print("Please enter your choice(1/2/3/4): ");
                choice = sc.nextLine();
            } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4"));
            switch (choice) {
                case "1":
                    viewRegisteredModule(matricNumber);
                    System.out.print("Please input the moduleCode you wish to register: ");
                    String code = sc.nextLine().toUpperCase();
                    add(con, code, matricNumber);
                    break;
                case "2":
                    viewRegisteredModule(matricNumber);
                    drop(matricNumber);
                    break;
                case "3":
                    viewRegisteredModule(matricNumber);
                    break;
                case "4":
                    StudentMainPage.runMainPage(matricNumber);
                    System.exit(0);
                    break;
            }
            System.out.print("\nPress any character to return to module registration page: ");
            sc.nextLine();
            runCourseRegistrationPage(matricNumber);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // This method prints user's registered module
    public static void viewRegisteredModule(String matricNumber) {
        try {
            Connection con = getSQLConnection();
            ArrayList<String> moduleName = new ArrayList<String>();
            ArrayList<String> courseCode = new ArrayList<String>();
            ArrayList<Integer> enrollmentID = new ArrayList<Integer>();
            ArrayList<String> activity = new ArrayList<String>();
            int creditHours = 0;
            PreparedStatement check = con.prepareStatement("Select creditHour FROM "+matricNumber+"");
            ResultSet sets = check.executeQuery();
            while (sets.next()) {
                int hour = sets.getInt("creditHour");
                creditHours += hour;
            }
            System.out.print("---------------\nModule Registeration\nCurrent Credit Hour: "+creditHours+"\nMaximum credit hours : 22\nCurrent registered modules\n");
            PreparedStatement registeredModule = con.prepareStatement("SELECT enrollmentID, courseCode, ModuleName, Activity, TIME1, TIME2, TIME3 FROM "+matricNumber+" ");
            ResultSet module = registeredModule.executeQuery();
            while (module.next()) {
                activity.add(module.getString("Activity"));
                int startTime = module.getInt("TIME1");
                int TIME2 = module.getInt("TIME2");
                enrollmentID.add(module.getInt("enrollmentID"));
                courseCode.add(module.getString("courseCode"));
                moduleName.add(module.getString("ModuleName"));
            }
            for (int i = 0; i < enrollmentID.size(); i++) {
                System.out.print(enrollmentID.get(i)+". "+courseCode.get(i)+" "+moduleName.get(i)+" "+activity.get(i));
                System.out.println();
            }
            System.out.println();
        } catch(Exception e) {
                e.printStackTrace();
        } 
    } 
    // This method adds new registered course info into database
    public static void addToTable(String matricNumber, String courseCode, int occurrence) {
        try {
            Connection con = getSQLConnection();
            String name = "";
            String email = "";
            PreparedStatement statement = con.prepareStatement("SELECT * FROM userdata WHERE matricNumber = \'"+matricNumber+"\'");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                name = set.getString("name");
                email = set.getString("siswamail");
            }
            PreparedStatement insert = con.prepareStatement("INSERT INTO "+courseCode+" (matricNumber, name, email, occurrence) VALUES (\'"+matricNumber+"\', \'"+name+"\', \'"+email+"\',"+occurrence+")");
            insert.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // This method allows user to drop specific module
    public static void drop(String matricNumber) {
        Scanner sc = new Scanner(System.in);
        int occurrence = 0;
        String moduleName = "";
        try {
            Connection con = getSQLConnection();
            System.out.print("Please enter the course code of the couse you wish to drop: ");
            String courseCode = sc.nextLine();
            if (isRegisteredCourse(matricNumber, courseCode)) {
                PreparedStatement statement = con.prepareStatement("SELECT * FROM "+matricNumber+" WHERE courseCode = \'"+courseCode+"\' ");
                ResultSet set = statement.executeQuery(); 
                while (set.next()) {
                    occurrence = set.getInt("Occurence");
                    moduleName = set.getString("ModuleName");
                }
                System.out.print("Are you sure you want to drop " + courseCode + " " + moduleName + " Occurrence " + occurrence + "? (Press Y to confirm) ");
                String answer = sc.nextLine().toUpperCase();
                if (answer.equals("Y")) {
                    PreparedStatement drop = con.prepareStatement("DELETE FROM "+matricNumber+" WHERE courseCode = \'"+courseCode+"\' ");
                    drop.executeUpdate();
                    System.out.println("Module successfully dropped.");
                } else System.out.println("No changes have been made. Please try again.");
            } else System.out.println("Dropping module failed because module is not registered. Please try again.");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // This method checks whether course is already registered or not
     public static boolean isRegisteredCourse(String matricNumber, String courseCode) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement check = con.prepareStatement("SELECT * FROM "+matricNumber+" WHERE courseCode = \'"+courseCode+"\'");
            ResultSet set = check.executeQuery();
            if (set.next()) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // This method allows user to add slash
    public static String addSlash(String s) {
        int slash = 0;
        for (int i=0; i<s.length(); i++) {
            if (s.charAt(i)=='\'') slash=i;
        }
        if (slash > 0) return s.substring(0, slash) + "\\"+ s.substring(slash+1);
        return s;
    }
    // This method allows user to register for a new valid module
    public static void add(Connection con, String moduleCode, String matricNumber) {
        Scanner sc = new Scanner(System.in);
        if (isRegisteredCourse(matricNumber, moduleCode)) System.out.println("Course is already registered!");            
        else if (isReachMaxCreditHour(matricNumber)) System.out.println("Credit hours have exceeded 22 hours, please drop some module to continue adding new modules.");        
        else {
            boolean isCourseExist = CourseSearchingController.isCourseExist(moduleCode);
            int startingTime = 0;
            int endTime = 0;
            String dayOfTheWeek = "";
            if (isCourseExist) {
                try {
                    PreparedStatement extract = con.prepareStatement("SELECT * FROM raw WHERE ModuleCode = \'"+moduleCode+"\'");
                    ResultSet module = extract.executeQuery();
                    System.out.printf("%-20s%-55s%-20s%-10s%-55s%-13s%-17s%-15s%n","ModuleCode", "ModuleName", "Occurrence","Activity", "Tutor", "Week", "Time", "Credit hours");
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
                        if(time3 != 0) endTime = time3;
                        if (startingTime == 0 || endTime == 0) timeInvalid = true;
                        String time = "";
                        if (timeInvalid) time = "-------";
                        else time = startingTime + ":00 - " + endTime + ":00";
                        int creditHour = module.getInt("credithour");
                        System.out.printf("%-20s%-55s%-20s%-10s%-55s%-13s%-17s%-15s%n", moduleCode, moduleName, occurrence, activity, tutor, dayOfTheWeek, time , credithour);
                    } 
                } catch(Exception e) {
                    e.printStackTrace();
                }
                int occurrence = selectOccurrence(moduleCode, matricNumber);
                createCourseTable(moduleCode);
                int creditHour = 0;
                if (occurrence == 0) {
                    System.out.println("Occurrence does not exist.");
                    viewRegisteredModule(matricNumber);
                    runCourseRegistrationPage(matricNumber);
                    System.exit(0);
                }
                if(occurrence == -1){
                    System.out.println("Selected occurrences crashes with your current timetable. Please try add the module again.");
                    viewRegisteredModule(matricNumber);
                    runCourseRegistrationPage(matricNumber);
                    System.exit(0);
                }
                
                    else {
                    try {
                        PreparedStatement select = con.prepareStatement("SELECT * FROM raw WHERE ModuleCode = \'"+moduleCode+"\' And Occurrence = \'"+occurrence+"\'  ");
                        ResultSet results = select.executeQuery();    
                        PreparedStatement count = con.prepareStatement("SELECT * FROM "+matricNumber+"");
                        ResultSet set = count.executeQuery();
                        while (set.next()) {
                            int temp = set.getInt("credithour");
                            creditHour += temp;
                        }
                        while (results.next()) {
                            String courseCode = results.getString("ModuleCode");
                            String moduleName = results.getString("ModuleName");
                            String lecturer = results.getString("Tutor");
                            int creditHours = results.getInt("credithour");
                            String week = results.getString("Week");
                            int TIME1 = results.getInt("TIME1");
                            int TIME2 = results.getInt("TIME2");
                            int TIME3 = results.getInt("TIME3");
                            String activity = results.getString("Activity");
                            if (creditHour + creditHours > 22) {
                                System.out.println("You have reached maximum credit hour, please drop some module to add new modules.");
                                viewRegisteredModule(matricNumber);
                            }
                            PreparedStatement insert = con.prepareStatement("INSERT INTO "+matricNumber
                                    + "(courseCode, ModuleName, Lecturer, Occurence, creditHour, Week, Activity, TIME1, TIME2, TIME3) "
                                    + "VALUES (\'"+courseCode+"\', \'"+moduleName+"\', \'"+addSlash(lecturer)+"\', "+occurrence+", "+creditHours+", \'"+week+"\', \'"+activity+"\', "+TIME1+", "+TIME2+", "+TIME3+");");
                            insert.executeUpdate();
                        }
                        addToTable(matricNumber, moduleCode, occurrence);
                        System.out.println("Course registered successfully.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } 
            } else System.out.println("Module does not exist.");
        }
    }
    // This method will check whether there is a crash among registered courses' time
    public static boolean isTimeCrash(String matricNumber, String week, int startTime, int endTime, int occurrence) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement check = con.prepareStatement("SELECT TIME1, TIME2, TIME3 FROM "+matricNumber+" WHERE (TIME1 between \'"+startTime+"\' And \'"+endTime+"\' Or TIME2 between \'"+startTime+"\' And \'"+endTime+"\' Or TIME3 between \'"+startTime+"\' And \'"+endTime+"\') And Week = \'"+week+"\'");
            ResultSet set = check.executeQuery();
            if (set.next()) return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Get a valid occurrence number from user and return back the occurrence number
    public static int selectOccurrence(String courseCode, String matricNumber) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter your occurrence: ");
            int occurrence = sc.nextInt();
            boolean crash = false;
            boolean isCrashLecture = false;
            boolean isCrashTutorial = false;
            ArrayList<Integer> startTime = new ArrayList<Integer>();
            ArrayList<Integer> endTime = new ArrayList<Integer>();
            ArrayList<String> week = new ArrayList<String>();
            Connection con = getSQLConnection();
            if (isOccurrenceExist(courseCode, occurrence)) {
                //check if the selected occurrence crashes with the student's timetable
                PreparedStatement fromRaw = con.prepareStatement("SELECT Week, TIME1, TIME2, TIME3 FROM raw WHERE ModuleCode = \'"+courseCode+"\' And Occurrence = \'"+occurrence+"\'");
                ResultSet raw = fromRaw.executeQuery();
                int counter = 0;
                while (raw.next()) {
                    int time1 = raw.getInt("TIME1");
                    int time2 = raw.getInt("TIME2");
                    int time3 = raw.getInt("TIME3");     
                    startTime.add(time1);
                    if(time3 != 0) endTime.add(time3);
                    else endTime.add(time2);
                    week.add(raw.getString("Week"));
                    counter++;
                }
                if (counter == 1) crash = isTimeCrash(matricNumber, week.get(0), startTime.get(0), endTime.get(0), occurrence);
                else if (counter == 2) {
                    isCrashLecture = isTimeCrash(matricNumber, week.get(0), startTime.get(0), endTime.get(0), occurrence);
                    isCrashTutorial = isTimeCrash(matricNumber, week.get(1), startTime.get(1), endTime.get(1), occurrence);                    
                }
                if (isCrashLecture || isCrashTutorial || crash) {
                    return -1;
                } else return occurrence;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;   
    }
    // Check whether specific course's occurrence number exists or not
    public static boolean isOccurrenceExist(String courseCode, int occurrence2){
        ArrayList<Integer> occurrence = new ArrayList<Integer>();
        try {
            Connection con = getSQLConnection();
            PreparedStatement check = con.prepareStatement("SELECT Occurrence FROM raw WHERE ModuleCode = \'"+courseCode+"\'");
            ResultSet set = check.executeQuery();
            while (set.next()) {
                occurrence.add(set.getInt("Occurrence"));
            }
            if(occurrence.contains(occurrence2)) return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    // Create a course table if it's not exists
    public static void createCourseTable(String courseCode) {
        try {
            Connection con = getSQLConnection();
            PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+courseCode+" (matricNumber varchar(255) NOT NULL PRIMARY KEY, FOREIGN KEY (matricNumber) REFERENCES userdata(matricNumber), name varchar(255) NOT NULL, FOREIGN KEY (name) REFERENCES userdata(NAME), email varchar(255) NOT NULL, occurrence int)");
            create.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // Check whether user had reach max credit hour or not
    public static boolean isReachMaxCreditHour(String matricNumber) {
        try {
            Connection con = getSQLConnection();
            int creditHour = 0;
            PreparedStatement count = con.prepareStatement("SELECT * FROM "+matricNumber+"");
            ResultSet set = count.executeQuery();
            while (set.next()) {
                int temp = set.getInt("credithour");
                creditHour += temp;
            }
            if (creditHour < 22) return false;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    } 
}
