import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateWorker extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<String> dropDownList1;
    private JComboBox<String> dropDownList2;
    private JButton updateButton;
    private JButton helpButton;

    public UpdateWorker()
    {
        setTitle("Update table: Worker");
        setSize(450, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] wrkATs = getWorkerATs();
        String[] brCodes = getBranchCodes();

        JLabel wrkAT = new JLabel("Worker AT:");
        panel.add(wrkAT);

        dropDownList1 = new JComboBox<>(wrkATs);
        panel.add(dropDownList1);

        JLabel name = new JLabel("Name:");
        panel.add(name);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel lname = new JLabel("Last Name:");
        panel.add(lname);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel salary = new JLabel("Salary:");
        panel.add(salary);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel brCode = new JLabel("Branch:");
        panel.add(brCode);

        dropDownList2 = new JComboBox<>(brCodes);
        panel.add(dropDownList2);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropDownList1.getSelectedItem();
                String name = field1.getText();
                String lname = field2.getText();
                String salary = field3.getText();
                String branch = (String) dropDownList2.getSelectedItem();

                String[] parts = selectedAT.split(",");
                String wrkAT = parts[0];

                String updateWorkerStatus = updateWorkerFunction(wrkAT, name, lname, salary, branch);
                JOptionPane.showMessageDialog(null, updateWorkerStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose a worker AT to update that workers data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateWorkerFunction(String wrkAT, String name, String lname, String salary, String branch)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE worker SET wrk_name=?,wrk_lname=?,wrk_salary=?,wrk_br_code=? WHERE wrk_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, lname);
            statement.setString(3, salary);
            statement.setString(4, branch);
            statement.setString(5, wrkAT);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Worker record updated successfully!";
            }

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }
        return UpdateStatus;
    }
    private String[] getWorkerATs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> wrkATs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_AT,wrk_name,wrk_lname,wrk_br_code FROM worker";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("wrk_AT");
                String name = resultSet.getString("wrk_name");
                String lname = resultSet.getString("wrk_lname");
                String branch = resultSet.getString("wrk_br_code");
                String info = currCode + ", Name-LastName: " + name + "-" + lname + ", Branch: " + branch;
                wrkATs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return wrkATs.toArray(new String[wrkATs.size()]);
    }

    private String[] getBranchCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> brCodes = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT br_code FROM branch";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("br_code");
                brCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return brCodes.toArray(new String[brCodes.size()]);
    }
}

