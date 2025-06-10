package br.eng.dgjl.teatro.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static br.eng.dgjl.teatro.Main.inciarMenuStart;
import static br.eng.dgjl.teatro.Main.usuarioLogado;

public class AdminController {
    public TextField pecaMaisLucrativa;
    public TextField pecaMenosLucrativa;
    public TextField pecaMaisOcupada;
    public TextField pecaMenosOcupada;
    public TextField sessaoMaisLucrativa;
    public TextField sessaoMenosLucrativa;
    public TextField sessaoMaisOcupada;
    public TextField sessaoMenosOcupada;
    public TextArea ganhosArea;
    public Button imprimirBtn;

    public void deslogarAction(ActionEvent actionEvent) throws IOException {
        usuarioLogado = null;
        inciarMenuStart((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
    }
}
