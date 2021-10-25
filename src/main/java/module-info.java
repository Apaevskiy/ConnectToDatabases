module sample {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires lombok;
    requires java.xml;
    requires org.firebirdsql.jaybird;
    requires java.sql;
    requires java.desktop;
    opens controller;
    opens Models;
    opens Config;
    opens DAO.entity;
    opens XML;
}
