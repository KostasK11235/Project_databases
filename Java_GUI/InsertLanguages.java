import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertLanguages extends JFrame {
    private JTextField field1;
    private JTextField field2;
    private JButton insertButton;

    public InsertLanguages(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel lngGuiAT = new JLabel("lng_gui_AT:");
        panel.add(lngGuiAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel lngs = new JLabel("lng_language");
        panel.add(lngs);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lngGuiAT = field1.getText();
                String languages = field2.getText();

                String insertLanguagesStatus = insertLanguagesFunction(lngGuiAT, languages);
                JOptionPane.showMessageDialog(null, insertLanguagesStatus);
            }
        });
    }

    private String insertLanguagesFunction(String at, String languages)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_AT FROM worker WHERE wrk_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Guide with the inserted AT does not exist!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO languages VALUES (?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, languages);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New data inserted into languages table!";

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }
}
