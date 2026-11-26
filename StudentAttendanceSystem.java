
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StudentAttendanceSystem {

    static Scanner sc = new Scanner(System.in);

    static String STUDENT_FILE = "students.txt";
    static String ATTENDANCE_FILE = "attendance.txt";

    public static void main(String[] args) {

        createFiles();

        while (true) {

            System.out.println("\n================================");
            System.out.println(" STUDENT ATTENDANCE SYSTEM");
            System.out.println("================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Student Attendance");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    adminLogin();
                    break;

                case "2":
                    studentAttendance();
                    break;

                case "3":
                    System.out.println("Thank you for using the system!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // create files
    public static void createFiles() {
        try {
            File students = new File(STUDENT_FILE);
            File attendance = new File(ATTENDANCE_FILE);

            students.createNewFile();
            attendance.createNewFile();

        } catch (IOException e) {
            System.out.println("Error creating files: " + e.getMessage());
        }
    }

    // admin log in
    public static void adminLogin() {

        System.out.println("\n===== ADMIN LOGIN =====");

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (username.equals("admin") &&
            password.equals("admin123")) {

            System.out.println("Login successful!");
            adminMenu();

        } else {
            System.out.println("Invalid username or password!");
        }
    }

    // admin style
    public static void adminMenu() {

        while (true) {

            System.out.println("\n================================");
            System.out.println("          ADMIN MENU");
            System.out.println("================================");
            System.out.println("1. Register Student");
            System.out.println("2. View Registered Students");
            System.out.println("3. Delete Student");
            System.out.println("4. View Attendance Records");
            System.out.println("5. Search Student Attendance");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    registerStudent();
                    break;

                case "2":
                    viewStudents();
                    break;

                case "3":
                    deleteStudent();
                    break;

                case "4":
                    viewAttendance();
                    break;

                case "5":
                    searchAttendance();
                    break;

                case "6":
                    System.out.println("Admin logged out!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // register studeents
    public static void registerStudent() {

        System.out.println("\n===== REGISTER STUDENT =====");

        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();

        if (id.isEmpty() || id.contains("|")) {
            System.out.println("Invalid Student ID!");
            return;
        }

        if (studentExists(id)) {
            System.out.println("Student ID already exists!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        System.out.print("Enter Year Level: ");
        String year = sc.nextLine();

        if (name.isEmpty() || course.isEmpty() ||
            year.isEmpty() || name.contains("|") ||
            course.contains("|") || year.contains("|")) {

            System.out.println("Invalid student information!");
            return;
        }

        try (FileWriter fw = new FileWriter(STUDENT_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(id + "|" + name + "|" + course + "|" + year);

            System.out.println("Student registered successfully!");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // check if students exist
    public static boolean studentExists(String id) {

        try (Scanner reader = new Scanner(new File(STUDENT_FILE))) {

            while (reader.hasNextLine()) {

                String[] data = reader.nextLine().split("\\|", -1);

                if (data.length >= 4 &&
                    data[0].equalsIgnoreCase(id)) {
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading student records.");
        }

        return false;
    }

    // get student name
    public static String getStudentName(String id) {

        try (Scanner reader = new Scanner(new File(STUDENT_FILE))) {

            while (reader.hasNextLine()) {

                String[] data = reader.nextLine().split("\\|", -1);

                if (data.length >= 4 &&
                    data[0].equalsIgnoreCase(id)) {
                    return data[1];
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading student records.");
        }

        return null;
    }

    // view student
    public static void viewStudents() {

        System.out.println("\n===== REGISTERED STUDENTS =====");

        boolean found = false;

        try (Scanner reader = new Scanner(new File(STUDENT_FILE))) {

            while (reader.hasNextLine()) {

                String line = reader.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length >= 4) {

                    System.out.println("----------------------------");
                    System.out.println("Student ID: " + data[0]);
                    System.out.println("Name: " + data[1]);
                    System.out.println("Course: " + data[2]);
                    System.out.println("Year Level: " + data[3]);

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No student records found.");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // delete
    public static void deleteStudent() {

        System.out.println("\n===== DELETE STUDENT =====");

        System.out.print("Enter Student ID to delete: ");
        String id = sc.nextLine();

        if (!studentExists(id)) {
            System.out.println("Student ID not found!");
            return;
        }

        String name = getStudentName(id);

        System.out.println("Student Name: " + name);

        System.out.print("Are you sure you want to delete? (Y/N): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        File original = new File(STUDENT_FILE);
        File temporary = new File("students_temp.txt");

        boolean deleted = false;

        try (Scanner reader = new Scanner(original);
             PrintWriter writer = new PrintWriter(temporary)) {

            while (reader.hasNextLine()) {

                String line = reader.nextLine();
                String[] data = line.split("\\|", -1);

                if (data.length >= 4 &&
                    data[0].equalsIgnoreCase(id)) {

                    deleted = true;
                    continue;
                }

                writer.println(line);
            }

        } catch (IOException e) {
            temporary.delete();
            System.out.println("Error: " + e.getMessage());
            return;
        }

        try {

            if (deleted) {

                Files.move(
                    temporary.toPath(),
                    original.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                );

                System.out.println("Student deleted successfully!");

            } else {
                temporary.delete();
                System.out.println("Student not found!");
            }

        } catch (IOException e) {
            temporary.delete();
            System.out.println("Unable to update student records: "
                               + e.getMessage());
        }
    }

    // attendance
    public static void studentAttendance() {

        System.out.println("\n===== STUDENT ATTENDANCE =====");

        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();

        String name = getStudentName(id);

        if (name == null) {
            System.out.println("Student ID not registered!");
            return;
        }

        if (alreadyRecorded(id)) {
            System.out.println("Attendance already recorded today!");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String date = now.format(formatter);

        try (FileWriter fw = new FileWriter(ATTENDANCE_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(date + "|" + id + "|" + name + "|PRESENT");

            System.out.println("Attendance recorded successfully!");
            System.out.println("Student Name: " + name);
            System.out.println("Status: PRESENT");
            System.out.println("Date and Time: " + date);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // attendance record today
    public static boolean alreadyRecorded(String id) {

        String today = java.time.LocalDate.now().toString();

        try (Scanner reader = new Scanner(new File(ATTENDANCE_FILE))) {

            while (reader.hasNextLine()) {

                String[] data = reader.nextLine().split("\\|", -1);

                if (data.length >= 4 &&
                    data[0].startsWith(today) &&
                    data[1].equalsIgnoreCase(id)) {

                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("Error checking attendance.");
        }

        return false;
    }

    //view attendance
    public static void viewAttendance() {

        System.out.println("\n===== ATTENDANCE RECORDS =====");

        boolean found = false;

        try (Scanner reader = new Scanner(new File(ATTENDANCE_FILE))) {

            while (reader.hasNextLine()) {

                String line = reader.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length >= 4) {

                    System.out.println("----------------------------");
                    System.out.println("Date and Time: " + data[0]);
                    System.out.println("Student ID: " + data[1]);
                    System.out.println("Student Name: " + data[2]);
                    System.out.println("Status: " + data[3]);

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No attendance records found.");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // search student
    public static void searchAttendance() {

        System.out.println("\n===== SEARCH ATTENDANCE =====");

        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();

        boolean found = false;

        try (Scanner reader = new Scanner(new File(ATTENDANCE_FILE))) {

            while (reader.hasNextLine()) {
                

                String line = reader.nextLine();

                String[] data = line.split("\\|", -1);

                if (data.length >= 4 &&
                    data[1].equalsIgnoreCase(id)) {

                    System.out.println("----------------------------");
                    System.out.println("Date and Time: " + data[0]);
                    System.out.println("Student ID: " + data[1]);
                    System.out.println("Student Name: " + data[2]);
                    System.out.println("Status: " + data[3]);

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No attendance records found for this ID.");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
