import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MainAppWindow extends JFrame {
    public MainAppWindow(String loggedUser) {
        setTitle("Main Application Window");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // Create buttons
        JButton insertButton = new JButton("INSERT INTO TABLE");
        JButton updateButton = new JButton("UPDATE TABLE");
        JButton deleteButton = new JButton("DELETE FROM TABLE");
        JButton branchTripInfoButton = new JButton("BRANCH TRIPS");
        JButton getCustomersButton = new JButton("CUSTOMERS PER OFFER");
        JButton branchInfoButton = new JButton("BRANCH INFO");
        JButton branchWorkersButton = new JButton("BRANCH WORKERS INFO");
        JButton newITButton = new JButton("INSERT NEW IT");
        JButton itLogsButton = new JButton("SHOW IT LOGS");

        // Add buttons to the main panel
        mainPanel.add(insertButton);
        mainPanel.add(updateButton);
        mainPanel.add(deleteButton);
        mainPanel.add(branchTripInfoButton);
        mainPanel.add(getCustomersButton);
        mainPanel.add(branchInfoButton);
        mainPanel.add(branchWorkersButton);
        mainPanel.add(newITButton);
        mainPanel.add(itLogsButton);

        // Insert data in tables Button Action Listener
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform INSERT INTO TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SelectTableWindow("insert", loggedUser).setVisible(true);
                    }
                });
            }
        });

        // Update data in tables Button Action Listener
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform UPDATE TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SelectTableWindow("update", loggedUser).setVisible(true);
                    }
                });
            }
        });

        // Delete data from tables Button Action Listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SelectTableWindow("delete", loggedUser).setVisible(true);
                    }
                });
            }
        });

        // Get trip data within certain dates Button Action Listener
        branchTripInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ChooseDatesWindow().setVisible(true);
                    }
                });
            }
        });

        // Πρέπει να βάλουμε και την πρώτη procedure απο το 3.1.3.4. a)
        // Get customer(s) data from reservation_offers Button Action Listener
        getCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new InsertCustNameWindow().setVisible(true);
                    }
                });
            }
        });

        // Get information about each branch Button Action Listener
        branchInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                List<String> branchInfo = getBranchInfo();
                openResultScreen(branchInfo);
            }
        });

        // Get information about every worker on each branch Button Action Listener
        branchWorkersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                List<String> results = fetchResultsFromDatabase();
                openResultScreen(results);
            }
        });

        // Insert new IT Button Action Listener
        newITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new InsertNewITWindow(loggedUser).setVisible(true);
                    }
                });
            }
        });

        // Get data from it_logs Button Action Listener
        itLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform DELETE FROM TABLE action
                List<String> logs = getITLogsFromDatabase();
                openResultScreen(logs);
            }
        });

        // Add the main panel to the JFrame
        setContentPane(mainPanel);
    }

    // Method that returns workers data from each branch
    private List<String> fetchResultsFromDatabase()
    {
        List<String> results = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT w.wrk_br_code,w.wrk_name,w.wrk_lname,w.wrk_salary,branch_total_salary.total_salary_per_branch FROM worker AS w " +
                    "LEFT JOIN (SELECT wrk_br_code, SUM(wrk_salary) AS total_salary_per_branch FROM worker GROUP BY wrk_br_code) " +
                    "AS branch_total_salary ON w.wrk_br_code = branch_total_salary.wrk_br_code ORDER BY w.wrk_br_code;";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                results.add("Branch Code\tWorker Name\tWorker Last Name\tWorker Salary\tBranch Salaries Sum");
                while (resultSet.next())
                {
                    String currRow = resultSet.getString("w.wrk_br_code") + ",\t" +
                            resultSet.getString("w.wrk_name") + ",\t" +
                            resultSet.getString("w.wrk_lname") + ",\t\t" +
                            resultSet.getString("w.wrk_salary") + ",\t" +
                            resultSet.getString("total_salary_per_branch");

                    results.add(currRow);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return results;
    }

    // Method that returns data from it_log table
    private List<String> getITLogsFromDatabase()
    {
        List<String> logs = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT i.table_name,i.action,w.wrk_lname,i.log_date FROM it_logs AS i INNER JOIN worker AS w ON w.wrk_AT=i.IT_id;";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            try
            {
                logs.add("Worker Last Name\tAction\tTable\tTimestamp");
                while(resultSet.next())
                {
                    String currLog = resultSet.getString("w.wrk_lname")+",\t\t"+
                            resultSet.getString("i.action")+"\t"+
                            resultSet.getString("i.table_name")+"\t"+
                            resultSet.getString("i.log_date");

                    logs.add(currLog);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return logs;
    }

    // Method that returns information for every branch, its manager, total reservations and income
    private List<String> getBranchInfo()
    {
        List<String> info = new ArrayList<>();

        // Database connection and query
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        try
        {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT b.br_code,b.br_street,b.br_num,b.br_city,w.wrk_name AS admin_name," +
                    "w.wrk_lname AS admin_lastname,COUNT(r.res_tr_id) AS total_reservations," +
                    "COUNT(r.res_tr_id)*t.tr_cost AS total_income " +
                    "FROM branch AS b " +
                    "LEFT JOIN worker AS w ON b.br_code = w.wrk_br_code " +
                    "LEFT JOIN admin AS ad ON w.wrk_AT = ad.adm_AT " +
                    "LEFT JOIN trip AS t ON b.br_code = t.tr_br_code " +
                    "LEFT JOIN reservation AS r ON t.tr_id = r.res_tr_id " +
                    "WHERE ad.adm_type = 'ADMINISTRATIVE' " +
                    "GROUP BY b.br_code,b.br_street,b.br_num,b.br_city,w.wrk_name,w.wrk_lname;";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            try
            {
                info.add("Branch Code\tWorker Name\tWorker Last Name\tWorker Salary\tBranch Salaries Sum");
                while(resultSet.next())
                {
                    String currInfo = resultSet.getString("b.br_code")+",\t"+
                            String.format("%-"+ 30 + "s", resultSet.getString("b.br_street"))+"\t"+
                            resultSet.getString("b.br_num")+"\t"+
                            resultSet.getString("b.br_city")+"\t"+
                            resultSet.getString("admin_name")+"\t"+
                            resultSet.getString("admin_lastname")+"\t"+
                            resultSet.getString("total_reservations")+"\t"+
                            resultSet.getString("total_income");

                    info.add(currInfo);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return info;
    }

    // Method that opens then ResultScreen with the fetched data
    private void openResultScreen(List<String> results)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ResultScreen(results).setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginScreen obj = new LoginScreen();
                obj.setVisible(true);
            }
        });
    }
}
