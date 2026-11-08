module com.bad215.electrohogar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.base;
    requires jbcrypt;

    opens com.bad215.electrohogar.controller to javafx.fxml;
    exports com.bad215.electrohogar;
}
