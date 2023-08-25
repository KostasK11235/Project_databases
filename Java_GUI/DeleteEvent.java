import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteEvent extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteEvent(String loggedAdmin)
    {
        setTitle("Delete data from table: Event");
        setSize(500, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] events = getEventIDs();

        JLabel evID = new JLabel("Event trip id:");
        panel.add(evID);

        dropdownList1 = new JComboBox<>(events);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String evCode;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    evCode = parts[0];
                }
                else
                {
                    evCode = "";
                }

                String deleteEventStatus = deleteEventFunction(evCode, loggedAdmin);
                JOptionPane.showMessageDialog(null, deleteEventStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an event code to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteEventFunction(String evID, String currAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(evID.equals("")) {
                sql = "DELETE FROM event";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        deleteStatus = "Event record(s) deleted successfully!";

                        sql = "SELECT log_date FROM it_logs WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, "event");
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
                        statement.setString(4, "event");

                        statement.executeUpdate();
                    }
                    else
                        deleteStatus = "Event table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM event WHERE ev_tr_id=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, evID);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                {
                    deleteStatus = "Event record deleted successfully!";
                    sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, currAdmin);
                    statement.setString(2, "event");
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

    private String[] getEventIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT ev_tr_id,ev_start,ev_end FROM event";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("ev_tr_id");
                String start = resultSet.getString("ev_start");
                String end = resultSet.getString("ev_end");
                String info = currCode + ", From-To: " + start + " - " + end;
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
