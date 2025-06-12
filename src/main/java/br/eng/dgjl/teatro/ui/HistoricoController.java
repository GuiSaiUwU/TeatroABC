package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.classes.Ingresso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static br.eng.dgjl.teatro.Main.usuarioLogado;

public class HistoricoController {
    @FXML private ChoiceBox<String> pecaFilter;
    @FXML private TableView<Ingresso> ingressosTable;
    @FXML private TableColumn<Ingresso, String> pecaColumn;
    @FXML private TableColumn<Ingresso, String> sessaoColumn;
    @FXML private TableColumn<Ingresso, String> areaColumn;
    @FXML private TableColumn<Ingresso, Integer> cadeiraColumn;
    @FXML private TableColumn<Ingresso, Integer> precoColumn;
    @FXML private Label maiorGastoLabel;
    @FXML private Label menorGastoLabel;
    @FXML private Label totalGastoLabel;

    private ObservableList<Ingresso> ingressosData;
    private Stage stage;

    public void initialize() {
        // Set up table columns
        pecaColumn.setCellValueFactory(new PropertyValueFactory<>("pecaNome"));
        sessaoColumn.setCellValueFactory(new PropertyValueFactory<>("sessaoNome"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaNome"));
        cadeiraColumn.setCellValueFactory(new PropertyValueFactory<>("cadeiraPosicao"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));

        // Format the price column
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

        // Format the chair column
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

        carregarDados();
    }

    private void carregarDados() {
        List<Ingresso> ingressos = usuarioLogado.getIngressos();
        ingressosData = FXCollections.observableArrayList(ingressos);
        ingressosTable.setItems(ingressosData);

        List<String> pecas = ingressos.stream()
                .map(Ingresso::getPecaNome)
                .distinct()
                .collect(Collectors.toList());
        pecaFilter.setItems(FXCollections.observableArrayList(pecas));

        updateStats();
    }

    private void updateStats() {
        if (ingressosData.isEmpty()) {
            maiorGastoLabel.setText("R$ 0,00");
            menorGastoLabel.setText("R$ 0,00");
            totalGastoLabel.setText("R$ 0,00");
            return;
        }

        int total = ingressosData.stream().mapToInt(Ingresso::getPreco).sum();
        int max = ingressosData.stream().mapToInt(Ingresso::getPreco).max().orElse(0);
        int min = ingressosData.stream().mapToInt(Ingresso::getPreco).min().orElse(0);

        totalGastoLabel.setText("R$ " + total);
        maiorGastoLabel.setText("R$ " + max);
        menorGastoLabel.setText("R$ " + min);
    }

    @FXML
    private void filterAction(ActionEvent event) {
        String selectedPeca = pecaFilter.getValue();

        if (selectedPeca == null || selectedPeca.isEmpty()) {
            ingressosTable.setItems(ingressosData);
        } else {
            List<Ingresso> filtered = ingressosData.stream()
                    .filter(i -> i.getPecaNome().equals(selectedPeca))
                    .collect(Collectors.toList());
            ingressosTable.setItems(FXCollections.observableArrayList(filtered));
        }

        updateStats();
    }

    @FXML
    private void clearFilterAction(ActionEvent event) {
        pecaFilter.setValue(null);
        ingressosTable.setItems(ingressosData);
        updateStats();
    }

    @FXML
    private void closeAction(ActionEvent event) {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setResizable(false);
    }
}