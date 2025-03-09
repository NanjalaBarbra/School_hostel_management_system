import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main extends JFrame {
  private JTextField nameField, idField, ageField, classField, contactField;
  private JComboBox<String> hostelComboBox; // ComboBox for hostel selection
  private JTextArea outputArea;
  private JPanel panel;

  public Main() {
    initComponents();
  }

  private void initComponents() {
    setTitle("School Hostel Management System");
    setSize(500, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);

    ImageIcon backgroundIcon = new ImageIcon("src/Hostel_Dormitory.jpg");
    JLabel backgroundLabel = new JLabel(backgroundIcon);
    backgroundLabel.setBounds(0, 0, 500, 700);
    add(backgroundLabel);

    backgroundLabel.setLayout(null);

    panel = new JPanel();
    panel.setLayout(null);
    panel.setOpaque(false);
    panel.setBounds(0, 0, 500, 700);
    backgroundLabel.add(panel);

    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setBounds(50, 10, 100, 25);
    panel.add(nameLabel);

    nameField = new JTextField();
    nameField.setBounds(150, 10, 150, 25);
    panel.add(nameField);

    JLabel idLabel = new JLabel("ID:");
    idLabel.setBounds(50, 50, 100, 25);
    panel.add(idLabel);

    idField = new JTextField();
    idField.setBounds(150, 50, 150, 25);
    panel.add(idField);

    JLabel ageLabel = new JLabel("Age:");
    ageLabel.setBounds(50, 90, 100, 25);
    panel.add(ageLabel);

    ageField = new JTextField();
    ageField.setBounds(150, 90, 150, 25);
    panel.add(ageField);

    JLabel classLabel = new JLabel("Class:");
    classLabel.setBounds(50, 130, 100, 25);
    panel.add(classLabel);

    classField = new JTextField();
    classField.setBounds(150, 130, 150, 25);
    panel.add(classField);

    JLabel contactLabel = new JLabel("Contact:");
    contactLabel.setBounds(50, 170, 100, 25);
    panel.add(contactLabel);

    contactField = new JTextField();
    contactField.setBounds(150, 170, 150, 25);
    panel.add(contactField);

    JLabel hostelLabel = new JLabel("Hostel:");
    hostelLabel.setBounds(50, 210, 100, 25);
    panel.add(hostelLabel);

    String[] hostels = { "Runda", "Muthaiga", "Kilimani", "Eastern B" };
    hostelComboBox = new JComboBox<>(hostels);
    hostelComboBox.setBounds(150, 210, 150, 25);
    panel.add(hostelComboBox);

    JButton addStudentButton = new JButton("Add Student");
    addStudentButton.setBounds(50, 270, 150, 25);
    addStudentButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addStudent();
      }
    });
    panel.add(addStudentButton);

    outputArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(outputArea);
    scrollPane.setBounds(50, 310, 400, 350);
    panel.add(scrollPane);

    setVisible(true);
  }

  private void addStudent() {
    String name = nameField.getText();
    String id = idField.getText();
    String age = ageField.getText();
    String studentClass = classField.getText();
    String contact = contactField.getText();
    String hostel = hostelComboBox.getSelectedItem().toString(); // Get selected hostel

    // Database connection details
    String url = "jdbc:mysql://localhost:3306/schooldb"; // Replace with your database URL
    String user = "root"; 
    String password = ""; 

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      String query = "INSERT INTO `students` (name, id, age, class, contact_info, hostel) VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString(1, name);
      stmt.setString(2, id);
      stmt.setInt(3, Integer.parseInt(age));
      stmt.setString(4, studentClass);
      stmt.setString(5, contact);
      stmt.setString(6, hostel); // Add hostel value

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        JOptionPane.showMessageDialog(this, "Student added successfully!");
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(this, "Error saving student: " + e.getMessage());
      e.printStackTrace();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Please enter a valid number for age.");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Main().setVisible(true));
  }
}
