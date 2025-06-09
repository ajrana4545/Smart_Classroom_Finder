import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TimetableViewer extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> sectionCombo;
    private JComboBox<String> dayCombo;
    private List<String[]> allRows; // Stores all CSV rows
    private String[] headers;

    public TimetableViewer(String csvFilePath) {
        setTitle("Filtered Timetable Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // UI Panel with Filters
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        sectionCombo = new JComboBox<>();
        dayCombo = new JComboBox<>(new String[]{"All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        JButton filterButton = new JButton("Apply Filter");

        topPanel.add(new JLabel("Section:"));
        topPanel.add(sectionCombo);
        topPanel.add(new JLabel("Day:"));
        topPanel.add(dayCombo);
        topPanel.add(filterButton);

        add(topPanel, BorderLayout.NORTH);

        // Table to show filtered timetable
        table = new JTable();
        model = new DefaultTableModel();
        table.setModel(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load CSV data
        loadCSV(csvFilePath);

        // Action on Filter Button
        filterButton.addActionListener(e -> applyFilter());
    }

    private void loadCSV(String filePath) {
        allRows = new ArrayList<>();
        Set<String> sectionSet = new TreeSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                if (isFirstLine) {
                    headers = values;
                    isFirstLine = false;
                } else {
                    allRows.add(values);
                    // Assuming section name is in the 0th column
                    sectionSet.add(values[0].trim());
                }
            }

            // Fill section dropdown
            sectionCombo.addItem("All Sections");
            for (String section : sectionSet) {
                sectionCombo.addItem(section);
            }

            applyFilter(); // Display all by default

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load CSV: " + e.getMessage());
        }
    }

    private void applyFilter() {
        String selectedSection = (String) sectionCombo.getSelectedItem();
        String selectedDay = (String) dayCombo.getSelectedItem();

        // Determine day column index
        int dayColIndex = -1;
        if (!selectedDay.equals("All Days")) {
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(selectedDay)) {
                    dayColIndex = i;
                    break;
                }
            }
        }

        // Filter rows
        List<String[]> filtered = allRows.stream()
            .filter(row -> selectedSection.equals("All Sections") || row[0].equalsIgnoreCase(selectedSection))
            .collect(Collectors.toList());

        // Clear old table model
        model.setRowCount(0);
        model.setColumnCount(0);

        // Set columns
        if (dayColIndex == -1) {
            model.setColumnIdentifiers(headers);
            for (String[] row : filtered) {
                model.addRow(row);
            }
        } else {
            model.setColumnIdentifiers(new String[]{headers[0], selectedDay});
            for (String[] row : filtered) {
                if (dayColIndex < row.length) {
                    model.addRow(new String[]{row[0], row[dayColIndex]});
                }
            }
        }
    }
}
