import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class InsertTravelTo extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertTravelTo(String tableName, String loggedAdmin) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel toTrId = new JLabel("to_tr_id:");
        panel.add(toTrId);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel toDstId = new JLabel("to_dst_id");
        panel.add(toDstId);

        field2 = new JTextField(15);
        panel.add(field2);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel toArrival = new JLabel("to_arrival:");
        panel.add(toArrival);

        // Add date fields to the panel
        JPanel arrivalDate = createDatePickerPanel1();
        panel.add(arrivalDate);

        JLabel toDeparture = new JLabel("to_departure:");
        panel.add(toDeparture);

        // Add date fields to the panel
        JPanel departureDate = createDatePickerPanel2();
        panel.add(departureDate);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String trID = field1.getText();
                String dstID = field2.getText();
                String arrival = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String returnDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);


                String insertTravelToStatus = insertTravelToFunction(trID, dstID, arrival, returnDate, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertTravelToStatus);
            }
        });
    }

    private String insertTravelToFunction(String trID, String dstID, String arrivalDate, String returnDate,
                                          String loggedAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, trID);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Trip with the same to_tr_id must already exists!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "SELECT * FROM destination WHERE dst_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, dstID);

            resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Destination with the same to_dst_id must already exists!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO travel_to VALUES (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, trID);
            statement.setString(2, dstID);
            statement.setString(3, arrivalDate);
            statement.setString(4, returnDate);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New travel information inserted into travel_to table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "travel_to");

                statement.executeUpdate();
            }
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
