package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.classes.Ingresso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public ObservableList<Ingresso> ingressosData;

    public TableView<Ingresso> ingressosTable;
    public TableColumn<Ingresso, String> cpfColumn;
    public TableColumn<Ingresso, String> pecaColumn;
    public TableColumn<Ingresso, String> sessaoColumn;
    public TableColumn<Ingresso, String> areaColumn;
    public TableColumn<Ingresso, Integer> cadeiraColumn;
    public TableColumn<Ingresso, Integer> precoColumn;

    @FXML private ChoiceBox<String> pecaFilter;
    @FXML private ChoiceBox<String> sessaoFilter;
    @FXML private ChoiceBox<String> areaFilter;

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
    
    public void instanciarColunas() {
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("CPF"));
        pecaColumn.setCellValueFactory(new PropertyValueFactory<>("pecaNome"));
        sessaoColumn.setCellValueFactory(new PropertyValueFactory<>("sessaoNome"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaNome"));
        cadeiraColumn.setCellValueFactory(new PropertyValueFactory<>("cadeiraPosicao"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));

        cpfColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String CPF, boolean vazio) {
                super.updateItem(CPF, vazio);
                if (vazio || CPF == null) {
                    setText(null);
                } else {
                    // Gera um CPF tipo 708.xxx.xxx-22
                    String cpfTemp = CPF.substring(0, 3) +
                            ".xxx.xxx-" +
                            CPF.substring(CPF.length() - 2);
                    setText(cpfTemp);
                }
            }
        });

        precoColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer preco, boolean vazio) {
                super.updateItem(preco, vazio);
                if (vazio || preco == null) {
                    setText(null);
                } else {
                    setText("R$ " + preco);
                }
            }
        });

        cadeiraColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer posicao, boolean empty) {
                super.updateItem(posicao, empty);
                if (empty || posicao == null) {
                    setText(null);
                } else {
                    setText(String.format("%02d", posicao + 1));
                }
            }
        });

        pecaFilter.setItems(FXCollections.observableArrayList(
                ingressosData.stream().map(Ingresso::getPecaNome).distinct().sorted().toList()
        ));
        sessaoFilter.setItems(FXCollections.observableArrayList(
                ingressosData.stream().map(Ingresso::getSessaoNome).distinct().sorted().toList()
        ));
        areaFilter.setItems(FXCollections.observableArrayList(
                ingressosData.stream().map(Ingresso::getAreaNome).distinct().sorted().toList()
        ));

        ingressosTable.setItems(ingressosData);
    }

    @FXML
    private void filterAction(ActionEvent event) {
        String pecaSelecionada = pecaFilter.getValue();
        String sessaoSelecionada = sessaoFilter.getValue();
        String areaSelecionada = areaFilter.getValue();

        List<Ingresso> filtrado = ingressosData.stream()
                .filter(i -> pecaSelecionada == null || pecaSelecionada.isEmpty() || i.getPecaNome().equals(pecaSelecionada))
                .filter(i -> sessaoSelecionada == null || sessaoSelecionada.isEmpty() || i.getSessaoNome().equals(sessaoSelecionada))
                .filter(i -> areaSelecionada == null || areaSelecionada.isEmpty() || i.getAreaNome().equals(areaSelecionada))
                .collect(Collectors.toList());

        ingressosTable.setItems(FXCollections.observableArrayList(filtrado));
    }

    @FXML
    private void clearFilterAction(ActionEvent event) {
        pecaFilter.setValue(null);
        sessaoFilter.setValue(null);
        areaFilter.setValue(null);
        ingressosTable.setItems(ingressosData);
    }
}
