import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertLanguages extends JFrame {
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertLanguages() {
        setTitle("Insert data for table: Languages");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] guidesAT = getGuidesAT();

        JLabel lngGuiAT = new JLabel("Guide AT:");
        panel.add(lngGuiAT);

        dropdownList1 = new JComboBox<>(guidesAT);
        panel.add(dropdownList1);

        JLabel lngs = new JLabel("Language");
        panel.add(lngs);

        field1 = new JTextField(15);
        panel.add(field1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String language = field1.getText();

                String[] parts = selectedAT.split(",");
                String guiAT = parts[0];

                String insertLanguagesStatus = insertLanguagesFunction(guiAT, language);
                JOptionPane.showMessageDialog(null, insertLanguagesStatus);
            }
        });
    }

    private String insertLanguagesFunction(String at, String languages)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO languages VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, languages);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New data inserted into languages table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getGuidesAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> workers = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker w INNER JOIN guide g ON w.wrk_AT=g.gui_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname;
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
}
