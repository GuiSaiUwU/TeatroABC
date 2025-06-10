package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.Main;
import br.eng.dgjl.teatro.classes.Area;
import br.eng.dgjl.teatro.classes.Cadeira;
import br.eng.dgjl.teatro.classes.Ingresso;
import br.eng.dgjl.teatro.classes.Peca;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static br.eng.dgjl.teatro.Main.*;

public class CompraMenu extends Application {
    static List<Peca> pecas = new LinkedList<Peca>();

    /* Variaveis que são utilizadas para inicializar as Peças */
    static String[] pecasNome = {"Romeu e Julieta", "Harry Potter e a criança amaldiçoada", "Digital Circus"};
    static String[] sessoes = {"Manhã", "Tarde", "Noite"};
    static Area[] areas = {
            new Area("Balcao Nobre", 50, 250),
            new Area("Camarote 1", 10, 80),
            new Area("Camarote 2", 10, 80),
            new Area("Camarote 3", 10, 80),
            new Area("Camarote 4", 10, 80),
            new Area("Camarote 5", 10, 80),
            new Area("Frisa 1", 5, 120),
            new Area("Frisa 2", 5, 120),
            new Area("Frisa 3", 5, 120),
            new Area("Frisa 4", 5, 120),
            new Area("Frisa 5", 5, 120),
            new Area("Frisa 6", 5, 120),
            new Area("Plateia A", 25, 40),
            new Area("Plateia B", 100, 60)
    };

