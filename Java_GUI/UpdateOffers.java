import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateOffers extends JFrame{
    private JTextField field1;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropDownList2;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton updateButton;
    private JButton helpButton;


    public UpdateOffers()
    {
        setTitle("Update table: Offers");
        setSize(400, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] offerCodes = getOfferCodes();
        String[] dstCodes = getDestinationCodes();

        JLabel admAT = new JLabel("Offer Code:");
        panel.add(admAT);

        dropdownList1 = new JComboBox<>(offerCodes);
        panel.add(dropdownList1);

        // Create date fields with drop-down lists
        createDatePickerComponents();

        JLabel fromDate = new JLabel("From Date:");
        panel.add(fromDate);

        JPanel startDate = createDatePickerPanel1();
        panel.add(startDate);

        JLabel toDate = new JLabel("To Date:");
        panel.add(toDate);

        JPanel endDate = createDatePickerPanel2();
        panel.add(endDate);

        JLabel cost = new JLabel("Cost per Person:");
        panel.add(cost);

        field1 = new JTextField(15);
        panel.add(field1);

        JLabel dstID = new JLabel("Destination id:");
        panel.add(dstID);

        dropDownList2 = new JComboBox<>(dstCodes);
        panel.add(dropDownList2);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        updateButton = new JButton("Update");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String offerCode = (String) dropdownList1.getSelectedItem();
                String fromDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                String toDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String cost = field1.getText();
                String selectedCode = (String) dropDownList2.getSelectedItem();

                String[] parts = selectedCode.split(",");
                String dstCode = parts[0];

                String updateOffersStatus = updateOffersFunction(offerCode, fromDate, toDate, cost, dstCode);
                JOptionPane.showMessageDialog(null, updateOffersStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose an offer code to update that offers data on the table.
                        """;
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });
    }

    private String updateOffersFunction(String offerCode, String fromDate, String toDate, String cost, String dstID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String UpdateStatus = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername,dbPassword);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(fromDate, formatter);
            LocalDate date2 = LocalDate.parse(toDate, formatter);

            if(date2.isBefore(date1))
            {
                UpdateStatus = "Second date must be chronologically after the first date!";
                return UpdateStatus;
            }

            String sql = "UPDATE offers SET from_date=?,to_date=?,cost_per_person=?,destination_id=? WHERE offer_code=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, fromDate);
            statement.setString(2, toDate);
            statement.setString(3, cost);
            statement.setString(4, dstID);
            statement.setString(5, offerCode);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0)
                UpdateStatus = "Offer record updated successfully!";

            statement.close();
            connection.close();
        }
        catch (SQLException ex)
        {
            UpdateStatus = ex.getMessage();
        }

        return UpdateStatus;
    }

    private String[] getOfferCodes()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> offerCodes = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT offer_code FROM offers";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("offer_code");
                offerCodes.add(currCode);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return offerCodes.toArray(new String[offerCodes.size()]);
    }

    private String[] getDestinationCodes()
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
                String currCode = resultSet.getString("dst_id");
                String currName = resultSet.getString("dst_name");
                String info = currCode + ", Destination: " + currName;
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