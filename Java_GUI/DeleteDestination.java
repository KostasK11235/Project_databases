import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteDestination extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteDestination(String loggedAdmin)
    {
        setTitle("Delete data from table: destination");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] destinations = getDestinationCodes();

        JLabel dst = new JLabel("Destination code:");
        panel.add(dst);

        dropdownList1 = new JComboBox<>(destinations);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String dstCode;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    dstCode = parts[0];
                }
                else
                {
                    dstCode = "";
                }

                String deleteDestinationStatus = deleteDestinationFunction(dstCode, loggedAdmin);
                JOptionPane.showMessageDialog(null, deleteDestinationStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a destination code to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteDestinationFunction(String dstID, String currAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(dstID.equals("")) {
                sql = "DELETE FROM destination";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        deleteStatus = "Destination record(s) deleted successfully!";

                        sql = "SELECT log_date FROM it_logs WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, "destination");
                        statement.setString(2, "DELETE");

                        ResultSet resultSet = statement.executeQuery();
                        resultSet.next();
                        String date = resultSet.getString("log_date");

                        // Extract the last two characters from the original string
                        String lastTwoCharacters = date.substring(date.length() - 2);

                        // Convert the last two characters to an integer
                        int oldValue = Integer.parseInt(lastTwoCharacters);

                        // Subtract 3 from the old value
                        int newValue = oldValue - 3;

                        // Create the modified string by replacing the last two characters with the new value
                        String bottomDate = date.substring(0, date.length() - 2) + String.format("%02d", newValue);

                        newValue = oldValue + 3;
                        String ceilingDate = date.substring(0, date.length() - 2) + String.format("%02d", newValue);

                        sql = "UPDATE it_logs SET IT_id=? WHERE log_date>=? AND log_date<=? AND table_name=?";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, currAdmin);
                        statement.setString(2, bottomDate);
                        statement.setString(3, ceilingDate);
                        statement.setString(4, "destination");

                        statement.executeUpdate();
                    }
                    else
                        deleteStatus = "Destination table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM destination WHERE dst_id=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dstID);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                {
                    deleteStatus = "Destination record deleted successfully!";
                    sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, currAdmin);
                    statement.setString(2, "destination");
                    statement.setString(3, "DELETE");

                    statement.executeUpdate();
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

    private String[] getDestinationCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String currName = resultSet.getString("dst_name");
                String info = currCode + ", Name: " + currName;
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
