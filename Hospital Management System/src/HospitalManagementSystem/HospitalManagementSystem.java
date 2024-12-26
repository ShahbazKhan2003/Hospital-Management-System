package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospitalSystem";
    private static final String username = "root";
    private static final String password = "hackerhaiyabot";

    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
             e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. EXIT");
                System.out.println("Enter you choice: ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointment
                        bookAppointement(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        //Exit
                        return;
                    default:
                        System.out.println("ENTER valid number");
                        break;
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointement(Patient patient, Doctor doctor, Connection connection,Scanner sc) {
        System.out.println("Enter Patient ID: ");
        int patientId = sc.nextInt();
        System.out.println("Enter Doctor ID: ");
        int doctorId = sc.nextInt();
        System.out.println("Enter appointment date yyyy-mm-dd");
        String appointmentDate = sc.next();

        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDocAvail(doctorId,appointmentDate,connection)) {
                String query = "insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int affectedRows = preparedStatement.executeUpdate();
                    if(affectedRows > 0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to book appointment");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor is not Available");
            }
        }else{
            System.out.println("Either Doctor/Patient Id is not present");
        }
    }
    public static boolean checkDocAvail(int docId, String appointmentDate , Connection connection){
        String query = "select count(*) from appointments where doctor_id=? and appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,docId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet =preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0) return true;
                else return false;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}