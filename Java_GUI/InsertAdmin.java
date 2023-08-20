import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertAdmin extends JFrame
{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton insertButton;

    public InsertAdmin()
    {
        setTitle("Insert data for table: Admin");
        setSize(350, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adm_types = {"LOGISTICS", "ADMINISTRATIVE", "ACCOUNTING"};
        String[] wrkAT = getWorkersAT();

        JLabel adminAT = new JLabel("Admin AT:");
        panel.add(adminAT);

        dropdownList2 = new JComboBox<>(wrkAT);
        panel.add(dropdownList2);

        JLabel admType = new JLabel("Admin Type:");
        panel.add(admType);

        dropdownList1 = new JComboBox<>(adm_types);
        panel.add(dropdownList1);

        JLabel admDiploma = new JLabel("Admin Diploma:");
        panel.add(admDiploma);

        field1 = new JTextField(15);
        panel.add(field1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList2.getSelectedItem();
                String admType = (String) dropdownList1.getSelectedItem();
                String admDiploma = field1.getText();

                if(!"".equalsIgnoreCase(selectedAT))
                {
                    String[] atParts = selectedAT.split(",");
                    selectedAT = atParts[0];
                }

                String insertAdminStatus = insertAdminFunction(selectedAT, admType, admDiploma);
                JOptionPane.showMessageDialog(null, insertAdminStatus);
            }
        });
    }

    private String insertAdminFunction(String at, String type, String diploma)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO admin VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, type);
            statement.setString(3, diploma);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New admin inserted into admin table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    // we select all the wrk_AT which are not in tables guide,driver,admin or it
    private String[] getWorkersAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> workers = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker w WHERE w.wrk_AT NOT IN" +
                    " (SELECT gui_AT FROM guide UNION SELECT drv_AT FROM driver UNION SELECT adm_AT FROM admin" +
                    " UNION SELECT it_AT FROM it)";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            boolean hasRows = false;

            while(resultSet.next())
            {
                hasRows = true;
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname;
                workers.add(info);
            }

            if (!hasRows) {
                workers.add("NULL"); // Add "NULL" if the result set has no rows
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
