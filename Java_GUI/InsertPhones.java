import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertPhones extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertPhones() {
        setTitle("Insert data for table: Phones");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] branches = getBranches();

        JLabel phBrCode = new JLabel("Branch:");
        panel.add(phBrCode);

        dropdownList1 = new JComboBox<>(branches);
        panel.add(dropdownList1);

        JLabel phNumber = new JLabel("Phone Number:");
        panel.add(phNumber);

        field1 = new JTextField(15);
        panel.add(field1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phBrCode = (String) dropdownList1.getSelectedItem();
                String phNumber = field1.getText();

                String insertPhonesStatus = insertPhonesFunction(phBrCode, phNumber);
                JOptionPane.showMessageDialog(null, insertPhonesStatus);
            }
        });
    }

    private String insertPhonesFunction(String brCode, String number)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO phones VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, brCode);
            statement.setString(2, number);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New phone inserted into phones table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
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
