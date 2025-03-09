package HOSTELMANAGEMENTSYSTEM2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> searchType;
    private Image backgroundImage;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource("/Hostel_Dormitory.jpg")).getImage();

        // Set up custom background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Search components
        searchField = new JTextField(15);
        searchType = new JComboBox<>(new String[] { "Class", "Age" });
        JButton searchButton = new JButton("Search");

        // Table setup
        tableModel = new DefaultTableModel(new String[] { "ID", "Class", "Hostel", "Name", "Age" }, 0);
        table = new JTable(tableModel);
        loadStudentData("", "");

        // Search action
        searchButton.addActionListener(new SearchAction());

        // CRUD buttons
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        // Add action listeners for CRUD buttons
        addButton.addActionListener(new AddAction());
        updateButton.addActionListener(new UpdateAction());
        deleteButton.addActionListener(new DeleteAction());

        // Layout setup
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchType);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel crudPanel = new JPanel();
        crudPanel.add(addButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);

        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        backgroundPanel.add(crudPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadStudentData(String filterValue, String filterType) {
        tableModel.setRowCount(0);
        String query = filterValue.isEmpty() ? "SELECT * FROM students"
                : "SELECT * FROM students WHERE " + filterType + " = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!filterValue.isEmpty()) {
                pstmt.setString(1, filterValue);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("class"),
                        rs.getString("hostel"),
                        rs.getString("name"),
                        rs.getString("age")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/schooldb", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    // Background panel class
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Search action class
    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filterValue = searchField.getText();
            String filterType = searchType.getSelectedItem().toString().toLowerCase();
            loadStudentData(filterValue, filterType);
        }
    }

    // Add action class
    private class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField classField = new JTextField(10);
            JTextField idField = new JTextField(10);
            JTextField nameField = new JTextField(10);
            JTextField ageField = new JTextField(10);
            JTextField hostelField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Class:"));
            panel.add(classField);
            panel.add(new JLabel("ID:"));
            panel.add(idField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(new JLabel("Hostel:"));
            panel.add(hostelField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = getConnection();
                        PreparedStatement stmt = conn.prepareStatement(
                                "INSERT INTO students (class, id, name, age, hostel) VALUES (?, ?, ?, ?, ?)")) {

                    // Handle Class input
                    String className = classField.getText();

                    // Handle ID input
                    String idInput = idField.getText();
                    if (idInput.isEmpty()) {
                        stmt.setNull(2, java.sql.Types.INTEGER); // Set as NULL if no value is provided
                    } else if (idInput.matches("\\d+")) {
                        stmt.setInt(2, Integer.parseInt(idInput)); // Set valid integer
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid ID! Please enter a number or leave it blank.");
                        return; // Exit without executing query
                    }

                    // Handle other fields
                    stmt.setString(1, className);
                    stmt.setString(3, nameField.getText());
                    stmt.setString(4, ageField.getText());
                    stmt.setString(5, hostelField.getText());

                    stmt.executeUpdate();
                    loadStudentData("", "");
                    JOptionPane.showMessageDialog(null, "Student added successfully!");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error adding student: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }
    }

    // Update action class
    private class UpdateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String className = (String) tableModel.getValueAt(selectedRow, 1);
                String name = (String) tableModel.getValueAt(selectedRow, 3);
                String age = (String) tableModel.getValueAt(selectedRow, 4);
                String hostel = (String) tableModel.getValueAt(selectedRow, 2);

                JTextField classField = new JTextField(className);
                JTextField nameField = new JTextField(name);
                JTextField ageField = new JTextField(age);
                JTextField hostelField = new JTextField(hostel);

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Class:"));
                panel.add(classField);
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Age:"));
                panel.add(ageField);
                panel.add(new JLabel("Hostel:"));
                panel.add(hostelField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Update Student", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = getConnection();
                            PreparedStatement stmt = conn.prepareStatement(
                                    "UPDATE students SET class = ?, name = ?, age = ?, hostel = ? WHERE id = ?")) {
                        stmt.setString(1, classField.getText());
                        stmt.setString(2, nameField.getText());
                        stmt.setString(3, ageField.getText());
                        stmt.setString(4, hostelField.getText());
                        stmt.setInt(5, id);
                        stmt.executeUpdate();
                        loadStudentData("", "");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating student: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to update.");
            }
        }
    }

    // Delete action class
    private class DeleteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?",
                        "Delete Student", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = getConnection();
                            PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                        loadStudentData("", "");
                        JOptionPane.showMessageDialog(null, "Student deleted successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting student: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            }
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
