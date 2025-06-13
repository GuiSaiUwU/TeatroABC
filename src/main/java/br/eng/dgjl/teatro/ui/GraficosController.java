package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.classes.Cadeira;
import br.eng.dgjl.teatro.classes.Ingresso;
import br.eng.dgjl.teatro.classes.Peca;
import br.eng.dgjl.teatro.classes.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static br.eng.dgjl.teatro.Main.usuarios;
import static br.eng.dgjl.teatro.ui.CompraMenu.pecas;

public class GraficosController implements Initializable {

    @FXML
    private ComboBox<String> tipoGraficoComboBox;

    @FXML
    private ComboBox<String> dadosComboBox;

    @FXML
    private Button gerarGraficoBtn;

    @FXML
    private HBox graficoContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar opções do ComboBox para tipo de gráfico e dados
        ObservableList<String> tiposGraficos = FXCollections.observableArrayList("Gráfico de Pizza", "Gráfico de Barras");
        tipoGraficoComboBox.setItems(tiposGraficos);
        tipoGraficoComboBox.setValue(tiposGraficos.get(0));

        ObservableList<String> tiposDados = FXCollections.observableArrayList("Sessões", "Peças", "Áreas");
        dadosComboBox.setItems(tiposDados);
        dadosComboBox.setValue(tiposDados.get(0));

        gerarGraficoBtn.setOnAction(this::gerarGrafico);
    }

    private void gerarGrafico(ActionEvent event) {
        graficoContainer.getChildren().clear();

        String tipoGrafico = tipoGraficoComboBox.getValue();
        String tipoDados = dadosComboBox.getValue();

        if ("Gráfico de Pizza".equals(tipoGrafico)) {
            criarGraficoPizza(tipoDados);
        } else if ("Gráfico de Barras".equals(tipoGrafico)) {
            criarGraficoBarras(tipoDados);
        }
    }

    private void criarGraficoPizza(String tipoDados) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        switch (tipoDados) {
            case "Sessões":
                Map<String, Integer> sessaoData = getDadosSessoes();
                sessaoData.forEach((sessao, valor) ->
                        pieChartData.add(new PieChart.Data(sessao, valor)));
                break;

            case "Peças":
                Map<String, Integer> pecaData = getDadosPecas();
                pecaData.forEach((peca, valor) ->
                        pieChartData.add(new PieChart.Data(
                                peca.length() > 20 ? peca.substring(0, 20) + "..." : peca,
                                valor)
                        ));
                break;

            case "Áreas":
                Map<String, Integer> areaData = getDadosAreas();
                areaData.forEach((area, valor) ->
                        pieChartData.add(new PieChart.Data(area, valor)));
                break;
        }

        final Label caption = new Label("");

        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Gráfico de " + tipoDados);
        chart.setPrefSize(500, 400);
        chart.setLabelsVisible(true);

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        double total = 0;
                        for (PieChart.Data d : chart.getData()) {
                            total += d.getPieValue();
                        }
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        String text = String.format("%.1f%%", 100*data.getPieValue()/total) ;
                        caption.setText(text);
                    }
            );
        }

        graficoContainer.getChildren().add(chart);
    }

    private void criarGraficoBarras(String tipoDados) {
        BarChart<String, Number> barChart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        barChart.setTitle("Gráfico de " + tipoDados);
        barChart.setPrefSize(500, 400);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valor Arrecadado");

        switch (tipoDados) {
            case "Sessões":
                Map<String, Integer> sessaoData = getDadosSessoes();
                sessaoData.forEach((sessao, valor) ->
                        series.getData().add(new XYChart.Data<>(sessao, valor)));
                break;

            case "Peças":
                Map<String, Integer> pecaData = getDadosPecas();
                pecaData.forEach((peca, valor) ->
                        series.getData().add(new XYChart.Data<>(
                                peca.length() > 20 ? peca.substring(0, 20) + "..." : peca, valor)
                        ));
                break;

            case "Áreas":
                Map<String, Integer> areaData = getDadosAreas();
                areaData.forEach((area, valor) ->
                        series.getData().add(new XYChart.Data<>(area, valor)));
                break;
        }

        barChart.getData().add(series);
        graficoContainer.getChildren().add(barChart);
    }

    private Map<String, Integer> getDadosSessoes() {
        List<Ingresso> ingressos = usuarios.stream()
                .map(Usuario::getIngressos)
                .flatMap(List::stream)
                .toList();

        Map<String, Integer> sessaoValores = new HashMap<>();
        sessaoValores.put("Manhã", 0);
        sessaoValores.put("Tarde", 0);
        sessaoValores.put("Noite", 0);

        ingressos.forEach(ingresso -> {
            String sessaoNome = ingresso.getSessaoNome();
            int valorAtual = sessaoValores.getOrDefault(sessaoNome, 0);
            sessaoValores.put(sessaoNome, valorAtual + ingresso.getPreco());
        });

        return sessaoValores;
    }

    private Map<String, Integer> getDadosPecas() {
        List<Ingresso> ingressos = usuarios.stream()
                .map(Usuario::getIngressos)
                .flatMap(List::stream)
                .toList();

        Map<String, Integer> pecaValores = new HashMap<>();

        // Inicializar o Map com todas as peças
        pecas.stream()
                .map(Peca::getNome)
                .distinct()
                .forEach(nome -> pecaValores.put(nome, 0));

        ingressos.forEach(ingresso -> {
            String pecaNome = ingresso.getPecaNome();
            int valorAtual = pecaValores.getOrDefault(pecaNome, 0);
            pecaValores.put(pecaNome, valorAtual + ingresso.getPreco());
        });

        return pecaValores;
    }

    private Map<String, Integer> getDadosAreas() {
        Map<String, Integer> areaValores = new HashMap<>();

        for (Peca peca : pecas) {
            List<Cadeira> cadeiras = peca.getCadeiraList().stream()
                    .filter(Cadeira::isComprada)
                    .toList();

            for (Cadeira cadeira : cadeiras) {
                String areaNome = cadeira.getArea().getNome();
                int valorAtual = areaValores.getOrDefault(areaNome, 0);
                areaValores.put(areaNome, valorAtual + cadeira.getArea().getPreco());
            }
        }

        return areaValores;
    }
}
