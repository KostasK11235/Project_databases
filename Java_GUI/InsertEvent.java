import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertEvent extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
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

    public InsertEvent(String loggedAdmin) {
        setTitle("Insert data for table: Event");
        setSize(550, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] evIDs = getEventIDs();

        JLabel evTrId = new JLabel("Event Trip ID:");
        panel.add(evTrId);

        dropdownList1 = new JComboBox<>(evIDs);
        panel.add(dropdownList1);

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

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String evTrId = (String) dropdownList1.getSelectedItem();
                String evStart = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1, hourComboBox1, minuteComboBox1, secondComboBox1);
                String evEnd = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2, hourComboBox2, minuteComboBox2, secondComboBox2);
                String evDescr = field1.getText();

                String insertEventStatus = insertEventFunction(evTrId, evStart, evEnd, evDescr, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertEventStatus);
            }
        });
    }

    private String insertEventFunction(String id, String start, String end, String description, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM event WHERE ev_tr_id=? AND ev_start=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, start);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(resultSet.first())
                {
                    insertStatus = "Two events with the same ev_tr_id and ev_start fields cannot exist!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                insertStatus = ex.getMessage();
            }

            sql = "INSERT INTO event VALUES (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, start);
            statement.setString(3, end);
            statement.setString(4, description);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New event inserted into event table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=? AND action=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "event");
                statement.setString(3, "INSERT");

                statement.executeUpdate();

                statement.close();
                connection.close();
            }

        } catch (SQLException ex) {
            insertStatus = ex.getMessage();
        }
        return insertStatus;
    }

    private String[] getEventIDs()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> tripIDs = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT tr_id FROM trip";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("tr_id");
                tripIDs.add(currCode);
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
}
