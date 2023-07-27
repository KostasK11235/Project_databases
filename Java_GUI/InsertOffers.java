import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class InsertOffers extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertOffers(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(300, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel offerCode = new JLabel("offer_code:");
        panel.add(offerCode);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel fromDate = new JLabel("from_date:");
        panel.add(fromDate);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        // Add date fields to the panel
        JPanel offersDate1 = createDatePickerPanel1();
        panel.add(offersDate1);

        JLabel toDate = new JLabel("to_date:");
        panel.add(toDate);

        // Add date fields to the panel
        JPanel offersDate2 = createDatePickerPanel2();
        panel.add(offersDate2);

        JLabel cost = new JLabel("cost_per_person");
        panel.add(cost);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel destinationID = new JLabel("destination_id:");
        panel.add(destinationID);

        field3 = new JTextField(15);
        panel.add(field3);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String offerCode = field1.getText();
                String startDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String endDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String cost = field2.getText();
                String destinationID = field3.getText();

                String insertOffersStatus = insertOffersFunction(offerCode, startDate, endDate, cost, destinationID);
                JOptionPane.showMessageDialog(null, insertOffersStatus);
            }
        });
    }

    private String insertOffersFunction(String code, String start, String end, String cost, String destinationId)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id FROM destination WHERE dst_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, destinationId);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "In order to add new offer the destinationID must match an existing destinationID!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO offers VALUES (?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            statement.setString(2, start);
            statement.setString(3, end);
            statement.setString(4, cost);
            statement.setString(5, destinationId);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New offer inserted into offers table!";

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return insertStatus;
    }

    private void createDatePickerComponents() {
        // Get current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Create year combo boxes
        yearComboBox1 = new JComboBox<>();
        yearComboBox2 = new JComboBox<>();
        for (int year = currentYear - 10; year <= currentYear + 10; year++) {
            yearComboBox1.addItem(year);
            yearComboBox2.addItem(year);
        }

        // Create month combo boxes
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthComboBox1 = new JComboBox<>(months);
        monthComboBox2 = new JComboBox<>(months);

        // Create day combo boxes
        dayComboBox1 = new JComboBox<>();
        dayComboBox2 = new JComboBox<>();
        for (int day = 1; day <= 31; day++) {
            dayComboBox1.addItem(day);
            dayComboBox2.addItem(day);
        }
    }

    private JPanel createDatePickerPanel1() {
        JPanel panel = new JPanel();
        panel.add(yearComboBox1);
        panel.add(monthComboBox1);
        panel.add(dayComboBox1);
        return panel;
    }

    private JPanel createDatePickerPanel2() {
        JPanel panel = new JPanel();
        panel.add(yearComboBox2);
        panel.add(monthComboBox2);
        panel.add(dayComboBox2);
        return panel;
    }

    private String getDateAsString(JComboBox<Integer> yearComboBox, JComboBox<String> monthComboBox, JComboBox<Integer> dayComboBox) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1; // Add 1 to adjust for zero-based index
        int day = (int) dayComboBox.getSelectedItem();

        // Format the date as a string in the desired format
        String dateAsString = String.format("%04d-%02d-%02d", year, month, day);

        return dateAsString;
    }
}