import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteBranch extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteBranch()
    {
        setTitle("Delete data from table: Branch");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] branchCodes = getBranchCodes();

        JLabel admAT = new JLabel("Branch Code:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(branchCodes);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String branch;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    branch = parts[0];
                }
                else
                {
                    branch = "";
                }

                String deleteBranchStatus = deleteBranchFunction(branch);
                JOptionPane.showMessageDialog(null, deleteBranchStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a branch code to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteBranchFunction(String brCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(("".equalsIgnoreCase(brCode)))
            {
                sql = "DELETE FROM branch";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Branch record(s) deleted successfully!";
                    else
                        deleteStatus = "Branch table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM branch WHERE br_code=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, brCode);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Branch record deleted successfully!";

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

    private String[] getBranchCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> branchCodes = new ArrayList<>();
        branchCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT br_code,br_city,br_street FROM branch";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("br_code");
                String city = resultSet.getString("br_city");
                String street = resultSet.getString("br_street");
                String info = currCode + ", City: " + city + ", Street: " + street;
                branchCodes.add(info);
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