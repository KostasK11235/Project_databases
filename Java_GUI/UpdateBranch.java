import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateBranch extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropdownList1;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateBranch()
    {
        setTitle("Update table: branch");
        setSize(400, 210);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] brCodes = getBranchCodes();

        JLabel admType = new JLabel("Branch Codes:");
        panel.add(admType);

        dropdownList1 = new JComboBox<>(brCodes);
        panel.add(dropdownList1);

        JLabel brStreet = new JLabel("Street:");
        panel.add(brStreet);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel brCity = new JLabel("City:");
        panel.add(brCity);

        field2 = new JTextField(15);
        panel.add(field2);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String branch = (String) dropdownList1.getSelectedItem();
                String street = field1.getText();
                String city = field2.getText();

                String deleteAdminStatus = updateBranchFunction(branch, street, city);
                JOptionPane.showMessageDialog(null, deleteAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a branch code to update its data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateBranchFunction(String brCode, String street, String city)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "UPDATE branch SET br_street=?,br_num=? WHERE br_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, street);
            statement.setString(2, city);
            statement.setString(3, brCode);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                UpdateStatus = "Branch record updated successfully!";

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getBranchCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> branchCodes = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT br_code FROM branch";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("br_code");
                branchCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return branchCodes.toArray(new String[branchCodes.size()]);
    }
}

