import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteManages extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteManages()
    {
        setTitle("Delete data from table: manages");
        setSize(420, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] managers = getManagersCodes();

        JLabel drvAT = new JLabel("Manager AT:");
        panel.add(drvAT);

        dropdownList1 = new JComboBox<>(managers);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String manager;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    manager = parts[0];
                }
                else
                    manager = "";

                String deleteManagerStatus = deleteManagerFunction(manager);
                JOptionPane.showMessageDialog(null, deleteManagerStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a manager AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteManagerFunction(String mngAT)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(mngAT.equals("")) {
                sql = "DELETE FROM manages";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Manages record(s) deleted successfully!";
                    else
                        deleteStatus = "Manages table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM manages where mng_adm_AT=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, mngAT);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Manages record deleted successfully!";


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

    private String[] getManagersCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> managers = new ArrayList<>();
        managers.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT m.mng_adm_AT,w.wrk_name,w.wrk_lname,m.mng_br_code FROM worker AS w " +
                    " INNER JOIN manages AS m ON w.wrk_AT=m.mng_adm_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currMng = resultSet.getString("m.mng_adm_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String brCode = resultSet.getString("m.mng_br_code");
                String info = currMng + ", Name-Lastname: " + name + "-" + lname + ", Manages Branch: " + brCode;
                managers.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return managers.toArray(new String[managers.size()]);
    }
}
