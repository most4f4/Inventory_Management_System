module ca.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ca.demo to javafx.fxml;
    exports ca.demo;
    exports ca.demo.Controllers;
    opens ca.demo.Controllers to javafx.fxml;
    opens ca.demo.Models to javafx.base;
    opens ca.demo.Utility to javafx.base;


}