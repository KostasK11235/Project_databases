import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertDriver extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JTextField field5;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton insertButton;

    public InsertDriver() {
        setTitle("Insert data for table: Driver");
        setSize(350, 330);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] licenseTypes = {"A", "B", "C", "D"};
        String[] routes = {"LOCAL", "ABROAD"};

        JLabel drvAT = new JLabel("Driver AT:");
        panel.add(drvAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel drvLicense = new JLabel("License:");
        panel.add(drvLicense);

        dropdownList1 = new JComboBox<>(licenseTypes);
        panel.add(dropdownList1);

        JLabel drvRoute = new JLabel("Route:");
        panel.add(drvRoute);

        dropdownList2 = new JComboBox<>(routes);
        panel.add(dropdownList2);

        JLabel drvExperience = new JLabel("Experience:");
        panel.add(drvExperience);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel drvName = new JLabel("Name:");
        panel.add(drvName);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel drvLastName = new JLabel("Last Name:");
        panel.add(drvLastName);

        field4 = new JTextField(15);
        panel.add(field4);

        JLabel drvSalary = new JLabel("Salary:");
        panel.add(drvSalary);

        field5 = new JTextField(15);
        panel.add(field5);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String drvAT = field1.getText();
                String drvLicecnse = (String) dropdownList1.getSelectedItem();
                String drvRoute = (String) dropdownList2.getSelectedItem();
                String drvExp = field2.getText();
                String drvName = field3.getText();
                String drvLName = field4.getText();
                String drvSalary = field5.getText();

                String insertDriverStatus = insertDriverFunction(drvAT, drvName, drvLName, drvSalary,
                        drvLicecnse, drvRoute, drvExp);
                JOptionPane.showMessageDialog(null, insertDriverStatus);
            }
        });
    }

    private String insertDriverFunction(String at, String name, String lastName, String salary, String license,
                                        String route, String experience)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            CallableStatement callableStatement = connection.prepareCall("{CALL newDriver(?,?,?,?,?,?,?)}");
            callableStatement.setString(1, at);
            callableStatement.setString(2, name);
            callableStatement.setString(3, lastName);
            callableStatement.setString(4, salary);
            callableStatement.setString(5, license);
            callableStatement.setString(6, route);
            callableStatement.setString(7, experience);

            boolean hasResultSet = callableStatement.execute();
            if (!hasResultSet) {
                int rowsAffected = callableStatement.getUpdateCount();
                if (rowsAffected > 1) {
                    insertStatus = "New driver inserted into driver and worker table!";
                } else if(rowsAffected == 1){
                    insertStatus = "Driver with the same drv_AT already exists!";
                }
            }

            callableStatement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            insertStatus = "Error occurred while calling the stored procedure";
        }
        return insertStatus;
    }
}

