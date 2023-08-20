import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertOffers extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertOffers() {
        setTitle("Insert data for table: Offers");
        setSize(330, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] destinations = getDestinations();

        JLabel fromDate = new JLabel("From Date:");
        panel.add(fromDate);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        // Add date fields to the panel
        JPanel offersDate1 = createDatePickerPanel1();
        panel.add(offersDate1);

        JLabel toDate = new JLabel("To Date:");
        panel.add(toDate);

        // Add date fields to the panel
        JPanel offersDate2 = createDatePickerPanel2();
        panel.add(offersDate2);

        JLabel cost = new JLabel("Cost per Person");
        panel.add(cost);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel destinationID = new JLabel("Destination ID:");
        panel.add(destinationID);

        dropdownList1 = new JComboBox<>(destinations);
        panel.add(dropdownList1);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String endDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String cost = field1.getText();
                String destinationID = (String) dropdownList1.getSelectedItem();

                String[] parts = destinationID.split(",");
                destinationID = parts[0];

                String insertOffersStatus = insertOffersFunction(startDate, endDate, cost, destinationID);
                JOptionPane.showMessageDialog(null, insertOffersStatus);
            }
        });
    }

    private String insertOffersFunction(String start, String end, String cost, String destinationId)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "INSERT INTO offers(from_date,to_date,cost_per_person,destination_id) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, start);
            statement.setString(2, end);
            statement.setString(3, cost);
            statement.setString(4, destinationId);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0)
                insertStatus = "New offer inserted into offers table!";

            statement.close();
            connection.close();

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
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

    private String[] getDestinations()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> branches = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = currCode + ", Name:" + name;
                branches.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return branches.toArray(new String[branches.size()]);
    }
}
