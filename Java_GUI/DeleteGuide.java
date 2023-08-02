import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteGuide extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteGuide()
    {
        setTitle("Delete data from table: guide");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] guides = getGuidesCodes();

        JLabel drvAT = new JLabel("Guide AT:");
        panel.add(drvAT);

        dropdownList1 = new JComboBox<>(guides);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String guide;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    guide = parts[0];
                }
                else
                {
                    guide = "";
                }

                String deleteGuideStatus = deleteGuideFunction(guide);
                JOptionPane.showMessageDialog(null, deleteGuideStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a guide AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteGuideFunction(String guiAT)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(guiAT.equals("")) {
                sql = "DELETE FROM guide";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Guide record(s) deleted successfully!";
                    else
                        deleteStatus = "Guide table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM guide WHERE gui_AT=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, guiAT);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Guide record deleted successfully!";


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

    private String[] getGuidesCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker AS w INNER JOIN guide AS g" +
                    " ON w.wrk_AT=g.gui_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currAT = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String currCode = resultSet.getString("w.wrk_br_code");
                String info = currAT + ", Name-Lastname: " + name + "-" + lname + ", Branch: " + currCode;
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
