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

public class AddPartController {

    @FXML private TextField idTF;
    @FXML private RadioButton inHouseRB;
    @FXML private TextField inventoryLevelTF;
    @FXML private ToggleGroup locationRB;
    @FXML private Label locationLabel;
    @FXML private TextField locationTF;
    @FXML private TextField maxValTF;
    @FXML private TextField minValTF;
    @FXML private RadioButton outsourcedRB;
    @FXML private TextField priceTF;
    @FXML private Button saveBtn;
    @FXML private TextField nameTF;


    public void initialize() {
        saveBtn.setDisable(true);
        idTF.setText(String.valueOf(Inventory.getNextPartId()));
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

    private void updateSaveBtn() {
        saveBtn.setDisable(inventoryLevelTF.getText().trim().isEmpty() ||
                priceTF.getText().trim().isEmpty() ||
                maxValTF.getText().trim().isEmpty() ||
                minValTF.getText().trim().isEmpty() ||
                locationTF.getText().trim().isEmpty());
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

    private void addNewPart() {

        int id = Integer.parseInt(idTF.getText());

        if (inHouseRB.isSelected()) {
            InHouse newPart = new InHouse(
                    id,
                    nameTF.getText(),
                    Double.parseDouble(priceTF.getText()),
                    Integer.parseInt(inventoryLevelTF.getText()),
                    Integer.parseInt(minValTF.getText()),
                    Integer.parseInt(maxValTF.getText()),
                    Integer.parseInt(locationTF.getText())
            );
            Inventory.addPart(newPart);
        } else {
            OutSourced newPart = new OutSourced(
                    id,
                    nameTF.getText(),
                    Double.parseDouble(priceTF.getText()),
                    Integer.parseInt(inventoryLevelTF.getText()),
                    Integer.parseInt(minValTF.getText()),
                    Integer.parseInt(maxValTF.getText()),
                    locationTF.getText()
            );
            Inventory.addPart(newPart);
        }
    }

    @FXML
    void saveBtnPressed(ActionEvent event) {
        if (validate()) {
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to add the part?");
            if (confirmed) {
                addNewPart();

                // Manually clear the fields and increment the id for future parts
                resetFields();
                idTF.setText(String.valueOf(Inventory.getNextPartId()));

                Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
            }

        }
    }

    public void resetFields() {
        nameTF.clear();
        priceTF.clear();
        inventoryLevelTF.clear();
        minValTF.clear();
        maxValTF.clear();
        locationTF.clear();
        locationLabel.setText("Machine ID");
        inHouseRB.setSelected(true);
        saveBtn.setDisable(true);
    }

}
