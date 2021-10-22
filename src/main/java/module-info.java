module sample {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires lombok;
    requires java.xml;
    requires org.firebirdsql.jaybird;
    requires java.sql;
    opens Controllers;
    opens Models;
    opens Config;
}
