import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertDriver extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton insertButton;

    public InsertDriver(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] licenseTypes = {"A", "B", "C", "D"};
        String[] routes = {"LOCAL", "ABROAD"};

        JLabel drvAT = new JLabel("drv_AT:");
        panel.add(drvAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel drvLicense = new JLabel("drv_license:");
        panel.add(drvLicense);

        dropdownList1 = new JComboBox<>(licenseTypes);
        panel.add(dropdownList1);

        JLabel drvRoute = new JLabel("drv_route:");
        panel.add(drvRoute);

        dropdownList2 = new JComboBox<>(routes);
        panel.add(dropdownList2);

        JLabel drvExperience = new JLabel("drv_experience:");
        panel.add(drvExperience);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String drvAT = field1.getText();
                String drvLicecnse = (String) dropdownList1.getSelectedItem();
                String drvRoute = (String) dropdownList2.getSelectedItem();
                String drvExp = field2.getText();

                String insertDriverStatus = insertDriverFunction(drvAT, drvLicecnse, drvRoute, drvExp);
                JOptionPane.showMessageDialog(null, insertDriverStatus);
            }
        });
    }

    private String insertDriverFunction(String at, String license, String route, String experience)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_AT FROM worker WHERE wrk_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "In order to add new driver, his data must exist on worker table!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO driver VALUES (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, license);
            statement.setString(3, route);
            statement.setString(4, experience);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New driver inserted into driver table!";

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }
}

