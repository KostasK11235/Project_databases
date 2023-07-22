import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JRadioButton showPasswordRadioButton;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField(15);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField(15);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (login(username, password)) {
                    // TODO: Open the main application window or perform other actions
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    setVisible(false);
                    new MainAppWindow().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });

        // Radio button for password
        showPasswordRadioButton = new JRadioButton("Show Password");
        showPasswordRadioButton.setBounds(140, 130, 150, 25);
        showPasswordRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordRadioButton.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });
        panel.add(showPasswordRadioButton);
    }

    private boolean login(String username, String password) {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM IT WHERE IT_AT = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            try {
                while (resultSet.next()) {
                    String id = resultSet.getString("IT_AT");
                    String passwd = resultSet.getString("password");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");

                    System.out.println("IT_id: " + id + ", password: " + passwd + ", start_date: " + startDate + ", end_date" + endDate);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return resultSet.first(); // If a matching IT record is found, login is successful

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}