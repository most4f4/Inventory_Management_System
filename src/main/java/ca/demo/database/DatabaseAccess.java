package ca.demo.database;

import ca.demo.Models.*;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseAccess {
    private static final String DB_NAME = "database.db";
    private static final String DB_JDBC = "jdbc:sqlite:";
    private static final String DB_CONNECTION = "C:\\Users\\mosta\\OneDrive\\Desktop\\Semester5\\APD545\\Workshops\\workshop5\\workshop\\src\\main\\java\\ca\\demo\\database\\";

    public static void createTables() {
        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME)) {

            String[] dropAllTables = {
                    "DROP TABLE IF EXISTS InHouseParts;",
                    "DROP TABLE IF EXISTS OutSourcedParts;",
                    "DROP TABLE IF EXISTS ProductParts;",
                    "DROP TABLE IF EXISTS Products;",
                    "DROP TABLE IF EXISTS Parts;"  // Drop Parts last because others reference it
            };

            for (String sql : dropAllTables) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.execute();
                }
            }


            // Create Parts Table
            String sql = "CREATE TABLE IF NOT EXISTS Parts (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "stock INTEGER NOT NULL," +
                    "min INTEGER NOT NULL," +
                    "max INTEGER NOT NULL," +
                    "type TEXT NOT NULL)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }

            // Create InHouseParts Table
            sql = "CREATE TABLE IF NOT EXISTS InHouseParts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "part_id INTEGER , " +
                    "machine_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (part_id) REFERENCES Parts(id) ON DELETE CASCADE)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }

            // Create OutSourcedParts Table
            sql = "CREATE TABLE IF NOT EXISTS OutSourcedParts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "part_id INTEGER NOT NULL, " +
                    "companyName TEXT NOT NULL, " +
                    "FOREIGN KEY (part_id) REFERENCES Parts(id) ON DELETE CASCADE)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }

            // Create Products Table
            sql = "CREATE TABLE IF NOT EXISTS Products (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "stock INTEGER NOT NULL, " +
                    "min INTEGER NOT NULL, " +
                    "max INTEGER NOT NULL)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }

            // Create ProductParts Table (Many-to-Many Relationship)
            sql = "CREATE TABLE IF NOT EXISTS ProductParts (" +
                    "product_id INTEGER NOT NULL, " +
                    "part_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (part_id) REFERENCES Parts(id) ON DELETE CASCADE, " +
                    "PRIMARY KEY (product_id, part_id))";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void storeData() {
        for (Part part : Inventory.getAllParts()) {
            if (part instanceof InHouse) {
                addPart(part.getId(), part.getName(), part.getPrice(), part.getStock(), part.getMin(), part.getMax(), "InHouse");
                addInHouse(part.getId(), ((InHouse) part).getMachineId());
            } else if (part instanceof OutSourced) {
                addPart(part.getId(), part.getName(), part.getPrice(), part.getStock(), part.getMin(), part.getMax(), "OutSourced");
                addOutSourced(part.getId(), ((OutSourced) part).getCompanyName());
            }
        }

        for (Product product : Inventory.getAllProducts()) {
            addProduct(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getMin(), product.getMax());
            for (Part part : product.getAllAssociatedParts()) {
                addAssociatedParts(product.getId(), part.getId());
            }
        }
    }

    private static void addPart(int id, String name, Double price, int stock, int min, int max, String type) {
        String sql = "INSERT INTO Parts (id, name, price, stock, min, max, type)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, min);
            ps.setInt(6, max);
            ps.setString(7, type);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addInHouse(int part_id, int machine_id) {
        String sql = "INSERT INTO InHouseParts (part_id, machine_id)"
                + "VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, part_id);
            ps.setInt(2, machine_id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addOutSourced(int part_id, String companyName) {
        String sql = "INSERT INTO OutSourcedParts (part_id, companyName)"
                + "VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, part_id);
            ps.setString(2, companyName);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addProduct(int id, String name, Double price, int stock, int min, int max) {
        String sql = "INSERT INTO Products (id, name, price, stock, min, max)"
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, min);
            ps.setInt(6, max);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addAssociatedParts(int product_id, int part_id) {
        String sql = "INSERT INTO ProductParts (product_id, part_id)"
                + "VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, product_id);
            ps.setInt(2, part_id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllParts() {
        ArrayList<Part> allParts = new ArrayList<>();
        String sql = "SELECT p.*, ih.machine_id, os.companyName " +
                "FROM Parts p " +
                "LEFT JOIN InHouseParts ih ON p.id = ih.part_id " +
                "LEFT JOIN OutSourcedParts os ON p.id = os.part_id";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int part_id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                int min = rs.getInt("min");
                int max = rs.getInt("max");
                String type = rs.getString("type");

                Part part;
                if ("InHouse".equals(type)) {
                    int machineId = rs.getInt("machine_id"); // Directly fetched
                    part = new InHouse(part_id, name, price, stock, min, max, machineId);
                } else {
                    String companyName = rs.getString("companyName"); // Directly fetched
                    part = new OutSourced(part_id, name, price, stock, min, max, companyName);
                }

                Inventory.addPart(part);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllProducts() {
        ArrayList<Product> allProducts = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (Connection connection = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int product_id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                int min = rs.getInt("min");
                int max = rs.getInt("max");

                Product product = new Product(product_id, name, price, stock, min, max);

                // Load associated parts for the product
                String query = "SELECT p.*, ih.machine_id, os.companyName " +
                        "FROM Parts p " +
                        "LEFT JOIN InHouseParts ih ON p.id = ih.part_id " +
                        "LEFT JOIN OutSourcedParts os ON p.id = os.part_id " +
                        "LEFT JOIN ProductParts pp ON p.id = pp.part_id " +
                        "WHERE pp.product_id = ?";  // Use product_id to filter relevant parts

                try (PreparedStatement assPartsPs = connection.prepareStatement(query)) {
                    assPartsPs.setInt(1, product_id);
                    try (ResultSet assParts = assPartsPs.executeQuery()) {

                        while (assParts.next()) {
                            int part_id = assParts.getInt("id");
                            String part_name = assParts.getString("name");
                            double part_price = assParts.getDouble("price");
                            int part_stock = assParts.getInt("stock");
                            int part_min = assParts.getInt("min");
                            int part_max = assParts.getInt("max");
                            String type = assParts.getString("type");

                            Part part;
                            if ("InHouse".equals(type)) {
                                int machineId = assParts.getInt("machine_id"); // Directly fetched for InHouse
                                part = new InHouse(part_id, part_name, part_price, part_stock, part_min, part_max, machineId);
                            } else {
                                String companyName = assParts.getString("companyName"); // Directly fetched for OutSourced
                                part = new OutSourced(part_id, part_name, part_price, part_stock, part_min, part_max, companyName);
                            }
                            product.addAssociatedPart(part);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                // Add the product to the inventory
                Inventory.addProduct(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
