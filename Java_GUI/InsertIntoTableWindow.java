import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.CallableStatement;

public class InsertIntoTableWindow extends JFrame
{
    // We will create 8 generic fields and use as many as we need for each insertion window
    // 8 because table trip needs 8 insert fields which is the max in the database
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JTextField field5;
    private JTextField field6;
    private JTextField field7;
    private JTextField field8;
    private JComboBox<String> dropdownList1;
    private JComboBox<String> dropdownList2;
    private JComboBox<Integer> yearComboBox1;
    private JComboBox<String> monthComboBox1;
    private JComboBox<Integer> dayComboBox1;
    private JComboBox<Integer> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<Integer> dayComboBox2;
    private JButton insertButton;

    public InsertIntoTableWindow(String tableName) {
        setTitle("Insert data for table" + tableName);
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        switch (tableName.toLowerCase()) {
            case "admin":
                String[] adm_types = {"LOGISTICS", "ADMINISTRATIVE", "ACCOUNTING"};

                JLabel admAT = new JLabel("adm_AT:");
                panel.add(admAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel admType = new JLabel("adm_type:");
                panel.add(admType);

                dropdownList1 = new JComboBox<>(adm_types);
                panel.add(dropdownList1);

                JLabel admDiploma = new JLabel("adm_diploma:");
                panel.add(admDiploma);

                field3 = new JTextField(15);
                panel.add(field3);

                break;
            case "branch":
                JLabel brCode = new JLabel("br_code:");
                panel.add(brCode);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel brStreet = new JLabel("br_street:");
                panel.add(brStreet);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel brNum = new JLabel("br_num:");
                panel.add(brNum);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel brCity = new JLabel("br_city:");
                panel.add(brCity);

                field4 = new JTextField(15);
                panel.add(field4);

                break;
            case "destination":
                String[] dstTypes = {"LOCAL", "ABROAD"};
                
                JLabel dstId = new JLabel("dst_id:");
                panel.add(dstId);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel dstName = new JLabel("dst_name:");
                panel.add(dstName);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel dstDescr = new JLabel("dst_descr:");
                panel.add(dstDescr);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel dst_type = new JLabel("dst_type:");
                panel.add(dst_type);

                dropdownList1 = new JComboBox<>(dstTypes);
                panel.add(dropdownList1);

                JLabel dstLanguage = new JLabel("dst_language:");
                panel.add(dstLanguage);

                field5 = new JTextField(15);
                panel.add(field5);
                
                JLabel dstLocation = new JLabel("dst_location:");
                panel.add(dstLocation);
                
                field6 = new JTextField(15);
                panel.add(field6);
                
                break;
            case "driver":
                String[] licenseTypes = {"A", "B", "C", "D"};
                String[] routes = {"LOCAL", "ABROAD"};

                JLabel drvAT = new JLabel("drv_AT:");
                panel.add(drvAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel drvLicense = new JLabel("drv_license:");
                panel.add(drvLicense);

                dropdownList1 = new JComboBox<>(licenseTypes);
                panel.add(dropdownList1);

                JLabel drvRoute = new JLabel("drv_route:");
                panel.add(drvRoute);

                dropdownList2 = new JComboBox<>(routes);
                panel.add(dropdownList2);

                JLabel drvExperience = new JLabel("drv_experience:");
                panel.add(drvExperience);

                break;
            case "event":
                JLabel evTrId = new JLabel("ev_tr_id:");
                panel.add(evTrId);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel evStart = new JLabel("ev_start:");
                panel.add(evStart);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel evEnd = new JLabel("ev_end:");
                panel.add(evEnd);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel evDescr = new JLabel("ev_descr:");
                panel.add(evDescr);

                field4 = new JTextField(15);
                panel.add(field4);

                break;
            case "guide":
                JLabel guyAT = new JLabel("guy_AT:");
                panel.add(guyAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel guiCV = new JLabel("gui_cv:");
                panel.add(guiCV);

                field2 = new JTextField(15);
                panel.add(field2);

                break;
            case "it":
                JLabel itAT = new JLabel("IT_AT:");
                panel.add(itAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel password = new JLabel("password:");
                panel.add(password);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel startDate = new JLabel("start_date:");
                panel.add(startDate);

                // Create date fields with drop-down lists
                createDatePickerComponents();

                // Add date fields to the panel
                JPanel firstDatePanel = createDatePickerPanel1();
                panel.add(firstDatePanel);

                break;
            case "it_logs":
                String[] actions = {"INSERT", "DELETE", "UPDATE"};
                String[] tables = {"Admin", "Branch", "Destination", "Driver", "Event", "Guide", "IT", "IT_logs", "Languages"
                        , "Manages", "Offers", "Phones", "Reservation", "Reservation_Offers", "Travel_to", "Trip", "Worker"};

                JLabel logId = new JLabel("log_id:");
                panel.add(logId);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel itId = new JLabel("IT_id:");
                panel.add(itId);

                field2 = new JTextField(15);
                panel.add(field2);

                dropdownList1 = new JComboBox<>(actions);
                dropdownList2 = new JComboBox<>(tables);
                panel.add(dropdownList1);
                panel.add(dropdownList2);

                // Create date fields with drop-down lists
                createDatePickerComponents();

                // Add date fields to the panel
                JPanel datePanel = createDatePickerPanel1();
                panel.add(datePanel);

                break;
            case "languages":
                JLabel lngGuiAT = new JLabel("lng_gui_AT:");
                panel.add(lngGuiAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel lngs = new JLabel("lng_language");
                panel.add(lngs);

                field2 = new JTextField(15);
                panel.add(field2);

                break;
            case "manages":
                JLabel mngAdmAT = new JLabel("mng_adm_AT:");
                panel.add(mngAdmAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel mngBrCode = new JLabel("mng_br_code:");
                panel.add(mngBrCode);

                field2 = new JTextField(15);
                panel.add(field2);

                break;
            case "offers":
                JLabel offerCode = new JLabel("offer_code:");
                panel.add(offerCode);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel fromDate = new JLabel("from_date:");
                panel.add(fromDate);

                // Create date fields with drop-down lists
                createDatePickerComponents();

                // Add date fields to the panel
                JPanel offersDate1 = createDatePickerPanel1();
                panel.add(offersDate1);

                JLabel toDate = new JLabel("to_date:");
                panel.add(toDate);

                // Add date fields to the panel
                JPanel offersDate2 = createDatePickerPanel2();
                panel.add(offersDate2);

                JLabel cost = new JLabel("cost_per_person");
                panel.add(cost);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel destinationID = new JLabel("destination_id:");
                panel.add(destinationID);

                field3 = new JTextField(15);
                panel.add(field3);
                break;
            case "phones":
                JLabel phBrCode = new JLabel("ph_br_code:");
                panel.add(phBrCode);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel phNumber = new JLabel("ph_number:");
                panel.add(phNumber);

                field2 = new JTextField(15);
                panel.add(field2);
                break;
            case "reservation":
                String[] resTypes = {"ADULT", "MINOR"};

                JLabel resTrID = new JLabel("res_tr_id:");
                panel.add(resTrID);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel resSeatnum = new JLabel("res_seatnum:");
                panel.add(resSeatnum);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel resName = new JLabel("res_name:");
                panel.add(resName);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel resLName = new JLabel("res_lname");
                panel.add(resLName);

                field4 = new JTextField(15);
                panel.add(field4);

                JLabel resIsAdult = new JLabel("res_isadult:");
                panel.add(resIsAdult);

                dropdownList1 = new JComboBox<>(resTypes);
                panel.add(dropdownList1);
                break;
            case "reservation_offers":
                JLabel resOfferCode = new JLabel("res_offer_code:");
                panel.add(resOfferCode);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel custName = new JLabel("cust_name:");
                panel.add(custName);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel custLName = new JLabel("cust_lname:");
                panel.add(custLName);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel trOfferCode = new JLabel("trip_offer_code");
                panel.add(trOfferCode);

                field4 = new JTextField(15);
                panel.add(field4);

                JLabel advanceFee = new JLabel("advance_fee");
                panel.add(advanceFee);

                field5 = new JTextField(15);
                panel.add(field5);

                break;
            case "travel_to":
                JLabel toTrId = new JLabel("to_tr_id:");
                panel.add(toTrId);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel toDstId = new JLabel("to_dst_id");
                panel.add(toDstId);

                // Create date fields with drop-down lists
                createDatePickerComponents();

                JLabel toArrival = new JLabel("to_arrival:");
                panel.add(toArrival);

                // Add date fields to the panel
                JPanel arrivalDate = createDatePickerPanel1();
                panel.add(arrivalDate);

                JLabel toDeparture = new JLabel("to_departure:");
                panel.add(toDeparture);

                // Add date fields to the panel
                JPanel departureDate = createDatePickerPanel2();
                panel.add(departureDate);

                break;
            case "trip":
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

                break;
            case "worker":
                JLabel wrkAT = new JLabel("wrk_AT:");
                panel.add(wrkAT);

                field1 = new JTextField(15);
                panel.add(field1);

                JLabel wrkName = new JLabel("wrk_name:");
                panel.add(wrkName);

                field2 = new JTextField(15);
                panel.add(field2);

                JLabel wrkLName = new JLabel("wrk_lname:");
                panel.add(wrkLName);

                field3 = new JTextField(15);
                panel.add(field3);

                JLabel wrkSalary = new JLabel("wrk_salary:");
                panel.add(wrkSalary);

                field4 = new JTextField(15);
                panel.add(field4);

                JLabel wrkBrCode = new JLabel("wrk_br_code:");
                panel.add(wrkBrCode);

                field5 = new JTextField(15);
                panel.add(field5);

                break;
        }

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "insert button pressed!");
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

    private List<String> searchLastName(String lastName)
    {
        List<String> participation = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "{CALL get_Offers_Participation(?)}";
            CallableStatement callStmt = connection.prepareCall(sql);
            callStmt.setString(1, lastName);

            boolean hasResultSet = callStmt.execute();

            if(hasResultSet)
            {
                ResultSet resultSet = callStmt.getResultSet();

                resultSet.last();
                int resultSetSize = resultSet.getRow();

                // Check if it's a single result of multiple rows
                if(resultSetSize==1)
                {
                    participation.add("Customer Name\tCustomer Last Name\tTrip Offer Code");
                    String participant = resultSet.getString("cust_name") + "\t\t" +
                            resultSet.getString("cust_lname") + "\t\t" +
                            resultSet.getInt("trip_offer_code");

                    participation.add(participant);
                }
                else if(resultSetSize>1)
                {
                    participation.add("Trip Offer Code\tTotal People");
                    resultSet.beforeFirst();	// Move the cursor back to the beginning
                    while(resultSet.next())
                    {
                        String participant = resultSet.getInt("trip_offer_code") + "\t"
                                + resultSet.getInt("total_people");

                        participation.add(participant);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return participation;
    }
}
