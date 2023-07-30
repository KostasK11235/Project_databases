import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteAdmin extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton deleteButton;
    private JButton helpButton;


    public DeleteAdmin()
    {
        setTitle("Delete data from table: admin");
        setSize(350, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adm_types = {"", "LOGISTICS", "ADMINISTRATIVE", "ACCOUNTING"};
        String[] adminsAT = getAdminsAT();

        helpButton = new JButton("Help");
        panel.add(helpButton);

        JLabel admAT = new JLabel("Admin AT:");
        panel.add(admAT);

        dropdownList2 = new JComboBox<>(adminsAT);
        panel.add(dropdownList2);

        JLabel admType = new JLabel("Admin type:");
        panel.add(admType);

        dropdownList1 = new JComboBox<>(adm_types);
        panel.add(dropdownList1);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String admAT = (String) dropdownList2.getSelectedItem();
                String admType = (String) dropdownList1.getSelectedItem();

                String deleteAdminStatus = deleteAdminFunction(admAT, admType);
                JOptionPane.showMessageDialog(null, deleteAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an admin AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String deleteAdminFunction(String at, String type)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(at.equals("") && type.equals(""))
            {
                sql = "DELETE FROM admin";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Admin record(s) deleted successfully!";
                    else
                        deleteStatus = "Admin table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else if(type.equals(""))
            {
                sql = "DELETE FROM admin WHERE adm_AT=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, at);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Admin record deleted successfully!";

                statement.close();
                connection.close();
            }
            else if(at.equals(""))
            {
                sql = "DELETE FROM admin WHERE adm_type=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, type);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Admin record deleted successfully!";

                statement.close();
                connection.close();
            }

        }
        catch (SQLException ex)
        {
            deleteStatus = ex.getMessage();
        }

        return deleteStatus;
    }

    private String[] getAdminsAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> admins = new ArrayList<>();
        admins.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT adm_AT FROM admin";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("adm_AT");
                admins.add(currCode);
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

