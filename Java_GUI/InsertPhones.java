import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertPhones extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JButton insertButton;

    public InsertPhones() {
        setTitle("Insert data for table: offers");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel phBrCode = new JLabel("ph_br_code:");
        panel.add(phBrCode);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel phNumber = new JLabel("ph_number:");
        panel.add(phNumber);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phBrCode = field1.getText();
                String phNumber = field2.getText();

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
            String sql = "SELECT br_code FROM branch WHERE br_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, brCode);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "In order to add a new phone, ph_br_code must match an existing br_code!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO phones VALUES (?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, brCode);
            statement.setString(2, number);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New phone(s) inserted into phones table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = "Inserted phone number already exists for the selected branch!";
        }
        return insertStatus;
    }
}
