import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

interface CarShowroomLoginInterface {
    void login(String username, String password);
}
class CarShowroomLoginGUI extends JFrame implements CarShowroomLoginInterface {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel contentPanel;
    private Component frame;
    public CarShowroomLoginGUI() {
        setTitle("Car Showroom Management Login");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Car Showroom Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 40));
        add(titleLabel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Georgia", Font.PLAIN, 30));
        formPanel.add(usernameLabel);
        usernameField = new JTextField();
        formPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Georgia", Font.PLAIN, 30));
        formPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        add(formPanel, BorderLayout.CENTER);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 28));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                login(username, password);
            }
        });
        add(loginButton, BorderLayout.SOUTH);
        setVisible(true);
    }
    public void login(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "root", "s1@s2#s3*")) {
               //this table is to save login details of admin user and staff through which they can login
                String createTableSQL = "CREATE TABLE IF NOT EXISTS login (username VARCHAR(40), password VARCHAR(40));";
                connection.createStatement().executeUpdate(createTableSQL);
                //displayEmpTable(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        // Dummy implementation for demonstration
        if (username.equals("admin") && password.equals("adminpassword")) {
            // Admin login
            JOptionPane.showMessageDialog(this, "Admin Login successful");
            showAdminPanel();
        } else if (username.equals("user") && password.equals("userpassword")) {
            // User login
            JOptionPane.showMessageDialog(this, "User Login successful");
            showUserPanel();
            // Add user functionalities here, such as accessing user dashboard
        } else if (username.equals("staff") && password.equals("staffpassword")) {
            // Staff login
            JOptionPane.showMessageDialog(this, "Staff Login successful");
            showStaffPanel(); 
            // Add staff functionalities here, such as accessing staff dashboard
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
        // Clear fields after login attempt
        usernameField.setText("");
        passwordField.setText("");
    }
    private void showAdminPanel() {
        getContentPane().removeAll();
        setTitle("Admin Panel");
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        contentPanel.setLayout(new GridLayout(3, 3));
        add(scrollPane, BorderLayout.CENTER);
        addAddCarButton();
        addDeleteCarButton();
        addUpdateCarButton();
        addManagePaymentsButton();
        addViewStaffButton();
        JButton salesManagementButton = new JButton("Sales Management");
        salesManagementButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSalesManagementOptions();
            }
        });
        contentPanel.add(salesManagementButton);
        revalidate();
    }
    private void showUserPanel() {
        getContentPane().removeAll();
        setTitle("User Panel");
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        contentPanel.setLayout(new GridLayout(3, 3));
        add(scrollPane, BorderLayout.CENTER);
        addUserViewCarButton();
        addUserSearchCarButton();
        addUserBookCarButton();
        addUserCancelBookCarButton();
        addUserInteractWithStaffButton();
        addUserFeedbackButton();
        revalidate();
    }
    private void showStaffPanel() {
        getContentPane().removeAll();
        setTitle("Staff Panel");
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        contentPanel.setLayout(new GridLayout(3, 1));
        add(scrollPane, BorderLayout.CENTER);
        JButton viewDetailsButton = new JButton("View My Details");
        viewDetailsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                viewStaffDetails();
            }
        });
        contentPanel.add(viewDetailsButton);
        JButton interactButton = new JButton("Interact with User");
        interactButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInteractWithUser();
            }
        });
        contentPanel.add(interactButton);
        JButton updateCarButton = new JButton("Update Car");
        updateCarButton.addActionListener(e -> {
            String selectedCar = (String) JOptionPane.showInputDialog(null, "Select car to update:", "Update Car", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Toyota Camry","Audi A4","Mercedes-Benz C-Class","Tesla Model S","Lexus RX","Subaru Outback","Honda Civic", "Ford Mustang", "Chevrolet Malibu", "BMW X5"}, "Toyota Camry");
            if (selectedCar != null) {
                JTextField modelNameField = new JTextField(10);
                JTextField priceField = new JTextField(10);
                int result = JOptionPane.showConfirmDialog(null, new Object[]{"Model Name:", modelNameField, "Price:", priceField}, "Update Car Details", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
                         PreparedStatement stmt = conn.prepareStatement("UPDATE Car SET model = ?, price = ? WHERE carname = ?")) {
                        stmt.setString(1, modelNameField.getText());
                        stmt.setDouble(2, Double.parseDouble(priceField.getText()));
                        stmt.setString(3, selectedCar);
                        int rowsUpdated = stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, rowsUpdated > 0 ? "Car details updated successfully." : "Failed to update car details.", rowsUpdated > 0 ? "Success" : "Error", rowsUpdated > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        contentPanel.add(updateCarButton);
        revalidate();
    }
    private void showSalesManagementOptions() {
        JFrame salesManagementFrame = new JFrame("Sales Management");
        salesManagementFrame.setSize(400, 300);
        salesManagementFrame.setLocationRelativeTo(this);
        JButton viewSalesRecordButton = new JButton("View Sales Record");
        viewSalesRecordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Placeholder for viewing sales records
                showSalesRecords();
            }
        });
         JButton editSalesRecordButton = new JButton("Edit Sales Record");
        editSalesRecordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Placeholder for editing sales records
                updateSalesRecord();
            }
        });
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(viewSalesRecordButton);
       buttonPanel.add(editSalesRecordButton);
        salesManagementFrame.add(buttonPanel);
        salesManagementFrame.setVisible(true);
    }
    public void showSalesRecords() {
        JTextArea salesRecordsArea = new JTextArea(10, 30);
        salesRecordsArea.setEditable(false);
        salesRecordsArea.setSize(500, 500);
        JScrollPane scrollPane = new JScrollPane(salesRecordsArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel panel = new JPanel();
        panel.add(scrollPane);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Open a connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
            // Create a statement
            stmt = conn.createStatement();
            // SQL query to select all records from the sales record table
            String sql = "SELECT * FROM sales_record";
            // Execute the query
            rs = stmt.executeQuery(sql);
            // Create a StringBuilder to store the records
            StringBuilder records = new StringBuilder();
            // Iterate over the result set and append each record to the StringBuilder
            while (rs.next()) {
                Date date = rs.getDate("sale_date");
                String carName = rs.getString("carname");
                String customerName = rs.getString("customer_name");
                double price = rs.getDouble("price");
                // Append the record details to the StringBuilder
                records.append("Date: ").append(date).append(", Car Name: ").append(carName)
                        .append(", Customer Name: ").append(customerName).append(", Price: $").append(price)
                        .append("\n");
            }
            // Set the text of the JTextArea to display the records
            salesRecordsArea.setText(records.toString());
            // Show the panel with sales records
            JOptionPane.showMessageDialog(null, panel, "Sales Records", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQL exception
            JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }}
        private void updateSalesRecord() {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE sales_record SET carname = ?, customer_name = ?, price = ? WHERE carname = ? AND customer_name = ? AND price = ?")) {
                String carName = JOptionPane.showInputDialog("Enter the current car name:");
                String customerName = JOptionPane.showInputDialog("Enter the current customer name:");
                double price = Double.parseDouble(JOptionPane.showInputDialog("Enter the current price:"));
                String newCarName = JOptionPane.showInputDialog("Enter the new car name:");
                String newCustomerName = JOptionPane.showInputDialog("Enter the new customer name:");
                double newPrice = Double.parseDouble(JOptionPane.showInputDialog("Enter the new price:"));
                stmt.setString(1, newCarName);
                stmt.setString(2, newCustomerName);
                stmt.setDouble(3, newPrice);
                stmt.setString(4, carName);
                stmt.setString(5, customerName);
                stmt.setDouble(6, price);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Sales record updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "No sales record found matching the provided details.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
 private void addAddCarButton() {
            JButton addCarButton = new JButton("Add Car");
            addCarButton.addActionListener(e -> {
                JTextField makeField = new JTextField();
                JTextField modelField = new JTextField();
                JTextField yearField = new JTextField();
                JTextField priceField = new JTextField();
                JTextField colourField = new JTextField();
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Name:"));
                panel.add(makeField);
                panel.add(new JLabel("Model:"));
                panel.add(modelField);
                panel.add(new JLabel("Year:"));
                panel.add(yearField);
                panel.add(new JLabel("Price:"));
                panel.add(priceField);
                panel.add(new JLabel("Colour:"));
                panel.add(colourField);
                int result = JOptionPane.showConfirmDialog(null, panel, "Add Car", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String make = makeField.getText(), model = modelField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    String colour= colourField.getText();
                    saveCarDetails(make, model, year, price,colour);
                    JOptionPane.showMessageDialog(null, "Car added successfully:\nName: " + make + "\nModel: " + model + "\nYear: " + year + "\nPrice: $" + price + "\nColour: " + colour);
                }
            });
            contentPanel.add(addCarButton);
        }    
        private void saveCarDetails(String make, String model, int year, double price, String colour) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO car (carname, model, caryear, price, colour) VALUES (?,?,?, ?, ?)")) {
                stmt.setString(1, make); stmt.setString(2, model);
                stmt.setInt(3, year); stmt.setDouble(4, price); stmt.setString(5, colour);
                int rowsAffected = stmt.executeUpdate();
                JOptionPane.showMessageDialog(null,rowsAffected > 0 ? "Car details saved successfully." : "Failed to save car details.", "Result", rowsAffected > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    private void addDeleteCarButton() {
        JButton deleteCarButton = new JButton("Delete Car");
        deleteCarButton.addActionListener(e -> {
            String[] cars = { "Toyota Camry", "Audi A4", "Mercedes-Benz C-Class", "Tesla Model S", "Lexus RX",
                              "Subaru Outback", "Honda Civic", "Ford Mustang", "Chevrolet Malibu", "BMW X5" };
            String selectedCar = (String) JOptionPane.showInputDialog(null, "Select car to delete:", "Delete Car",
                                                                        JOptionPane.QUESTION_MESSAGE, null, cars, cars[0]);
            if (selectedCar != null) {
                deleteCar(selectedCar);
            }
        });
        contentPanel.add(deleteCarButton);
    }
    
    private void deleteCar(String carName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM your_table_name WHERE carname = ?")) {
            stmt.setString(1, carName);
            int rowsAffected = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rowsAffected > 0 ? "Deleted car: " + carName : "Failed to delete car: " + carName);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
    private void addUpdateCarButton() {
        JButton updateCarButton = new JButton("Update Car");
        updateCarButton.addActionListener(e -> {
            String selectedCar = (String) JOptionPane.showInputDialog(null, "Select car to update:", "Update Car", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Toyota Camry","Audi A4","Mercedes-Benz C-Class","Tesla Model S","Lexus RX","Subaru Outback", "Honda Civic", "Ford Mustang", "Chevrolet Malibu", "BMW X5"}, "Toyota Camry");
            if (selectedCar != null) {
                JTextField modelNameField = new JTextField(10);
                JTextField priceField = new JTextField(10);
                int result = JOptionPane.showConfirmDialog(null, new Object[]{"Model Name:", modelNameField, "Price:", priceField}, "Update Car Details", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
                         PreparedStatement stmt = conn.prepareStatement("UPDATE Car SET model = ?, price = ? WHERE carname = ?")) {
                        stmt.setString(1, modelNameField.getText());
                        stmt.setDouble(2, Double.parseDouble(priceField.getText()));
                        stmt.setString(3, selectedCar);
                        int rowsUpdated = stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, rowsUpdated > 0 ? "Car details updated successfully." : "Failed to update car details.", rowsUpdated > 0 ? "Success" : "Error", rowsUpdated > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        contentPanel.add(updateCarButton);
    }
    private void addManagePaymentsButton() {
        JButton managePaymentsButton = new JButton("Manage Payments");
        managePaymentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality to manage payments
                managePayments();
            }
        });
        contentPanel.add(managePaymentsButton);
    }
    private void managePayments() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM payment_record")) {
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Customer_name", "Date", "Amount"}, 0);
            while (rs.next()) {
                String customer_name = rs.getString("customer_name");
                Date payment_date = rs.getDate("payment_date");
                double amount = rs.getDouble("amount");
                tableModel.addRow(new Object[]{customer_name, payment_date, amount});
            }
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);    
            JFrame frame = new JFrame("Payment Records");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);    
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
    private void addViewStaffButton() {
        JButton viewStaffButton = new JButton("View Staff");
        viewStaffButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStaffDetails();
            }
        });
        contentPanel.add(viewStaffButton);
    }    
    private void showStaffDetails() {
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
    
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        panel.add(new JLabel("Contact:"));
        panel.add(contactField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Staff Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String position = positionField.getText();
            String contact = contactField.getText();
            String address = addressField.getText();
    
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO Staff (name, age, position, contact, address) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setString(1, name);
                stmt.setInt(2, age);
                stmt.setString(3, position);
                stmt.setString(4, contact);
                stmt.setString(5, address);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Staff details saved successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to save staff details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
       
    private void addUserViewCarButton() {
        JButton viewCarButton = new JButton("View Cars");
        viewCarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality for users to view cars
                showCarDetailsDialog();
            }
        });
        contentPanel.add(viewCarButton);
    }  
    private void showCarDetailsDialog() {
        // Simulated list of cars, replace with your actual list
        String[] cars = {"Toyota Camry","Audi A4","Mercedes-Benz C-Class","Tesla Model S","Lexus RX","Subaru Outback", "Honda Civic", "Ford Mustang", "Chevrolet Malibu", "BMW X5"};
        JComboBox<String> carList = new JComboBox<>(cars);
        carList.setEditable(false);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Select a car:"));
        panel.add(carList);    
        int result = JOptionPane.showConfirmDialog(null, panel, "View Car Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedCar = (String) carList.getSelectedItem();
            if (selectedCar != null) {
                // Replace this with actual car details retrieval based on the selected car
                String carDetails = getCarDetails(selectedCar);
                JOptionPane.showMessageDialog(null, carDetails, "Car Details", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No car selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Method to get car details (dummy implementation)
    private String getCarDetails(String carName) {
        String details = "No details available.";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM car WHERE carname = ?")) {
            stmt.setString(1, carName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int year = rs.getInt("caryear");
                String color = rs.getString("colour"); // Corrected column name
                double price = rs.getDouble("price");
                details = carName + " details:\nYear: " + year + "\nColor: " + color + "\nPrice: $" + price;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return details;
    }  
    private void addUserSearchCarButton() {
        JButton searchCarButton = new JButton("Search Cars");
        searchCarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create and show a search dialog
                showSearchDialog();
            }
        });
        contentPanel.add(searchCarButton);
    }
    private void showSearchDialog() {
        // Create dialog components
        JLabel makeLabel = new JLabel("Name:");
        JTextField makeField = new JTextField();
        JLabel modelLabel = new JLabel("Model:");
        JTextField modelField = new JTextField();
        JLabel priceLabel = new JLabel("Price Range:");
        JTextField minPriceField = new JTextField();
        JTextField maxPriceField = new JTextField();
        // Create panel and add components
        JPanel searchPanel = new JPanel(new GridLayout(4, 2));
        searchPanel.add(makeLabel);
        searchPanel.add(makeField);
        searchPanel.add(modelLabel);
        searchPanel.add(modelField);
        searchPanel.add(priceLabel);
        searchPanel.add(minPriceField);
        searchPanel.add(maxPriceField);
        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, searchPanel, "Search Cars", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Get search criteria
            String make = makeField.getText();
            String model = modelField.getText();
            double minPrice = Double.parseDouble(minPriceField.getText());
            double maxPrice = Double.parseDouble(maxPriceField.getText());
            // Perform search based on criteria
            performSearch(make, model, minPrice, maxPrice);
        }
    }
    private void performSearch(String make, String model, double minPrice, double maxPrice) {
        // Perform search based on criteria and display results
        String message = "Search Results:\n" +
                         "Name: " + make + "\n" +
                         "Model: " + model + "\n" +
                         "Price Range: $" + minPrice + " - $" + maxPrice;
        JOptionPane.showMessageDialog(null, message);
    }
    private void addUserBookCarButton() {
        JButton bookCarButton = new JButton("Book Car");
        bookCarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show available cars to the user
                String[] availableCars = {"Toyota Camry","Audi A4","Mercedes-Benz C-Class","Tesla Model S","Lexus RX","Subaru Outback", "Honda Civic", "Ford Mustang", "Chevrolet Malibu", "BMW X5"};
                String selectedCar = (String) JOptionPane.showInputDialog(null, "Select a car to book:", "Book Car",
                        JOptionPane.PLAIN_MESSAGE, null, availableCars, availableCars[0]);
                if (selectedCar != null) {
                    // Generate a booking ID
                    int bookingID = generateBookingID();
                    // Update the database or internal data structure to mark the car as booked
                    if (bookCar(selectedCar, bookingID)) {
                        JOptionPane.showMessageDialog(null, "You have booked the car: " + selectedCar + " with Booking ID: " + bookingID);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to book the car. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Booking canceled.");
                }
            }
        });
        contentPanel.add(bookCarButton);
    }
    // Method to book the selected car
    private boolean bookCar(String carName, int bookingID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root")) {
                // Query to update the car table to mark the car as booked
                String bookCarQuery = "UPDATE car SET bookid = ? WHERE carname = ?";
                PreparedStatement bookCarStatement = connection.prepareStatement(bookCarQuery);
                bookCarStatement.setInt(1, bookingID);
                bookCarStatement.setString(2, carName);
                int rowsUpdated = bookCarStatement.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Method to generate a unique booking ID
    private int generateBookingID() {
        int bookingID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root")) {
                // Query to get the maximum booking ID from the database
                String maxBookingIDQuery = "SELECT MAX(bookid) FROM car";
                PreparedStatement maxBookingIDStatement = connection.prepareStatement(maxBookingIDQuery);
                ResultSet resultSet = maxBookingIDStatement.executeQuery();
                if (resultSet.next()) {
                    // Get the maximum booking ID and increment it to generate a new ID
                    bookingID = resultSet.getInt(1) + 1;
                } else {
                    // No existing bookings, start from 1
                    bookingID = 1;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return bookingID;
    }
    private void addUserCancelBookCarButton() {
        JButton cancelBookCarButton = new JButton("Cancel Booked Car");
        cancelBookCarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Fetch booked cars from the database
                ArrayList<Integer> bookedCarIDs = fetchBookedCarIDs();
                if (!bookedCarIDs.isEmpty()) {
                    // Convert ArrayList to array for display in combo box
                    Integer[] bookedCarIDsArray = new Integer[bookedCarIDs.size()];
                    bookedCarIDs.toArray(bookedCarIDsArray);
                    // Display a combo box to select the booked car ID
                    Integer selectedBookingID = (Integer) JOptionPane.showInputDialog(null, "Select a booked car to cancel:", "Cancel Booked Car",
                            JOptionPane.PLAIN_MESSAGE, null, bookedCarIDsArray, bookedCarIDsArray[0]);
                    if (selectedBookingID != null) {
                        // Cancel the selected booked car
                        if (cancelBookedCar(selectedBookingID)) {
                            JOptionPane.showMessageDialog(null, "Booking with ID: " + selectedBookingID + " has been canceled successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to cancel the booking. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Cancellation canceled.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No booked cars found.");
                }
            }
        });
        contentPanel.add(cancelBookCarButton);
    }
    // Method to fetch all booked car IDs from the database
    private ArrayList<Integer> fetchBookedCarIDs() {
        ArrayList<Integer> bookedCarIDs = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root")) {
                // Query to fetch all booked car IDs
                String fetchBookedCarIDsQuery = "SELECT bookid FROM car WHERE bookid IS NOT NULL";
                PreparedStatement fetchBookedCarIDsStatement = connection.prepareStatement(fetchBookedCarIDsQuery);
                ResultSet resultSet = fetchBookedCarIDsStatement.executeQuery();
                while (resultSet.next()) {
                    // Add each booked car ID to the list
                    bookedCarIDs.add(resultSet.getInt("bookid"));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return bookedCarIDs;
    }
    // Method to cancel the booked car with the given booking ID
    private boolean cancelBookedCar(int bookingID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "ad in", "root")) {
                // Query to update the car table to mark the booked car as available (cancel booking)
                String cancelBookedCarQuery = "UPDATE car SET bookid = NULL WHERE bookid = ?";
                PreparedStatement cancelBookedCarStatement = connection.prepareStatement(cancelBookedCarQuery);
                cancelBookedCarStatement.setInt(1, bookingID);
                int rowsUpdated = cancelBookedCarStatement.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void addUserInteractWithStaffButton() {
        JButton interactWithStaffButton = new JButton("Interact with Staff");
        interactWithStaffButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality for users to interact with staff
                interactWithStaff();
            }
        });
        contentPanel.add(interactWithStaffButton);
    }
    private void interactWithStaff() {
        // Placeholder for interacting with staff
        JOptionPane.showMessageDialog(null, "Interacting with staff...");
        // Placeholder for staff interaction response
        String staffResponse = JOptionPane.showInputDialog(null, "Staff: How may I assist you?", "Staff Interaction", JOptionPane.QUESTION_MESSAGE);
        // Placeholder for user response to staff
        if (staffResponse != null) {
            JOptionPane.showMessageDialog(null, "User response: " + staffResponse, "User Interaction", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void addUserFeedbackButton() {
        JButton feedbackButton = new JButton("Give Feedback");
        feedbackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality for users to give feedback
                giveFeedback();
            }
        });
        contentPanel.add(feedbackButton);
    }
    private void giveFeedback() {
        // Get user's feedback
        String feedback = JOptionPane.showInputDialog(null, "Please provide your feedback:", "Feedback", JOptionPane.PLAIN_MESSAGE);
        // You can handle the feedback (e.g., store it in a database)
        if (feedback != null) {
            JOptionPane.showMessageDialog(null, "Thank you for your feedback!", "Feedback Received", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void viewStaffDetails() {
        String staffIdInput = JOptionPane.showInputDialog(frame, "Enter Staff ID:");
        if (staffIdInput == null || staffIdInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No Staff ID entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int staffId;
        try {
            staffId = Integer.parseInt(staffIdInput);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid Staff ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_db", "admin", "root");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Staff WHERE staffid = ?")) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("staffid");
                String name = rs.getString("name");
                String position = rs.getString("position");
                int age = rs.getInt("age");
                String contact = rs.getString("contact");
                String address = rs.getString("address");
                String details = "Staff ID: " + id + "\n" +
                        "Name: " + name + "\n" +
                        "Position: " + position + "\n" +
                        "Age: " + age + "\n" +
                        "Contact: " + contact + "\n" +
                        "Address: " + address;
                JTextArea staffDetailsArea = new JTextArea(details);
                staffDetailsArea.setEditable(false);
                staffDetailsArea.setLineWrap(true);
                staffDetailsArea.setWrapStyleWord(true);
                JOptionPane.showMessageDialog(frame, new JScrollPane(staffDetailsArea), "Staff Details", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "No staff found with the provided ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showInteractWithUser() {
        // Placeholder for interacting with staff
        JOptionPane.showMessageDialog(null, "Interacting with user...");
        // Placeholder for staff interaction response
        String staffResponse = JOptionPane.showInputDialog(null, "User:Can we do online payment?", "User Interaction", JOptionPane.QUESTION_MESSAGE);
        // Placeholder for user response to staff
        if (staffResponse != null) {
            JOptionPane.showMessageDialog(null, "Staff response: " + staffResponse, "User Interaction", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

public class Mainclass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CarShowroomLoginGUI();
            }
        });
    }
}
