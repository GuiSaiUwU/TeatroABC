module br.eng.dgjl.teatro {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires com.google.gson;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires caelum.stella.core;

    opens br.eng.dgjl.teatro to javafx.fxml;
    exports br.eng.dgjl.teatro;
    exports br.eng.dgjl.teatro.ui;
    opens br.eng.dgjl.teatro.ui to javafx.fxml;
    exports br.eng.dgjl.teatro.classes;
    opens br.eng.dgjl.teatro.classes to javafx.fxml, com.google.gson;
}