import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.util.HashSet;

public class EditorWindow extends JPanel {

    private final JButton openButton, exportButton;
    private final JFileChooser openFileChooser, exportFileChooser;
    private final JLabel indicatorLabel, attributeLabel;
    private final JPanel attributePanel;
    private final JScrollPane attributeScrollPane;

    private File selectedFile = null;
    private NodeList alarmList = null;
    private HashSet<String> selectedAttributes;

    private final DocumentBuilder docBuilder;

    public EditorWindow () throws ParserConfigurationException {

        /* ------------------------ INITIALIZE WINDOW ELEMENTS ------------------------ */

        openButton = new JButton("Open File");
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        exportButton = new JButton("Export File");
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        indicatorLabel = new JLabel();
        indicatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setSelectedFile(null);

        attributeLabel = new JLabel("Select Fault Attributes:");
        attributeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        attributePanel = new JPanel();
        attributePanel.setLayout(new GridLayout(0, 3));
        attributeScrollPane = new JScrollPane(attributePanel);
        attributeScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectedAttributes = new HashSet<>();

        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        /* ------------------------ INITIALIZE FILE OPENERS ------------------------ */

        // Set open file chooser to only open .xml files
        openFileChooser = new JFileChooser();
        openFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;

                return f.getName().toLowerCase().endsWith(".xml");
            }

            @Override
            public String getDescription() {
                return "XML Documents (*.xml)";
            }
        });

        // Set export file chooser to only export in .csv files
        exportFileChooser = new JFileChooser();
        exportFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;

                return f.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV Documents (*.csv)";
            }
        });

        // ------------ Action when opening a file ------------
        openButton.addActionListener(e -> openFaultFile());

        // ------------ Action when exporting the CSV file ------------
        exportButton.addActionListener(e -> exportFaultFile());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Add all elements to the window
        add(openButton);
        add(indicatorLabel);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(attributeLabel);
        add(attributeScrollPane);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(exportButton);
    }

    /**
     * Opens a file chooser and allows the user to select an XML file to extract the faults from.
     */
    private void openFaultFile() {
        if (openFileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        setSelectedFile(openFileChooser.getSelectedFile());
        attributePanel.removeAll();
        selectedAttributes.clear();

        // Ensure the input file uses the .xml file extension
        if (!isFileExtensionValid(selectedFile, ".xml")) {
            setSelectedFile(null);
            return;
        }

        // Import attribute fields from the input file
        try {
            alarmList = docBuilder.parse(selectedFile).getElementsByTagName("Alarm");
            NamedNodeMap attributes = alarmList.item(0).getAttributes();

            // Create a checkbox for each attribute of the alarm node
            for (int i = 0; i < attributes.getLength(); i++) {
                String attributeText = attributes.item(i).getNodeName().trim();
                System.out.println("attribute " + i + ": " + attributeText);

                JCheckBox attributeCheckBox = new JCheckBox(attributeText);
                attributeCheckBox.addActionListener(ae -> {
                    if (attributeCheckBox.isSelected()) {
                        selectedAttributes.add(attributeCheckBox.getText());
                    } else {
                        selectedAttributes.remove(attributeCheckBox.getText());
                    }
                });
                attributePanel.add(attributeCheckBox);
            }
            attributePanel.revalidate();
            attributePanel.repaint();
        } catch (SAXException | IOException ex) {
            setSelectedFile(null);

            JOptionPane.showMessageDialog(
                    null,
                    "No \"Alarm\" components present in the XML file.",
                    "Invalid File Contents",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Exports a .csv file to the specified location in the file chooser.
     * Data is parsed from the previously opened file and the selected fault attributes are exported.
     */
    private void exportFaultFile() {
        if (selectedFile == null) return;
        if (exportFileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

        System.out.println("attribute buttons: " + attributePanel.getComponentCount());

        File fileToSave = exportFileChooser.getSelectedFile();

        // Ensure the destination file uses a ".csv" file extension
        if (!isFileExtensionValid(fileToSave, ".csv")) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

            writer.write(getCSVHeader());

            for (int i = 0; i < alarmList.getLength(); i++) {
                writer.write(getCSVRow(alarmList.item(i)));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verifies that the given file matches the specified file extension.
     *
     * @param file The file to be examined.
     * @param extension The file extension that the file must have.
     * @return Whether the file's extension matches the specified extension
     */
    private boolean isFileExtensionValid(File file, String extension) {
        if (selectedFile.getName().toLowerCase().endsWith(extension)) return true;

        JOptionPane.showMessageDialog(
                null,
                "File must end with \"" + extension + "\"",
                "Invalid File Format",
                JOptionPane.ERROR_MESSAGE
        );

        return false;
    }

    /**
     * Constructs the CSV file column headers based on the selected fault fields.
     *
     * @return A comma-separated string containing the fault fields as column headers.
     */
    private String getCSVHeader() {
        return "Message," + String.join(",", selectedAttributes) + "\n";
    }

    /**
     * Constructs the row of data (in CSV format) corresponding to the selected fault fields for the specified fault alarm.
     *
     * @param alarmNode The fault alarm to extract the data from.
     * @return A comma-separated string of the alarm data.
     */
    private String getCSVRow(Node alarmNode) {
        NamedNodeMap attributeList = alarmNode.getAttributes();

        // Search for message child node and set first element to its text contents
        String entries = "";
        for (int i = 0; i < alarmNode.getChildNodes().getLength(); i++) {
            if (alarmNode.getChildNodes().item(i).getNodeName().trim().equals("Message")) {
                entries = alarmNode.getChildNodes().item(i).getTextContent().trim();
                break;
            }
        }

        // Join together all the specified fields, separated by commas
        for (String field : selectedAttributes) {
            entries = String.join(",", entries, attributeList.getNamedItem(field).getTextContent().trim());
        }

        return entries + "\n";
    }

    /**
     * Sets the selectedFile variable to the specified file and updates necessary fields.
     * If the specified files is null, the export button is disabled.
     *
     * @param file The newly opened file, or null if the file is closed.
     */
    public void setSelectedFile(File file) {
        selectedFile = file;

        if (file == null) {
            indicatorLabel.setText("No file selected");
            exportButton.setEnabled(false);
        } else {
            indicatorLabel.setText("Selected File: " + file.getName());
            exportButton.setEnabled(true);
        }
    }
}
