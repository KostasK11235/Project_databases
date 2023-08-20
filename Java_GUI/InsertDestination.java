import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertDestination extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton insertButton;

    public InsertDestination(String loggedAdmin) {
        setTitle("Insert data for table: Destination");
        setSize(360, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] dstTypes = {"LOCAL", "ABROAD"};
        String[] dstLocations = getLocations();

        JLabel dstName = new JLabel("Destination Name:");
        panel.add(dstName);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel dstDescr = new JLabel("Description:");
        panel.add(dstDescr);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel dst_type = new JLabel("Type:");
        panel.add(dst_type);

        dropdownList1 = new JComboBox<>(dstTypes);
        panel.add(dropdownList1);

        JLabel dstLanguage = new JLabel("Language:");
        panel.add(dstLanguage);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel dstLocation = new JLabel("Location:");
        panel.add(dstLocation);

        dropdownList2 = new JComboBox<>(dstLocations);
        panel.add(dropdownList2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dstName = field1.getText();
                String dstDescr = field2.getText();
                String dstType = (String) dropdownList1.getSelectedItem();
                String dstLanguage = field3.getText();
                String selectedLoc = (String) dropdownList2.getSelectedItem();

                if(!("NULL".equalsIgnoreCase(selectedLoc)))
                {
                    String[] parts = selectedLoc.split(",");
                    selectedLoc = parts[0];

                }

                System.out.println("selected Location="+ selectedLoc);
                String insertDestinationStatus = insertDestinationFunction(dstName, dstDescr, dstType,
                        dstLanguage, selectedLoc, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertDestinationStatus);
            }
        });
    }

    private String insertDestinationFunction(String name, String description, String type, String language,
    String location, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "";
            PreparedStatement statement;

            if("NULL".equalsIgnoreCase(location))
            {
                sql = "INSERT INTO destination(dst_name,dst_descr,dst_rtype,dst_language) VALUES (?,?,?,?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, type);
                statement.setString(4, language);
            }
            else
            {
                sql = "INSERT INTO destination(dst_name,dst_descr,dst_rtype,dst_language,dst_location) VALUES (?,?,?,?,?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, type);
                statement.setString(4, language);
                statement.setString(5, location);
            }


            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New destination inserted into destination table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "destination");
                statement.setString(3, "INSERT");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getLocations()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> locations = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            locations.add("NULL");
            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = currCode + ", Name:" + name;
                locations.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return locations.toArray(new String[locations.size()]);
    }
}
