import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertGuide extends JFrame {
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertGuide() {
        setTitle("Insert data for table: Guide");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] wrkAT = getWorkersAT();

        JLabel guiAT = new JLabel("Guide AT:");
        panel.add(guiAT);

        dropdownList1 = new JComboBox<>(wrkAT);
        panel.add(dropdownList1);

        JLabel guiCV = new JLabel("CV:");
        panel.add(guiCV);

        field1 = new JTextField(15);
        panel.add(field1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String guiCV = field1.getText();

                String[] parts = selectedAT.split(",");
                String guiAT = parts[0];

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
            String sql = "INSERT INTO guide VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, CV);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New guide inserted into guide table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
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

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname;
                workers.add(info);
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
