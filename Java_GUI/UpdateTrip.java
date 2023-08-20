import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateTrip extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<String> dropDownList1;
    private JComboBox<String> dropDownList2;
    private JComboBox<String> dropDownList3;
    private JComboBox<String> dropDownList4;
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
    private JButton updateButton;
    private JButton helpButton;

    public UpdateTrip(String loggedAdmin)
    {
        setTitle("Update table: Trip");
        setSize(550, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] tripID = getTripIDs();
        String[] brCodes = getBranchCodes();
        String[] guideIDs = getGuideIDs();
        String[] drvIDs = getDriverIDs();

        JLabel trID = new JLabel("Trip ID:");
        panel.add(trID);

        dropDownList1 = new JComboBox<>(tripID);
        panel.add(dropDownList1);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel dep = new JLabel("Departure (Year/Month/Day/Hour/Minute/Second):");
        panel.add(dep);

        JPanel trDep = createDatePickerPanel1();
        panel.add(trDep);

        JLabel ret = new JLabel("Return (Year/Month/Day/Hour/Minute/Second):");
        panel.add(ret);

        JPanel trRet = createDatePickerPanel2();
        panel.add(trRet);

        JLabel seats = new JLabel("No. Seats:");
        panel.add(seats);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel cost = new JLabel("Cost:");
        panel.add(cost);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel brCode = new JLabel("Branch code:");
        panel.add(brCode);

        dropDownList2 = new JComboBox<>(brCodes);
        panel.add(dropDownList2);

        JLabel guide = new JLabel("Guide:");
        panel.add(guide);

        dropDownList3 = new JComboBox<>(guideIDs);
        panel.add(dropDownList3);

        JLabel driver = new JLabel("Driver:");
        panel.add(driver);

        dropDownList4 = new JComboBox<>(drvIDs);
        panel.add(dropDownList4);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTrID = (String) dropDownList1.getSelectedItem();
                String departure = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String returnDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);
                String seat = field1.getText();
                String cost = field2.getText();
                String branch = (String) dropDownList2.getSelectedItem();
                String selectedGuide = (String) dropDownList3.getSelectedItem();
                String selectedDriver = (String) dropDownList4.getSelectedItem();

                String[] parts = selectedTrID.split(",");
                String tripID = parts[0];
                String[] guideParts = selectedGuide.split(",");
                String guide = guideParts[0];
                String[] driverParts = selectedDriver.split(",");
                String driver = driverParts[0];

                String updateTripStatus = updateTripFunction(tripID, departure, returnDate, seat, cost, branch, guide, driver, loggedAdmin);
                JOptionPane.showMessageDialog(null, updateTripStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Update options:
                        1. Choose an event trip id to update that trips data on the table.
                        
                        NOTES:
                        Different trips cannot have the same departure and return dates!
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateTripFunction(String tripID, String departure, String returnDate, String seat, String cost,
                                      String branch, String guide, String driver, String loggedAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tripDepDate = LocalDateTime.parse(departure, formatter);
            LocalDateTime tripRetDate = LocalDateTime.parse(returnDate, formatter);

            if(tripRetDate.isBefore(tripDepDate))
            {
                UpdateStatus = "Trip Departure Date must be chronologically before Return Date!";
                return UpdateStatus;
            }

            String sql = "UPDATE trip SET tr_departure=?,tr_return=?,tr_maxseats=?,tr_cost=?,tr_br_code=?,tr_gui_AT=?" +
                    ",tr_drv_AT=? WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, departure);
            statement.setString(2, returnDate);
            statement.setString(3, seat);
            statement.setString(4, cost);
            statement.setString(5, branch);
            statement.setString(6, guide);
            statement.setString(7, driver);
            statement.setString(8, tripID);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Trip record updated successfully!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "trip");
                statement.setString(3, "UPDATE");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }
        return UpdateStatus;
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

    private String[] getBranchCodes()
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

    private String[] getGuideIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker w INNER JOIN guide g ON w.wrk_AT=g.gui_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-LastName: " + name + "-" + lname;
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

    private String[] getDriverIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> drvIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname FROM worker w INNER JOIN driver d ON w.wrk_AT=d.drv_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String info = currCode + ", Name-LastName: " + name + "-" + lname;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new UpdateTrip("AT051").setVisible(true); }
        });
    }
}

