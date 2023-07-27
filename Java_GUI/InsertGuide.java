import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertGuide extends JFrame {
    private JTextField field1;
    private JTextField field2;
    private JButton insertButton;

    public InsertGuide(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel guiAT = new JLabel("gui_AT:");
        panel.add(guiAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel guiCV = new JLabel("gui_cv:");
        panel.add(guiCV);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guiAT = field1.getText();
                String guiCV = field2.getText();

                String insertGuideStatus = insertGuideFunction(guiAT, guiCV);
                JOptionPane.showMessageDialog(null, insertGuideStatus);
            }
        });
    }

    private String insertGuideFunction(String at, String CV)
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
                    insertStatus = "In order to add new guide, his data must exist on worker table!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO guide VALUES (?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, CV);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New guide inserted into guide table!";

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }

}