import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertManages extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JButton insertButton;

    public InsertManages() {
        setTitle("Insert data for table: manages");
        setSize(350, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel mngAdmAT = new JLabel("mng_adm_AT:");
        panel.add(mngAdmAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel mngBrCode = new JLabel("mng_br_code:");
        panel.add(mngBrCode);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mngAT = field1.getText();
                String mngBrCode = field2.getText();

                String insertManagesStatus = insertManagesFunction(mngAT, mngBrCode);
                JOptionPane.showMessageDialog(null, insertManagesStatus);
            }
        });
    }

    private String insertManagesFunction(String at, String branchCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT adm_AT FROM admin WHERE adm_AT=? AND adm_type=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, "ADMINISTRATIVE");

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Worker must be in admin personnel with 'ADMINISTRATIVE' adm_type!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO manages VALUES (?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, branchCode);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New data inserted into manages table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            //ex.printStackTrace();
            insertStatus = "Manager with the same mng_adm_AT and mng_br_code already exists!";
        }
        return insertStatus;
    }
}
