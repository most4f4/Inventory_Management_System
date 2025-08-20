package ca.demo.Controllers;

import ca.demo.InventoryApplication;
import ca.demo.Models.Inventory;
import ca.demo.Models.Part;
import ca.demo.Models.Product;
import ca.demo.Utility.AlertHelper;
import ca.demo.Utility.FieldValidator;
import ca.demo.Utility.SceneName;
import ca.demo.Utility.TableColumnModifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {

    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();

    @FXML private TextField invLevelTF;
    @FXML private TableColumn<Part, Integer> allParts_InvLevelTC;
    @FXML private TableColumn<Part, Integer> allParts_PartIdTC;
    @FXML private TableColumn<Part, String> allParts_PartNameTC;
    @FXML private TableColumn<Part, Double> allParts_PriceTC;
    @FXML private TableView<Part> allpartsTV;
    @FXML private TableColumn<Part, Integer> assoc_InvLevelTC;
    @FXML private TableColumn<Part, Integer> assoc_partIdTC;
    @FXML private TableColumn<Part, String> assoc_partNameTC;
    @FXML private TableView<Part> assoc_partsTV;
    @FXML private TableColumn<Part, Double> assoc_priceTC;
    @FXML private Button cancelBtn;
    @FXML private TextField idTF;
    @FXML private TextField maxTF;
    @FXML private TextField minTF;
    @FXML private TextField nameTF;
    @FXML private TextField priceTF;
    @FXML private Button removeBtn;
    @FXML private Button saveBtn;
    @FXML private TextField searchTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        idTF.setText(String.valueOf(Inventory.getNextProductId()));

        allpartsTV.setItems(Inventory.getAllParts());
        allParts_InvLevelTC.setCellValueFactory(new PropertyValueFactory<>("stock"));
        allParts_PartIdTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        allParts_PartNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        allParts_PriceTC.setCellValueFactory(new PropertyValueFactory<>("price"));

        assoc_partsTV.setItems(associatedParts);
        assoc_InvLevelTC.setCellValueFactory(new PropertyValueFactory<>("stock"));
        assoc_partIdTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        assoc_partNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        assoc_priceTC.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumnModifier.centerAlignColumn(allParts_InvLevelTC);
        TableColumnModifier.centerAlignColumn(allParts_PartIdTC);
        TableColumnModifier.centerAlignColumn(allParts_PartNameTC);
        TableColumnModifier.centerAlignColumn(allParts_PriceTC);
        TableColumnModifier.centerAlignColumn(assoc_InvLevelTC);
        TableColumnModifier.centerAlignColumn(assoc_partIdTC);
        TableColumnModifier.centerAlignColumn(assoc_partNameTC);
        TableColumnModifier.centerAlignColumn(assoc_priceTC);


        saveBtn.setDisable(true);
        nameTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        invLevelTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        priceTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        maxTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        minTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());

        removeBtn.setDisable(true);
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> updatePartsTable(newValue)));
    }

    private void updatePartsTable(String newValue) {
        if (newValue == null || newValue.trim().isEmpty()) {
            allpartsTV.setItems(Inventory.getAllParts()); // Reset to the full list
            return;
        }

        String searchText = newValue.trim().toLowerCase();
        ObservableList<Part> filteredParts = FXCollections.observableArrayList();

        for (Part part : Inventory.getAllParts()) {
            if (String.valueOf(part.getId()).contains(searchText) || part.getName().toLowerCase().contains(searchText)) {
                filteredParts.add(part);
            }
        }
        allpartsTV.setItems(filteredParts);
    }

    private void updateSaveBtn() {
        saveBtn.setDisable(invLevelTF.getText().trim().isEmpty() ||
                priceTF.getText().trim().isEmpty() ||
                maxTF.getText().trim().isEmpty() ||
                minTF.getText().trim().isEmpty() ||
                nameTF.getText().trim().isEmpty()
                );
    }

    private void addNewProduct() {
        int id = Integer.parseInt(idTF.getText());
        String name = nameTF.getText();
        int inventoryLevel = Integer.parseInt(invLevelTF.getText());
        double price = Double.parseDouble(priceTF.getText());
        int max = Integer.parseInt(maxTF.getText());
        int min = Integer.parseInt(minTF.getText());

        // public Product(int id, String name, double price, int stock, int min, int max)
        Product newProduct = new Product(id, name, price, inventoryLevel, min, max);
        for (Part p : associatedParts) {
            newProduct.addAssociatedPart(p);
        }
        Inventory.addProduct(newProduct);
    }

    @FXML void addBtnPressed(ActionEvent event) {
        Part selectedItem = allpartsTV.getSelectionModel().getSelectedItem();
        for (Part p: associatedParts){
            if (p.getId() == selectedItem.getId()){
                AlertHelper.showErrorAlert("Error", "Part Already Exists!");
                return;
            }
        }
        associatedParts.add(selectedItem);
        // Check if associatedParts has any parts, and enable/disable the remove button
        removeBtn.setDisable(associatedParts.isEmpty());
    }

    @FXML
    void cancelBtnPressed(ActionEvent event) {
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to cancel?");
        if (confirmed) {
            resetFields();
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
        }
    }

    @FXML
    void removeBtnPressed(ActionEvent event) {
        Part selectedProduct = assoc_partsTV.getSelectionModel().getSelectedItem();
        if (selectedProduct != null){
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure you want to delete this part?");
            if (confirmed) {
                associatedParts.remove(selectedProduct);
                // After removing a part, check if the list is empty again
                removeBtn.setDisable(associatedParts.isEmpty());
            }
        }
    }

    @FXML
    void saveBtnPressed(ActionEvent event) {
        if (validate()){
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to add this product?");
            if (confirmed) {
                addNewProduct();

                // Manually Clear the fields and increment the id
                resetFields();
                idTF.setText(String.valueOf(Inventory.getNextProductId()));

                Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
            }
        }
    }

    private boolean validate() {
        StringBuilder errors = new StringBuilder();

        // Validate inventory level (stock)
        int stock = FieldValidator.validateIntegerField(invLevelTF.getText());
        if (stock == -1) {
            errors.append("Inventory Level must be a valid positive number.\n");
        }

        // Validate price
        double price = FieldValidator.validateDoubleField(priceTF.getText());
        if (price == -1) {
            errors.append("Price must be a valid positive number.\n");
        }

        // Validate max level
        int max = FieldValidator.validateIntegerField(maxTF.getText());
        if (max == -1) {
            errors.append("Max Level must be a valid positive number.\n");
        }

        // Validate min level
        int min = FieldValidator.validateIntegerField(minTF.getText());
        if (min == -1) {
            errors.append("Min Level must be a valid positive number.\n");
        }

        // Validate stock level against min/max values
        if (stock < min) {
            errors.append("Stock Level must be greater than Min Value.\n");
        }
        if (stock > max) {
            errors.append("Stock Level must be smaller than Max Value.\n");
        }
        if (max < min) {
            errors.append("Max Level must be greater than Min Value.\n");
        }

        if (associatedParts.isEmpty()){
            errors.append("Product must always have at least one associated part. \n");
        } else {
            for (Part p: associatedParts){
                if (price < p.getPrice()){
                    errors.append("Price of product cannot be less than the cost of the parts.\n");
                    break;
                }
            }
        }

        // Show errors if any
        if (errors.length() > 0) {
            AlertHelper.showErrorAlert("Error", errors.toString());
            return false;
        }
        return true;
    }

    public void resetFields() {
        nameTF.clear();
        priceTF.clear();
        invLevelTF.clear();
        minTF.clear();
        maxTF.clear();
        associatedParts.clear();
        //saveBtn.setDisable(true);
    }

}


