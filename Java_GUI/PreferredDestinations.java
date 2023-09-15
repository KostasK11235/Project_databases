import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


public class PreferredDestinations extends JFrame{

    public PreferredDestinations() {
        List<String> countries = getPreferredDestinations();
        openResultScreen(countries);
    }

    // We get the number of people that traveled to each destination for each trip before the today's date
    private List<String> getPreferredDestinations()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> destinations = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT d.dst_name,trips.booked FROM destination d INNER JOIN " +
                    "(SELECT k.dest_id AS destination,k.sum_part AS booked FROM trip tr INNER JOIN " +
                    "(SELECT t.to_tr_id AS trip,t.to_dst_id AS dest_id,SUM(res.participation) AS sum_part FROM travel_to t " +
                    "LEFT JOIN (SELECT res_tr_id,COUNT(*) AS participation FROM reservation GROUP BY res_tr_id) " +
                    "AS res ON res.res_tr_id=t.to_tr_id " +
                    "GROUP BY t.to_dst_id) k ON tr.tr_id=k.trip WHERE tr.tr_return<NOW()) AS trips " +
                    "ON trips.destination=d.dst_id " +
                    "ORDER BY d.dst_id;";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            Integer total_customers = 0;
            Integer max_visitors = -1;
            String popular_dest = "";

            destinations.add("Destination\tPeople Visited");
            while(resultSet.next())
            {
                String currDestination = resultSet.getString("d.dst_name") + "\t" +
                        resultSet.getString("trips.booked");
                total_customers += Integer.parseInt(resultSet.getString("trips.booked"));
                if(Integer.parseInt(resultSet.getString("trips.booked")) > max_visitors)
                {
                    max_visitors = Integer.parseInt(resultSet.getString("trips.booked"));
                    popular_dest = resultSet.getString("d.dst_name");
                }
                destinations.add(currDestination);
            }

            destinations.add("---------------------------");
            destinations.add("Total visitors:\t" + total_customers);
            destinations.add("\n\nMost popular destination:\t" + popular_dest);

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return destinations;
    }

    private void openResultScreen(List<String> results)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ResultScreen(results).setVisible(true);
            }
        });
    }
}


