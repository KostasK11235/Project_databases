import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteWorker extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteWorker()
    {
        setTitle("Delete data from table: worker");
        setSize(500, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] workers = getWorkersID();

        JLabel wrkID = new JLabel("Worker AT:");
        panel.add(wrkID);

        dropdownList1 = new JComboBox<>(workers);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String wrkAT;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    wrkAT = parts[0];
                }
                else
                {
                    wrkAT = "";
                }

                String deleteWorkerStatus = deleteWorkerFunction(wrkAT);
                JOptionPane.showMessageDialog(null, deleteWorkerStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a worker AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteWorkerFunction(String wrkAT)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(wrkAT.equals("")) {
                sql = "DELETE FROM worker";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Worker record(s) deleted successfully!";
                    else
                        deleteStatus = "Worker table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "SELECT * FROM admin WHERE adm_AT=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, wrkAT);

                ResultSet resultSet = statement.executeQuery();

                if(!resultSet.first())
                {
                    sql = "DELETE FROM worker WHERE wrk_AT=?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, wrkAT);

                    int rowsAffected = statement.executeUpdate();

                    if(rowsAffected>0)
                        deleteStatus = "Worker record deleted successfully!";
                }
                else
                {
                    try
                    {
                        sql = "SELECT wrk_name,wrk_lname FROM worker WHERE wrk_AT=?";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, wrkAT);

                        resultSet = statement.executeQuery();

                        resultSet.next();
                        String admin_name = resultSet.getString("wrk_name");
                        String admin_lname = resultSet.getString("wrk_lname");

                        String sql1 = "{CALL del_admin(?, ?)}";
                        CallableStatement statement1 = connection.prepareCall(sql1);
                        statement1.setString(1, admin_name);
                        statement1.setString(2, admin_lname);

                        boolean hasResults = statement1.execute();

                        if (hasResults) {
                            ResultSet resultSet1 = statement1.getResultSet();
                            if (resultSet1.next()) {
                                deleteStatus = resultSet1.getString("message");
                            }
                        }

                        statement1.close();
                        connection.close();
                    } catch (SQLException ex) {
                        deleteStatus = ex.getMessage();
                    }
                }

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

    private String[] getWorkersID()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_AT,wrk_name,wrk_lname FROM worker";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("wrk_AT");
                String name = resultSet.getString("wrk_name");
                String lastName = resultSet.getString("wrk_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lastName;
                guideCodes.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return guideCodes.toArray(new String[guideCodes.size()]);
    }
}
