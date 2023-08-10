import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateManages extends JFrame{
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateManages()
    {
        setTitle("Update table: manages");
        setSize(400, 210);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adminsAT = getAdminsAT();
        String[] brCodes = getBranchCodes();

        JLabel admAT = new JLabel("Admin AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(adminsAT);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("Branch Codes:");
        panel.add(admType);

        dropdownList2 = new JComboBox<>(brCodes);
        panel.add(dropdownList2);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String branch = (String) dropdownList2.getSelectedItem();

                String[] parts = selectedAT.split(",");
                String admAT = parts[0];

                String updateManagesStatus = updateManagesFunction(admAT, branch);
                JOptionPane.showMessageDialog(null, updateManagesStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an admin AT to update that admins data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateManagesFunction(String at, String brCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE manages SET mng_br_code=? WHERE mng_adm_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, brCode);
            statement.setString(2, at);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Manages record updated successfully!";

            statement.close();
            connection.close();
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
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,m.mng_br_code FROM worker AS w INNER JOIN manages AS m" +
                    " ON w.wrk_AT=m.mng_adm_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String mngBr = resultSet.getString("m.mng_br_code");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname + "at Branch: " + mngBr;
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

