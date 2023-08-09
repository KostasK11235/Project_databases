import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateGuide extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateGuide()
    {
        setTitle("Update table: guide");
        setSize(400, 210);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] guidesAT = getGuideAT();

        JLabel admAT = new JLabel("Guide AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(guidesAT);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("Guide CV:");
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
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String cv = field1.getText();

                String[] parts = selectedAT.split(",");
                String guideAT = parts[0];

                String deleteAdminStatus = updateGuideFunction(guideAT, cv);
                JOptionPane.showMessageDialog(null, deleteAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a guide AT to update that guides data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateGuideFunction(String at, String cv)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE guide SET gui_cv=? WHERE gui_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, cv);
            statement.setString(2, at);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Guide record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getGuideAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> admins = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker AS w INNER JOIN guide AS g" +
                    " ON w.wrk_AT=g.gui_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String guiBr = resultSet.getString("w.wrk_br_code");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname + "at Branch: " + guiBr;
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