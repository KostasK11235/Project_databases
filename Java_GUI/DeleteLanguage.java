import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteLanguage extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteLanguage()
    {
        setTitle("Delete data from table: languages");
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
                String lng;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    guide = parts[0];

                    String[] lngParts = parts[1].split(": ");
                    lng = lngParts[1];
                }
                else
                {
                    guide = "";
                    lng = "";
                }

                String deleteLanguageStatus = deleteLanguageFunction(guide, lng);
                JOptionPane.showMessageDialog(null, deleteLanguageStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a language guide AT to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteLanguageFunction(String guiAT, String lng)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "";

            if(guiAT.equals("") && lng.equals("")) {
                sql = "DELETE FROM languages";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if(choice == 0)
                {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Language record(s) deleted successfully!";
                    else
                        deleteStatus = "Language table has no records to delete!";
                }
                else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            }
            else
            {
                sql = "DELETE FROM languages WHERE lng_gui_AT=? AND lng_language=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, guiAT);
                statement.setString(2, lng);

                int rowsAffected = statement.executeUpdate();

                if(rowsAffected > 0)
                    deleteStatus = "Language record(s) deleted successfully!";

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
            String sql = "SELECT lng_gui_AT,lng_language FROM languages";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("lng_gui_AT");
                String currLng = resultSet.getString("lng_language");
                String data = currCode + ", Speaks: " + currLng;
                guideCodes.add(data);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return guideCodes.toArray(new String[guideCodes.size()]);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new DeleteLanguage().setVisible(true);}
        });
    }
}
