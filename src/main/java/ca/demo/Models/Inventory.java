package ca.demo.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Inventory {

    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private static int nextPartId = 1;
    private static int nextProductId = 1;

    public static void addPart(Part newPart){
        allParts.add(newPart);
    }

    public static void addProduct(Product newProduct){
        allProducts.add(newProduct);
    }

    public static Part searchPartByID(int partId){
        for (Part p : allParts){
            if (p.getId() == partId)
                return p;
        }
        return null;
    }

    public static Product searchProductByID(int productId){
        for (Product p : allProducts){
            if (p.getId() == productId)
                return p;
        }
        return null;
    }

    public static ObservableList<Part> searchPartByName(String name){
        ObservableList<Part> obs = FXCollections.observableArrayList();

        if (name == null || name.trim().isEmpty()) {
            return obs;
        }

        for (Part p : allParts) {
            if (p.getName().toLowerCase().contains(name)) {
                obs.add(p);
            }
        }
        return obs;
    }

    public static ObservableList<Product> searchProductByName(String name){
        ObservableList<Product> obs = FXCollections.observableArrayList();

        if (name == null || name.trim().isEmpty()) {
            return obs;
        }

        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(name)) {
                obs.add(p);
            }
        }
        return obs;
    }

    public static void updatePart(int id, Part selectedPart){
        if (id > 0) {
            if (selectedPart != null) {
                for(int i = 0; i < allParts.size(); i++) {
                    if (allParts.get(i).getId() == id){
                        allParts.set(i, selectedPart);
                    }
                }
            } else {
                System.out.println("Selected part is null.");
            }
        } else {
            System.out.println("Invalid index: " + id);
        }
    }

    public static void updateProduct(int id, Product newProduct){
        if (id > 0 ) {
            if (newProduct != null) {
                for(int i = 0; i < allProducts.size(); i++) {
                    if (allProducts.get(i).getId() == id){
                        allProducts.set(i, newProduct);
                    }
                }
            } else {
                System.out.println("Selected product is null.");
            }
        } else {
            System.out.println("Invalid index: " + id);
        }
    }

    public static boolean deletePart(Part selectedPart){
        if (selectedPart != null) {
            return allParts.remove(selectedPart);
        }
        return false;
    }

    public static boolean deletedProduct(Product selectedProduct){
        if (selectedProduct != null) {
            return allProducts.remove(selectedProduct);
        }
        return false;
    }

    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }

    public static int getNextPartId() {
        return nextPartId++;
    }

    public static int getNextProductId() {
        return nextProductId++;
    }

    public static void setInventory(ArrayList<Part> parts, ArrayList<Product> products) {
        allParts = FXCollections.observableArrayList(parts);
        allProducts = FXCollections.observableArrayList(products);
    }


}
