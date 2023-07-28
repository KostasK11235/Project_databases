import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InsertReservation extends JFrame{
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    private JTextField field4;
    private JComboBox<String> dropdownList1;
    private JButton insertButton;

    public InsertReservation(String loggedAdmin) {
        setTitle("Insert data for table: reservation");
        setSize(360, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

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

        insertButton = new JButton("Insert");
        panel.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resTrID = field1.getText();
                String seatnum = field2.getText();
                String resName = field3.getText();
                String resLName = field4.getText();
                String isAdult = (String) dropdownList1.getSelectedItem();

                String insertReservationStatus = insertReservationFunction(resTrID, seatnum, resName, resLName,
                        isAdult, loggedAdmin);
                JOptionPane.showMessageDialog(null, insertReservationStatus);
            }
        });
    }

    private String insertReservationFunction(String trID, String seatnum, String name, String lname, String isAdult,
                                             String adminsID)
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String insertStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT * FROM trip WHERE tr_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, trID);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                if(!resultSet.first())
                {
                    insertStatus = "Field res_tr_id must match an existing tr_id!";
                    return insertStatus;
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            sql = "INSERT INTO reservation VALUES (?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, trID);
            statement.setString(2, seatnum);
            statement.setString(3, name);
            statement.setString(4, lname);
            statement.setString(5, isAdult);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected>0) {
                insertStatus = "New reservation inserted into reservation table!";

                sql = "UPDATE it_logs SET IT_id=? WHERE log_id=(SELECT MAX(log_id) FROM it_logs WHERE table_name=?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, adminsID);
                statement.setString(2, "reservation");

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            // ex.printStackTrace();
            insertStatus = "Reservation with the same res_tr_id and res_seatnum already exists!";
        }
        return insertStatus;
    }
}
