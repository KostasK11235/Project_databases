import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertBranch extends JFrame {
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JButton insertButton;

    public InsertBranch() {
        setTitle("Insert data for table: Branch");
        setSize(350, 220);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel brStreet = new JLabel("Branch Street:");
        panel.add(brStreet);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel brNum = new JLabel("Branch Number:");
        panel.add(brNum);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel brCity = new JLabel("Branch City:");
        panel.add(brCity);

        field3 = new JTextField(15);
        panel.add(field3);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String brStreet = field1.getText();
                String brNum = field2.getText();
                String brCity = field3.getText();

                String insertBranchStatus = insertBranchFunction(brStreet, brNum, brCity);
                JOptionPane.showMessageDialog(null, insertBranchStatus);
            }
        });

    }
    private String insertBranchFunction(String street, String number, String city)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO branch(br_street,br_num,br_city) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, street);
            statement.setString(2, number);
            statement.setString(3, city);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                insertStatus = "New branch inserted into branch table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }
}

