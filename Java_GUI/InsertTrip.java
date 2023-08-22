import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertTrip extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JComboBox<String> dropdownList3;
    private JComboBox<String> dropdownList4;
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

    public InsertTrip(String loggedAdmin) {
        setTitle("Insert data for table: Trip");
        setSize(350, 405);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] branchIDs = getBranchIDs();
        String[] guides = getGuides();
        String[] drivers = getDrivers();
        String[] seats = new String[50];
        for (int i = 0; i < 50; i++)
        {
            seats[i] = String.valueOf(i + 1);
        }

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel trDeparture = new JLabel("Departure:");
        panel.add(trDeparture);

        // Add date fields to the panel
        JPanel departureTr = createDatePickerPanel1();
        panel.add(departureTr);

        JLabel trReturn = new JLabel("Return:");
        panel.add(trReturn);

        // Add date fields to the panel
        JPanel returnTr = createDatePickerPanel2();
        panel.add(returnTr);

        JLabel trMaxseats = new JLabel("Max Seats:");
        panel.add(trMaxseats);

        dropdownList1 = new JComboBox<>(seats);
        panel.add(dropdownList1);

        JLabel trCost = new JLabel("Cost:");
        panel.add(trCost);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel trBrCode = new JLabel("Branch Code:");
        panel.add(trBrCode);

        dropdownList2 = new JComboBox<>(branchIDs);
        panel.add(dropdownList2);

        JLabel trGuiAt = new JLabel("Guide:");
        panel.add(trGuiAt);

        dropdownList3 = new JComboBox<>(guides);
        panel.add(dropdownList3);

        JLabel trDrvAt = new JLabel("Driver:");
        panel.add(trDrvAt);

        dropdownList4 = new JComboBox<>(drivers);
        panel.add(dropdownList4);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String departure = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String returnDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);
                String maxSeats = (String) dropdownList1.getSelectedItem();
                String cost = field1.getText();
                String trBrCode = (String) dropdownList2.getSelectedItem();
                String selectedGuide = (String) dropdownList3.getSelectedItem();
                String selectedDriver = (String) dropdownList4.getSelectedItem();

                String[] parts = selectedGuide.split(",");
                String trGuiAT = parts[0];
                parts = selectedDriver.split(",");
                String trDrvAT = parts[0];

                String insertTripStatus = insertTripFunction(departure, returnDate, maxSeats,
                        cost, trBrCode, trGuiAT, trDrvAT, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertTripStatus);
            }
        });
    }

    private String insertTripFunction(String trDeparture, String trReturn, String maxSeats, String trCost,
                                             String trBrCode, String GuiAT, String DrvAT, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tripDepDate = LocalDateTime.parse(trDeparture, formatter);
            LocalDateTime tripRetDate = LocalDateTime.parse(trReturn, formatter);

            if(tripRetDate.isBefore(tripDepDate))
            {
                insertStatus = "Trips Return Date must be chronologically after Departure Date!";
                return insertStatus;
            }

            String sql = "SELECT w.wrk_AT,w.wrk_br_code FROM worker AS w INNER JOIN driver AS d ON\n" +
                    "w.wrk_AT=d.drv_AT WHERE d.drv_AT=? AND w.wrk_br_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, DrvAT);
            statement.setString(2, trBrCode);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "The selected driver does not work on the selected branch!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "SELECT w.wrk_AT,w.wrk_br_code FROM worker AS w INNER JOIN guide AS g ON\n" +
                    "w.wrk_AT=g.gui_AT WHERE g.gui_AT=? AND w.wrk_br_code=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, GuiAT);
            statement.setString(2, trBrCode);

            resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "The selected guide does not work on the selected branch!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO trip(tr_departure,tr_return,tr_maxseats,tr_cost,tr_br_code,tr_gui_AT,tr_drv_AT) VALUES (?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, trDeparture);
            statement.setString(2, trReturn);
            statement.setString(3, maxSeats);
            statement.setString(4, trCost);
            statement.setString(5, trBrCode);
            statement.setString(6, GuiAT);
            statement.setString(7, DrvAT);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New trip inserted into trip table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "trip");
                statement.setString(3, "INSERT");

                statement.executeUpdate();
            }
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

    private String[] getGuides()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker w INNER JOIN guide g ON w.wrk_AT=g.gui_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String branch = resultSet.getString("w.wrk_br_code");
                String info = currCode + ", Name-LastName: " + name + "-" + lname +", Branch: " + branch;
                guideIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return guideIDs.toArray(new String[guideIDs.size()]);
    }

    private String[] getDrivers()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> drvIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker w INNER JOIN driver d ON w.wrk_AT=d.drv_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String branch = resultSet.getString("w.wrk_br_code");
                String info = currCode + ", Name-LastName: " + name + "-" + lname +", Branch: " + branch;
                drvIDs.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return drvIDs.toArray(new String[drvIDs.size()]);
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
