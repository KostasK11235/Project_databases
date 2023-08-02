import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeletePhones extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeletePhones()
    {
        setTitle("Delete data from table: phones");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] phones = getPhones();

        JLabel drvAT = new JLabel("Branch Phone:");
        panel.add(drvAT);

        dropdownList1 = new JComboBox<>(phones);
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
                String phNumber;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    branch = parts[0];

                    String[] phoneParts = parts[1].split(": ");
                    phNumber = phoneParts[1];
                }
                else
                {
                    branch = "";
                    phNumber = "";
                }

                String deletePhoneStatus = deletePhonesFunction(branch, phNumber);
                JOptionPane.showMessageDialog(null, deletePhoneStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a branch code to delete its phone from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deletePhonesFunction(String brCode, String phone)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(brCode.equals("") && phone.equals("")) {
                sql = "DELETE FROM phones";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Phones record(s) deleted successfully!";
                    else
                        deleteStatus = "Phones table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM phones WHERE ph_br_code=? AND ph_number=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, brCode);
                statement.setString(2, phone);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Phone record deleted successfully!";

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

    private String[] getPhones()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> phones = new ArrayList<>();
        phones.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT ph_br_code,ph_number FROM phones";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("ph_br_code");
                String currNumber = resultSet.getString("ph_number");
                String data = currCode + ", Number: " + currNumber;
                phones.add(data);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return phones.toArray(new String[phones.size()]);
    }
}
