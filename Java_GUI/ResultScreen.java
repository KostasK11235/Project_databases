import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultScreen extends JFrame {
    private JTextArea resultTextArea;

    public ResultScreen(List<String> results) {
        setTitle("Results");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Only close the result screen, not the whole application
        setLocationRelativeTo(null);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // Display the fetched results in the text area
        for (String result : results) {
            resultTextArea.append(result + "\n");
        }
    }
}