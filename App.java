import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class App extends JFrame implements ActionListener {
    private JButton login, cancel;
    private JTextField tfuser;
    private JPasswordField fpass;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/schooldb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public App() {
        // Setting up the JFrame
        setTitle("Login Page");
        setSize(400, 250);
        setLocation(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // Username Label and TextField
        JLabel username = new JLabel("Username:");
        username.setBounds(40, 20, 100, 30);
        add(username);

        tfuser = new JTextField();
        tfuser.setBounds(150, 20, 180, 30);
        add(tfuser);

        // Password Label and PasswordField
        JLabel pass = new JLabel("Password:");
        pass.setBounds(40, 70, 100, 30);
        add(pass);

        fpass = new JPasswordField();
        fpass.setBounds(150, 70, 180, 30);
        add(fpass);

        // Login Button - Adjusted position to be closer to the password field
        login = new JButton("Login");
        login.setBounds(40, 120, 120, 30); // Reduced vertical distance from the password field
        login.setBackground(Color.BLACK);
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Tahoma", Font.BOLD, 16));
        login.addActionListener(this);
        add(login);

        // Add action listener with debug statements for troubleshooting
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = tfuser.getText();
                String enteredPassword = new String(fpass.getPassword());
                System.out.println("Attempting login for user: " + enteredUsername); // Debug statement

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("Driver loaded successfully.");
                } catch (ClassNotFoundException ex) {
                    System.out.println("MySQL driver not found in classpath.");
                    ex.printStackTrace();
                    return; // Exit if the driver is not found
                }

                try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                    System.out.println("Database connected successfully"); // Debug statement
                    String checkUserQuery = "SELECT password FROM login WHERE userName = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(checkUserQuery)) {
                        preparedStatement.setString(1, enteredUsername);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            String storedPassword = resultSet.getString("password");
                            System.out.println("User found, verifying password..."); // Debug statement
                            if (storedPassword.equals(enteredPassword)) {
                                JOptionPane.showMessageDialog(login, "Login successful", "Login",
                                        JOptionPane.INFORMATION_MESSAGE);

                                // Dispose of the login window
                                App.this.dispose(); // Use App.this.dispose() to refer to the outer JFrame
                                System.out.println("Login window closed"); // Debug statement

                                // Open the Main window
                                SwingUtilities.invokeLater(() -> {
                                    Main mainWindow = new Main();
                                    mainWindow.setVisible(true);
                                    System.out.println("Main window opened"); // Debug statement
                                });
                            } else {
                                JOptionPane.showMessageDialog(null, "Incorrect password", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                System.out.println("Incorrect password entered"); // Debug statement
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Username not registered", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.out.println("Username not found in database"); // Debug statement
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("Database connection error: " + ex.getMessage()); // Debug statement
                }
            }
        });

        // Cancel Button - Adjusted position to be closer to the login button
        cancel = new JButton("Cancel");
        cancel.setBounds(210, 120, 120, 30); // Reduced vertical distance from the password field
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.setFont(new Font("Tahoma", Font.BOLD, 16));
        cancel.addActionListener(this);
        add(cancel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            // Optional: Debug or additional actions
        } else if (ae.getSource() == cancel) {
            setVisible(false);
            dispose(); // Close the login window
        }
    }

    public static void main(String[] args) {
        new App();
    }
}
