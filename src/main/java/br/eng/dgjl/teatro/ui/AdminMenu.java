package br.eng.dgjl.teatro.ui;


import br.eng.dgjl.teatro.Main;
import br.eng.dgjl.teatro.classes.Cadeira;
import br.eng.dgjl.teatro.classes.Ingresso;
import br.eng.dgjl.teatro.classes.Peca;
import br.eng.dgjl.teatro.classes.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.eng.dgjl.teatro.Main.*;
import static br.eng.dgjl.teatro.ui.CompraMenu.criarPecas;
import static br.eng.dgjl.teatro.ui.CompraMenu.pecas;

public class AdminMenu extends Application {

    private AdminController controller;
    public AdminMenu(AdminController controller) {
        this.controller = controller;
    }

    class PecaInfo {
        String nome;
        int valorArrecadado;
        int ingressosVendidos;

        public PecaInfo(String nome, Integer valorArrecadado, int ingressosVendidos) {
            this.nome = nome;
            this.valorArrecadado = valorArrecadado;
            this.ingressosVendidos = ingressosVendidos;
        }
    }

    class SessaoInfo {
        String nome;
        int valorArrecadado;
        int ingressosVendidos;

        public SessaoInfo(String nome, int valorArrecadado, int ingressosVendidos) {
            this.nome = nome;
            this.valorArrecadado = valorArrecadado;
            this.ingressosVendidos = ingressosVendidos;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        criarPecas(); /* É necessário pois o usuário loga as informações das cadeiras estão vazias */
        controller.imprimirBtn.setOnAction(
                this::imprimirAction
        );
        List<Ingresso> ingressos = usuarios.stream().map(Usuario::getIngressos).flatMap(List::stream).toList();

        List<PecaInfo> pecasInfos = new ArrayList<>(
                pecas.stream()
                .map(Peca::getNome).collect(Collectors.toSet()).stream()
                .map(nome -> new PecaInfo(nome, 0, 0)).toList()
        );

        List<SessaoInfo> sessaosInfos = new ArrayList<>();
        sessaosInfos.add(new SessaoInfo("Manhã", 0, 0));
        sessaosInfos.add(new SessaoInfo("Tarde", 0, 0));
        sessaosInfos.add(new SessaoInfo("Noite", 0, 0));


        ingressos.forEach(ingresso -> {
            /*
             * Propagando as informações dos ingressos para as listas de informações
             * Basicamente iterando sobre os igrenssos e adicionando suas informações as peças / sessões relativas
             */
            String pecaNome = ingresso.getPecaNome();
            pecasInfos.stream().filter(info -> info.nome.equals(pecaNome)).forEach(
                info -> {
                    info.ingressosVendidos += 1;
                    info.valorArrecadado += ingresso.getPreco();
                }
            );

            // Sessões
            String sessaoNome = ingresso.getSessaoNome();
            sessaosInfos.stream().filter(info -> info.nome.equals(sessaoNome)).forEach(
                    info -> {
                        info.ingressosVendidos += 1;
                        info.valorArrecadado += ingresso.getPreco();
                    }
            );
        });

        /*
         * Parte responsável da coleta das informações sobre Peça mais XXX, Peça menos YYY
         * Como podem ver eu amo as stream do Java. 🤑
         */
        // Peças
        Optional<PecaInfo> pecaMaisLucrativaCMP = pecasInfos.stream()
                .max(Comparator.comparing(x -> x.valorArrecadado));

        Optional<PecaInfo> pecaMenosLucrativaCMP = pecasInfos.stream()
                .min(Comparator.comparing(x -> x.valorArrecadado));

        Optional<PecaInfo> pecaMaisOcupadaCMP = pecasInfos.stream()
                .max(Comparator.comparing(x -> x.ingressosVendidos));

        Optional<PecaInfo> pecaMenosOcupadaCMP = pecasInfos.stream()
                .min(Comparator.comparing(x -> x.ingressosVendidos));

        // Sessões
        Optional<SessaoInfo> sessaoMaisLucrativaCMP = sessaosInfos.stream()
                .max(Comparator.comparing(x -> x.valorArrecadado));

        Optional<SessaoInfo> sessaoMenosLucrativaCMP = sessaosInfos.stream()
                .min(Comparator.comparing(x -> x.valorArrecadado));

        Optional<SessaoInfo> sessaoMaisOcupadaCMP = sessaosInfos.stream()
                .max(Comparator.comparing(x -> x.ingressosVendidos));

        Optional<SessaoInfo> sessaoMenosOcupadaCMP = sessaosInfos.stream()
                .min(Comparator.comparing(x -> x.ingressosVendidos));

        /* Colocando os valores nos textField */
        // Peças
        controller.pecaMaisLucrativa.setText(pecaMaisLucrativaCMP.isPresent() ? pecaMaisLucrativaCMP.get().nome : "Não foi possivel determinar.");
        controller.pecaMenosLucrativa.setText(pecaMenosLucrativaCMP.isPresent() ? pecaMenosLucrativaCMP.get().nome : "Não foi possível determinar.");
        controller.pecaMaisOcupada.setText(pecaMaisOcupadaCMP.isPresent() ? pecaMaisOcupadaCMP.get().nome : "Não foi possivel determinar.");
        controller.pecaMenosOcupada.setText(pecaMenosOcupadaCMP.isPresent() ? pecaMenosOcupadaCMP.get().nome : "Não foi possivel determinar.");

        // Sessões
        controller.sessaoMaisLucrativa.setText(sessaoMaisLucrativaCMP.isPresent() ? sessaoMaisLucrativaCMP.get().nome : "Não foi possivel determinar.");
        controller.sessaoMenosLucrativa.setText(sessaoMenosLucrativaCMP.isPresent() ? sessaoMenosLucrativaCMP.get().nome : "Não foi possível determinar.");
        controller.sessaoMaisOcupada.setText(sessaoMaisOcupadaCMP.isPresent() ? sessaoMaisOcupadaCMP.get().nome : "Não foi possivel determinar.");
        controller.sessaoMenosOcupada.setText(sessaoMenosOcupadaCMP.isPresent() ? sessaoMenosOcupadaCMP.get().nome : "Não foi possivel determinar.");


        /*
        "Placar": {
            "Sessao Menos Lucrativa": "Manhã",
            "Peca Menos Lucrativa": "Harry Potter e a criança amaldiçoada",
            "Sessao Menos Ocupada": "Manhã",
            ...
        },
        "Romeu e Julieta": {
            "QtdCadeiras": 5,
            "Vendas": {
              "Camarote 1": {
                "Tarde": {
                  "Receita": 400,
                  "qtdCadeiras": 5
                },
                ...
              }
            },
            "Receita": 400,
            "Nome": "Romeu e Julieta"
        },
        ...
         */
        /*
         * Ganhos é o hashmap responsável por gerar o relatório.
         * Sim, sim, deveria ser uma classe própria para facilitar a implementação.
         * Entretanto, o software, com data prevista de entrega, precisa de algo funcionando, não perfeito. 😝
         */
        Map<String, Map<String, Object>> ganhos = new HashMap<>();
        Gson gsonGanhos = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting().create();

        Map<String, Object> tops = new HashMap<>();
        tops.put("Peca Mais Lucrativa", controller.pecaMaisLucrativa.getText());
        tops.put("Peca Menos Lucrativa", controller.pecaMenosLucrativa.getText());
        tops.put("Peca Mais Ocupada", controller.pecaMaisOcupada.getText());
        tops.put("Peca Menos Ocupada", controller.pecaMenosOcupada.getText());
        tops.put("Sessao Mais Lucrativa", controller.sessaoMaisLucrativa.getText());
        tops.put("Sessao Menos Lucrativa", controller.sessaoMenosLucrativa.getText());
        tops.put("Sessao Mais Ocupada", controller.sessaoMaisOcupada.getText());
        tops.put("Sessao Menos Ocupada", controller.sessaoMenosOcupada.getText());

        ganhos.put("Placar", tops);

        /* Propagando as informações das peças e propagando para o hashmap */
        for(Peca peca : pecas) {
            List<Cadeira> cadeiras = peca.getCadeiraList();
            String pecaNome = peca.getNome();
            if (!ganhos.containsKey(pecaNome)) {
                ganhos.put(pecaNome, new HashMap<>());
            }
            Map<String, Object> ganhosMap = ganhos.get(pecaNome);
            ganhosMap.put("Nome", peca.getNome());
            ganhosMap.put("Receita",
                    cadeiras.stream()
                            .map(c -> c.isComprada() ? c.getArea().getPreco() : 0).toList().stream()
                            .reduce(0, Integer::sum)
            );
            ganhosMap.put("QtdCadeiras",
                    cadeiras.stream()
                            .map(c -> c.isComprada() ? 1 : 0).toList().stream()
                            .reduce(0, Integer::sum)
            );

            // Parte reponsável por colocar as informações relativas as áreas
            ganhosMap.put("Vendas", new HashMap<String, Map<String, Integer>>());
            Map<String, Map<String, Map<String, Integer>>> VendasMap = (Map<String, Map<String, Map<String, Integer>>>) ganhosMap.get("Vendas");
            for (Cadeira cadeira: cadeiras) {
                if (!cadeira.isComprada()) continue;

                String areaNome = cadeira.getArea().getNome();
                String sessaoNome = cadeira.getSessao();

                if (!VendasMap.containsKey(areaNome))  {
                    VendasMap.put(areaNome, new HashMap<>());
                }

                Map<String, Map<String, Integer>> sessoesMap = VendasMap.get(areaNome);

                if (!sessoesMap.containsKey(sessaoNome))  {
                    sessoesMap.put(sessaoNome, new HashMap<>());
                }
                Map<String, Integer> sessaoMap = sessoesMap.get(sessaoNome);

                sessaoMap.put("Receita", sessaoMap.containsKey("Receita") ?
                        sessaoMap.get("Receita") + cadeira.getArea().getPreco() : cadeira.getArea().getPreco()
                );

                sessaoMap.put("qtdCadeiras", sessaoMap.containsKey("qtdCadeiras") ?
                        sessaoMap.get("qtdCadeiras") + 1 : 1
                );

            }

        }

        controller.ganhosArea.setText(gsonGanhos.toJson(ganhos));
    }

    public void imprimirAction(ActionEvent event) {
        /*  */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        fileChooser.setInitialFileName("relatorio");

        File file = fileChooser.showSaveDialog(controller.ganhosArea.getScene().getWindow());
        if (file != null) {
            try {
                file.createNewFile();
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(controller.ganhosArea.getText());
                myWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        Main.salvarUsuarios();
    }
}
