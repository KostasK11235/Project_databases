import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateReservationOffers extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropDownList2;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateReservationOffers()
    {
        setTitle("Update table: Reservation Offers");
        setSize(400, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] resCodes = getReservationCodes();
        String[] offerCodes = getTripOfferCodes();

        JLabel admAT = new JLabel("Reservation Code:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(resCodes);
        panel.add(dropdownList1);

        JLabel name = new JLabel("Customer Name:");
        panel.add(name);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel lname = new JLabel("Customer Last Name:");
        panel.add(lname);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel code = new JLabel("Trip Offer Code:");
        panel.add(code);

        dropDownList2 = new JComboBox<>(offerCodes);
        panel.add(dropDownList2);

        JLabel advance = new JLabel("Advance:");
        panel.add(advance);

        field3 = new JTextField(15);
        panel.add(field3);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCode = (String) dropdownList1.getSelectedItem();
                String resName = field1.getText();
                String resLName = field2.getText();
                String tripOffer = (String) dropDownList2.getSelectedItem();
                String advance = field3.getText();

                String[] parts = selectedCode.split(",");
                String resCode = parts[0];

                String updateReservationOffersStatus = updateReservationOffersFunction(resCode, resName, resLName,
                        tripOffer, advance);
                JOptionPane.showMessageDialog(null, updateReservationOffersStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose a reservation offer code to update that reservations data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateReservationOffersFunction(String resCode, String name, String lName, String tripOffer, String advance)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);

            if(Double.parseDouble(advance) < 50 || Double.parseDouble(advance) > 200)
            {
                UpdateStatus = "Advance must be more that 50$ and less than 200$!";
                return UpdateStatus;
            }

            String sql = "UPDATE reservation_offers SET cust_name=?,cust_lname=?,trip_offer_code=?,advance_fee=?" +
                    " WHERE res_offer_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, lName);
            statement.setString(3, tripOffer);
            statement.setString(4, advance);
            statement.setString(5, resCode);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Reservation offer record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }
    private String[] getReservationCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> reservations = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT res_offer_code,cust_name,cust_lname FROM reservation_offers";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("res_offer_code");
                String name = resultSet.getString("cust_name");
                String lName =resultSet.getString("cust_lname");
                String info = currCode + ", Name-LastName: " + name + "-" + lName;
                reservations.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return reservations.toArray(new String[reservations.size()]);
    }

    private String[] getTripOfferCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> offerCodes = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT offer_code FROM offers";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("offer_code");
                offerCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return offerCodes.toArray(new String[offerCodes.size()]);
    }
}
