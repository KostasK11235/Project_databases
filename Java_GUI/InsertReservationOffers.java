import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertReservationOffers extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertReservationOffers() {
        setTitle("Insert data for table: Reservation Offers");
        setSize(350, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] offerIDs = getOfferIDs();

        JLabel custName = new JLabel("Customer Name:");
        panel.add(custName);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel custLName = new JLabel("Customer Last Name:");
        panel.add(custLName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel trOfferCode = new JLabel("Trip Offer Code");
        panel.add(trOfferCode);

        dropdownList1 = new JComboBox<>(offerIDs);
        panel.add(dropdownList1);

        JLabel advanceFee = new JLabel("Advance Fee");
        panel.add(advanceFee);

        field3 = new JTextField(15);
        panel.add(field3);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = field1.getText();
                String lname = field2.getText();
                String selectedCode = (String) dropdownList1.getSelectedItem();
                String advanceFee = field3.getText();

                String[] parts = selectedCode.split(",");
                String offerCode = parts[0];

                String insertReservationOffersStatus = insertReservationOffersFunction(name, lname, offerCode, advanceFee);
                JOptionPane.showMessageDialog(null, insertReservationOffersStatus);
            }
        });
    }

    private String insertReservationOffersFunction(String name, String lname, String trOfferCode, String advanceFee)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO reservation_offers(cust_name,cust_lname,trip_offer_code,advance_fee) VALUES(?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, lname);
            statement.setString(3, trOfferCode);
            statement.setString(4, advanceFee);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New reservation inserted into reservation_offers table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getOfferIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> offers = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT o.offer_code,d.dst_name FROM offers o INNER JOIN destination d ON o.destination_id=d.dst_id";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("o.offer_code");
                String name = resultSet.getString("d.dst_name");
                String info = currCode + ", Destination: " + name;
                offers.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return offers.toArray(new String[offers.size()]);
    }
}