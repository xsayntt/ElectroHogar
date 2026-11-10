module com.bad215.electrohogar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires java.base;
    requires jbcrypt;
    requires com.github.librepdf.openpdf;

    opens com.bad215.electrohogar.controller to javafx.fxml;
    exports com.bad215.electrohogar;
}
