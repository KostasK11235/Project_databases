import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateEvent extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropDownList1;
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

    public UpdateEvent(String loggedAdmin)
    {
        setTitle("Update table: Event");
        setSize(550, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] eventID = getEventIDs();

        JLabel evID = new JLabel("Event trip ID:");
        panel.add(evID);

        dropDownList1 = new JComboBox<>(eventID);
        panel.add(dropDownList1);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel evStart = new JLabel("Event start(Year/Month/Day/Hour/Minute/Second):");
        panel.add(evStart);

        JPanel startDate = createDatePickerPanel1();
        panel.add(startDate);

        JLabel evEnd = new JLabel("Event end(Year/Month/Day/Hour/Minute/Second):");
        panel.add(evEnd);

        JPanel endDate = createDatePickerPanel2();
        panel.add(endDate);

        JLabel descr = new JLabel("Event Description:");
        panel.add(descr);

        field1 = new JTextField(15);
        panel.add(field1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEvID = (String) dropDownList1.getSelectedItem();
                String startDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String endDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);
                String descr = field1.getText();

                String[] parts = selectedEvID.split(",");
                String evID = parts[0];
                String[] datePart = parts[1].split(":");
                String oldStartDate = datePart[1];

                String updateEventStatus = updateEventFunction(evID, startDate, endDate, descr, oldStartDate, loggedAdmin);
                JOptionPane.showMessageDialog(null, updateEventStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an event trip id to update that travel_to trips data on the table.
                        
                        NOTES:
                        The starting date of two events, for the same trip, cannot be the same.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateEventFunction(String evID, String evStart, String evEnd, String descr, String oldStartDate, String loggedAdmin)
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
            statement.setString(1, evID);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String tripDep = resultSet.getString("tr_departure");
            String tripRet = resultSet.getString("tr_return");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tripDepDate = LocalDateTime.parse(tripDep, formatter);
            LocalDateTime tripRetDate = LocalDateTime.parse(tripRet, formatter);
            LocalDateTime evStartDate = LocalDateTime.parse(evStart, formatter);
            LocalDateTime evEndDate = LocalDateTime.parse(evEnd, formatter);

            if(evEndDate.isAfter(evStartDate))
            {
                System.out.println("First cond: " + (evStartDate.isAfter(tripDepDate) && evStartDate.isBefore(tripRetDate)));
                System.out.println("Second cond: " + (evEndDate.isAfter(tripDepDate) && evEndDate.isBefore(tripRetDate)));
                if(!(evStartDate.isAfter(tripDepDate) && evStartDate.isBefore(tripRetDate)) ||
                !(evEndDate.isAfter(tripDepDate) && evEndDate.isBefore(tripRetDate)))
                {
                    UpdateStatus = "Event must Start and End between the Trips Departure and Return date!";
                    return UpdateStatus;
                }
            }
            else
            {
                UpdateStatus = "Event Start Date must be chronologically before Event End Date";
                return UpdateStatus;
            }

            sql = "UPDATE event SET ev_start=?,ev_end=?,ev_descr=? WHERE ev_tr_id=? AND ev_start=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, evStart);
            statement.setString(2, evEnd);
            statement.setString(3, descr);
            statement.setString(4, evID);
            statement.setString(5, oldStartDate);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
            {
                UpdateStatus = "Event to record updated successfully!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loggedAdmin);
                statement.setString(2, "event");
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
    private String[] getEventIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> tripIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT ev_tr_id, ev_start FROM event";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("ev_tr_id");
                String start = resultSet.getString("ev_start");
                String info = currCode + ", Start Date: " + start;
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
            public void run() { new UpdateEvent("AT051").setVisible(true); }
        });
    }
}

