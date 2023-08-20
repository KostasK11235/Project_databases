import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateReservation extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropDownList1;
    private JComboBox<String> dropDownList3;
    private JButton updateButton;
    private JButton helpButton;

    public UpdateReservation(String loggedAdmin)
    {
        setTitle("Update table: Reservation");
        setSize(400, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] isAdult = {"ADULT", "MINOR"};

        String[] resTrID = getReservationIDs();

        JLabel resID = new JLabel("Reservation Trip ID:");
        panel.add(resID);

        dropDownList1 = new JComboBox<>(resTrID);
        panel.add(dropDownList1);

        JLabel name = new JLabel("Reservation Name:");
        panel.add(name);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel lName = new JLabel("Reservation Last Name:");
        panel.add(lName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel age = new JLabel("ADULT/MINOR:");
        panel.add(age);

        dropDownList3 = new JComboBox<>(isAdult);
        panel.add(dropDownList3);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedID = (String) dropDownList1.getSelectedItem();
                String name = field1.getText();
                String lName = field2.getText();
                String isAdult = (String) dropDownList3.getSelectedItem();

                String[] parts = selectedID.split(",");
                String resID = parts[0];
                String secondPart = parts[parts.length - 1].trim();
                String[] seatPart = secondPart.split(":");
                String oldSeat = seatPart[1];

                // Open new window to select new seat
                String[] remSeats = getRemainingSeats(resID);

                JLabel seat = new JLabel("Seat Number:");
                JComboBox<String> dropDownList2 = new JComboBox<>(remSeats);
                JButton confirmSeatButton = new JButton("Confirm Seat");

                JPanel seatPanel = new JPanel();
                seatPanel.add(seat);
                seatPanel.add(dropDownList2);
                seatPanel.add(confirmSeatButton);

                JFrame seatFrame = new JFrame("Select New Seat");
                seatFrame.setSize(300, 150);
                seatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                seatFrame.setLocationRelativeTo(null);
                seatFrame.add(seatPanel);
                seatFrame.setVisible(true);

                confirmSeatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String newSeat = (String) dropDownList2.getSelectedItem();
                        seatFrame.dispose(); // Close the seat selection window
                        String updateReservationStatus = updateReservationFunction(resID, oldSeat, newSeat, name, lName, isAdult, loggedAdmin);
                        JOptionPane.showMessageDialog(null, updateReservationStatus);
                    }
                });
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose a reservation id to update that reservations data on the table.
                        2. Choose a new seat or leave the field empty to retain the existing reserved seat.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateReservationFunction(String resID, String oldSeat, String newSeat, String name, String lname,
                                             String isAdult, String loggedAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE reservation SET res_seatnum=?,res_name=?,res_lname=?,res_isadult=?" +
                    " WHERE res_tr_id=? AND res_seatnum=?";
            PreparedStatement statement = connection.prepareStatement(sql);

            if(newSeat.equalsIgnoreCase(" "))
                statement.setString(1, oldSeat);
            else
                statement.setString(1, newSeat);

            statement.setString(2, name);
            statement.setString(3, lname);
            statement.setString(4, isAdult);
            statement.setString(5, resID);
            statement.setString(6, oldSeat);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Reservation record updated successfully!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "reservation");
                statement.setString(3, "UPDATE");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }
        return UpdateStatus;
    }
    private String[] getReservationIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> resIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT res_tr_id,res_name,res_lname,res_seatnum FROM reservation";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("res_tr_id");
                String name = resultSet.getString("res_name");
                String lname = resultSet.getString("res_lname");
                String seat = resultSet.getString("res_seatnum");
                String info = currCode + ", Name-LastName: " + name + "-" + lname + ", Seat:" + seat;
                resIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return resIDs.toArray(new String[resIDs.size()]);
    }

    private String[] getRemainingSeats(String resID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> remSeats = new ArrayList<>();
        List<String> resSeats = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT tr_maxseats FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, resID);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String maxSeats = resultSet.getString("tr_maxseats");

            sql = "SELECT res_seatnum FROM reservation WHERE res_tr_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, resID);

            resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currSeat = resultSet.getString("res_seatnum");
                resSeats.add(currSeat);
            }

            remSeats.add(" ");
            for(int i=1;i<=Integer.parseInt(maxSeats); i++)
            {
                if(!resSeats.contains(String.valueOf(i)))
                    remSeats.add(String.valueOf(i));
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return remSeats.toArray(new String[remSeats.size()]);
    }
}
