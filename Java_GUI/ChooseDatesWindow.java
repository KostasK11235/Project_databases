import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChooseDatesWindow extends JFrame {
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;

    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton searchButton;

    public ChooseDatesWindow() {
        setTitle("Select two dates");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        setContentPane(panel);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        // Add date fields to the panel
        JPanel firstDatePanel = createDatePickerPanel1();
        panel.add(firstDatePanel, BorderLayout.NORTH);

        JPanel secondDatePanel = createDatePickerPanel2();
        panel.add(secondDatePanel, BorderLayout.CENTER);

        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        panel.add(searchButton, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String secondDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);

                List<String> tripsInfo = searchTrips(firstDate, secondDate);
                if (tripsInfo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No trips between these dates!");
                } else {
                    new ResultScreen(tripsInfo).setVisible(true);
                }
            }
        });
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

    private List<String> searchTrips(String first_date, String second_date)
    {
        List<String> trips = new ArrayList<>();
        List<Integer> branchCodes = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String selectQuery = "SELECT br_code FROM branch;";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            try
            {
                while(resultSet.next())
                {
                    int currCode = resultSet.getInt("br_code");
                    branchCodes.add(currCode);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            trips.add("Branch Code\tTrip Cost\t#Seats\t#Reserved Seats\t#Empty Seats\tDriver Name\tDriver Last Name\t" +
                    "Guide Name\tGuide Last Name\tDeparture Date\t\tReturn Date");

            for(int code=0;code<branchCodes.size();code++)
            {
                int branch_code = branchCodes.get(code);

                String sql = "{CALL get_trip_info(?,?,?)}";
                CallableStatement callStmt = connection.prepareCall(sql);
                callStmt.setInt(1, branch_code);
                callStmt.setString(2, first_date);
                callStmt.setString(3, second_date);

                boolean hasResultSet = callStmt.execute();

                if(hasResultSet)
                {
                    ResultSet resultSet2 = callStmt.getResultSet();
                    String currTrip = String.valueOf(branch_code);
                    try
                    {
                        while(resultSet2.next())
                        {
                            currTrip += "\t" + resultSet2.getFloat("Trip Cost") + "\t" +
                                    resultSet2.getInt("#Seats") + "\t" +
                                    resultSet2.getInt("#Researved Seats") + "\t\t" +
                                    resultSet2.getInt("#Empty Seats") + "\t" +
                                    resultSet2.getString("Driver Name") + "\t" +
                                    resultSet2.getString("Driver Last Name") + "\t\t" +
                                    resultSet2.getString("Guide Name") + "\t" +
                                    resultSet2.getString("Guide Last Name") + "\t\t" +
                                    resultSet2.getString("Departure Date") + "\t" +
                                    resultSet2.getString("Return Date");

                            trips.add(currTrip);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return trips;
    }
}
