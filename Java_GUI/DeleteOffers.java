import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteOffers extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteOffers()
    {
        setTitle("Delete data from table: Offers");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] offers = getOffersCodes();

        JLabel offerCode = new JLabel("Offer code:");
        panel.add(offerCode);

        dropdownList1 = new JComboBox<>(offers);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String offer;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    offer = parts[0];
                }
                else
                {
                    offer = "";
                }

                String deleteOfferStatus = deleteOfferFunction(offer);
                JOptionPane.showMessageDialog(null, deleteOfferStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an offer code to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteOfferFunction(String offerCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(offerCode.equals("")) {
                sql = "DELETE FROM offers";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Offers record(s) deleted successfully!";
                    else
                        deleteStatus = "Offers table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM offers WHERE offer_code=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, offerCode);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Offer record deleted successfully!";


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

    private String[] getOffersCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> offerCodes = new ArrayList<>();
        offerCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT o.offer_code,d.dst_name FROM offers AS o INNER JOIN destination AS d" +
                    " ON d.dst_id=o.destination_id";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("o.offer_code");
                String dstName = resultSet.getString("d.dst_name");
                String info = currCode + ", Destination: " + dstName;
                offerCodes.add(info);
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
