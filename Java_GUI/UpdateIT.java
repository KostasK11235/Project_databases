import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateIT extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<String> dayComboBox1;
    private JComboBox<String> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<String> dayComboBox2;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateIT()
    {
        setTitle("Update table: IT");
        setSize(400, 270);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] ITat = getItsAT();

        JLabel admAT = new JLabel("IT AT:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(ITat);
        panel.add(dropdownList1);

        JLabel admType = new JLabel("New Password:");
        panel.add(admType);

        field1 = new JTextField(15);
        panel.add(field1);

        createDatePickerComponents();

        JLabel evStart = new JLabel("Start Date:");
        panel.add(evStart);

        JPanel startDate = createDatePickerPanel1();
        panel.add(startDate);

        JLabel evEnd = new JLabel("End Date:");
        panel.add(evEnd);

        JPanel endDate = createDatePickerPanel2();
        panel.add(endDate);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAT = (String) dropdownList1.getSelectedItem();
                String newPassword = field1.getText();
                String startDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String endDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);

                String[] parts = selectedAT.split(",");
                String itAT = parts[0];
                String updateITStatus = updateITFunction(itAT, newPassword, startDate, endDate);
                JOptionPane.showMessageDialog(null, updateITStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an IT AT to update that ITs data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateITFunction(String at, String newPassword, String startDate, String endDate)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);
            String sql = "SELECT start_date FROM it WHERE IT_AT=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, at);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String oldStartDate = resultSet.getString("start_date");

            String[] parts = oldStartDate.split(" ");
            String oldDate = parts[0];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if(startDate.equalsIgnoreCase("NULL"))
            {
                startDate = oldDate;

                if(endDate.equalsIgnoreCase("NULL"))
                    endDate = "DEFAULT";
                else
                {
                    LocalDate date2 = LocalDate.parse(startDate, formatter);
                    LocalDate date3 = LocalDate.parse(endDate, formatter);
                    if(date3.isBefore(date2))
                        endDate = "DEFAULT";
                }

            }
            else if(endDate.equalsIgnoreCase("NULL"))
            {
                endDate = "DEFAULT";
                LocalDate date1 = LocalDate.parse(oldDate, formatter);
                LocalDate date2 = LocalDate.parse(startDate, formatter);
                if(date2.isBefore(date1))
                    startDate = oldDate;
            }
            else
            {
                LocalDate date1 = LocalDate.parse(oldDate, formatter);
                LocalDate date2 = LocalDate.parse(startDate, formatter);
                LocalDate date3 = LocalDate.parse(endDate, formatter);

                if(date2.isBefore(date1))
                {
                    startDate = oldDate;
                    date2 = LocalDate.parse(startDate, formatter);
                }

                if(date3.isBefore(date2))
                    endDate = "DEFAULT";
            }


            if(endDate.equalsIgnoreCase("DEFAULT"))
            {
                sql = "UPDATE it SET password=?,start_date=?,end_date=DEFAULT WHERE IT_AT=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, newPassword);
                statement.setString(2, startDate);
                statement.setString(3, at);
            }
            else
            {
                sql = "UPDATE it SET password=?,start_date=?,end_date=? WHERE IT_AT=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, newPassword);
                statement.setString(2, startDate);
                statement.setString(3, endDate);
                statement.setString(4, at);
            }

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "IT record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getItsAT()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> admins = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_AT,w.wrk_name,w.wrk_lname,w.wrk_br_code FROM worker AS w INNER JOIN it" +
                    " ON w.wrk_AT=it.IT_AT";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("w.wrk_AT");
                String name = resultSet.getString("w.wrk_name");
                String lname = resultSet.getString("w.wrk_lname");
                String guiBr = resultSet.getString("w.wrk_br_code");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname + "at Branch: " + guiBr;
                admins.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return admins.toArray(new String[admins.size()]);
    }

    private void createDatePickerComponents() {
        // Get current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Create year combo boxes
        yearComboBox1 = new JComboBox<>();
        yearComboBox2 = new JComboBox<>();
        yearComboBox1.addItem(" ");
        yearComboBox2.addItem(" ");
        for (int year = currentYear - 10; year <= currentYear + 10; year++) {
            yearComboBox1.addItem(String.valueOf(year));
            yearComboBox2.addItem(String.valueOf(year));
        }

        // Create month combo boxes
        String[] months = {" ", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthComboBox1 = new JComboBox<>(months);
        monthComboBox2 = new JComboBox<>(months);

        // Create day combo boxes
        dayComboBox1 = new JComboBox<>();
        dayComboBox2 = new JComboBox<>();
        dayComboBox1.addItem(" ");
        dayComboBox2.addItem(" ");
        for (int day = 1; day <= 31; day++) {
            dayComboBox1.addItem(String.valueOf(day));
            dayComboBox2.addItem(String.valueOf(day));
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

    private String getDateAsString(JComboBox<String> yearComboBox, JComboBox<String> monthComboBox, JComboBox<String> dayComboBox) {
        String yearString = (String) yearComboBox.getSelectedItem();
        String monthString = (String) monthComboBox.getSelectedItem();
        String dayString = (String) dayComboBox.getSelectedItem();

        // Check if any of the selected items are empty strings
        if (yearString.equalsIgnoreCase(" ") || monthString.equalsIgnoreCase(" ") || dayString.equalsIgnoreCase(" ")) {
            return "NULL";
        }

        int year = Integer.parseInt(yearString);
        int month = monthComboBox.getSelectedIndex() + 1; // Add 1 to adjust for zero-based index
        int day = Integer.parseInt(dayString);

        return String.format("%04d-%02d-%02d", year, month, day);
    }
}