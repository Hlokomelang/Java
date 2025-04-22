package com.example.demo2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.print.PrinterJob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleRentalSystem extends Application {

    private String currentUserRole = "Employee";
    private Map<String, String> users = new HashMap<>(); // Map for storing users (username, password)
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Check if the username and password are valid
            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUserRole = username.equals("admin") ? "Admin" : "Employee"; // Set role based on username
                showMainApp(primaryStage);
            } else {
                messageLabel.setText("Invalid credentials!");
            }
        });

        registerButton.setOnAction(e -> showRegistrationScreen(primaryStage));

        VBox loginLayout = new VBox(10, userLabel, usernameField, passLabel, passwordField, loginButton, registerButton, messageLabel);
        loginLayout.setPadding(new Insets(20));
        Scene scene = new Scene(loginLayout, 300, 300);
        primaryStage.setTitle("Vehicle Rental Login");

        String vboxCss = "-fx-background-color: #ffccff; -fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #c71585;";
        loginLayout.setStyle(vboxCss);
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6699, #ff3366); -fx-text-fill: white;");
        registerButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6699, #ff3366); -fx-text-fill: white;");
        usernameField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");
        passwordField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");

        scene.setRoot(loginLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRegistrationScreen(Stage primaryStage) {
        Stage registrationStage = new Stage();
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label messageLabel = new Label();

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("All fields must be filled.");
            } else if (users.containsKey(username)) {
                messageLabel.setText("Username already exists. Choose another.");
            } else {
                users.put(username, password); // Store the new user in the map
                showAlert("Registration Success", "User registered successfully! You can now log in.");
                registrationStage.close();
            }
        });

        VBox registrationLayout = new VBox(10, userLabel, usernameField, passLabel, passwordField, registerButton, messageLabel);
        registrationLayout.setPadding(new Insets(20));
        Scene scene = new Scene(registrationLayout, 300, 250);
        registrationStage.setTitle("Vehicle Rental Registration");

        String vboxCss = "-fx-background-color: #ffccff; -fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #c71585;";
        registrationLayout.setStyle(vboxCss);
        registerButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6699, #ff3366); -fx-text-fill: white;");
        usernameField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");
        passwordField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");

        registrationStage.setScene(scene);
        registrationStage.show();
    }

    private void showMainApp(Stage stage) {
        TabPane tabPane = new TabPane();
        if (currentUserRole.equals("Admin")) {
            tabPane.getTabs().addAll(createVehicleTab(), createCustomerTab(), createBookingTab(), createBillingTab(), createReportsTab());
        } else {
            tabPane.getTabs().addAll(createBookingTab(), createBillingTab());
        }

        VBox root = new VBox(new Label("Logged in as: " + currentUserRole), tabPane);
        Button logoutButton = addLogoutButton(root, stage);
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Vehicle Rental System");

        String vboxCss = "-fx-background-color: #e0e0e0; -fx-font-family: 'Arial'; -fx-text-fill: #4a148c;";
        root.setStyle(vboxCss);
        tabPane.setStyle("-fx-background-color: white;");
        logoutButton.setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

        scene.setRoot(root);
        stage.setScene(scene);
        stage.show();
    }

    private Button addLogoutButton(VBox root, Stage stage) {
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            currentUserRole = "Employee"; // Reset to default
            showLoginScreen(stage);
        });
        root.getChildren().add(logoutButton);
        return logoutButton;
    }

    private Tab createVehicleTab() {
        TableView<Vehicle> table = new TableView<>();
        setupVehicleTableColumns(table);

        vehicles.add(new Vehicle("V001", "Toyota", "Corolla", "Car", 40, true));
        vehicles.add(new Vehicle("V002", "Honda", "CBR", "Bike", 20, true));
        vehicles.add(new Vehicle("V003", "Ford", "Mustang", "Car", 100, false));
        vehicles.add(new Vehicle("V004", "Chevrolet", "Camaro", "Car", 90, true));
        vehicles.add(new Vehicle("V005", "Nissan", "370Z", "Car", 60, false));
        vehicles.add(new Vehicle("V006", "BMW", "X5", "SUV", 80, true));

        table.getItems().addAll(vehicles);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Brand or Model");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterVehicleTable(table, newValue);
        });

        HBox buttons = new HBox(10, createVehicleManagementButtons(table));
        VBox layout = new VBox(10, searchField, new Label("Vehicle Management"), table, buttons);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #FFA500;");

        searchField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");

        return new Tab("Vehicles", layout);
    }

    private HBox createVehicleManagementButtons(TableView<Vehicle> table) {
        Button addButton = new Button("Add Vehicle");
        addButton.setOnAction(e -> addVehicle(table));
        Button updateButton = new Button("Update Vehicle");
        updateButton.setOnAction(e -> updateVehicle(table));
        Button deleteButton = new Button("Delete Vehicle");
        deleteButton.setOnAction(e -> deleteVehicle(table));
        Button exportButton = new Button("Export to CSV");
        exportButton.setOnAction(e -> exportVehiclesToCSV());

        String buttonCss = "-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;";
        addButton.setStyle(buttonCss);
        updateButton.setStyle(buttonCss);
        deleteButton.setStyle(buttonCss);
        exportButton.setStyle(buttonCss);

        return new HBox(10, addButton, updateButton, deleteButton, exportButton);
    }

    private void setupVehicleTableColumns(TableView<Vehicle> table) {
        table.getColumns().addAll(
                createColumn("ID", "id", 50),
                createColumn("Brand", "brand", 100),
                createColumn("Model", "model", 100),
                createColumn("Category", "category", 100),
                createColumn("Price/Day", "pricePerDay", 100),
                createColumn("Available", "available", 100)
        );
    }

    private void filterVehicleTable(TableView<Vehicle> table, String query) {
        table.getItems().clear();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                    vehicle.getModel().toLowerCase().contains(query.toLowerCase())) {
                table.getItems().add(vehicle);
            }
        }
    }

    private void updateVehicle(TableView<Vehicle> table) {
        Vehicle selectedVehicle = table.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Update Vehicle");

            VBox vbox = new VBox(10);
            TextField idField = new TextField(selectedVehicle.getId());
            TextField brandField = new TextField(selectedVehicle.getBrand());
            TextField modelField = new TextField(selectedVehicle.getModel());
            TextField categoryField = new TextField(selectedVehicle.getCategory());
            TextField priceField = new TextField(String.valueOf(selectedVehicle.getPricePerDay()));
            CheckBox availableCheckBox = new CheckBox("Available");
            availableCheckBox.setSelected(selectedVehicle.isAvailable());

            String textFieldCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
            idField.setStyle(textFieldCss);
            brandField.setStyle(textFieldCss);
            modelField.setStyle(textFieldCss);
            categoryField.setStyle(textFieldCss);
            priceField.setStyle(textFieldCss);

            vbox.getChildren().addAll(idField, brandField, modelField, categoryField, priceField, availableCheckBox);
            dialog.getDialogPane().setContent(vbox);

            ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

            dialog.getDialogPane().lookupButton(saveButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");
            dialog.getDialogPane().lookupButton(cancelButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

            dialog.setResultConverter(button -> {
                if (button == saveButton) {
                    try {
                        if (idField.getText().isEmpty() || brandField.getText().isEmpty() || modelField.getText().isEmpty() ||
                                categoryField.getText().isEmpty() || priceField.getText().isEmpty()) {
                            showAlert("Input Error", "All fields must be filled.");
                            return null;
                        }
                        double price = Double.parseDouble(priceField.getText());
                        if (price < 0) {
                            showAlert("Input Error", "Price cannot be negative.");
                            return null;
                        }
                        selectedVehicle.setId(idField.getText());
                        selectedVehicle.setBrand(brandField.getText());
                        selectedVehicle.setModel(modelField.getText());
                        selectedVehicle.setCategory(categoryField.getText());
                        selectedVehicle.setPricePerDay(price);
                        selectedVehicle.setAvailable(availableCheckBox.isSelected());
                        table.refresh();
                    } catch (NumberFormatException e) {
                        showAlert("Input Error", "Price must be a valid number.");
                    }
                }
                return null;
            });

            dialog.showAndWait();
        } else {
            showAlert("No Vehicle Selected", "Please select a vehicle to update.");
        }
    }

    private void addVehicle(TableView<Vehicle> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Vehicle");

        VBox vbox = new VBox(10);
        TextField idField = new TextField();
        TextField brandField = new TextField();
        TextField modelField = new TextField();
        TextField categoryField = new TextField();
        TextField priceField = new TextField();
        CheckBox availableCheckBox = new CheckBox("Available");

        idField.setPromptText("Enter vehicle ID");
        brandField.setPromptText("Enter vehicle brand");
        modelField.setPromptText("Enter vehicle model");
        categoryField.setPromptText("Enter vehicle category");
        priceField.setPromptText("Enter price per day");

        String textFieldCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
        idField.setStyle(textFieldCss);
        brandField.setStyle(textFieldCss);
        modelField.setStyle(textFieldCss);
        categoryField.setStyle(textFieldCss);
        priceField.setStyle(textFieldCss);

        vbox.getChildren().addAll(idField, brandField, modelField, categoryField, priceField, availableCheckBox);
        dialog.getDialogPane().setContent(vbox);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        dialog.getDialogPane().lookupButton(saveButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(cancelButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                try {
                    if (idField.getText().isEmpty() || brandField.getText().isEmpty() || modelField.getText().isEmpty() ||
                            categoryField.getText().isEmpty() || priceField.getText().isEmpty()) {
                        showAlert("Input Error", "All fields must be filled.");
                        return null;
                    }
                    double price = Double.parseDouble(priceField.getText());
                    if (price < 0) {
                        showAlert("Input Error", "Price cannot be negative.");
                        return null;
                    }
                    Vehicle newVehicle = new Vehicle(idField.getText(), brandField.getText(), modelField.getText(),
                            categoryField.getText(), price, availableCheckBox.isSelected());
                    vehicles.add(newVehicle);
                    table.getItems().add(newVehicle);
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Price must be a valid number.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteVehicle(TableView<Vehicle> table) {
        Vehicle selectedVehicle = table.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            vehicles.remove(selectedVehicle);
            table.getItems().remove(selectedVehicle);
        } else {
            showAlert("No Vehicle Selected", "Please select a vehicle to delete.");
        }
    }

    private void exportVehiclesToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Vehicles CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("ID,Brand,Model,Category,Price per Day,Available\n");
                for (Vehicle vehicle : vehicles) {
                    writer.append(String.format("%s,%s,%s,%s,%.2f,%b\n",
                            vehicle.getId(),
                            vehicle.getBrand(),
                            vehicle.getModel(),
                            vehicle.getCategory(),
                            vehicle.getPricePerDay(),
                            vehicle.isAvailable()));
                }
                showAlert("Export Successful", "Vehicles data exported to CSV successfully.");
            } catch (IOException e) {
                showAlert("Export Failed", "An error occurred while exporting to CSV.");
            }
        }
    }

    private Tab createCustomerTab() {
        TableView<Customer> table = new TableView<>();
        setupCustomerTableColumns(table);

        customers.add(new Customer("Alice", "123-456", "DL123"));
        customers.add(new Customer("Bob", "789-000", "DL456"));
        customers.add(new Customer("Charlie", "234-567", "DL789"));
        customers.add(new Customer("David", "345-678", "DL890"));
        customers.add(new Customer("Eva", "456-789", "DL901"));

        table.getItems().addAll(customers);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Name");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomerTable(table, newValue);
        });

        HBox buttons = new HBox(10, createCustomerManagementButtons(table));
        VBox layout = new VBox(10, searchField, new Label("Customer Management"), table, buttons);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #FFA500;");

        searchField.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;");

        return new Tab("Customers", layout);
    }

    private HBox createCustomerManagementButtons(TableView<Customer> table) {
        Button addButton = new Button("Add Customer");
        addButton.setOnAction(e -> addCustomer(table));
        Button updateButton = new Button("Update Customer");
        updateButton.setOnAction(e -> updateCustomer(table));
        Button deleteButton = new Button("Delete Customer");
        deleteButton.setOnAction(e -> deleteCustomer(table));

        String buttonCss = "-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;";
        addButton.setStyle(buttonCss);
        updateButton.setStyle(buttonCss);
        deleteButton.setStyle(buttonCss);

        return new HBox(10, addButton, updateButton, deleteButton);
    }

    private void setupCustomerTableColumns(TableView<Customer> table) {
        table.getColumns().addAll(
                createColumn("Name", "name", 150),
                createColumn("Contact", "contact", 150),
                createColumn("License", "license", 150)
        );
    }

    private void filterCustomerTable(TableView<Customer> table, String query) {
        table.getItems().clear();
        for (Customer customer : customers) {
            if (customer.getName().toLowerCase().contains(query.toLowerCase())) {
                table.getItems().add(customer);
            }
        }
    }

    private void addCustomer(TableView<Customer> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");

        VBox vbox = new VBox(10);
        TextField nameField = new TextField();
        nameField.setPromptText("Enter customer name");

        TextField contactField = new TextField();
        contactField.setPromptText("Enter contact number");

        TextField licenseField = new TextField();
        licenseField.setPromptText("Enter license number");

        String textFieldCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
        nameField.setStyle(textFieldCss);
        contactField.setStyle(textFieldCss);
        licenseField.setStyle(textFieldCss);

        vbox.getChildren().addAll(nameField, contactField, licenseField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        dialog.getDialogPane().lookupButton(saveButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(cancelButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                if (nameField.getText().isEmpty() || contactField.getText().isEmpty() || licenseField.getText().isEmpty()) {
                    showAlert("Input Error", "All fields must be filled.");
                    return null;
                }
                Customer newCustomer = new Customer(nameField.getText(), contactField.getText(), licenseField.getText());
                customers.add(newCustomer);
                table.getItems().add(newCustomer);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void updateCustomer(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Update Customer");

            VBox vbox = new VBox(10);
            TextField nameField = new TextField(selectedCustomer.getName());
            TextField contactField = new TextField(selectedCustomer.getContact());
            TextField licenseField = new TextField(selectedCustomer.getLicense());

            String textFieldCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
            nameField.setStyle(textFieldCss);
            contactField.setStyle(textFieldCss);
            licenseField.setStyle(textFieldCss);

            vbox.getChildren().addAll(nameField, contactField, licenseField);
            dialog.getDialogPane().setContent(vbox);

            ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

            dialog.getDialogPane().lookupButton(saveButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");
            dialog.getDialogPane().lookupButton(cancelButton).setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

            dialog.setResultConverter(button -> {
                if (button == saveButton) {
                    if (nameField.getText().isEmpty() || contactField.getText().isEmpty() || licenseField.getText().isEmpty()) {
                        showAlert("Input Error", "All fields must be filled.");
                        return null;
                    }
                    selectedCustomer.setName(nameField.getText());
                    selectedCustomer.setContact(contactField.getText());
                    selectedCustomer.setLicense(licenseField.getText());
                    table.refresh();
                }
                return null;
            });

            dialog.showAndWait();
        } else {
            showAlert("No Customer Selected", "Please select a customer to update.");
        }
    }

    private void deleteCustomer(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            customers.remove(selectedCustomer);
            table.getItems().remove(selectedCustomer);
        } else {
            showAlert("No Customer Selected", "Please select a customer to delete.");
        }
    }

    private Tab createBookingTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label bookingLabel = new Label("Booking Management");

        ComboBox<Vehicle> vehicleSelect = new ComboBox<>();
        vehicleSelect.getItems().addAll(vehicles);

        ComboBox<Customer> customerSelect = new ComboBox<>();
        customerSelect.getItems().addAll(customers);

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        Button bookBtn = new Button("Book");
        Label message = new Label();

        String comboBoxCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
        vehicleSelect.setStyle(comboBoxCss);
        customerSelect.setStyle(comboBoxCss);
        startDate.setStyle(comboBoxCss);
        endDate.setStyle(comboBoxCss);

        bookBtn.setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

        bookBtn.setOnAction(e -> {
            Vehicle selectedVehicle = vehicleSelect.getValue();
            Customer selectedCustomer = customerSelect.getValue();
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            if (selectedVehicle == null || selectedCustomer == null || start == null || end == null) {
                message.setText("Please fill all fields.");
                return;
            }
            if (end.isBefore(start)) {
                message.setText("End date cannot be before start date.");
                return;
            }
            if (selectedVehicle.isAvailable()) {
                Booking newBooking = new Booking(selectedVehicle.getId(), selectedCustomer.getName(), start, end);
                bookings.add(newBooking);
                selectedVehicle.setAvailable(false);
                message.setText("Booking successful!");
            } else {
                message.setText("Vehicle is not available for booking.");
            }
        });

        layout.getChildren().addAll(bookingLabel, new Label("Select Vehicle: "), vehicleSelect,
                new Label("Select Customer: "), customerSelect,
                new Label("Start Date: "), startDate,
                new Label("End Date: "), endDate,
                bookBtn, message);
        layout.setStyle("-fx-background-color: #FFA500;");

        return new Tab("Bookings", layout);
    }

    private Tab createBillingTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label billingLabel = new Label("Billing Information");

        ComboBox<Customer> customerSelect = new ComboBox<>();
        customerSelect.getItems().addAll(customers);

        ComboBox<Vehicle> vehicleSelect = new ComboBox<>();
        vehicleSelect.getItems().addAll(vehicles);

        TextField rentalDaysField = new TextField();
        rentalDaysField.setPromptText("Enter number of rental days");

        TextField additionalServicesField = new TextField();
        additionalServicesField.setPromptText("Enter additional services fee");

        TextField lateFeesField = new TextField();
        lateFeesField.setPromptText("Enter late fees");

        ComboBox<String> paymentMethodSelect = new ComboBox<>();
        paymentMethodSelect.getItems().addAll("Cash", "Credit Card", "Online");

        Button generateInvoiceButton = new Button("Generate Invoice");
        Label messageLabel = new Label();

        String fieldCss = "-fx-background-color: #f7f7f7; -fx-border-color: #ff6699;";
        customerSelect.setStyle(fieldCss);
        vehicleSelect.setStyle(fieldCss);
        rentalDaysField.setStyle(fieldCss);
        additionalServicesField.setStyle(fieldCss);
        lateFeesField.setStyle(fieldCss);
        paymentMethodSelect.setStyle(fieldCss);

        generateInvoiceButton.setStyle("-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;");

        generateInvoiceButton.setOnAction(e -> {
            Customer selectedCustomer = customerSelect.getValue();
            Vehicle selectedVehicle = vehicleSelect.getValue();
            String paymentMethod = paymentMethodSelect.getValue();

            if (selectedCustomer == null || selectedVehicle == null || rentalDaysField.getText().isEmpty() || paymentMethod == null) {
                messageLabel.setText("Please fill all required fields.");
                return;
            }

            try {
                int rentalDays = Integer.parseInt(rentalDaysField.getText());
                if (rentalDays <= 0) {
                    messageLabel.setText("Rental days must be a positive number.");
                    return;
                }
                double additionalServices = additionalServicesField.getText().isEmpty() ? 0 : Double.parseDouble(additionalServicesField.getText());
                double lateFees = lateFeesField.getText().isEmpty() ? 0 : Double.parseDouble(lateFeesField.getText());

                if (additionalServices < 0 || lateFees < 0) {
                    messageLabel.setText("Fees cannot be negative.");
                    return;
                }

                double rentalCost = selectedVehicle.getPricePerDay() * rentalDays;
                double totalAmount = rentalCost + additionalServices + lateFees;

                String invoice = String.format(
                        "===== Vehicle Rental Invoice =====\n" +
                                "Date: %s\n" +
                                "Customer: %s\n" +
                                "Contact: %s\n" +
                                "Vehicle: %s (%s)\n" +
                                "Rental Days: %d\n" +
                                "Rental Cost: $%.2f\n" +
                                "Additional Services: $%.2f\n" +
                                "Late Fees: $%.2f\n" +
                                "Total Amount: $%.2f\n" +
                                "Payment Method: %s\n" +
                                "================================",
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        selectedCustomer.getName(),
                        selectedCustomer.getContact(),
                        selectedVehicle.getBrand() + " " + selectedVehicle.getModel(),
                        selectedVehicle.getCategory(),
                        rentalDays,
                        rentalCost,
                        additionalServices,
                        lateFees,
                        totalAmount,
                        paymentMethod);

                Payment payment = new Payment(selectedCustomer.getName(), totalAmount, LocalDate.now());
                payments.add(payment);
                printInvoice(invoice);
                messageLabel.setText("Invoice generated successfully! Check print dialog for details.");
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter valid numbers for rental days and fees.");
            }
        });

        layout.getChildren().addAll(billingLabel, new Label("Select Customer: "), customerSelect,
                new Label("Select Vehicle: "), vehicleSelect, rentalDaysField, additionalServicesField,
                lateFeesField, new Label("Select Payment Method: "), paymentMethodSelect,
                generateInvoiceButton, messageLabel);
        layout.setStyle("-fx-background-color: #FFA500;");

        return new Tab("Billing", layout);
    }

    private Tab createReportsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        layout.getChildren().add(new Label("Reports"));

        Button availableVehiclesButton = new Button("Available Vehicles Report");
        availableVehiclesButton.setOnAction(e -> showAvailableVehiclesReport());
        Button rentalHistoryButton = new Button("Customer Rental History");
        rentalHistoryButton.setOnAction(e -> showCustomerRentalHistory());
        Button revenueReportButton = new Button("Revenue Report");
        revenueReportButton.setOnAction(e -> showRevenueReport());
        Button exportReportsButton = new Button("Export Reports");
        exportReportsButton.setOnAction(e -> exportReports());

        String buttonCss = "-fx-background-color: linear-gradient(to right, #3333cc, #3d5afe); -fx-text-fill: white;";
        availableVehiclesButton.setStyle(buttonCss);
        rentalHistoryButton.setStyle(buttonCss);
        revenueReportButton.setStyle(buttonCss);
        exportReportsButton.setStyle(buttonCss);

        HBox reportButtons = new HBox(10, availableVehiclesButton, rentalHistoryButton, revenueReportButton, exportReportsButton);
        layout.getChildren().add(reportButtons);

        layout.getChildren().add(createCharts());
        layout.setStyle("-fx-background-color: #FFA500;");

        return new Tab("Reports", layout);
    }

    private void showAvailableVehiclesReport() {
        StringBuilder report = new StringBuilder("Available Vehicles:\n");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                report.append(String.format("ID: %s, Brand: %s, Model: %s, Category: %s, Price/Day: %.2f\n",
                        vehicle.getId(), vehicle.getBrand(), vehicle.getModel(), vehicle.getCategory(), vehicle.getPricePerDay()));
            }
        }
        showAlert("Available Vehicles Report", report.toString());
    }

    private void showCustomerRentalHistory() {
        StringBuilder report = new StringBuilder("Customer Rental History:\n");
        for (Customer customer : customers) {
            report.append(customer.getName()).append(":\n");
            for (Booking booking : bookings) {
                if (booking.getCustomer().equals(customer.getName())) {
                    report.append(String.format(" - Vehicle: %s, Start: %s, End: %s\n",
                            booking.getVehicle(), booking.getStartDate(), booking.getEndDate()));
                }
            }
        }
        showAlert("Customer Rental History", report.toString());
    }

    private void showRevenueReport() {
        double totalRevenue = 0;
        StringBuilder report = new StringBuilder("Revenue Report:\n");

        for (Booking booking : bookings) {
            Vehicle vehicle = vehicles.stream().filter(v -> v.getId().equals(booking.getVehicle())).findFirst().orElse(null);
            if (vehicle != null) {
                double rentalDays = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
                double revenue = rentalDays * vehicle.getPricePerDay();
                totalRevenue += revenue;
                report.append(String.format("Customer: %s, Vehicle: %s, Days: %.0f, Revenue: $%.2f\n",
                        booking.getCustomer(), booking.getVehicle(), rentalDays, revenue));
            }
        }
        report.append(String.format("Total Revenue: $%.2f", totalRevenue));
        showAlert("Revenue Report", report.toString());
    }

    private void exportReports() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                exportReportsToCSV(file);
                showAlert("Export Successful", "Reports exported successfully to " + file.getName());
            } catch (Exception e) {
                showAlert("Export Failed", "An error occurred while exporting: " + e.getMessage());
            }
        }
    }

    private void exportReportsToCSV(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Available Vehicles\n");
            writer.append("ID,Brand,Model,Category,Price per Day,Available\n");
            for (Vehicle vehicle : vehicles) {
                if (vehicle.isAvailable()) {
                    writer.append(String.format("%s,%s,%s,%s,%.2f,%b\n",
                            vehicle.getId(), vehicle.getBrand(), vehicle.getModel(), vehicle.getCategory(),
                            vehicle.getPricePerDay(), vehicle.isAvailable()));
                }
            }
            writer.append("\n");

            writer.append("Customer Rental History\n");
            writer.append("Customer,Vehicle,Start Date,End Date\n");
            for (Customer customer : customers) {
                for (Booking booking : bookings) {
                    if (booking.getCustomer().equals(customer.getName())) {
                        writer.append(String.format("%s,%s,%s,%s\n",
                                customer.getName(), booking.getVehicle(), booking.getStartDate(), booking.getEndDate()));
                    }
                }
            }
            writer.append("\n");

            writer.append("Revenue Report\n");
            writer.append("Customer,Vehicle,Days,Revenue\n");
            double totalRevenue = 0;
            for (Booking booking : bookings) {
                Vehicle vehicle = vehicles.stream().filter(v -> v.getId().equals(booking.getVehicle())).findFirst().orElse(null);
                if (vehicle != null) {
                    double rentalDays = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
                    double revenue = rentalDays * vehicle.getPricePerDay();
                    totalRevenue += revenue;
                    writer.append(String.format("%s,%s,%.0f,%.2f\n",
                            booking.getCustomer(), booking.getVehicle(), rentalDays, revenue));
                }
            }
            writer.append(String.format("Total Revenue,%.2f\n", totalRevenue));
        }
    }

    private VBox createCharts() {
        VBox chartLayout = new VBox(10);

        PieChart vehicleTypeChart = new PieChart();
        vehicleTypeChart.getData().addAll(
                new PieChart.Data("Cars", countByCategory("Car")),
                new PieChart.Data("Bikes", countByCategory("Bike")),
                new PieChart.Data("Trucks", countByCategory("Truck")),
                new PieChart.Data("Vans", countByCategory("Van"))
        );

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> vehiclePriceChart = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Vehicle");
        yAxis.setLabel("Price per Day");
        for (Vehicle vehicle : vehicles) {
            vehiclePriceChart.getData().add(new XYChart.Series<>(vehicle.getBrand() + " " + vehicle.getModel(),
                    FXCollections.observableArrayList(new XYChart.Data<>(vehicle.getBrand() + " " + vehicle.getModel(), vehicle.getPricePerDay()))));
        }

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        LineChart<String, Number> vehicleAvailabilityChart = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Vehicle");
        yAxis.setLabel("Availability (0 = Not Available, 1 = Available)");
        for (Vehicle vehicle : vehicles) {
            vehicleAvailabilityChart.getData().add(new XYChart.Series<>(vehicle.getBrand() + " " + vehicle.getModel(),
                    FXCollections.observableArrayList(new XYChart.Data<>(vehicle.getBrand() + " " + vehicle.getModel(), vehicle.isAvailable() ? 1 : 0))));
        }

        chartLayout.getChildren().addAll(vehicleTypeChart, vehiclePriceChart, vehicleAvailabilityChart);
        return chartLayout;
    }

    private int countByCategory(String category) {
        int count = 0;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCategory().equals(category) && vehicle.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    private <T> TableColumn<T, String> createColumn(String name, String property, int width) {
        TableColumn<T, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(width);
        return column;
    }

    private void printInvoice(String invoiceContent) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            TextArea textArea = new TextArea(invoiceContent);
            textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            textArea.setWrapText(true);
            textArea.setEditable(false);

            job.printPage(textArea);
            job.endJob();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Vehicle {
        private String id;
        private String brand;
        private String model;
        private String category;
        private double pricePerDay;
        private boolean available;

        public Vehicle(String id, String brand, String model, String category, double pricePerDay, boolean available) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.category = category;
            this.pricePerDay = pricePerDay;
            this.available = available;
        }

        public String getId() {
            return id;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        public String getCategory() {
            return category;
        }

        public double getPricePerDay() {
            return pricePerDay;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setPricePerDay(double pricePerDay) {
            this.pricePerDay = pricePerDay;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        @Override
        public String toString() {
            return brand + " " + model;
        }
    }

    public static class Customer {
        private String name;
        private String contact;
        private String license;

        public Customer(String name, String contact, String license) {
            this.name = name;
            this.contact = contact;
            this.license = license;
        }

        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }

        public String getLicense() {
            return license;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Booking {
        private String vehicle;
        private String customer;
        private LocalDate startDate;
        private LocalDate endDate;

        public Booking(String vehicle, String customer, LocalDate startDate, LocalDate endDate) {
            this.vehicle = vehicle;
            this.customer = customer;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getVehicle() {
            return vehicle;
        }

        public String getCustomer() {
            return customer;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }
    }

    public static class Payment {
        private String customerName;
        private double amount;
        private LocalDate paymentDate;

        public Payment(String customerName, double amount, LocalDate paymentDate) {
            this.customerName = customerName;
            this.amount = amount;
            this.paymentDate = paymentDate;
        }

        public String getCustomerName() {
            return customerName;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDate getPaymentDate() {
            return paymentDate;
        }
    }
}