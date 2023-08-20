import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateAdmin extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateAdmin()
    {
        setTitle("Update table: Admin");
        setSize(400, 210);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adm_types = {"LOGISTICS", "ADMINISTRATIVE", "ACCOUNTING"};

        String[] adminsAT = getAdminsAT();

        JLabel admAT = new JLabel("Admin AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(adminsAT);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("Admin type:");
        panel.add(admType);

        dropdownList2 = new JComboBox<>(adm_types);
        panel.add(dropdownList2);

        JLabel admDiploma = new JLabel("Admin diploma:");
        panel.add(admDiploma);

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
                String type = (String) dropdownList2.getSelectedItem();
                String diploma = field1.getText();

                String[] parts = selectedAT.split(",");
                String admAT = parts[0];

                String updateAdminStatus = updateAdminFunction(admAT, type, diploma);
                JOptionPane.showMessageDialog(null, updateAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose an admin AT to update that admins data on the table.
                        
                        Note!: You can not alter the type of an admin manager!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateAdminFunction(String at, String type, String diploma)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            sql = "SELECT adm_type FROM admin WHERE adm_AT=?";
            PreparedStatement  statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            if(Objects.equals(resultSet.getString("adm_type"), "ADMINISTRATIVE") &&
            !Objects.equals(type, "ADMINISTRATIVE"))
            {
                UpdateStatus = "Chosen admin is a branch manager. Altering his type is not enabled!";
                resultSet.close();
                statement.close();
                connection.close();

                return UpdateStatus;
            }
            else
            {
                sql = "UPDATE admin SET adm_type=?,adm_diploma=? WHERE adm_AT=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, type);
                statement.setString(2, diploma);
                statement.setString(3, at);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    UpdateStatus = "Admin record updated successfully!";

                statement.close();
                connection.close();
            }
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getAdminsAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> admins = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker AS w INNER JOIN admin AS a" +
                    " ON w.wrk_AT=a.adm_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname;
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

