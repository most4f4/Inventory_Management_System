package ca.demo.Controllers;

import ca.demo.InventoryApplication;
import ca.demo.Models.InHouse;
import ca.demo.Models.Inventory;
import ca.demo.Models.OutSourced;
import ca.demo.Models.Part;
import ca.demo.Utility.AlertHelper;
import ca.demo.Utility.FieldValidator;
import ca.demo.Utility.SceneName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class ModifyPartController {

    @FXML private RadioButton inHouseRB;
    @FXML private TextField inventoryLevelTF;
    @FXML private Label locationLabel;
    @FXML private ToggleGroup locationRB;
    @FXML private TextField locationTF;
    @FXML private TextField maxValTF;
    @FXML private TextField minValTF;
    @FXML private TextField nameTF;
    @FXML private RadioButton outsourcedRB;
    @FXML private TextField priceTF;
    @FXML private TextField partIdTF;
    @FXML private Button saveBtn;

    public void initialize() {

        saveBtn.setDisable(true);

        inventoryLevelTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        priceTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        maxValTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        minValTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());
        locationTF.textProperty().addListener((observable, oldValue, newValue) -> updateSaveBtn());

        locationRB.selectedToggleProperty().addListener((observable -> {
            if (locationRB.getSelectedToggle() == outsourcedRB) {
                locationLabel.setText("Company Name");
            } else {
                locationLabel.setText("Machine ID");
            }
        }));
    }

    private void updateSaveBtn(){
        saveBtn.setDisable(inventoryLevelTF.getText().trim().isEmpty() ||
                priceTF.getText().trim().isEmpty() ||
                maxValTF.getText().trim().isEmpty() ||
                minValTF.getText().trim().isEmpty() ||
                locationTF.getText().trim().isEmpty());
    }

    @FXML
    void cancelBtnPressed(ActionEvent event) {
        Stage currentStage = (Stage) saveBtn.getScene().getWindow();
        currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
    }

    @FXML
    void saveBtnPressed(ActionEvent event) {
        if (validate()) {
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to add the part?");
            if (confirmed) {
                int id = Integer.parseInt(partIdTF.getText());
                String name = nameTF.getText();
                double price = Double.parseDouble(priceTF.getText());
                int stock = Integer.parseInt(inventoryLevelTF.getText());
                int max =Integer.parseInt(maxValTF.getText());
                int min = Integer.parseInt(minValTF.getText());

                if (inHouseRB.isSelected()) {
                    int machineId = Integer.parseInt(locationTF.getText());
                    InHouse updatedPart = new InHouse(id, name, price, stock, min, max, machineId);
                    Inventory.updatePart(id, updatedPart);
                } else {
                    String companyName = locationTF.getText();
                    OutSourced updatedPart = new OutSourced(id, name, price, stock, min, max, companyName);
                    Inventory.updatePart(id, updatedPart);
                }
                Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
            }

        }

    }

    private boolean validate() {
        StringBuilder errors = new StringBuilder();

        // Validate inventory level (stock)
        int stock = FieldValidator.validateIntegerField(inventoryLevelTF.getText());
        if (stock == -1) {
            errors.append("Inventory Level must be a valid positive number.\n");
        }

        // Validate price
        double price = FieldValidator.validateDoubleField(priceTF.getText());
        if (price == -1) {
            errors.append("Price must be a valid positive number.\n");
        }

        // Validate max level
        int max = FieldValidator.validateIntegerField(maxValTF.getText());
        if (max == -1) {
            errors.append("Max Level must be a valid positive number.\n");
        }

        // Validate min level
        int min = FieldValidator.validateIntegerField(minValTF.getText());
        if (min == -1) {
            errors.append("Min Level must be a valid positive number.\n");
        }

        // Validate machine ID (only for in-house part)
        if (inHouseRB.isSelected() && !locationTF.getText().matches("\\d+")) {
            errors.append("Machine ID must be a valid number.\n");
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

        // Show errors if any
        if (errors.length() > 0) {
            AlertHelper.showErrorAlert("Error", errors.toString());
            return false;
        }
        return true;
    }

    public void setPart(Part part) {
        partIdTF.setText(String.valueOf(part.getId()));
        nameTF.setText(part.getName());
        inventoryLevelTF.setText(String.valueOf(part.getStock()));
        priceTF.setText(String.valueOf(part.getPrice()));
        maxValTF.setText(String.valueOf(part.getMax()));
        minValTF.setText(String.valueOf(part.getMin()));

        if (part instanceof InHouse){
            inHouseRB.setSelected(true);
            locationLabel.setText("Machine ID");
            locationTF.setText(String.valueOf(((InHouse) part).getMachineId()));
        } else {
            outsourcedRB.setSelected(true);
            locationTF.setText("Company Name");
            locationTF.setText(((OutSourced) part).getCompanyName());
        }
    }

}
