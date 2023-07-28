import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertAdmin extends JFrame
{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertAdmin()
    {
        setTitle("Insert data for table: admin");
        setSize(350, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] adm_types = {"LOGISTICS", "ADMINISTRATIVE", "ACCOUNTING"};

        JLabel admAT = new JLabel("adm_AT:");
        panel.add(admAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel admType = new JLabel("adm_type:");
        panel.add(admType);

        dropdownList1 = new JComboBox<>(adm_types);
        panel.add(dropdownList1);

        JLabel admDiploma = new JLabel("adm_diploma:");
        panel.add(admDiploma);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String admAT = field1.getText();
                String admType = (String) dropdownList1.getSelectedItem();
                String admDiploma = field2.getText();

                String insertAdminStatus = insertAdminFunction(admAT, admType, admDiploma);
                JOptionPane.showMessageDialog(null, insertAdminStatus);
            }
        });
    }

    private String insertAdminFunction(String at, String type, String diploma)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT FROM worker AS w WHERE w.wrk_AT=? AND w.wrk_AT NOT IN\n" +
                    "(SELECT adm_AT FROM admin UNION SELECT drv_AT FROM driver UNION SELECT gui_AT FROM guide);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "In order to add new admin, his data must exist on worker table" +
                            " and not in admin, driver or guide tables!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO admin VALUES (?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, type);
            statement.setString(3, diploma);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New admin inserted into admin table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = "There is already an admin with the same adm_AT!";
        }
        return insertStatus;
    }
}
