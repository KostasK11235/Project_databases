import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteReservation extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteReservation(String loggedAdmin)
    {
        setTitle("Delete data from table: reservation");
        setSize(500, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] reservations = getReservations();

        JLabel evID = new JLabel("Reservation trip id:");
        panel.add(evID);

        dropdownList1 = new JComboBox<>(reservations);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String resID;
                String seatNum;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    resID = parts[0];

                    String[] resParts = parts[1].split(": ");
                    seatNum = resParts[1];
                }
                else
                {
                    resID = "";
                    seatNum = "";
                }

                String deleteReservationStatus = deleteReservationFunction(resID, seatNum, loggedAdmin);
                JOptionPane.showMessageDialog(null, deleteReservationStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a reservation id to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteReservationFunction(String resID,String seat, String currAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(resID.equals("")) {
                sql = "DELETE FROM reservation";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        deleteStatus = "Reservation record(s) deleted successfully!";

                        sql = "SELECT log_date FROM it_logs WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, "reservation");
                        statement.setString(2, "DELETE");

                        ResultSet resultSet = statement.executeQuery();
                        resultSet.next();
                        String date = resultSet.getString("log_date");

                        // Extract the last two characters from the original string
                        String lastTwoCharacters = date.substring(date.length() - 2);

                        // Convert the last two characters to an integer
                        int oldValue = Integer.parseInt(lastTwoCharacters);

                        // Subtract 3 from the old value
                        int newValue = oldValue - 3;

                        // Create the modified string by replacing the last two characters with the new value
                        String bottomDate = date.substring(0, date.length() - 2) + String.format("%02d", newValue);

                        newValue = oldValue + 3;
                        String ceilingDate = date.substring(0, date.length() - 2) + String.format("%02d", newValue);

                        sql = "UPDATE it_logs SET IT_id=? WHERE log_date>=? AND log_date<=? AND table_name=?";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, currAdmin);
                        statement.setString(2, bottomDate);
                        statement.setString(3, ceilingDate);
                        statement.setString(4, "reservation");

                        statement.executeUpdate();
                    }
                    else
                        deleteStatus = "Reservation table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM reservation WHERE res_tr_id=? AND res_seatnum=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, resID);
                statement.setString(2, seat);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                {
                    deleteStatus = "Reservation record deleted successfully!";
                    sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, currAdmin);
                    statement.setString(2, "reservation");
                    statement.setString(3, "DELETE");

                    statement.executeUpdate();
                }

                statement.close();
                connection.close();
            }
        }
        catch (SQLException ex)
        {
            deleteStatus = ex.getMessage();
        }

        return deleteStatus;
    }

    private String[] getReservations()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT res_tr_id,res_seatnum FROM reservation";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("res_tr_id");
                String seatNumber = resultSet.getString("res_seatnum");
                String info = currCode + ", Seat Number: " + seatNumber;
                guideCodes.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return guideCodes.toArray(new String[guideCodes.size()]);
    }
}
