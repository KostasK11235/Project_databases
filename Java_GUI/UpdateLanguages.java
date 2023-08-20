import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateLanguages extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateLanguages()
    {
        setTitle("Update table: Languages");
        setSize(400, 170);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] guidesAT = getGuideAT();

        JLabel admAT = new JLabel("Guide AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(guidesAT);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("Language:");
        panel.add(admType);

        field1 = new JTextField(15);
        panel.add(field1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String language = field1.getText();

                String[] parts = selectedAT.split(",");
                String guideAT = parts[0];
                String secondPart = parts[1];
                String[] languagePart = secondPart.split(":");
                String speaks = languagePart[1];

                String updateLanguagesStatus = updateLanguagesFunction(guideAT, speaks, language);
                JOptionPane.showMessageDialog(null, updateLanguagesStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose a guide AT to update that guides language data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateLanguagesFunction(String at, String speaks, String language)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "UPDATE languages SET lng_language=? WHERE lng_gui_AT=? AND lng_language=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, language);
            statement.setString(2, at);
            statement.setString(3, speaks);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Languages record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getGuideAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guides = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT lng_gui_AT,lng_language FROM languages";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currAT = resultSet.getString("lng_gui_AT");
                String language = resultSet.getString("lng_language");
                String info = currAT + ", Speaks:" + language;
                guides.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return guides.toArray(new String[guides.size()]);
    }
}