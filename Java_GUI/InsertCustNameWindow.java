import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.CallableStatement;

public class InsertCustNameWindow extends JFrame
{
    private JTextField LastNameField;
    private JButton searchButton;

    public InsertCustNameWindow() {
        setTitle("Enter customer last name for search");
        setSize(300, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);

        JLabel lastNameLabel = new JLabel("Last Name:");
        panel.add(lastNameLabel);

        LastNameField = new JTextField(15);
        panel.add(LastNameField);

        searchButton = new JButton("Search");
        panel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lastName = LastNameField.getText();

                List<String> participation = searchLastName(lastName);
                if (participation.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No matches found!");
                } else {
                    new ResultScreen(participation).setVisible(true);
                }
            }
        });

    }

    private List<String> searchLastName(String lastName)
    {
        List<String> participation = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "{CALL get_Offers_Participation(?)}";
            CallableStatement callStmt = connection.prepareCall(sql);
            callStmt.setString(1, lastName);

            boolean hasResultSet = callStmt.execute();

            if(hasResultSet)
            {
                ResultSet resultSet = callStmt.getResultSet();

                resultSet.last();
                int resultSetSize = resultSet.getRow();

                // Check if it's a single result of multiple rows
                if(resultSetSize==1)
                {
                    participation.add("Customer Name\tCustomer Last Name\tTrip Offer Code");
                    String participant = resultSet.getString("cust_name") + "\t\t" +
                            resultSet.getString("cust_lname") + "\t\t" +
                            resultSet.getInt("trip_offer_code");

                    participation.add(participant);
                }
                else if(resultSetSize>1)
                {
                    participation.add("Trip Offer Code\tTotal People");
                    resultSet.beforeFirst();	// Move the cursor back to the beginning
                    while(resultSet.next())
                    {
                        String participant = resultSet.getInt("trip_offer_code") + "\t"
                                + resultSet.getInt("total_people");

                        participation.add(participant);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return participation;
    }
}
