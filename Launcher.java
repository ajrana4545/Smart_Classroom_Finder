import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Launcher extends JFrame {

    private final ImageIcon originalIcon;   
    private final JLabel     imageLabel;    
    private final JButton    findBtn;    

    public Launcher() {
        super("Smart Classroom Finder");

        /* ---------- basic frame ---------- */
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);                  
        setLocationRelativeTo(null);       
        setLayout(new BorderLayout());

        /* ---------- image in the middle ---------- */
        originalIcon = new ImageIcon(
                getClass().getResource("/resources/ppclassroom__image.jpg"));
        imageLabel   = new JLabel();
        
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        /* ---------- blue bar at the bottom ---------- */
        findBtn = new JButton("Find Classroom");
        findBtn.setFont(new Font("Arial", Font.BOLD, 20));
        findBtn.setForeground(Color.WHITE);
        findBtn.setBackground(new Color(0xFF0000));   //red
        findBtn.setFocusPainted(false);
        findBtn.setBorderPainted(false);
        findBtn.setPreferredSize(new Dimension(0, 60));
        findBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Updated ActionListener with role selection dialog
        findBtn.addActionListener(e -> {
            String[] options = {"Teacher", "Student"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Select your role:",
                "Login as",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == JOptionPane.CLOSED_OPTION) return;

            boolean isTeacher = (choice == 0);  // Teacher selected
            new SmartClassroomFinder(isTeacher).setVisible(true);
            dispose();
        });

        add(findBtn, BorderLayout.SOUTH);

        /* ---------- scale image whenever frame resizes ---------- */
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                resizeImage();        
            }
        });

        resizeImage(); 
        setVisible(true);
    }

    private void resizeImage() {
        if (originalIcon == null) return;

        int availableW = getWidth();
        int availableH = getHeight() - findBtn.getPreferredSize().height;

        Image scaled = originalIcon.getImage()
                                   .getScaledInstance(availableW,
                                                      availableH,
                                                      Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
    }


    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(Launcher::new);
    }
}
