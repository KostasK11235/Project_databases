import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectTableWindow extends JFrame {
    private JComboBox<String> dropdownList;
    private JButton confirmButton;

    public SelectTableWindow(String action, String userID) {
        setTitle("Choose a table to " + action + " data:");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        setContentPane(panel);

        // Create the list of string
        String[] tables = {"Admin", "Branch", "Destination", "Driver", "Event", "Guide", "Languages"
                , "Manages", "Offers", "Phones", "Reservation", "Reservation_Offers", "Travel_to", "Trip", "Worker"};

        // Create the dropdown list
        dropdownList = new JComboBox<>(tables);
        panel.add(dropdownList, BorderLayout.CENTER);

        confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(100, 30));
        panel.add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Perform INSERT INTO TABLE action
                String selectedTable = (String) dropdownList.getSelectedItem();

                if("insert".equalsIgnoreCase(action))
                {
                    switch (selectedTable.toLowerCase())
                    {
                        case "admin":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertAdmin().setVisible(true);
                                }
                            });
                            break;
                        case "branch":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertBranch().setVisible(true);
                                }
                            });
                            break;
                        case "destination":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertDestination(userID).setVisible(true);
                                }
                            });
                            break;
                        case "driver":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertDriver().setVisible(true);
                                }
                            });
                            break;
                        case "event":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertEvent(userID).setVisible(true);
                                }
                            });
                            break;
                        case "guide":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertGuide().setVisible(true);
                                }
                            });
                            break;
                        case "languages":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertLanguages().setVisible(true);
                                }
                            });
                            break;
                        case "manages":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertManages().setVisible(true);
                                }
                            });
                            break;
                        case "offers":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertOffers().setVisible(true);
                                }
                            });
                            break;
                        case "phones":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertPhones().setVisible(true);
                                }
                            });
                            break;
                        case "reservation":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertReservation(userID).setVisible(true);
                                }
                            });
                            break;
                        case "reservation_offers":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertReservationOffers().setVisible(true);
                                }
                            });
                            break;
                        case "travel_to":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertTravelTo(userID).setVisible(true);
                                }
                            });
                            break;
                        case "trip":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() { new InsertTrip(userID).setVisible(true); }
                            });
                            break;
                        case "worker":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new InsertWorker().setVisible(true);
                                }
                            });
                            break;
                    }
                }
                else
                {
                    switch (selectedTable.toLowerCase())
                    {
                        case "admin":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteAdmin().setVisible(true);
                                }
                            });
                            break;
                        case "branch":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteBranch().setVisible(true);
                                }
                            });
                            break;
                        case "destination":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteDestination(userID).setVisible(true);
                                }
                            });
                            break;
                        case "driver":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteDriver().setVisible(true);
                                }
                            });
                            break;
                        case "event":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteEvent(userID).setVisible(true);
                                }
                            });
                            break;
                        case "guide":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteGuide().setVisible(true);
                                }
                            });
                            break;
                        case "languages":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteLanguage().setVisible(true);
                                }
                            });
                            break;
                        case "manages":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteManages().setVisible(true);
                                }
                            });
                            break;
                        case "offers":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteOffers().setVisible(true);
                                }
                            });
                            break;
                        case "phones":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeletePhones().setVisible(true);
                                }
                            });
                            break;
                        case "reservation":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteReservation(userID).setVisible(true);
                                }
                            });
                            break;
                        case "reservation_offers":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteReservationOffers().setVisible(true);
                                }
                            });
                            break;
                        case "travel_to":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteTravelTo(userID).setVisible(true);
                                }
                            });
                            break;
                        case "trip":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() { new DeleteTrip(userID).setVisible(true); }
                            });
                            break;
                        case "worker":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteWorker().setVisible(true);
                                }
                            });
                            break;
                        case "it":
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new DeleteIT().setVisible(true);
                                }
                            });
                            break;
                    }
                }

            }
        });
    }
}