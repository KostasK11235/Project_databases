import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class InsertEvent extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertEvent(String loggedAdmin) {
        setTitle("Insert data for table: event");
        setSize(350, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel evTrId = new JLabel("ev_tr_id:");
        panel.add(evTrId);

        field1 = new JTextField(15);
        panel.add(field1);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel evStart = new JLabel("ev_start:");
        panel.add(evStart);

        JPanel startDate = createDatePickerPanel1();
        panel.add(startDate);

        JLabel evEnd = new JLabel("ev_end:");
        panel.add(evEnd);

        JPanel endDate = createDatePickerPanel2();
        panel.add(endDate);

        JLabel evDescr = new JLabel("ev_descr:");
        panel.add(evDescr);

        field2 = new JTextField(15);
        panel.add(field2);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String evTrId = field1.getText();
                String evStart = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String evEnd = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String evDescr = field2.getText();

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
            String sql = "SELECT * FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "In order to add a new event, the ev_tr_id must match an existing tr_id!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO event VALUES (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, start);
            statement.setString(3, end);
            statement.setString(4, description);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New destination inserted into destination table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "event");

                statement.executeUpdate();

                statement.close();
                connection.close();
            }

        } catch (SQLException ex) {
            //ex.printStackTrace();
            insertStatus = "Event with the same ev_tr_id and ev_start already exists!";
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
