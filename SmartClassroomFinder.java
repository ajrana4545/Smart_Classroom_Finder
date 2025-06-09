import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;

public class SmartClassroomFinder extends JFrame {
    private JComboBox<String> dayCombo;
    private JTextField startField, endField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private ClassroomFinder finder;
    private JButton bookButton;
    private Set<String> bookedRooms = new HashSet<>();

    public SmartClassroomFinder(boolean isTeacher) {
        setTitle("Smart Classroom Finder");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // UI components
        dayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        startField = new JTextField("08:00", 5);
        endField = new JTextField("09:00", 5);
        JButton findButton = new JButton("Find Rooms");
        JButton timetableButton = new JButton("View Timetable");
        bookButton = new JButton("Book Selected Room");

        tableModel = new DefaultTableModel(new String[]{"Day", "Time", "Available Room"}, 0);
        resultTable = new JTable(tableModel);

        add(new JLabel("Day:"));
        add(dayCombo);
        add(new JLabel("Start Time (HH:MM):"));
        add(startField);
        add(new JLabel("End Time (HH:MM):"));
        add(endField);
        add(findButton);
        add(timetableButton);
        if (isTeacher) add(bookButton);
        add(new JScrollPane(resultTable));

        finder = new ClassroomFinder();
        try {
            finder.loadFromCSV("C:/Users/AJAY RANA/Desktop/Samart_Classroom_Finder/CSE_Sections_A_to_H_Timetable.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Actions
        findButton.addActionListener(this::onFindRooms);
        timetableButton.addActionListener(e -> {
            TimetableViewer viewer = new TimetableViewer("C:/Users/AJAY RANA/Desktop/Samart_Classroom_Finder/CSE_Sections_A_to_H_Timetable.csv");
            viewer.setVisible(true);
        });

        if (isTeacher) {
            bookButton.addActionListener(e -> {
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String room = (String) resultTable.getValueAt(selectedRow, 2);
                    String day = (String) resultTable.getValueAt(selectedRow, 0);
                    String time = (String) resultTable.getValueAt(selectedRow, 1);

                    String message = JOptionPane.showInputDialog(this, "Enter reason for booking " + room + ":");
                    if (message != null && !message.isEmpty()) {
                        bookedRooms.add(day + "_" + time + "_" + room);
                        JOptionPane.showMessageDialog(this, "Room " + room + " booked successfully.");
                        onFindRooms(null); // Refresh table
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a room to book.");
                }
            });
        }
    }

    private void onFindRooms(ActionEvent event) {
        String day = (String) dayCombo.getSelectedItem();
        String start = startField.getText().trim();
        String end = endField.getText().trim();
        int startMin = ClassroomFinder.timeToMinutes(start);
        int endMin = ClassroomFinder.timeToMinutes(end);

        java.util.List<String> available = finder.getFreeRooms(day, startMin, endMin);
        tableModel.setRowCount(0);
        for (String room : available) {
            String bookingKey = day + "_" + start + " - " + end + "_" + room;
            if (!bookedRooms.contains(bookingKey)) {
                tableModel.addRow(new Object[]{day, start + " - " + end, room});
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Teacher", "Student"};
            int choice = JOptionPane.showOptionDialog(null, "Select your role:", "User Role",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            boolean isTeacher = (choice == 0); // Teacher = 0, Student = 1
            new SmartClassroomFinder(isTeacher).setVisible(true);
        });
    }
}
