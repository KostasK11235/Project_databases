import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class InsertTrip extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JTextField field5;
    private JTextField field6;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertTrip(String loggedAdmin) {
        setTitle("Insert data for table: trip");
        setSize(350, 385);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel trId = new JLabel("tr_id:");
        panel.add(trId);

        field1 = new JTextField(15);
        panel.add(field1);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel trDeparture = new JLabel("tr_departure:");
        panel.add(trDeparture);

        // Add date fields to the panel
        JPanel departureTr = createDatePickerPanel1();
        panel.add(departureTr);

        JLabel trReturn = new JLabel("tr_return:");
        panel.add(trReturn);

        // Add date fields to the panel
        JPanel returnTr = createDatePickerPanel2();
        panel.add(returnTr);

        JLabel trMaxseats = new JLabel("tr_maxseats:");
        panel.add(trMaxseats);

        field2 = new JTextField(15);
        panel.add(field2);

        JLabel trCost = new JLabel("tr_cost:");
        panel.add(trCost);

        field3 = new JTextField(15);
        panel.add(field3);

        JLabel trBrCode = new JLabel("tr_br_code:");
        panel.add(trBrCode);

        field4 = new JTextField(15);
        panel.add(field4);

        JLabel trGuiAt = new JLabel("tr_gui_AT:");
        panel.add(trGuiAt);

        field5 = new JTextField(15);
        panel.add(field5);

        JLabel trDrvAt = new JLabel("tr_drv_AT:");
        panel.add(trDrvAt);

        field6 = new JTextField(15);
        panel.add(field6);

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String trID = field1.getText();
                String trDeparture = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String trReturn = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String maxSeats = field2.getText();
                String cost = field3.getText();
                String trBrCode = field4.getText();
                String trGuiAT = field5.getText();
                String trDrvAT = field6.getText();

                String insertTripStatus = insertTripFunction(trID, trDeparture, trReturn, maxSeats,
                        cost, trBrCode, trGuiAT, trDrvAT, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertTripStatus);
            }
        });
    }

    private String insertTripFunction(String id, String trDeparture, String trReturn, String maxSeats, String trCost,
                                             String trBrCode, String GuiAT, String DrvAT, String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
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
                    insertStatus = "Driver with the given drv_AT does not exist or work on given tr_br_code";
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
                    insertStatus = "Guide with the given gui_AT does not exist or work on given tr_br_code";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO trip VALUES (?,?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, trDeparture);
            statement.setString(3, trReturn);
            statement.setString(4, maxSeats);
            statement.setString(5, trCost);
            statement.setString(6, trBrCode);
            statement.setString(7, GuiAT);
            statement.setString(8, DrvAT);

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
            // ex.printStackTrace();
            insertStatus = "Trip with the same tr_id,tr_departure and tr_return already exists!";
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
