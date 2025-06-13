package br.eng.dgjl.teatro.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

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

    public void exibirGraficosAction(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GraficosView.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Teatro - Visualização de Gráficos");
            stage.getIcons().add(new Image(Objects.requireNonNull
                    (CompraMenu.class.getResourceAsStream("images/Logo.png"))
            ));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
