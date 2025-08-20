package ca.demo;

import ca.demo.Models.InHouse;
import ca.demo.Models.Inventory;
import ca.demo.Models.OutSourced;
import ca.demo.Models.Product;
import ca.demo.Utility.SceneName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryApplication extends Application {

    private static Map<SceneName, Scene> scenes = new HashMap<>();
    private static Map<SceneName, Object> controllers = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {

        // Create and store all the scenes up front by loading FXML files
        scenes.put(SceneName.login, loadScene("Login-view.fxml", SceneName.login));
        scenes.put(SceneName.MAIN, loadScene("Main-View.fxml", SceneName.MAIN));
        scenes.put(SceneName.addPart, loadScene("Add-Part.fxml", SceneName.addPart));
        scenes.put(SceneName.addProduct, loadScene("Add-Product.fxml", SceneName.addProduct));
        scenes.put(SceneName.modifyPart, loadScene("Mod-Part.fxml", SceneName.modifyPart));
        scenes.put(SceneName.modifyProduct, loadScene("Mod-Product.fxml", SceneName.modifyProduct));

        stage.setTitle("Login");
        stage.setScene(scenes.get(SceneName.login));
        stage.show();
    }

    private Scene loadScene(String fxmlFile, SceneName sceneName){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            controllers.put(sceneName, loader.getController());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<SceneName, Scene> getScenes() {
        return scenes;
    }

    public static Map<SceneName, Object> getControllers() {
        return controllers;
    }

    public static void main(String[] args) {
        //loadInventory();
        launch();
    }

    public static void loadInventory() {
        // Creating parts
        OutSourced part1 = new OutSourced(Inventory.getNextPartId(), "Board Frame", 12.99, 10, 1, 20, "Microsoft");
        InHouse part2 = new InHouse(Inventory.getNextPartId(), "Wood Pieces", 3.99, 15, 1, 20, 43);
        OutSourced part3 = new OutSourced(Inventory.getNextPartId(), "Burger Buns", 1.50, 25, 1, 50, "McDonald's");
        InHouse part4 = new InHouse(Inventory.getNextPartId(), "Patty", 2.50, 20, 1, 40, 15);
        OutSourced part5 = new OutSourced(Inventory.getNextPartId(), "Toy Car Wheels", 5.99, 10, 1, 10, "ToyFactory");
        InHouse part6 = new InHouse(Inventory.getNextPartId(), "Car Frame", 8.99, 12, 1, 15, 23);
        OutSourced part7 = new OutSourced(Inventory.getNextPartId(), "Laptop Screen", 99.99, 8, 1, 10, "Dell");
        InHouse part8 = new InHouse(Inventory.getNextPartId(), "Keyboard", 29.99, 12, 1, 15, 51);
        OutSourced part9 = new OutSourced(Inventory.getNextPartId(), "Phone Battery", 45.00, 8, 1, 10, "Samsung");
        InHouse part10 = new InHouse(Inventory.getNextPartId(), "Phone Camera", 55.00, 8, 1, 10, 61);

// Adding parts to inventory
        Inventory.addPart(part1);
        Inventory.addPart(part2);
        Inventory.addPart(part3);
        Inventory.addPart(part4);
        Inventory.addPart(part5);
        Inventory.addPart(part6);
        Inventory.addPart(part7);
        Inventory.addPart(part8);
        Inventory.addPart(part9);
        Inventory.addPart(part10);

// Creating products and associating at least one part
        Product product1 = new Product(Inventory.getNextProductId(), "Chessboard", 24.99, 15, 1, 20);
        product1.addAssociatedPart(part1);

        Product product2 = new Product(Inventory.getNextProductId(), "Big Mac", 4.59, 20, 1, 20);
        product2.addAssociatedPart(part3);

        Product product3 = new Product(Inventory.getNextProductId(), "Toy Car", 15.99, 25, 1, 30);
        product3.addAssociatedPart(part5);

        Product product4 = new Product(Inventory.getNextProductId(), "Gaming Laptop", 1299.99, 5, 1, 5);
        product4.addAssociatedPart(part7);

        Product product5 = new Product(Inventory.getNextProductId(), "Smartphone", 799.99, 5, 1, 5);
        product5.addAssociatedPart(part9);

        Product product6 = new Product(Inventory.getNextProductId(), "Mechanical Keyboard", 99.99, 10, 1, 10);
        product6.addAssociatedPart(part8);

        Product product7 = new Product(Inventory.getNextProductId(), "Bluetooth Speaker", 49.99, 10, 1, 10);
        product7.addAssociatedPart(part6);

        Product product8 = new Product(Inventory.getNextProductId(), "4K Monitor", 399.99, 5, 1, 5);
        product8.addAssociatedPart(part7);

        Product product9 = new Product(Inventory.getNextProductId(), "Smartwatch", 199.99, 10, 1, 10);
        product9.addAssociatedPart(part10);

        Product product10 = new Product(Inventory.getNextProductId(), "Wireless Mouse", 39.99, 10, 1, 10);
        product10.addAssociatedPart(part2);

// Adding products to inventory
        Inventory.addProduct(product1);
        Inventory.addProduct(product2);
        Inventory.addProduct(product3);
        Inventory.addProduct(product4);
        Inventory.addProduct(product5);
        Inventory.addProduct(product6);
        Inventory.addProduct(product7);
        Inventory.addProduct(product8);
        Inventory.addProduct(product9);
        Inventory.addProduct(product10);
    }

}