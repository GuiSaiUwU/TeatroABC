package br.eng.dgjl.teatro.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.io.IOException;

import static br.eng.dgjl.teatro.Main.inciarMenuStart;
import static br.eng.dgjl.teatro.Main.usuarioLogado;

public class CompraController {
    public CheckListView<String> checkListView;
    public ChoiceBox<String> pecaBox;
    public ChoiceBox<String> sessaoBox;
    public ChoiceBox<String> areaBox;
    public ImageView imageView;
    public Button compraBtn;
    public Label ingressosLabel;
    public Label totalLabel;
    public Button historicoBtn;

    public void deslogarAction(ActionEvent actionEvent) throws IOException {
        usuarioLogado = null;
        inciarMenuStart((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
    }
}
