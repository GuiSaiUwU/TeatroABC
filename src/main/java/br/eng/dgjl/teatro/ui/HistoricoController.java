package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.classes.Ingresso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

import static br.eng.dgjl.teatro.Main.gson;
import static br.eng.dgjl.teatro.Main.usuarioLogado;

public class HistoricoController {
    @FXML private ChoiceBox<String> pecaFilter;
    @FXML private ChoiceBox<String> sessaoFilter;
    @FXML private ChoiceBox<String> areaFilter;

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
        pecaColumn.setCellValueFactory(new PropertyValueFactory<>("pecaNome"));
        sessaoColumn.setCellValueFactory(new PropertyValueFactory<>("sessaoNome"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaNome"));
        cadeiraColumn.setCellValueFactory(new PropertyValueFactory<>("cadeiraPosicao"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));

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

        carregarDados();
    }

    private void carregarDados() {
        List<Ingresso> ingressos = usuarioLogado.getIngressos();
        ingressosData = FXCollections.observableArrayList(ingressos);
        ingressosTable.setItems(ingressosData);

        pecaFilter.setItems(FXCollections.observableArrayList(
                ingressos.stream().map(Ingresso::getPecaNome).distinct().sorted().toList()
        ));
        sessaoFilter.setItems(FXCollections.observableArrayList(
                ingressos.stream().map(Ingresso::getSessaoNome).distinct().sorted().toList()
        ));
        areaFilter.setItems(FXCollections.observableArrayList(
                ingressos.stream().map(Ingresso::getAreaNome).distinct().sorted().toList()
        ));

        atualizarDados();
    }

    private void atualizarDados() {
        if (ingressosData.isEmpty()) {
            maiorGastoLabel.setText("R$ 0,00");
            menorGastoLabel.setText("R$ 0,00");
            totalGastoLabel.setText("R$ 0,00");
            return;
        }

        int total = calcGasto();
        int maiorGasto = ingressosData.stream().mapToInt(Ingresso::getPreco).max().orElse(0);
        int menorGasto = ingressosData.stream().mapToInt(Ingresso::getPreco).min().orElse(0);

        totalGastoLabel.setText("R$ " + total);
        maiorGastoLabel.setText("R$ " + maiorGasto);
        menorGastoLabel.setText("R$ " + menorGasto);
    }

    /*
    * Temos que chamar a função recursiva com um valor inicial
    * Pois o Java não permite iniciar variaveis nos parametros
    * Não da para fazer calcGasto(int valorAtual = 0, indice = 0);
    */
    int calcGasto() {
        return calcGasto(0, 0);
    }

    int calcGasto(int valorAtual, int indice) {
        /* Caso base */
        if (indice >= ingressosData.size()) {
            return valorAtual;
        }

        return calcGasto(valorAtual + ingressosData.get(indice).getPreco(), indice+1);
    }


    @FXML
    private void filterAction(ActionEvent event) {
        String pecaSelecionada = pecaFilter.getValue();
        String sessaoSelecionada = sessaoFilter.getValue();
        String areaSelecionada = areaFilter.getValue();

        List<Ingresso> filtrados = ingressosData.stream()
                .filter(i -> pecaSelecionada == null || pecaSelecionada.isEmpty() || i.getPecaNome().equals(pecaSelecionada))
                .filter(i -> sessaoSelecionada == null || sessaoSelecionada.isEmpty() || i.getSessaoNome().equals(sessaoSelecionada))
                .filter(i -> areaSelecionada == null || areaSelecionada.isEmpty() || i.getAreaNome().equals(areaSelecionada))
                .collect(Collectors.toList());

        ingressosTable.setItems(FXCollections.observableArrayList(filtrados));
        atualizarDados();
    }

    @FXML
    private void clearFilterAction(ActionEvent event) {
        pecaFilter.setValue(null);
        sessaoFilter.setValue(null);
        areaFilter.setValue(null);
        ingressosTable.setItems(ingressosData);
        atualizarDados();
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