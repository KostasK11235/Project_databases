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
        setSize(400, 250);
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
                        Update options:
                        1. Choose a drivers AT to update that drivers data on the table.
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
            String sql = "UPDATE driver SET drv_license=?,drv_route=?,drv_experience=? WHERE drv_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, license);
            statement.setString(2, route);
            statement.setString(3, experience);
            statement.setString(4, drvAT);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Driver record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }
    private String[] getDriverCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> drvIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker AS w INNER JOIN driver AS d" +
                    " ON d.drv_AT=w.wrk_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lName =resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-LastName: " + name + "-" + lName;
                drvIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return drvIDs.toArray(new String[drvIDs.size()]);
    }
}
