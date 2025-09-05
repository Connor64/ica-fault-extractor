import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException {
        JFrame frame = new JFrame("ICA Fault Extractor Utility");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        EditorWindow mainWindow = new EditorWindow();

        // Try to set the ICA logo as the application icon
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

        // Add and enable the main window
        frame.add(mainWindow);
        frame.setVisible(true);
    }
}
