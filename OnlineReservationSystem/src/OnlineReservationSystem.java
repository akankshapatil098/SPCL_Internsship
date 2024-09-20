import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Random;
import java.sql.ResultSet;

public class OnlineReservationSystem {
    private static  final int min=1000;
    private static  final int max=9999;

    public static class user
    {
        private String username;
        private String password;

        Scanner sc = new Scanner(System.in);

        public String getUserName()
        {
            System.out.println("Enter Username: ");
            username = sc.nextLine();
            return username;
        }
        public String getPassword()
        {
            System.out.println("Enter Password: ");
            password = sc.nextLine();
            return password;
        }
    }
    public static class pnrRecord{
        private int pnrNumber;
        private String PassengerName;
        private String TrainNumber;
        private String ClassType;
        private String JourneyDate;
        private String Start;
        private String End;

        Scanner sc=new Scanner(System.in);
        public int getpnrNumber()
        {
            Random r=new Random();
            pnrNumber=r.nextInt(max+min);
            return pnrNumber;
        }
        public String getPassengerName()
        {
            System.out.println("Enter Passenger Name: ");
            PassengerName=sc.nextLine();
            return PassengerName;
        }
        public String getTrainNumber()
        {
            System.out.println("Enter Train Number: ");
            TrainNumber=sc.nextLine();
            return TrainNumber;
        }
        public String getClassType()
        {
            System.out.println("Enter Class Type: ");
            ClassType=sc.nextLine();
            return ClassType;
        }
        public String getJourneyDate()
        {
            System.out.println("Enter Journey Date as 'YYYY-MM-DD' Format: ");
            JourneyDate=sc.nextLine();
            return JourneyDate;
        }
        public String getStart()
        {
            System.out.println("Enter the Starting Place: ");
            Start=sc.nextLine();
            return Start;
        }
        public String getEnd()
        {
            System.out.println("Enter the Destination : ");
            End=sc.nextLine();
            return End;
        }

    }

    public static void main(String[] args) {
       Scanner sc =new Scanner(System.in);
        user obj =new user();
        boolean p= true;
        String username=obj.getUserName();
        String password=obj.getPassword();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/onlinereservationsystem",username,password))
            {
                System.out.println("User Accesssed..!");
                while(p){
                    String InsertQuery="Insert into passengerDetails values(?,?,?,?,?,?,?)";
                    String DeleteQuery="Delete From passengerDetails where pnrNumber=?";
                    String ShowQuery="Select * from passengerDetails";
                    String ShowQuery2="Select * from passengerDetails where pnrNumber=?";
                    System.out.println("Please Enter the Choice: ");
                    System.out.println("1.Insert Record \n 2.Delete Record \n 3.Show all passenger Details \n 4.Show details using pnr \n 5.Exit the Application");

                    int choice=sc.nextInt();
                    switch(choice)
                    {
                        case 1:
                            pnrRecord p1=new pnrRecord();
                            int pnrNum=p1.getpnrNumber();
                            String pname=p1.getPassengerName();
                            String tno=p1.getTrainNumber();
                            String classtype=p1.getClassType();
                            String jdate=p1.getJourneyDate();
                            String startplace=p1.getStart();
                            String endplace=p1.getEnd();

                            try(PreparedStatement ps1=con.prepareStatement(InsertQuery))
                        {
                            ps1.setInt(1,pnrNum);
                            ps1.setString(2,pname);
                            ps1.setString(3,tno);
                            ps1.setString(4,classtype);
                            ps1.setString(5,jdate);
                            ps1.setString(6,startplace);
                            ps1.setString(7,endplace);

                            int changeRowInTable=ps1.executeUpdate();
                            if(changeRowInTable > 0)
                            {
                                System.out.println("Passenger Details added Successfully..!");
                            }
                            else {
                                System.out.println("failed to Add details");
                            }
                        }
                        catch(SQLException e){
                            System.out.println("SQLException : "+e.getMessage());
                        }
                        break;
                        case 2:
                            System.out.println("Enter PNR Number to delete the details: ");
                            int pnrnum=sc.nextInt();
                            try(PreparedStatement ps2=con.prepareStatement(DeleteQuery))
                            {
                                ps2.setInt(1,pnrnum);
                                int rowsaffect=ps2.executeUpdate();
                                if(rowsaffect > 0){
                                    System.out.println("Passenger Details deleted Successfully");
                                }
                                else{
                                    System.out.println("Passenger Details are not  deleted");
                                }

                            }
                            catch(SQLException e){
                                System.out.println("SQLException : "+e.getMessage());
                            }
                            break;
                        case 3:
                            try(PreparedStatement ps3=con.prepareStatement(ShowQuery))
                            {
                                ResultSet rs3=ps3.executeQuery();
                                while(rs3.next())
                                {
                                    int pnrNumb=rs3.getInt(1);
                                    String PasName=rs3.getString(2);
                                    String Trainname=rs3.getString(3);
                                    String classtyp=rs3.getString(4);
                                    String journeydat=rs3.getString(5);
                                    String started=rs3.getString(6);
                                    String ended=rs3.getString(7);

                                    System.out.println("------Passenger details------");
                                    System.out.println("PNR Number: "+pnrNumb);
                                    System.out.println("Passe Name: "+PasName);
                                    System.out.println("Train Name: "+Trainname);
                                    System.out.println("Class Type: "+classtyp);
                                    System.out.println("Journey Date: "+journeydat);
                                    System.out.println("From Location: "+started);
                                    System.out.println("To Location: "+ended);

                                    System.out.println();
                                }


                            }catch(SQLException e)
                            {
                                System.out.println("SQL Exception "+e.getMessage());
                            }
                            break;

                        case 4:
                            System.out.println("Enter PNR Number to get passenger details: ");

                            int pnrnum1=sc.nextInt();
                            try(PreparedStatement ps4=con.prepareStatement(ShowQuery2))
                            {
                                ps4.setInt(1, pnrnum1);
                                ResultSet rs4=ps4.executeQuery();
                                rs4.next();
                                int pnrNumb1=rs4.getInt(1);
                                String PasName1=rs4.getString(2);
                                String Trainname1=rs4.getString(3);
                                String classtyp1=rs4.getString(4);
                                String journeydat1=rs4.getString(5);
                                String started1=rs4.getString(6);
                                String ended1=rs4.getString(7);

                                System.out.println("PNR     Number: "+pnrNumb1+"   ");
                                System.out.println("Passe   Name:   "+PasName1+"   ");
                                System.out.println("Train   Name:   "+Trainname1+"   ");
                                System.out.println("Class   Type:   "+classtyp1+"   ");
                                System.out.println("Journey Date:   "+journeydat1+"   ");
                                System.out.println("From    Location:"+started1+"   ");
                                System.out.println("To Location:     "+ended1);


                            }catch(SQLException e)
                            {
                                System.out.println("SQL Exception "+e.getMessage());
                            }
                            break;
                        case 5:
                            System.out.println("Exiting the program - Thank You");
                            p=false;
                            break;
                        default:
                            System.out.println("Invalid choice, Please Enter valid choice ");

                    }

                }


            }catch (SQLException e) {

                System.out.println("SQL Exception : "+e.getMessage());
            }


        } catch (ClassNotFoundException e) {

            System.out.println("Error Loading JDBC driver : "+e.getMessage());
        }
        sc.close();

    }

}


