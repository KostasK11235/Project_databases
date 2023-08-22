import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertTravelTo extends JFrame{
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> hourComboBox1;
    private JComboBox<Integer> minuteComboBox1;
    private JComboBox<Integer> secondComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JComboBox<Integer> hourComboBox2;
    private JComboBox<Integer> minuteComboBox2;
    private JComboBox<Integer> secondComboBox2;
    private JButton insertButton;

    public InsertTravelTo(String loggedAdmin) {
        setTitle("Insert data for table: Travel to");
        setSize(450, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] tripIDs = getTripIDs();
        String[] dstIDs = getDstIDs();

        JLabel toTrId = new JLabel("Trip ID:");
        panel.add(toTrId);

        dropdownList1 = new JComboBox<>(tripIDs);
        panel.add(dropdownList1);

        JLabel toDstId = new JLabel("Destination ID");
        panel.add(toDstId);

        dropdownList2 = new JComboBox<>(dstIDs);
        panel.add(dropdownList2);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel toArrival = new JLabel("Arrival:");
        panel.add(toArrival);

        // Add date fields to the panel
        JPanel arrivalDate = createDatePickerPanel1();
        panel.add(arrivalDate);

        JLabel toDeparture = new JLabel("Departure:");
        panel.add(toDeparture);

        // Add date fields to the panel
        JPanel departureDate = createDatePickerPanel2();
        panel.add(departureDate);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTrip = (String) dropdownList1.getSelectedItem();
                String selectedDst = (String) dropdownList2.getSelectedItem();
                String arrival = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String returnDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);

                String[] parts = selectedDst.split(",");
                String dstID = parts[0];

                parts = selectedTrip.split(",");
                String trID = parts[0];


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

            String sql = "SELECT tr_departure,tr_return FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, trID);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            String tripDeparture = resultSet.getString("tr_departure");
            String tripReturn = resultSet.getString("tr_return");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tripDepDate = LocalDateTime.parse(tripDeparture, formatter);
            LocalDateTime tripRetDate = LocalDateTime.parse(tripReturn, formatter);
            LocalDateTime travelArrival = LocalDateTime.parse(arrivalDate, formatter);
            LocalDateTime travelReturn = LocalDateTime.parse(returnDate, formatter);

            if(!(travelArrival.isAfter(tripDepDate) && travelArrival.isBefore(tripRetDate)) ||
            !(travelReturn.isAfter(travelArrival) && travelReturn.isBefore(tripRetDate)))
            {
                insertStatus = "Arrival Date must be between trips Departure and Return Dates\n" +
                        "and Departure Date must be between Arrival and trips Return Dates";
                return insertStatus;
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

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "travel_to");
                statement.setString(3, "INSERT");

                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getTripIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> tripIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT tr_id,tr_departure,tr_return FROM trip ORDER BY tr_id";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("tr_id");
                String depDate = resultSet.getString("tr_departure");
                String retDate = resultSet.getString("tr_return");
                String info = currCode + ", Departure-Return: " + depDate + "-" + retDate;
                tripIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return tripIDs.toArray(new String[tripIDs.size()]);
    }

    private String[] getDstIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> dstIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination ORDER BY dst_id";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = currCode + ", Destination: " + name;
                dstIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return dstIDs.toArray(new String[dstIDs.size()]);
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

        // Create hour combo boxes
        hourComboBox1 = new JComboBox<>();
        hourComboBox2 = new JComboBox<>();
        for (int hour = 0; hour < 24; hour++) {
            hourComboBox1.addItem(hour);
            hourComboBox2.addItem(hour);
        }

        // Create minute and second combo boxes
        minuteComboBox1 = new JComboBox<>();
        minuteComboBox2 = new JComboBox<>();
        secondComboBox1 = new JComboBox<>();
        secondComboBox2 = new JComboBox<>();
        for (int minute = 0; minute < 60; minute++) {
            minuteComboBox1.addItem(minute);
            minuteComboBox2.addItem(minute);
            secondComboBox1.addItem(minute);
            secondComboBox2.addItem(minute);
        }
    }

    private JPanel createDatePickerPanel1() {
        JPanel panel = new JPanel();
        panel.add(yearComboBox1);
        panel.add(monthComboBox1);
        panel.add(dayComboBox1);
        panel.add(hourComboBox1);
        panel.add(minuteComboBox1);
        panel.add(secondComboBox1);
        return panel;
    }

    private JPanel createDatePickerPanel2() {
        JPanel panel = new JPanel();
        panel.add(yearComboBox2);
        panel.add(monthComboBox2);
        panel.add(dayComboBox2);
        panel.add(hourComboBox2);
        panel.add(minuteComboBox2);
        panel.add(secondComboBox2);
        return panel;
    }

    private String getDateAsString(JComboBox<Integer> yearComboBox, JComboBox<String> monthComboBox, JComboBox<Integer> dayComboBox,
                                   JComboBox<Integer> hourComboBox, JComboBox<Integer> minuteComboBox, JComboBox<Integer> secondComboBox) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1; // Add 1 to adjust for zero-based index
        int day = (int) dayComboBox.getSelectedItem();
        int hour = (int) hourComboBox.getSelectedItem();
        int minute = (int) minuteComboBox.getSelectedItem();
        int second = (int) secondComboBox.getSelectedItem();

        // Format the date and time as a string in the desired format
        String dateTimeAsString = String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);

        return dateTimeAsString;
    }
}
