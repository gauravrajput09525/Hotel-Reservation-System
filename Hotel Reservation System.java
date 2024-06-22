//import javax.swing.text.DefaultEditorKit;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class Reservation {
  private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "Gaurav@2001";

  public static void main(String [] args){
     try {
         Class.forName("com.mysql.cj.jdbc.Driver");
     }catch(ClassNotFoundException e){
         System.out.println(e.getMessage());
     }
     //    CONNECTION ESTABLISH
     try{
         Connection connection = DriverManager.getConnection(url , username , password);
         while(true){
             System.out.println();
             System.out.println("HOTEL MANAGEMENT SYSTEM");
             Scanner scanner = new Scanner(System.in);
             System.out.println(" 1. Reserve a Room");
             System.out.println("2. View reservation");
             System.out.println("3. Get Room Number");
             System.out.println("4. Update Reservation");
             System.out.println("6. Delete Reservation");
             System.out.println("0. Exit");
             System.out.println("Choose an Option: ");

             int choice = scanner.nextInt();

             switch(choice){
                 case 1:
                    reserveRoom(connection , scanner);
                    break;
                 case 2:
                     viewReservation(connection);
                     break;
                 case 3:
                     getRoomNumber(connection , scanner);
                     break;
                 case 4:
                     updateReservation(connection , scanner);
                     break;
                 case 5:
                     deleteReservation(connection , scanner);
                     break;
                 case 0:
                     exit();
                     scanner.close();
                     return;
                 default:
                     System.out.println("Invalid choice. Try Again.");
             }


         }
     }catch(SQLException e){
         System.out.println(e.getMessage());
     }catch(InterruptedException e){
         throw new RuntimeException(e);

     }
    }
    private static void reserveRoom(Connection connection , Scanner scanner)throws  SQLException{
      try{
          System.out.println("Enter Guest Name: ");
          String guestName = scanner.nextLine();
          System.out.println("Enter Room Number: ");
          int roomNumber = scanner.nextInt();
          System.out.println("Enter Contact Number: ");
          String contactNumber = scanner.nextLine();

          String sql = "INSERT INTO reservations (guest_name , room_number , contact_number)" +
                  " VALUES(" + guestName + " , " + roomNumber + " , " + contactNumber + ")";
          // STATEMENT ESTABLISH
          try(Statement statement = connection.createStatement()){
              int affectedRows = statement.executeUpdate(sql);
              if(affectedRows>0){
                  System.out.println("Reservation Successfully.");
              }else{
                  System.out.println("Reservation Failed.");
              }
          }

      }catch (SQLException e){
          e.printStackTrace();
      }
  }

  private  static void viewReservation(Connection connection) throws SQLException{
      String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

      try (Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(sql)) {

          System.out.println(" Current Reservation: ");
          System.out.println("+----------------+---------------+---------------+------------------+-------------------+");
          System.out.println("| Reservation_id | Guest         | Room Number   | Contact Number   | Reservation Date  |");
          System.out.println("+----------------+---------------+---------------+------------------+-------------------+");

          while (resultSet.next()){
              int reservationId = resultSet.getInt("reservation_id");
              String guestName = resultSet.getString("guest_name");
              int roomNumber = resultSet.getInt("room_number");
              String contactNumber = resultSet.getString("contact_number");
              String reservationDate = resultSet.getTimestamp("reservation_date").toString();

              // Format and Display the reservation date in a table_like format
              System.out.printf("| %-14d | %-15s | %-13d |%-20s | %-19s  |\n",
                      reservationId,guestName,roomNumber,contactNumber,reservationDate);
          }
          System.out.println("+-------------+---------------+------------------+--------------------+------------------+");
      }
  }

    private  static void getRoomNumber(Connection connection, Scanner scanner) throws SQLException{
      try{
          System.out.println("Enter Reservation Id: ");
          int reservationId = scanner.nextInt();
          System.out.println("Enter guest name: ");
          String guestName = scanner.nextLine();

        String sql = "SELECT room_number FROM reservations " +
        "WHERE reservation_id = " + reservationId +
        "AND guest_name = " + guestName + " ";


          try (Statement statement = connection.createStatement();
               ResultSet resultSet = statement.executeQuery(sql)) {

              if(resultSet.next()) {
                 int roomNumber = resultSet.getInt("room_number");
                  System.out.println("Room number for Reservation ID " + reservationId +
                          " and guest " + guestName + " is: " + roomNumber);
              }
              else{
                  System.out.println("Reservation not Found for the given ID and guest name. ");
              }
          }
      }catch (SQLException e){
          e.printStackTrace();
      }
  }

    private  static void updateReservation(Connection connection, Scanner scanner)throws SQLException {
        System.out.println(" Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine();
try{
     if(!reservationExists(connection, reservationId)) {
         System.out.println("Reservation not found for the given ID. ");

         return;
     }

     System.out.println("Enter new guest name: ");
     String newGuestName = scanner.nextLine();
     System.out.println("Enter new room number: ");
     int newRoomNumber = scanner.nextInt();
     System.out.println("Enter new contact number: ");
     String newContactNumber   = scanner.nextLine();

     String sql = "UPDATE reservation SET guest_name = " + newGuestName + "," +
             "room_number = " + newRoomNumber + ", " +
             "contact_number = " + newContactNumber + ", " +
             "WHERE reservation_id = " + reservationId;

     try(Statement statement = connection.createStatement()){
         int affectedRows = statement.executeUpdate(sql);

         if(affectedRows>0){
             System.out.println("Reservation Update Successfully! ");
         }else{
             System.out.println("Reservation Update Failed. ");
         }
     }

    }catch(SQLException e){
      e.printStackTrace();
  }
}

private  static void deleteReservation(Connection connection, Scanner scanner) throws SQLException{
    try {
        System.out.println("Enter reservation ID to delete: ");
        int reservationId = scanner.nextInt();

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID. ");
            return;
        }
        String sql = "DELETE FROM reservations WHERE reservation_id " + reservationId;

        try(Statement statement = connection.createStatement()){
            int affectedRows = statement.executeUpdate(sql);

            if(affectedRows> 0){
                System.out.println("Reservation deleted Successfully.");
            }
            else{
                System.out.println("Reservation deletion Failed.");
            }
        }
    }catch(SQLException e){
        e.printStackTrace();
    }
}

    private static boolean reservationExists(Connection connection, int reservationId) throws SQLException {

    try{
        String sql = " SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId ;

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

           return resultSet.next();
        }
    }catch (SQLException e){
        e.printStackTrace();
      return false;
    }
}

public  static void exit() throws InterruptedException {

    System.out.println("Exiting System");

    int i = 5;
    while (i!=0) {

        System.out.print("...");
        Thread.sleep(412);
        i--;
    }
    System.out.println();
    System.out.println("ThankYou For Using Hotel Reservation System!!!");
}
}
