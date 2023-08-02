import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteTravelTo extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteTravelTo(String loggedAdmin)
    {
        setTitle("Delete data from table: travel_to");
        setSize(500, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] travelTo = getTravelIDs();

        JLabel trID = new JLabel("Travel trip id:");
        panel.add(trID);

        dropdownList1 = new JComboBox<>(travelTo);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String trID;
                String dstID;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    trID = parts[0];

                    String[] resParts = parts[1].split(": ");
                    dstID = resParts[1];
                }
                else
                {
                    trID = "";
                    dstID = "";
                }

                String deleteTravelStatus = deleteTravelFunction(trID, dstID, loggedAdmin);
                JOptionPane.showMessageDialog(null, deleteTravelStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a travel trip id to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteTravelFunction(String trID,String dstID, String currAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(trID.equals("")) {
                sql = "DELETE FROM travel_to";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        deleteStatus = "Travel_to record(s) deleted successfully!";

                        sql = "SELECT log_date FROM it_logs WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, "travel_to");
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
                        statement.setString(4, "travel_to");

                        statement.executeUpdate();
                    }
                    else
                        deleteStatus = "Travel_to table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM travel_to WHERE to_tr_id=? AND to_dst_id=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, trID);
                statement.setString(2, dstID);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                {
                    deleteStatus = "Travel_to record deleted successfully!";
                    sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, currAdmin);
                    statement.setString(2, "travel_to");
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

    private String[] getTravelIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT t.to_tr_id,t.to_dst_id,d.dst_name FROM travel_to AS t INNER JOIN destination AS d" +
                    " ON d.dst_id=t.to_dst_id";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("t.to_tr_id");
                String dstId = resultSet.getString("t.to_dst_id");
                String dstName = resultSet.getString("d.dst_name");
                String info = currCode + ", Destination ID: " + dstId + ", Name: " + dstName;
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
