import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertManages extends JFrame{
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JButton insertButton;

    public InsertManages() {
        setTitle("Insert data for table: Manages");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adminAT = getAdmins();
        String[] branches = getBranches();

        JLabel mngAdmAT = new JLabel("mng_adm_AT:");
        panel.add(mngAdmAT);

        dropdownList1 = new JComboBox<>(adminAT);
        panel.add(dropdownList1);

        JLabel mngBrCode = new JLabel("mng_br_code:");
        panel.add(mngBrCode);

        dropdownList2 = new JComboBox<>(branches);
        panel.add(dropdownList2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mngAT = (String) dropdownList1.getSelectedItem();
                String mngBrCode = (String) dropdownList2.getSelectedItem();

                if(!"".equalsIgnoreCase(mngAT))
                {
                    String[] parts = mngAT.split(",");
                    mngAT = parts[0];
                }

                String insertManagesStatus = insertManagesFunction(mngAT, mngBrCode);
                JOptionPane.showMessageDialog(null, insertManagesStatus);
            }
        });
    }

    private String insertManagesFunction(String at, String branchCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO manages VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, branchCode);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New data inserted into manages table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    // Get the AT of the admins in admin table with 'ADMINISTRATIVE' type, who are not in table manages
    private String[] getAdmins()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> workers = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker w INNER JOIN admin a ON a.adm_AT=w.wrk_AT " +
                    "AND a.adm_type='ADMINISTRATIVE' WHERE a.adm_AT NOT IN (SELECT mng_adm_AT FROM manages)";
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

    private String[] getBranches()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> branches = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT br_code FROM branch";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("br_code");
                branches.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return branches.toArray(new String[branches.size()]);
    }
}