    private CompraController controller;
    private ListChangeListener<String> checkListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends String> c) {
            /* Função responsável por atualizar o texto de preço e ingressos totais */ 
            if (controller.areaBox.getValue() == null) return;

            Optional<Area> area = Arrays.stream(areas).filter(x -> x.getNome().equals(controller.areaBox.getValue())).findFirst();
            if (area.isEmpty()) return;

            ObservableList<String> checkados = controller.checkListView.getCheckModel().getCheckedItems();
            if (checkados.size() == 0) {
                controller.compraBtn.setDisable(true);
            } else {
                controller.compraBtn.setDisable(false);
            }
            controller.ingressosLabel.textProperty().set(String.valueOf(checkados.size()));
            controller.totalLabel.textProperty().set(String.valueOf(checkados.size() * area.get().getPreco()));
        }
    };

    public CompraMenu(CompraController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) throws Exception {
        criarPecas();

        controller.pecaBox.getItems().addAll(pecasNome);
        controller.pecaBox.setValue("Peça");

        /* Inserindo as  */
        controller.pecaBox.setOnAction(
                this::pecaBoxAction
        );
        controller.sessaoBox.setOnAction(
                this::sessaoBoxAction
        );
        controller.areaBox.setOnAction(
                this::areaBoxAction
        );
        controller.compraBtn.setOnAction(
                this::compraAction
        );
        controller.historicoBtn.setOnAction(
                this::historicoAction
        );
        controller.sessaoBox.setDisable(true);
        controller.sessaoBox.setValue("Sessões");
        controller.areaBox.setDisable(true);
        controller.areaBox.setValue("Área");
        controller.compraBtn.setDisable(true);

        stage.show();
    }

    public static void criarPecas() {
        for (String nomePeca : pecasNome) {
            List<Cadeira> cadeiras = new LinkedList<>();
            pecas.add(new Peca(nomePeca, sessoes, cadeiras));

            for (String sessao : sessoes) {
                for (Area area : areas) {
                    for (int i = 0; i < area.getQtdMaxCadeiras(); i++) {
                        Cadeira cadeira = new Cadeira(area, i, sessao);
                        cadeiras.add(cadeira);

                        int localI = i;
                        /*
                         * Se o ingresso e as cadeiras partilhassem de algum valor idêntico como um ID.
                         * Seria bem menos linhas, mas aqui estamos. 🙄
                         */
                        usuarios.forEach(user -> user.getIngressos().forEach(
                                ingresso -> {
                                    if (!ingresso.getAreaNome().equals(area.getNome())) return;
                                    if (!ingresso.getPecaNome().equals(nomePeca)) return;
                                    if (!ingresso.getSessaoNome().equals(sessao)) return;
                                    if (!(ingresso.getCadeiraPosicao() == localI)) return;

                                    cadeira.setComprada(true);
                                }
                        ));
                    }
                }
            }
        }
    }

    public void historicoAction(ActionEvent event) {
        /*
         *Função responsável por gerar os comprovantes de ingressos
         */
        StringBuilder ingressos = new StringBuilder();
        for (Ingresso ingresso : usuarioLogado.getIngressos()) {
            ingressos.append(String.format("Peça: %s.\n", ingresso.getPecaNome()));
            ingressos.append(String.format("Sessão: %s.\n", ingresso.getSessaoNome()));
            ingressos.append(String.format("Área: %s.\n", ingresso.getAreaNome()));
            ingressos.append(String.format("Cadeira: %02d.\n\n", ingresso.getCadeiraPosicao() + 1));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra - Histórico");
        alert.setHeaderText("Estes são os seus ingressos:");
        alert.setContentText(
                ingressos.isEmpty() ? "Sem ingressos!" :
                        usuarioLogado.getIngressos().size() < 5 ? ingressos.toString() : "Você possui muitos ingressos.\nPorfavor utilize o botão de imprimir (OK)."
        );
        Optional<ButtonType> alertBtn = alert.showAndWait();

        if (alertBtn.isEmpty()) return;
        if (!(alertBtn.get() == ButtonType.OK)) return;

        /* Parte responsavel por gerar o pop-up de salvar arquivo*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Comprovante");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        fileChooser.setInitialFileName(usuarioLogado.getNome());
        File file = fileChooser.showSaveDialog(controller.areaBox.getScene().getWindow());

        if (file != null) {
            try { /* Salvando o arquivo */
                file.createNewFile();
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(ingressos.toString());
                myWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void compraAction(ActionEvent event) {
        String pecaSelecionada = controller.pecaBox.getValue();
        String sessaoSelecionada = controller.sessaoBox.getValue();
        String areaSelecionada = controller.areaBox.getValue();

        /* Checagens para caso o ingresso seja inválido ( faltando informações ). */
        if (pecaSelecionada == null || sessaoSelecionada == null || areaSelecionada == null) {
            return;
        }
        if (pecaSelecionada.equals("Peças") || pecaSelecionada.isEmpty()) {
            return;
        }
        if (sessaoSelecionada.equals("Sessões") || sessaoSelecionada.isEmpty()) {
            return;
        }
        if (areaSelecionada.equals("Áreas") || areaSelecionada.isEmpty()) {
            return;
        }

        Peca peca = pecas.stream().filter(
                x -> x.getNome().equals(pecaSelecionada)
        ).findFirst().get();

        /* Variavel responsavel por gerar o texto da caixa de alerta */
        StringBuilder ingressos = new StringBuilder();
        ingressos.append(String.format("Peça: %s.\n", peca.getNome()));
        ingressos.append(String.format("Sessão: %s.\n", sessaoSelecionada));
        ingressos.append(String.format("Área: %s.\n", areaSelecionada));
        ingressos.append("Cadeiras: ").append(controller.checkListView.getCheckModel().getCheckedItems());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Compra - Confirmação");
        alert.setHeaderText("Deseja prosseguir com a compra?");
        alert.setContentText(ingressos.toString());
        Optional<ButtonType> alertBtn = alert.showAndWait();

        if (alertBtn.isEmpty()) {
            return;
        }
        if (!(alertBtn.get() == ButtonType.OK)) {
            return;
        }

        List<Integer> cadeirasSelecionadas = controller.checkListView.getCheckModel().getCheckedItems().stream().map(
                x -> Integer.parseInt(x.substring(8).trim()) - 1 // "Cadeira 02" -> 1 (o seu indice)
        ).toList();

        /* Filtrando as cadeiras para marca-las como compradas e instanciar os ingressos para o usuário. */
        peca.getCadeiraList().forEach(cadeira -> {
            if (cadeira.getArea().getNome().equals(areaSelecionada) &&
                    !cadeira.isComprada() &&
                    cadeira.getSessao().equals(sessaoSelecionada)
            ) {
                if (cadeirasSelecionadas.contains(cadeira.getPosicao())) {
                    cadeira.setComprada(true);
                    usuarioLogado.getIngressos().add(
                            new Ingresso(
                                    peca.getNome(),
                                    sessaoSelecionada,
                                    areaSelecionada,
                                    cadeira.getPosicao(),
                                    cadeira.getArea().getPreco(),
                                    usuarioLogado.getCPF()
                            )
                    );
                }
            }
        });

        atualizarCadeiras();
    }

    public void areaBoxAction(ActionEvent event) {
        atualizarCadeiras();
        atualizarImagem();
    }

    public void sessaoBoxAction(ActionEvent event) {
        /* 
         * Sempre que é adicionado itens a lista de uma ChoiceBox a sua onAction é chamada;
         * Então precisamos remover a função onAction e adicionar depois de mudar os valores.
         */
        EventHandler<ActionEvent> areaHandler = controller.areaBox.getOnAction();
        controller.areaBox.setOnAction(null);

        controller.areaBox.setDisable(false);
        controller.areaBox.getItems().clear();
        controller.areaBox.setValue("Área");
        controller.areaBox.getItems().addAll(Arrays.stream(areas).map(Area::getNome).toList());

        controller.areaBox.setOnAction(areaHandler);
        atualizarCadeiras();
        atualizarImagem();
    }

    public void pecaBoxAction(ActionEvent event) {
        controller.areaBox.setDisable(true);
        controller.areaBox.getItems().clear();
        controller.areaBox.setValue("Área");

        EventHandler<ActionEvent> sessaoHandler = controller.sessaoBox.getOnAction();
        controller.sessaoBox.setOnAction(null);

        controller.sessaoBox.setDisable(false);
        controller.sessaoBox.getItems().clear();
        controller.sessaoBox.setValue("Sessões");
        controller.sessaoBox.getItems().addAll(sessoes);

        controller.sessaoBox.setOnAction(sessaoHandler);
        atualizarCadeiras();
        atualizarImagem();
    }

    public void atualizarCadeiras() {
        /* Limpando os itens selecionados para que possa ser selecionado dnv :V */
        controller.checkListView.getCheckModel().clearChecks();
        controller.checkListView.getItems().clear();
        controller.checkListView.getCheckModel().getCheckedItems().removeListener(checkListener);

        /* Coletando as informações selecionadas */
        String pecaSelecionada = controller.pecaBox.getValue();
        String sessaoSelecionada = controller.sessaoBox.getValue();
        String areaSelecionada = controller.areaBox.getValue();

        /* Checagens para caso o ingresso seja inválido ( faltando informações ). */
        if (pecaSelecionada == null || sessaoSelecionada == null || areaSelecionada == null) {
            return;
        }
        if (pecaSelecionada.equals("Peças") || pecaSelecionada.isEmpty()) {
            return;
        }
        if (sessaoSelecionada.equals("Sessões") || sessaoSelecionada.isEmpty()) {
            return;
        }
        if (areaSelecionada.equals("Áreas") || areaSelecionada.isEmpty()) {
            return;
        }

        Peca peca = pecas.stream().filter(
                x -> x.getNome().equals(pecaSelecionada)
        ).findFirst().get();


        ObservableList<String> cadeirasObservable = FXCollections.observableArrayList();
        /*
         * Para criar a lista de opções de cadeiras é filtrado:
         * o nome da área selecionada, o nome da sessão, e se ela já foi comprada.
         * Lembrando que as cadeiras são pertencentes a uma peça;
         * então não é preciso filtrar por peça.
         */
        cadeirasObservable.addAll(
                peca.getCadeiraList().stream()
                        .filter(x -> x.getArea().getNome().equals(areaSelecionada))
                        .filter(x -> x.getSessao().equals(sessaoSelecionada))
                        .filter(x -> !x.isComprada())
                        .map(x ->
                            String.format("Cadeira %02d", x.getPosicao()+1))
                        .toList()
        );

        controller.checkListView.getItems().addAll(cadeirasObservable);
        controller.checkListView.getCheckModel().getCheckedItems().addListener(checkListener);
    }

    public void atualizarImagem() {
        /*
         * Função chamada sempre que uma área, peça é selecionada.
         * Ela faz com que a área selecionada seja colocada como imagem no canto superior direito.
         */
        String areaSelecionada = controller.areaBox.getValue();
        if (areaSelecionada != null) {
            if (areaSelecionada.isEmpty() || areaSelecionada.equals("Área")) {
                areaSelecionada = "Teatro PB";
            }
        } else {
            areaSelecionada = "Teatro PB";
        }

        controller.imageView.setImage(new Image(
                Objects.requireNonNull(CompraMenu.class.getResourceAsStream(String.format("images/%s.png", areaSelecionada)))
        ));
    }

    @Override
    public void stop() {
        Main.salvarUsuarios();
    }
}