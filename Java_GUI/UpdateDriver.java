import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateDriver extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropDownList2;
    private JComboBox<String> dropDownList3;
    private JButton updateButton;
    private JButton helpButton;

    public UpdateDriver()
    {
        setTitle("Update table: Driver");
        setSize(400, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] licenseTypes = {"A", "B", "C", "D"};
        String[] routes = {"LOCAL", "ABROAD"};

        String[] drvCodes = getDriverCodes();

        JLabel admAT = new JLabel("Driver AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(drvCodes);
        panel.add(dropdownList1);

        JLabel license = new JLabel("License:");
        panel.add(license);

        dropDownList2 = new JComboBox<>(licenseTypes);
        panel.add(dropDownList2);

        JLabel route = new JLabel("Route:");
        panel.add(route);

        dropDownList3 = new JComboBox<>(routes);
        panel.add(dropDownList3);

        JLabel exp = new JLabel("Experience:");
        panel.add(exp);

        field1 = new JTextField(15);
        panel.add(field1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String license = (String) dropDownList2.getSelectedItem();
                String route = (String) dropDownList3.getSelectedItem();
                String experience = field1.getText();

                String[] parts = selectedAT.split(",");
                String drvAT = parts[0];

                String updateDriverStatus = updateDriverFunction(drvAT, license, route,experience);
                JOptionPane.showMessageDialog(null, updateDriverStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a drivers AT to update that reservations drivers data on the table.
                        2. Experience needs to be in months.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateDriverFunction(String drvAT, String license, String route, String experience)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);

            String sql = "SELECT d.dst_name,d.dst_location FROM trip AS t JOIN travel_to AS tt ON t.tr_id=tt.to_tr_id" +
                    " JOIN destination AS d ON tt.to_dst_id=d.dst_id WHERE t.tr_drv_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, drvAT);

            ResultSet resultSet = statement.executeQuery();

            // get a list of the dst_name and dst_location for each trip with that driver and compare if it is local or not
            // to do that say if dst_name OR dst_location != LONDON and newDriverRoute == LOCAL, deny the update
            while(resultSet.next())
            {
                String currCode = resultSet.getString("offer_code");
                offerCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

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

        List<String> dstIDs = new ArrayList<>();

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
                dstIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return dstIDs.toArray(new String[dstIDs.size()]);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new UpdateReservationOffers().setVisible(true); }
        });
    }
}
