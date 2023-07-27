import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertBranch extends JFrame {
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertBranch(String tableName) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel brCode = new JLabel("br_code:");
        panel.add(brCode);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel brStreet = new JLabel("br_street:");
        panel.add(brStreet);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel brNum = new JLabel("br_num:");
        panel.add(brNum);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel brCity = new JLabel("br_city:");
        panel.add(brCity);

        field4 = new JTextField(15);
        panel.add(field4);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String brCode = field1.getText();
                String brStreet = field2.getText();
                String brNum = field3.getText();
                String brCity = field4.getText();

                String insertBranchStatus = insertBranchFunction(brCode, brStreet, brNum, brCity);
                JOptionPane.showMessageDialog(null, insertBranchStatus);
            }
        });

    }
    private String insertBranchFunction(String code, String street, String number, String city)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM branch WHERE br_code=? AND br_street=? AND br_number=? AND br_city=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            statement.setString(2, street);
            statement.setString(3, number);
            statement.setString(4, city);

            ResultSet resultSet = statement.executeQuery();

            try {
                if (resultSet.first()) {
                    insertStatus = "Branch with the same information already exists!";
                    return insertStatus;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            sql = "INSERT INTO branch VALUES (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            statement.setString(2, street);
            statement.setString(3, number);
            statement.setString(4, city);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                insertStatus = "New branch inserted into branch table!";

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }

}

