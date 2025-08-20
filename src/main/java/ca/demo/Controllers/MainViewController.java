package ca.demo.Controllers;

import ca.demo.InventoryApplication;
import ca.demo.Models.Inventory;
import ca.demo.Models.Part;
import ca.demo.Models.Product;
import ca.demo.Utility.AlertHelper;
import ca.demo.Utility.SceneName;
import ca.demo.Utility.TableColumnModifier;
import ca.demo.database.DatabaseAccess;
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
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainViewController implements Initializable {

    @FXML private Button exitBtn;
    @FXML private TableView<Part> partsTV;
    @FXML private TableColumn<Part, Integer> partIDTC;
    @FXML private TableColumn<Part, Integer> partInvLevelTC;
    @FXML private TableColumn<Part, String> partNameTC;
    @FXML private TableColumn<Part, Double> partPriceTC;
    @FXML private TextField partsSearchTF;
    @FXML private TableView<Product> productsTV;
    @FXML private TableColumn<Product, Integer> productIDTC;
    @FXML private TableColumn<Product, String> productNameTC;
    @FXML private TableColumn<Product, Double> productPriceTC;
    @FXML private TableColumn<Product, Integer> ProductInvLevelTC;
    @FXML private TextField productsSearchTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPartsTableView();
        setProductsTableView();
        partsSearchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> updatePartsTable(newValue) ));
        productsSearchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> updateProductsTable(newValue) ));
    }

    private void setPartsTableView() {
        partsTV.setItems(Inventory.getAllParts());
        partIDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        partInvLevelTC.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        partPriceTC.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumnModifier.centerAlignColumn(partIDTC);
        TableColumnModifier.centerAlignColumn(partInvLevelTC);
        TableColumnModifier.centerAlignColumn(partNameTC);
        TableColumnModifier.centerAlignColumn(partPriceTC);
    }

    private void setProductsTableView(){
        productsTV.setItems(Inventory.getAllProducts());
        productIDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        ProductInvLevelTC.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceTC.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumnModifier.centerAlignColumn(productIDTC);
        TableColumnModifier.centerAlignColumn(productNameTC);
        TableColumnModifier.centerAlignColumn(ProductInvLevelTC);
        TableColumnModifier.centerAlignColumn(productPriceTC);
    }

    private void updatePartsTable(String newValue) {
        if (newValue == null || newValue.trim().isEmpty()) {
            partsTV.setItems(Inventory.getAllParts()); // Reset to the full list
            return;
        }

        String searchText = newValue.trim().toLowerCase();
        ObservableList<Part> filteredParts = FXCollections.observableArrayList();

        try {
            // Try to search by ID if the input is a number
            int partId = Integer.parseInt(searchText);
            Part part = Inventory.searchPartByID(partId);
            if (part != null) {
                // If part found by ID, add it to the list
                filteredParts.add(part);
            }
        } catch (NumberFormatException e) {
            // If input is not a number, proceed with searching by name
            filteredParts = Inventory.searchPartByName(searchText);
        }

        partsTV.setItems(filteredParts);
    }

    private void updateProductsTable(String newValue){
        if (newValue == null || newValue.trim().isEmpty()){
            productsTV.setItems(Inventory.getAllProducts());
            return;
        }

        String searchText = newValue.trim().toLowerCase();
        ObservableList<Product> filteredproducts = FXCollections.observableArrayList();

        try {
            // Try to search by ID if the input is a number
            int productId = Integer.parseInt(searchText);
            Product product = Inventory.searchProductByID(productId);
            if (product != null) {
                // If part found by ID, add it to the list
                filteredproducts.add(product);
            }
        } catch (NumberFormatException e) {
            // If input is not a number, proceed with searching by name
            filteredproducts = Inventory.searchProductByName(searchText);
        }

        productsTV.setItems(filteredproducts);
    }

    public Stage getStage() {
        return (Stage) exitBtn.getScene().getWindow();
    }

    @FXML void addPartButtonClicked(ActionEvent event) {
        Stage currentStage = getStage();
        currentStage.setTitle("Add Part");
        currentStage.setScene(InventoryApplication.getScenes().get(SceneName.addPart));
    }

    @FXML void addProductBtnPressed(ActionEvent event) {
        Stage currentStage = getStage();
        currentStage.setTitle("Add Product");
        currentStage.setScene(InventoryApplication.getScenes().get(SceneName.addProduct));
    }

    @FXML void deletePartBtnClicked(ActionEvent event) {
        Part selectedpart = handlePartSelection();
        if (selectedpart != null) {
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure you want to delete this part?");
            if (confirmed) {
                for (Product product : Inventory.getAllProducts()) {
                    if (product.getAllAssociatedParts().size() > 0) {
                        for (Part part : product.getAllAssociatedParts()) {
                            if (part.getId() == selectedpart.getId()){
                                boolean secondConfirmed = AlertHelper.showConfirmationAlert("Confirm",  String.format("This part is associated with %s (Product ID: %d). Are you sure you want to delete it?",  product.getName(),  product.getId()));
                                if (secondConfirmed) {
                                    Inventory.deletePart(selectedpart);
                                } else {
                                    return;
                                }
                            }
                        }

                    }
                }
                Inventory.deletePart(selectedpart);
            }

        }
    }

    @FXML void deleteProductBtnPressed(ActionEvent event) {
        Product selectedProduct = handleProductSelection();
        if (selectedProduct != null) {
            boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure you want to delete this Product?");
            if (confirmed) {
                if (selectedProduct.getAllAssociatedParts().size() > 0) {
                    AlertHelper.showErrorAlert("Error", "Product has associated part!");
                } else {
                    Inventory.deletedProduct(selectedProduct);
                }
            }
        }
    }

    @FXML void exitBtnPressed(ActionEvent event) {
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure you want to exit the program?");
        if (confirmed) {
            System.exit(0);
        }
    }

    private Part handlePartSelection(){
        return partsTV.getSelectionModel().getSelectedItem();
    }

    private Product handleProductSelection(){
        return productsTV.getSelectionModel().getSelectedItem();
    }

    @FXML void modifyPartBtnPressed(ActionEvent event) {
        Part selectedpart = handlePartSelection();
        if (selectedpart != null){
            Stage currentStage = getStage();
            ModifyPartController modifyPartController = (ModifyPartController) InventoryApplication.getControllers().get(SceneName.modifyPart);
            modifyPartController.setPart(selectedpart);
            currentStage.setTitle("Modify Part");
            currentStage.setScene(InventoryApplication.getScenes().get(SceneName.modifyPart));
        }
    }

    @FXML void modifyProdcuctBtnPressed(ActionEvent event) {
        Product selectedProduct = handleProductSelection();
        if (selectedProduct != null){
            Stage currentStage = getStage();
            ModifyProductController modifyProductController = (ModifyProductController) InventoryApplication.getControllers().get(SceneName.modifyProduct);
            modifyProductController.setProduct(selectedProduct);
            currentStage.setTitle("Modify Product");
            currentStage.setScene(InventoryApplication.getScenes().get(SceneName.modifyProduct));
        }

    }

    @FXML void logoutBtnPressed(ActionEvent event) {
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure you want to logout?");
        if (confirmed) {
            Stage currentStage = getStage();
            currentStage.setScene(InventoryApplication.getScenes().get(SceneName.login));
        }
    }

    @FXML void saveToFileBtnPressed(ActionEvent event) {

        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to save the Inventory to file?");
        if (confirmed) {
            // Write parts list to the file
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("parts.dat"))) {
                out.writeObject(new ArrayList<>(Inventory.getAllParts()));
                System.out.println("Parts saved successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot open file for writing");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            // Write products list to the file
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("products.dat"))) {
                out.writeObject(new ArrayList<>(Inventory.getAllProducts()));
                System.out.println("products saved successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot open file for writing");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        AlertHelper.showInformationAlert("Saved", "Inventory saved successfully to the database.");

    }

    @FXML void loadFromFileBtnPressed(ActionEvent event) {
        Inventory.getAllProducts().clear();
        Inventory.getAllParts().clear();

        // Load the parts list from file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("parts.dat"))) {
            List<Part> parts = (List<Part>) in.readObject();
            Inventory.getAllParts().addAll(parts); // Add the parts to the ObservableList
            System.out.println("Parts loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open parts file for reading");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        setPartsTableView();

        // Load the products list from file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("products.dat"))) {
            List<Product> products = (List<Product>) in.readObject();
            Inventory.getAllProducts().addAll(products); // Add the products to the ObservableList
            System.out.println("Products loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open products file for reading");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        setProductsTableView();

        AlertHelper.showInformationAlert("Loaded", "Inventory loaded from file.");

    }

    @FXML void writeToDBBtnPressed(ActionEvent event) {
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm", "Are you sure to save the Inventory to DataBase?");
        if (confirmed) {
            DatabaseAccess.createTables();
            DatabaseAccess.storeData();
        }
        AlertHelper.showInformationAlert("Saved", "Inventory saved successfully to the database.");
    }

    @FXML void loadFromDBButtonPressed(ActionEvent event) {
        Inventory.getAllParts().clear();
        Inventory.getAllProducts().clear();

        DatabaseAccess.getAllParts();
        DatabaseAccess.getAllProducts();

        setPartsTableView();
        setProductsTableView();

        AlertHelper.showInformationAlert("Loaded", "Inventory loaded from database.");
    }



}
