import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertNewITWindow extends JFrame
{
    private JTextField atField;
    private JTextField nameField;
    private JTextField lnameField;
    private JTextField salaryField;
    // private JTextField branchCodeField;
    private JButton createITButton;


    public InsertNewITWindow(String loggedUser)
    {
        setTitle("Insert new IT");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel atLabel = new JLabel("IT AT:");
        panel.add(atLabel);

        atField = new JTextField(10);
        panel.add(atField);

        JLabel nameLabel = new JLabel("IT Name:");
        panel.add(nameLabel);

        nameField = new JTextField(20);
        panel.add(nameField);

        JLabel lnameLabel = new JLabel("IT Lastname:");
        panel.add(lnameLabel);

        lnameField = new JTextField(20);
        panel.add(lnameField);

        JLabel salaryLabel = new JLabel("IT Salary:");
        panel.add(salaryLabel);

        salaryField = new JTextField(10);
        panel.add(salaryField);

        createITButton = new JButton("Create");
        panel.add(createITButton);

        createITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String it_at = atField.getText();
                String it_name = nameField.getText();
                String it_lname = lnameField.getText();
                String it_salary = salaryField.getText();

                String createITStatus = createIT(it_at,it_name,it_lname,it_salary, loggedUser);
                JOptionPane.showMessageDialog(null, createITStatus);
            }
        });
    }

    private String createIT(String at, String name, String lname, String salary, String loggedAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";
        int adminsBranch = 0;

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT wrk_br_code FROM worker WHERE wrk_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(resultSet.first())
                {
                    insertStatus = "Worker with the same AT already exists!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "SELECT wrk_br_code FROM worker WHERE wrk_AT=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, loggedAdmin);

            resultSet = statement.executeQuery();
            try
            {
                resultSet.next();
                adminsBranch = resultSet.getInt("wrk_br_code");
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO worker VALUES (?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);
            statement.setString(2, name);
            statement.setString(3, lname);
            statement.setFloat(4, Float.parseFloat(salary));
            statement.setInt(5, adminsBranch);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "ITs data inserted into worker table";

            sql = "INSERT INTO it(IT_AT,start_date) VALUES (?,CURDATE())";
            statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            int rowsAffected2 = statement.executeUpdate();

            if(rowsAffected2>0)
                insertStatus += " and IT table!";


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }
}