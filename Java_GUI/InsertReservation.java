import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertReservation extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList3;

    private JButton insertButton;

    public InsertReservation(String loggedAdmin) {
        setTitle("Insert data for table: Reservation");
        setSize(360, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] resTypes = {"ADULT", "MINOR"};
        String[] tripIDs = getTripIDs();

        JLabel resTrID = new JLabel("Trip ID:");
        panel.add(resTrID);

        dropdownList1 = new JComboBox<>(tripIDs);
        panel.add(dropdownList1);

        JLabel resName = new JLabel("res_name:");
        panel.add(resName);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel resLName = new JLabel("res_lname");
        panel.add(resLName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel resIsAdult = new JLabel("Is Adult:");
        panel.add(resIsAdult);

        dropdownList3 = new JComboBox<>(resTypes);
        panel.add(dropdownList3);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resTrID = (String) dropdownList1.getSelectedItem();
                String resName = field1.getText();
                String resLName = field2.getText();
                String isAdult = (String) dropdownList3.getSelectedItem();
                
                // Open new window to select new seat
                String[] remSeats = getRemainingSeats(resTrID);

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
                        String seatnum = (String) dropDownList2.getSelectedItem();
                        seatFrame.dispose(); // Close the seat selection window
                        String insertReservationStatus = insertReservationFunction(resTrID, seatnum, resName, resLName,
                                isAdult, loggedAdmin);
                        JOptionPane.showMessageDialog(null, insertReservationStatus);
                    }
                });
            }
        });
    }

    private String insertReservationFunction(String trID, String seatnum, String name, String lname, String isAdult,
                                             String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, trID);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Field res_tr_id must match an existing tr_id!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO reservation VALUES (?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, trID);
            statement.setString(2, seatnum);
            statement.setString(3, name);
            statement.setString(4, lname);
            statement.setString(5, isAdult);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New reservation inserted into reservation table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "reservation");
                statement.setString(3, "INSERT");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = "Reservation with the same res_tr_id and res_seatnum already exists!";
        }
        return insertStatus;
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

    private String[] getTripIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> branches = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT tr_id FROM trip";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("tr_id");
                branches.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return branches.toArray(new String[branches.size()]);
    }
}
