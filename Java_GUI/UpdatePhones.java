import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdatePhones extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton updateButton;
    private JButton helpButton;


    public UpdatePhones()
    {
        setTitle("Update table: phones");
        setSize(400, 170);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] brCodes = getBranchCodes();

        JLabel admAT = new JLabel("Branch Code:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(brCodes);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("Phone Number:");
        panel.add(admType);

        field1 = new JTextField(15);
        panel.add(field1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBr = (String) dropdownList1.getSelectedItem();
                String newPhone = field1.getText();

                String[] parts = selectedBr.split(",");
                String brCode = parts[0];
                String secondPart = parts[1];
                String[] phonePart = secondPart.split(":");
                String oldPhone = phonePart[1];

                String deleteAdminStatus = updatePhonesFunction(brCode, oldPhone, newPhone);
                JOptionPane.showMessageDialog(null, deleteAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a branch code to update that branches data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updatePhonesFunction(String brCode, String oldPhone, String newPhone)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE phones SET ph_number=? WHERE ph_br_code=? AND ph_number=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newPhone);
            statement.setString(2, brCode);
            statement.setString(3, oldPhone);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Phones record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getBranchCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> admins = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT ph_br_code,ph_number FROM phones";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currAT = resultSet.getString("ph_br_code");
                String language = resultSet.getString("ph_number");
                String info = currAT + ", Phone:" + language;
                admins.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return admins.toArray(new String[admins.size()]);
    }
}