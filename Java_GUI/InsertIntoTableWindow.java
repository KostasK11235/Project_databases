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

    public InsertIntoTableWindow(String tableName, String loggedAdmin) {
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

                field2 = new JTextField(15);
                panel.add(field2);

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

                field4 = new JTextField(15);
                panel.add(field4);
                
                JLabel dstLocation = new JLabel("dst_location:");
                panel.add(dstLocation);
                
                field5 = new JTextField(15);
                panel.add(field5);
                
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

                field2 = new JTextField(15);
                panel.add(field2);

                break;
            case "event":
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

                break;
            case "guide":
                JLabel guiAT = new JLabel("gui_AT:");
                panel.add(guiAT);

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

                JLabel startITDate = new JLabel("start_date:");
                panel.add(startITDate);

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

                switch (tableName.toLowerCase())
                {
                    case "admin":
                        String admAT = field1.getText();
                        String admType = (String) dropdownList1.getSelectedItem();
                        String admDiploma = field2.getText();
                        break;
                    case "branch":
                        String brCode = field1.getText();
                        String brStreet = field2.getText();
                        String brNum = field3.getText();
                        String brCity = field4.getText();
                        break;
                    case "destination":
                        String dstTypes = field1.getText();
                        String dstName = field2.getText();
                        String dstDescr = field3.getText();
                        String dstType = (String) dropdownList1.getSelectedItem();
                        String dstLanguage = field4.getText();
                        String dstLocation = field5.getText();
                        break;
                    case "driver":
                        String drvAT = field1.getText();
                        String drvLicense = (String) dropdownList1.getSelectedItem();
                        String drvRoute = (String) dropdownList2.getSelectedItem();
                        String drvExperience = field2.getText();
                        break;
                    case "event":
                        String evTrId = field1.getText();
                        String evStart = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        String evEnd = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                        String evDescr = field2.getText();
                        break;
                    case "guide":
                        String guiAT = field1.getText();
                        String guiCV = field2.getText();
                        break;
                    case "it":
                        String itAT = field1.getText();
                        String password = field2.getText();
                        String itStartDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        break;
                    case "it_logs":
                        String logId = field1.getText();
                        String itID = field2.getText();
                        String action = (String) dropdownList1.getSelectedItem();
                        String table = (String) dropdownList2.getSelectedItem();
                        String logDate = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        break;
                    case "languages":
                        String lngGuiAt = field1.getText();
                        String lngs = field2.getText();
                        break;
                    case "manages":
                        String mngAdmAt = field1.getText();
                        String mngBrCode = field2.getText();
                        break;
                    case "offers":
                        String offerCode = field1.getText();
                        String offerDate1 = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        String offerDate2 = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                        String cost = field2.getText();
                        String destination = field3.getText();
                        break;
                    case "phones":
                        String phBrCode = field1.getText();
                        String phNumber = field2.getText();
                        break;
                    case "reservation":
                        String resTrId = field1.getText();
                        String resSeatnum = field2.getText();
                        String resName = field3.getText();
                        String resLName = field4.getText();
                        String isAdult = (String) dropdownList1.getSelectedItem();
                        break;
                    case "reservation_offers":
                        String resOfferCode = field1.getText();
                        String custName = field2.getText();
                        String custLName = field3.getText();
                        String trOfferCode = field4.getText();
                        String advanceFee = field5.getText();
                        break;
                    case "travel_to":
                        String toTrId = field1.getText();
                        String toDstId = field2.getText();
                        String arrival = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        String departure = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                        break;
                    case "trip":
                        String trId = field1.getText();
                        String trDeparture = getDateAsString(yearComboBox1, monthComboBox1, dayComboBox1);
                        String trReturn = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                        String trMaxSeats = field2.getText();
                        String trCost = field3.getText();
                        String trBrCode = field4.getText();
                        String trGuiAT = field5.getText();
                        String trDrvAT = field6.getText();
                        break;
                    case "worker":
                        String wrkAT = field1.getText();
                        String wrkName = field2.getText();
                        String wrkLName = field3.getText();
                        String wrkSalary = field4.getText();
                        String wrkBrCode = field5.getText();
                        break;
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
}
