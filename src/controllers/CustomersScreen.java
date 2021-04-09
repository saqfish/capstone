package controllers;

import javafx.collections.transformation.FilteredList;
import models.Appointment;
import models.SQL;
import models.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Customer Screen Controller
 * @author Abdirisaq Sheikh
 */
public class CustomersScreen extends TranslatableScreen{

  private final Session session;

  @FXML private Button newCustomerButton;
  @FXML private Button updateCustomerButton;
  @FXML private Button deleteCustomerButton;
  @FXML private Button backButton;

  @FXML private Label heading;
  @FXML private Label banner;

  @FXML private TableView<Customer> customerTableView;
  @FXML private TableColumn<?, ?> countryTableColumn;
  @FXML private TableColumn<?, ?> addressTableColumn;
  @FXML private TableColumn<?, ?> cuidTableColumn;
  @FXML private TableColumn<?, ?> customerNameTableColumn;
  @FXML private TableColumn<?, ?> didTableColumn;
  @FXML private TableColumn<?, ?> phoneNumberTableColumn;
  @FXML private TableColumn<?, ?> zipTableColumn;

  // Added search elements
  @FXML private TextField searchTextField;
  @FXML private Button searchButton;

  /**
   * Customer Screen Constructor
   * @param session current session
   */
  public CustomersScreen(Session session) { this.session = session; }

  /**
   * Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    translateLabels();
    customerTableView.setItems(session.getCache().getCustomers());
  }
  /**
   * Shows label in proper language
   */
  private void translateLabels() {
    translate(cuidTableColumn, "ID");
    translate(customerNameTableColumn, "NAME");
    translate(addressTableColumn, "ADDRESS");
    translate(zipTableColumn, "ZIP_CODE");
    translate(phoneNumberTableColumn, "PHONE");
    translate(didTableColumn, "DIVISON");
    translate(countryTableColumn, "COUNTRY");
    translate(newCustomerButton, "ADD");
    translate(updateCustomerButton, "UPDATE");
    translate(deleteCustomerButton, "DELETE");
    translate(backButton, "BACK");
    translate(heading, "LOGIN_BANNER");
    translate(banner, "CUSTOMERS");
  }

  /**
   * Add a customer
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void newCustomer(ActionEvent event) throws IOException {
    loadScreen(event, new NewCustomerScreen(session), "/views/NewCustomerScreen.fxml");
  }

  /**
   * Update selected appointment
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  private void updateCustomer(ActionEvent event) throws IOException {
    Customer selection = customerTableView.getSelectionModel().getSelectedItem();
    if (selection != null) {
      loadScreen(event, new UpdateCustomerScreen(session, selection), "/views/UpdateCustomerScreen.fxml");
    } else {
      alert(Alert.AlertType.ERROR
              ,language().getString("ERROR")
              ,language().getString("ERROR")
              ,language().getString("CUSTOMER_SELECT")
      );
    }
  }

  /**
   * Remove selected appointment
   * @param event Action Event
   * @throws SQLException Exception on failure
   */
  @FXML
  void deleteCustomer(ActionEvent event) throws SQLException{
    Customer selection = customerTableView.getSelectionModel().getSelectedItem();
    if (selection!= null) {
      if(customerHasAppointments(selection))
        alert(Alert.AlertType.INFORMATION,
                language().getString("CUSTOMER_APPOINTMENT_EXISTS_TITLE"),
                language().getString("CUSTOMER_APPOINTMENT_EXISTS_HEADER"),
                language().getString("CUSTOMER_APPOINTMENT_EXISTS_CONTENT")
        );
      else {
      ObservableList<Customer> customers = FXCollections.observableArrayList();
      customers = customerTableView.getItems();
      var sqlString = "DELETE FROM customers WHERE Customer_ID = "
              + selection.getCid();
      try (var statement = SQL.connect().prepareStatement(sqlString)) {
        statement.executeUpdate();
      }
      alert(Alert.AlertType.INFORMATION,
              language().getString("DELETE_CUSTOMER_TITLE"),
              language().getString("DELETE_CUSTOMER_TITLE"),
              language().getString("DELETE_CUSTOMER_TEXT")
      );

      customers.remove(customerTableView.getSelectionModel().getSelectedItem());
      customerTableView.setItems(customers);
    }
    } else {
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("ERROR"),
              language().getString("CUSTOMER_NOT_FOUND")
      );
    }
  }

  /**
   * Return to main screen
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void goBack(ActionEvent event) throws IOException { loadMain(event, session);}

  private boolean customerHasAppointments(Customer customer){
  int appointments = 0;
      for( Appointment appointment : session.getCache().getAppointments()){
    if (appointment.getCuid() == customer.getCid()) appointments++;
  };
      return appointments > 0;
}

  /**
   * Search appointments by value
   * search is done by ID, Name, and Address
   * @param event Action Event
   */
  @FXML void doSearch(ActionEvent event) {
    String text = searchTextField.getText();
    FilteredList<Customer> data = new FilteredList<>(session.getCache().getCustomers());
    data.setPredicate(row -> {
      String rowId = String.valueOf(row.getCid());
      String rowTitle = row.getName();
      String rowDesc = row.getAddress();
      return rowTitle.contains(text) || rowDesc.contains(text) || rowId.contains(text);
    });
    customerTableView.setItems(data);
  }
}