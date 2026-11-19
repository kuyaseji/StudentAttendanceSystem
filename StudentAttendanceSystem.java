
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class StudentAttendanceSystem {

    static Scanner sc = new Scanner(System.in);

    static final String STUDENTS = "students.txt";
    static final String ATTENDANCE = "attendance.txt";

    public static void main(String[] args) {

        createFiles();

        while (true) {
            System.out.println("\n================================");
            System.out.println(" STUDENT ATTENDANCE SYSTEM");
            System.out.println("================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Student Attendance");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    attendance();
                    break;
                case "3":
                    System.out.println("Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    static void createFiles() {
        try {
            new File(STUDENTS).createNewFile();
            new File(ATTENDANCE).createNewFile();
        } catch (IOException e) {
            System.out.println("File error!");
        }
    }

    static void login() {
        System.out.print("Username: ");
        String user = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        if (user.equals("admin") && pass.equals("admin123")) {
            adminMenu();
        } else {
            System.out.println("Invalid login!");
        }
    }

    static void adminMenu() {
        while (true) {
            System.out.println("\n========== ADMIN DASHBOARD ==========");
            System.out.println("1. Register Student");
            System.out.println("2. View Students");
            System.out.println("3. View Attendance");
            System.out.println("4. Search Attendance");
            System.out.println("5. Logout");
            System.out.print("Choose: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": registerStudent(); break;
                case "2": viewStudents(); break;
                case "3": viewAttendance(null); break;
                case "4":
                    System.out.print("Enter Student ID: ");
                    viewAttendance(sc.nextLine().trim());
                    break;
                case "5": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void registerStudent() {
        System.out.print("Student ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || id.contains("|") || findStudent(id) != null) {
            System.out.println("Invalid or existing Student ID!");
            return;
        }

        System.out.print("Student Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Course: ");
        String course = sc.nextLine().trim();

        System.out.print("Year Level: ");
        String year = sc.nextLine().trim();

        if (name.isEmpty() || course.isEmpty() || year.isEmpty()
                || name.contains("|") || course.contains("|")
                || year.contains("|")) {
            System.out.println("Invalid information!");
            return;
        }

        try (FileWriter fw = new FileWriter(STUDENTS, true)) {
            fw.write(id + "|" + name + "|" + course + "|" + year
                    + System.lineSeparator());
            System.out.println("Student registered successfully!");
        } catch (IOException e) {
            System.out.println("Unable to save student!");
        }
    }

    static String findStudent(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                if (data.length == 4 && data[0].equalsIgnoreCase(id)) {
                    return data[1];
                }
            }
        } catch (IOException e) {
            System.out.println("Read error!");
        }
        return null;
    }

    static void viewStudents() {
        System.out.println("\n========== STUDENT LIST ==========");
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                if (data.length == 4) {
                    System.out.println("ID: " + data[0]);
                    System.out.println("Name: " + data[1]);
                    System.out.println("Course: " + data[2]);
                    System.out.println("Year: " + data[3]);
                    System.out.println("--------------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("Read error!");
        }
    }

    static void attendance() {
        System.out.println("\n========== STUDENT ATTENDANCE ==========");
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();

        String name = findStudent(id);

        if (name == null) {
            System.out.println("Student not found!");
            return;
        }

        String now = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String today = LocalDate.now().toString();

        if (alreadyRecorded(id, today)) {
            System.out.println("Attendance already recorded today!");
            return;
        }

        try (FileWriter fw = new FileWriter(ATTENDANCE, true)) {
            fw.write(now + "|" + id + "|" + name + "|PRESENT"
                    + System.lineSeparator());

            System.out.println("\nATTENDANCE SUCCESSFUL!");
            System.out.println("Student ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Date: " + now);
            System.out.println("Status: PRESENT");
        } catch (IOException e) {
            System.out.println("Attendance error!");
        }
    }

    static boolean alreadyRecorded(String id, String date) {
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                if (data.length == 4 &&
                    data[0].startsWith(date) &&
                    data[1].equalsIgnoreCase(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Read error!");
        }
        return false;
    }

    static void viewAttendance(String searchID) {
        System.out.println("\n========== ATTENDANCE RECORDS ==========");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                if (data.length == 4 &&
                    (searchID == null || data[1].equalsIgnoreCase(searchID))) {
                    System.out.println("Date: " + data[0]);
                    System.out.println("ID: " + data[1]);
                    System.out.println("Name: " + data[2]);
                    System.out.println("Status: " + data[3]);
                    System.out.println("--------------------------------");
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Read error!");
        }

        if (!found) {
            System.out.println("No attendance records found.");
        }
    }
}
