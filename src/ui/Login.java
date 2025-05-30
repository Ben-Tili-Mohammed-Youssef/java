package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.ClientDAO;
import model.Client;
import ui.Interfaces_Client.AccueilClient;
import ui.Interfaces_Gerant.AccueilGerant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class Login extends JFrame {
    // Main colors
    private static final Color PRIMARY_GREEN = new Color(21, 111, 98);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    // UI Components
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel titleLabel;

    public Login() {
        // Setup FlatLaf Look and Feel
        setupLookAndFeel();

        // Basic frame setup
        setTitle("Café Shop Management - Login");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialize UI
        initializeUI();

        // Add event listeners
        setupEventListeners();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Customize some UI defaults
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        // Create main container with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Create left panel (banner/logo area)
        createLeftPanel();

        // Create right panel (login form)
        createRightPanel();
    }

    private void createLeftPanel() {
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(400, getHeight()));
        leftPanel.setBackground(PRIMARY_GREEN);

        // Add logo/banner to left panel
        JLabel logoLabel = new JLabel(new ImageIcon(getClass().getResource("/ressources/cafe_logo.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add logo if available, otherwise use a text title
        if (logoLabel.getIcon() == null) {
            JLabel bannerTitle = new JLabel("Café Shop");
            bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            bannerTitle.setForeground(WHITE);
            bannerTitle.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel bannerSubtitle = new JLabel("Management System");
            bannerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            bannerSubtitle.setForeground(WHITE);
            bannerSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel bannerTextPanel = new JPanel(new GridLayout(2, 1));
            bannerTextPanel.setOpaque(false);
            bannerTextPanel.add(bannerTitle);
            bannerTextPanel.add(bannerSubtitle);

            leftPanel.add(bannerTextPanel, BorderLayout.CENTER);
        } else {
            leftPanel.add(logoLabel, BorderLayout.CENTER);
        }

        // Decorative elements
        JPanel decorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 15;
                int spacing = 40;
                g2d.setColor(new Color(255, 255, 255, 50));

                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.fillOval(x, y, size, size);
                    }
                }

                g2d.dispose();
            }
        };
        decorPanel.setOpaque(false);
        leftPanel.add(decorPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
    }

    private void createRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setBackground(WHITE);
        rightPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // Title
        titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(PRIMARY_GREEN);

        // Description
        JLabel descLabel = new JLabel("Please enter your credentials to continue");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setForeground(Color.GRAY);

        // Email field with hint
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField("Enter your email");
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setForeground(Color.GRAY);
        emailField.setBackground(LIGHT_GRAY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field with hint
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField("Enter your password");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0); // Initially show the hint text
        passwordField.setBackground(LIGHT_GRAY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        loginButton = new JButton("SIGN IN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setBackground(PRIMARY_GREEN);
        loginButton.setForeground(WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Error label
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to the right panel with spacing
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(descLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(emailField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(errorLabel);

        mainPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        // Add focus listeners for placeholder text behavior
        setupTextFieldPlaceholder(emailField, "Enter your email");

        // Special handling for password field to toggle echo char
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Enter your password")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText("Enter your password");
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        // Add login button listener
        loginButton.addActionListener(e -> performLogin());
    }

    private void setupTextFieldPlaceholder(JTextField textField, String placeholder) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Check if fields contain placeholder text
        if (email.equals("Enter your email") || password.equals("Enter your password")) {
            showError("Please enter your credentials");
            return;
        }

        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        // Perform login
        if (email.equals("admin") && password.equals("admin")) {
            // Admin login
            showSuccess("Admin login successful!");
            new AccueilGerant().setVisible(true);
            dispose();
        } else {
            // Client login
            ClientDAO clientDAO = new ClientDAO();
            Client client = clientDAO.verifierConnexion(email, password);

            if (client != null) {
                showSuccess("Login successful!");
                new AccueilClient(client.getId()).setVisible(true);
                dispose();
            } else {
                showError("Invalid email or password");
                // Shake animation for invalid credentials
                shakeComponent(mainPanel);
            }
        }
    }

    private void showError(String message) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(message);
    }

    private void showSuccess(String message) {
        errorLabel.setForeground(new Color(0, 128, 0));
        errorLabel.setText(message);
    }

    private void shakeComponent(Component component) {
        final int originalX = component.getLocation().x;
        final int originalY = component.getLocation().y;

        Timer timer = new Timer(20, null);
        final int[] oscillations = {-5, 5, -5, 5, -3, 3, -2, 2, -1, 1, 0};
        final int[] currentOscillation = {0};

        timer.addActionListener(e -> {
            if (currentOscillation[0] < oscillations.length) {
                component.setLocation(originalX + oscillations[currentOscillation[0]], originalY);
                currentOscillation[0]++;
            } else {
                timer.stop();
                component.setLocation(originalX, originalY);
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        // Make sure to add FlatLaf to your dependencies
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}