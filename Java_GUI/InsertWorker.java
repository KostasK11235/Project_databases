import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertWorker extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertWorker() {
        setTitle("Insert data for table: Worker");
        setSize(300, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] branchIDs = getBranchIDs();

        JLabel wrkAT = new JLabel("Worker AT:");
        panel.add(wrkAT);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel wrkName = new JLabel("Name:");
        panel.add(wrkName);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel wrkLName = new JLabel("Last Name:");
        panel.add(wrkLName);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel wrkSalary = new JLabel("Salary:");
        panel.add(wrkSalary);

        field4 = new JTextField(15);
        panel.add(field4);

        JLabel wrkBrCode = new JLabel("Branch:");
        panel.add(wrkBrCode);

        dropdownList1 = new JComboBox<>(branchIDs);
        panel.add(dropdownList1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String wrkAT = field1.getText();
                String wrkName = field2.getText();
                String wrkLName = field3.getText();
                String salary = field4.getText();
                String wrkBrCode = (String) dropdownList1.getSelectedItem();

                String insertWorkerStatus = insertWorkerFunction(wrkAT, wrkName, wrkLName, salary, wrkBrCode);
                JOptionPane.showMessageDialog(null, insertWorkerStatus);
            }
        });
    }

    private String insertWorkerFunction(String wrkAT, String name, String lastName, String salary, String wrkBrCode)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO worker VALUES (?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, wrkAT);
            statement.setString(2, name);
            statement.setString(3, lastName);
            statement.setString(4, salary);
            statement.setString(5, wrkBrCode);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New worker inserted into worker table!";

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getBranchIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> brCodes = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT br_code FROM branch";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("br_code");
                brCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return brCodes.toArray(new String[brCodes.size()]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new InsertWorker().setVisible(true); }
        });
    }
}
