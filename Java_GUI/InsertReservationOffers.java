import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertReservationOffers extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JTextField field5;
    private JButton insertButton;

    public InsertReservationOffers() {
        setTitle("Insert data for table: reservation_offers");
        setSize(350, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel resOfferCode = new JLabel("res_offer_code:");
        panel.add(resOfferCode);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel custName = new JLabel("cust_name:");
        panel.add(custName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel custLName = new JLabel("cust_lname:");
        panel.add(custLName);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel trOfferCode = new JLabel("trip_offer_code");
        panel.add(trOfferCode);

        field4 = new JTextField(15);
        panel.add(field4);

        JLabel advanceFee = new JLabel("advance_fee");
        panel.add(advanceFee);

        field5 = new JTextField(15);
        panel.add(field5);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resOfferCode = field1.getText();
                String name = field2.getText();
                String lname = field3.getText();
                String offerCode = field4.getText();
                String advanceFee = field5.getText();

                String insertReservationOffersStatus = insertReservationOffersFunction(resOfferCode, name, lname,
                        offerCode, advanceFee);
                JOptionPane.showMessageDialog(null, insertReservationOffersStatus);
            }
        });
    }

    private String insertReservationOffersFunction(String resOfferCode, String name, String lname, String trOfferCode,
                                                   String advanceFee)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM offers WHERE offer_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, trOfferCode);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Trip_offer_code must match an existing offer_code in offers table!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO reservation_offers VALUES (?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, resOfferCode);
            statement.setString(2, name);
            statement.setString(3, lname);
            statement.setString(4, trOfferCode);
            statement.setString(5, advanceFee);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New reservation inserted into reservation_offers table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = "Reservation with the same res_offer_code already exists in reservation_offers table!";
        }
        return insertStatus;
    }
}