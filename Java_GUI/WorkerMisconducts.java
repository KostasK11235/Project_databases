import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerMisconducts extends JFrame {
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public WorkerMisconducts() {
        setTitle("Report a worker's misconduct: ");
        setSize(350, 220);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] workersAT = getWorkersAT();

        JLabel admAT = new JLabel("Worker AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(workersAT);
        panel.add(dropdownList1);

        JLabel brStreet = new JLabel("Misconduct Description:");
        panel.add(brStreet);

        field1 = new JTextField(15);
        panel.add(field1);

        insertButton = new JButton("Report");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String descr = field1.getText();

                String[] parts = selected.split(",");
                String workerAT = parts[0].trim();
                String branch = parts[1].trim().replace("Branch:", "");
                String[] nameLastname = parts[2].trim().replace("Name-Lastname:", "").split("-");

                String name = nameLastname[0].trim();
                String lastname = nameLastname[1].trim();


                String reportMisconductStatus = reportFunction(workerAT, branch, name, lastname, descr);
                JOptionPane.showMessageDialog(null, reportMisconductStatus);
            }
        });

    }
    private String reportFunction(String wrkAT, String branch, String name, String lname, String descr)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO misconducts(msc_wrk_AT,msc_wrk_name,msc_wrk_lname,msc_wrk_branch,msc_descr) VALUES (?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, wrkAT);
            statement.setString(2, name);
            statement.setString(3, lname);
            statement.setString(4, branch);
            statement.setString(5, descr);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                insertStatus = "Report has been added to misconducts table!";

            // Select the workers AT from blacklist where they have records in table misconducts so we can erase those records
            List<String> returnedWorkers = new ArrayList<>();

            sql = "SELECT b.blk_wrk_AT FROM blacklist b INNER JOIN misconducts m ON m.msc_wrk_AT=b.blk_wrk_AT";
            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                returnedWorkers.add(resultSet.getString("b.blk_wrk_AT"));

            }

            String[] blacklisted = returnedWorkers.toArray(new String[returnedWorkers.size()]);

            for(String person: blacklisted)
            {
                CallableStatement callableStatement = connection.prepareCall("{CALL delete_worker(?)}");
                callableStatement.setString(1, person);

                boolean hasResultSet = callableStatement.execute();
                if (!hasResultSet) {
                    rowsAffected = callableStatement.getUpdateCount();
                    if (rowsAffected > 4) {
                        insertStatus = "Worker has been deleted from worker table!";
                    }
                }

                callableStatement.close();
            }

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    // We select only the wrk_AT,wrk_name and wrk_lname from the workers that are either drivers or guides
    private String[] getWorkersAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> workers = new ArrayList<>();
        workers.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_AT,wrk_name,wrk_lname, wrk_br_code FROM worker WHERE wrk_AT IN " +
                    "(SELECT drv_AT FROM driver UNION SELECT gui_AT FROM guide)";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currAT = resultSet.getString("wrk_AT");
                String name = resultSet.getString("wrk_name");
                String lname = resultSet.getString("wrk_lname");
                String branch = resultSet.getString("wrk_br_code");
                String info = currAT + ", Branch: " + branch + ", Name-Lastname: " + name + "-" + lname;
                workers.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return workers.toArray(new String[workers.size()]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new WorkerMisconducts().setVisible(true);
            }
        });
    }
}

