import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException {
        JFrame frame = new JFrame("ICA Fault Extractor Utility");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        EditorWindow mainWindow = new EditorWindow();

        // Try to set the ICA logo as the application icon (only on Windows)
        try {
            URL resource = mainWindow.getClass().getResource("/resources/ICA-Logo.png");
            if (resource != null) {
                BufferedImage image = ImageIO.read(resource);
                frame.setIconImage(image);
            } else {
                System.out.println("Unable to find ICA Logo image.");
            }
        } catch (IOException e) {
            System.err.println("Unable to set the ICA logo icon image.");
            e.printStackTrace();
        }

        // Create menu "help" button to take users to Github page
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Info");
        helpItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/Connor64/ica-fault-extractor"));
            } catch (UnsupportedOperationException | IOException | URISyntaxException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Visit \"https://github.com/Connor64/ica-fault-extractor\" for more information about the program.",
                        "Program Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu.add(helpItem);
        menuBar.add(menu);

        // Add and enable the main window
        frame.setJMenuBar(menuBar);
        frame.add(mainWindow);
        frame.setVisible(true);
    }
}
