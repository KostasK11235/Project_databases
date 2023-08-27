import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.CallableStatement;

public class getCustomersByAdvance extends JFrame
{
    private JTextField field1;
    private JTextField field2;
    private JButton searchButton;

    public getCustomersByAdvance() {
        setTitle("Enter two numbers to search in that range:");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);

        JLabel base = new JLabel("Base:");
        panel.add(base);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel ceil= new JLabel("Ceil:");
        panel.add(ceil);

        field2 = new JTextField(15);
        panel.add(field2);

        searchButton = new JButton("Search");
        panel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String base = field1.getText();
                String ceil = field2.getText();

                if(Double.parseDouble(base) < Double.parseDouble(ceil))
                {
                    String temp = base;
                    base = ceil;
                    ceil = temp;
                }

                List<String> customers = searchCustomers(base, ceil);
                new ResultScreen(customers).setVisible(true);
            }
        });

    }

    private List<String> searchCustomers(String base, String ceil)
    {
        List<String> customers = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "{CALL get_Res_Offers_Names(?,?)}";
            CallableStatement callStmt = connection.prepareCall(sql);
            callStmt.setString(1, base);
            callStmt.setString(2, ceil);

            boolean hasResultSet = callStmt.execute();

            if(hasResultSet)
            {
                ResultSet resultSet = callStmt.getResultSet();

                customers.add("Customer Name\tCustomer Last Name");

                while(resultSet.next())
                {
                    String participant = resultSet.getString("Customer Name") + "\t" +
                            resultSet.getString("Customer Last Name");

                    customers.add(participant);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return customers;
    }
}
