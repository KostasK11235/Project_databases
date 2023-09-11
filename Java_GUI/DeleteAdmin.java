import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteAdmin extends JFrame{
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;


    public DeleteAdmin()
    {
        setTitle("Delete data from table: Admin");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adminsAT = getAdminsAT();

        JLabel admAT = new JLabel("Admin AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(adminsAT);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String admAT;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    admAT = parts[0];
                }
                else
                {
                    admAT = "";
                }

                String deleteAdminStatus = deleteAdminFunction(admAT);
                JOptionPane.showMessageDialog(null, deleteAdminStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an admin AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!
                        
                        (Note!: Managers are not included for being unable to delete!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String deleteAdminFunction(String at)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(at.equals(""))
            {
                sql = "DELETE FROM admin WHERE adm_type NOT LIKE 'ADMINISTRATIVE'";
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
            else
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
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,a.adm_type FROM worker AS w INNER JOIN admin AS a" +
                    " ON w.wrk_AT=a.adm_AT AND a.adm_type NOT LIKE 'ADMINISTRATIVE'";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String type = resultSet.getString("a.adm_type");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname + ", Type: " + type;
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

