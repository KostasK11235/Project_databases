import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateTravelTo extends JFrame{
    private JComboBox<String> dropDownList1;
    private JComboBox<String> dropDownList2;
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

    public UpdateTravelTo(String loggedAdmin)
    {
        setTitle("Update table: Travel to");
        setSize(550, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] tripID = getTripIDs();
        String[] dstID = getDestinationIDs();

        JLabel trID = new JLabel("Trip ID:");
        panel.add(trID);

        dropDownList1 = new JComboBox<>(tripID);
        panel.add(dropDownList1);

        JLabel dst = new JLabel("Destination ID:");
        panel.add(dst);

        dropDownList2 = new JComboBox<>(dstID);
        panel.add(dropDownList2);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel arr = new JLabel("Arrival Date(Year/Month/Day/Hour/Minute/Second):");
        panel.add(arr);

        JPanel arrDate = createDatePickerPanel1();
        panel.add(arrDate);

        JLabel ret = new JLabel("Departure Date(Year/Month/Day/Hour/Minute/Second):");
        panel.add(ret);

        JPanel retDate = createDatePickerPanel2();
        panel.add(retDate);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTripID = (String) dropDownList1.getSelectedItem();
                String selectedDstID = (String) dropDownList2.getSelectedItem();
                String arrDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String retDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);

                String[] parts = selectedDstID.split(",");
                String dstID = parts[0];
                parts = selectedTripID.split(",");
                String tripID = parts[0];
                String[] dstParts = parts[1].split(":");
                String[] dstParts2 = dstParts[1].split(",");
                String oldDstID = dstParts2[0];


                String updateTravelToStatus = updateTravelToFunction(tripID, dstID, arrDate, retDate, oldDstID, loggedAdmin);
                JOptionPane.showMessageDialog(null, updateTravelToStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a trip id to update that travel_to trips data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateTravelToFunction(String tripID, String dstID, String arrDate, String retDate, String oldDstID, String loggedAdmin)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "SELECT tr_departure,tr_return FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, tripID);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String tripDep = resultSet.getString("tr_departure");
            String tripRet = resultSet.getString("tr_return");

            System.out.println("trip departure: " + tripDep + ", Trip return: " + tripRet + "\ntravel arrival: "
            + arrDate + ", travel departure: "+ retDate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tripDepDate = LocalDateTime.parse(tripDep, formatter);
            LocalDateTime tripRetDate = LocalDateTime.parse(tripRet, formatter);
            LocalDateTime travelArrDate = LocalDateTime.parse(arrDate, formatter);
            LocalDateTime travelDepDate = LocalDateTime.parse(retDate, formatter);

            System.out.println("First cond: " + (travelArrDate.isAfter(tripDepDate) && travelArrDate.isBefore(tripRetDate)));
            System.out.println("Second cond: " + !(travelDepDate.isAfter(travelArrDate) && travelDepDate.isBefore(tripRetDate)));

            if(travelArrDate.isAfter(tripDepDate) && travelArrDate.isBefore(tripRetDate))
            {
                if(!(travelDepDate.isAfter(travelArrDate) && travelDepDate.isBefore(tripRetDate)))
                {
                    UpdateStatus = "Arrival date must be between tr_departure and tr_return dates and\n" +
                            "Departure date must be between between arrival and tr_return dates";
                    return UpdateStatus;
                }
            }
            else
            {
                UpdateStatus = "Arrival date must be between tr_departure and tr_return dates and\n" +
                        "Departure date must be between between arrival and tr_return dates";
                return UpdateStatus;
            }

            sql = "UPDATE travel_to SET to_dst_id=?,to_arrival=?,to_departure=? WHERE to_tr_id=? AND to_dst_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, dstID);
            statement.setString(2, arrDate);
            statement.setString(3, retDate);
            statement.setString(4, tripID);
            statement.setString(5, oldDstID);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Travel to record updated successfully!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "travel_to");
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
            String sql = "SELECT to_tr_id,to_dst_id,to_arrival,to_departure FROM travel_to";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("to_tr_id");
                String dst = resultSet.getString("to_dst_id");
                String arr = resultSet.getString("to_arrival");
                String dep = resultSet.getString("to_departure");
                String info = currCode + ", Destination ID: " + dst + ", Arrival:" + arr + ", Departure:" + dep;
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

    private String[] getDestinationIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> dstIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT dst_id,dst_name FROM destination";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String dstID = resultSet.getString("dst_id");
                String name = resultSet.getString("dst_name");
                String info = dstID + ", Name: " + name;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new UpdateTravelTo("AT051").setVisible(true); }
        });
    }
}
