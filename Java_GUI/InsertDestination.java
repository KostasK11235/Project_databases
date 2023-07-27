import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertDestination extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JTextField field5;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertDestination(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] dstTypes = {"LOCAL", "ABROAD"};

        JLabel dstId = new JLabel("dst_id:");
        panel.add(dstId);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel dstName = new JLabel("dst_name:");
        panel.add(dstName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel dstDescr = new JLabel("dst_descr:");
        panel.add(dstDescr);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel dst_type = new JLabel("dst_type:");
        panel.add(dst_type);

        dropdownList1 = new JComboBox<>(dstTypes);
        panel.add(dropdownList1);

        JLabel dstLanguage = new JLabel("dst_language:");
        panel.add(dstLanguage);

        field4 = new JTextField(15);
        panel.add(field4);

        JLabel dstLocation = new JLabel("dst_location:");
        panel.add(dstLocation);

        field5 = new JTextField(15);
        panel.add(field5);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dstID = field1.getText();
                String dstName = field2.getText();
                String dstDescr = field3.getText();
                String dstType = (String) dropdownList1.getSelectedItem();
                String dstLanguage = field4.getText();
                String dstLocation = field5.getText();

                String insertDestinationStatus = insertDestinationFunction(dstID, dstName, dstDescr, dstType,
                        dstLanguage, dstLocation, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertDestinationStatus);
            }
        });
    }

    private String insertDestinationFunction(String id, String name, String description, String type, String language,
    String location, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM destination WHERE dst_id=? AND dst_name=? AND dst_descr=? AND dst_rtype=? AND" +
                    "dst_language=? AND dst_location=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, type);
            statement.setString(5, language);
            statement.setString(6, location);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(resultSet.first())
                {
                    insertStatus = "Destination with the same data already exists!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO destination VALUES (?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, type);
            statement.setString(5, language);
            statement.setString(6, location);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New destination inserted into destination table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "destination");

                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }
}
