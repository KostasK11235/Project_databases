import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateDestination extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<String> dropDownList1;
    private JComboBox<String> dropDownList2;
    private JComboBox<String> dropDownList3;
    private JButton updateButton;
    private JButton helpButton;

    public UpdateDestination(String loggedAdmin)
    {
        setTitle("Update table: Destination");
        setSize(400, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] routes = {"LOCAL", "ABROAD"};

        String[] dstIDs = getDstIDs();
        String[] dstLoc = getDstLocation();

        JLabel dstID = new JLabel("Destination ID:");
        panel.add(dstID);

        dropDownList1 = new JComboBox<>(dstIDs);
        panel.add(dropDownList1);

        JLabel name = new JLabel("Name:");
        panel.add(name);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel descr = new JLabel("Description:");
        panel.add(descr);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel route = new JLabel("Route:");
        panel.add(route);

        dropDownList2 = new JComboBox<>(routes);
        panel.add(dropDownList2);

        JLabel lng = new JLabel("Language:");
        panel.add(lng);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel loc = new JLabel("Location:");
        panel.add(loc);

        dropDownList3 = new JComboBox<>(dstLoc);
        panel.add(dropDownList3);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropDownList1.getSelectedItem();
                String name = field1.getText();
                String description = field2.getText();
                String route = (String) dropDownList2.getSelectedItem();
                String language = field3.getText();
                String location = (String) dropDownList3.getSelectedItem();

                String[] parts = selectedAT.split(",");
                String dstID = parts[0];

                String updateDestinationStatus = updateDestinationFunction(dstID, name, description, route, language,
                        location, loggedAdmin);
                JOptionPane.showMessageDialog(null, updateDestinationStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a destination id to update that destinations data on the table.
                        2. Choose a location if the new destination belongs to an existing destination. If not, leave empty.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateDestinationFunction(String dstID, String name, String description, String route,
                                             String language, String location, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);

            String sql = " ";
            PreparedStatement statement = connection.prepareStatement(sql);

            if(location.equalsIgnoreCase(" "))
            {
                sql = "UPDATE destination SET dst_name=?,dst_descr=?,dst_rtype=?,dst_language=?,dst_location=DEFAULT" +
                        " WHERE dst_id=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, route);
                statement.setString(4, language);
                statement.setString(5, dstID);
            }
            else
            {
                sql = "UPDATE destination SET dst_name=?,dst_descr=?,dst_rtype=?,dst_language=?,dst_location=?" +
                        " WHERE dst_id=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, route);
                statement.setString(4, language);
                statement.setString(5, location);
                statement.setString(6, dstID);
            }

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Destination record updated successfully!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "destination");
                statement.setString(3, "UPDATE");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }
        return UpdateStatus;
    }
    private String[] getDstIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> dstIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = currCode + ", Destination Name: " + name;
                dstIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return dstIDs.toArray(new String[dstIDs.size()]);
    }

    private String[] getDstLocation()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> dstLocs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_name FROM destination WHERE dst_id=dst_name";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            dstLocs.add(" ");
            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = currCode + ", Destination Name: " + name;
                dstLocs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return dstLocs.toArray(new String[dstLocs.size()]);
    }
}
