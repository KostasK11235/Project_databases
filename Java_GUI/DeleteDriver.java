import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteDriver extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteDriver()
    {
        setTitle("Delete data from table: driver");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] drivers = getDriversCodes();

        JLabel drvAT = new JLabel("Driver AT:");
        panel.add(drvAT);

        dropdownList1 = new JComboBox<>(drivers);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String driver;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    driver = parts[0];
                }
                else
                {
                    driver = "";
                }

                String deleteDriverStatus = deleteDriverFunction(driver);
                JOptionPane.showMessageDialog(null, deleteDriverStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a driver AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteDriverFunction(String drvAT)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(drvAT.equals("")) {
                sql = "DELETE FROM driver";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Driver record(s) deleted successfully!";
                    else
                        deleteStatus = "Driver table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM driver WHERE drv_AT=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, drvAT);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Driver record deleted successfully!";


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

    private String[] getDriversCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> driversCodes = new ArrayList<>();
        driversCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker AS w INNER JOIN driver AS d" +
                    " ON w.wrk_AT=d.drv_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currAT = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String currCode = resultSet.getString("w.wrk_br_code");
                String info = currAT + ", Name-Lastname: " + name + "-" + lname + ", Branch: " + currCode;
                driversCodes.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return driversCodes.toArray(new String[driversCodes.size()]);
    }
}
