import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton helpButton;
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

        helpButton = new JButton("Help");
        panel.add(helpButton);

        loginButton = new JButton("Login");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String loggedAdmin = login(username, password);

                if (loggedAdmin!=null) {
                    // TODO: Open the main application window or perform other actions
                    System.out.println("Admin: "+loggedAdmin);
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    setVisible(false);
                    new MainAppWindow(loggedAdmin).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        The username field must be filled with the ITs last name and the password field with his password.""";
                JOptionPane.showMessageDialog(null, helpMessage);
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

    private String login(String username, String password) {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String loggedUser = null;
        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_name, w.wrk_AT FROM worker AS w INNER JOIN it ON w.wrk_AT=it.IT_AT " +
                    "WHERE it.password=? AND w.wrk_lname=?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, password);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();
            try {
                resultSet.first();
                String loggedName = resultSet.getString("w.wrk_name");
                String id = resultSet.getString("w.wrk_AT");
                loggedUser = id;
                System.out.println("User id: " + id + "Username: " + loggedName);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("login:" + loggedUser);
            return loggedUser; // If a matching IT record is found, login is successful

        } catch (SQLException ex) {
            ex.printStackTrace();
            return loggedUser;
        }
    }
}