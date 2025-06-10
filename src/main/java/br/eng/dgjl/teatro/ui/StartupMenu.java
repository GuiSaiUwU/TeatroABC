package br.eng.dgjl.teatro.ui;

import br.eng.dgjl.teatro.Main;
import br.eng.dgjl.teatro.classes.Ingresso;
import br.eng.dgjl.teatro.classes.Usuario;
import br.eng.dgjl.teatro.classes.Utility;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import net.synedra.validatorfx.TooltipWrapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static br.eng.dgjl.teatro.Main.*;

public class StartupMenu extends Application {

    private Validator validadorCadastro = new Validator();
    private Validator validadorLogin = new Validator();

    private StartupController controller;

    public StartupMenu(StartupController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) throws IOException {
        controller.btnCadastro.setOnAction(
                this::cadastroAction
        );
        controller.btnLogin.setOnAction(
                actionEvent -> {
                    /*
                     * Quando carregamos cenas novas existe a possibilidade de ocorrer algum erro.
                     * Pois as Views serem arquivos.
                     */
                    try {
                        loginAction(actionEvent);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        /*
         * Injetando os validadores para os campos de entrada de texto (CPF, Telefone e Nome)
         * Referência: https://github.com/effad/ValidatorFX?tab=readme-ov-file#example
         */
        validadorLogin.createCheck()
                .dependsOn("CPF", controller.fieldCPFLogin.textProperty())
                .withMethod(c -> {
                    if (!((String)c.get("CPF")).isEmpty()) {
                        if (!Utility.checkCPF(c.get("CPF")) && !c.get("CPF").equals("ADMIN")) {
                            c.error("CPF inválido.");
                        }
                    }
                })
                .decorates(controller.fieldCPFLogin)
                .immediate();

        validadorCadastro.createCheck()
                .dependsOn("CPF", controller.fieldCPFCadastro.textProperty())
                .withMethod(c -> {
                    if (!((String)c.get("CPF")).isEmpty()) {
                        if (!Utility.checkCPF(c.get("CPF"))) {
                            c.error("CPF inválido.");
                        }
                    }
                })
                .decorates(controller.fieldCPFCadastro)
                .immediate();

        validadorCadastro.createCheck()
                .dependsOn("Nome", controller.fieldNomeCadastro.textProperty())
                .withMethod(c -> {
                    String nome = c.get("Nome");
                    if (!nome.isEmpty()) {
                        if (!Utility.checkName(nome)) {
                            c.error("Nome contém caracteres inválidos.");
                        }
                    }
                })
                .decorates(controller.fieldNomeCadastro)
                .immediate();

        validadorCadastro.createCheck()
                .dependsOn("Telefone", controller.fieldTelefoneCadastro.textProperty())
                .withMethod(c -> {
                    String telefone = c.get("Telefone");
                    if (!telefone.isEmpty()) {
                        if (!Utility.checkTelefone(telefone)) {

                            c.error("O telefone deve ser escrito com o DDD e o 9, contendo 11 dígitos no total!");
                        }
                    }

                })
                .decorates(controller.fieldTelefoneCadastro)
                .immediate();

        // Wrapper do ValidatorFX para fazer os botões desativarem ao terem algum erro
        TooltipWrapper<Button> btnLoginEncapsulado = new TooltipWrapper<>(
                controller.btnLogin,
                validadorLogin.containsErrorsProperty(),
                Bindings.concat("Não é possivel logar:\n", validadorLogin.createStringBinding())
        );

        TooltipWrapper<Button> btnCadastroEncapsulado = new TooltipWrapper<>(
                controller.btnCadastro,
                validadorCadastro.containsErrorsProperty(),
                Bindings.concat("Não é possivel cadastrar-se:\n", validadorCadastro.createStringBinding())
        );

        /*
         * Quando utilizamos o wrapper do ValidatorFX
         * É necessário adicionar os componentes devolta
         */
        GridPane.setMargin(btnLoginEncapsulado, new Insets(0, 0, 35,0));
        GridPane.setMargin(btnCadastroEncapsulado, new Insets(0, 0, 35,0));
        controller.gridPane.add(btnLoginEncapsulado, 2, 7);
        controller.gridPane.add(btnCadastroEncapsulado, 0, 9);
        
        stage.show();
    }

    public void cadastroAction(ActionEvent actionEvent) {
        // Pegando os valores da interface gráfica. 🥶
        String nome = controller.fieldNomeCadastro.getText();
        String senha = controller.fieldSenhaCadastro.getText();
        String cpf = controller.fieldCPFCadastro.getText();
        String endereco = controller.fieldEnderecoCadastro.getText();
        String telefone = controller.fieldTelefoneCadastro.getText();

        // É preciso converter o tipo de data que o JavaFx provém para Date
        // Pq se não vai dar problema na hora de salvar em JSON 😎
        LocalDate ld = controller.fieldDataCadastro.getValue();
        Date dataNascimento = new Date();
        
        // Checagem de erros
        StringBuilder erros = new StringBuilder();
        if (ld != null) {
	        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                dataNascimento = Date.from(instant);
                if (ld.isAfter(LocalDate.now())) {
		    erros.append("A data de aniversario nao pode ser depois de hoje.\n");
		}
		if (ld.isBefore(LocalDate.now().minusYears(120))) {
		    erros.append("O senhor ta bem velhinho né?.\n");
		}
        } else {
                erros.append("Data vazia.\n");
        }
        
        if (usuarios.stream().anyMatch(x -> x.getCPF().equals(cpf)))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro - Cadastro");
            alert.setHeaderText("Não foi possivel realizar o Cadastro:");
            alert.setContentText("CPF já cadastrado.");
            alert.show();
            return;
        }

        if (nome.isEmpty()) {
            erros.append("Nome vazio.\n");
        }
        if (senha.isEmpty()) {
            erros.append("Senha vazia.\n");
        }
        if (endereco.isEmpty()) {
            erros.append("Endereço vazio.\n");
        }
        if (telefone.isEmpty()) {
            erros.append("Telefone vazio.\n");
        }

        // Fim da checagem de erros, e criação do alerta de erros caso aja.
        if (!erros.toString().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro - Cadastro");
            alert.setHeaderText("Não foi possivel realizar o Cadastro:");
            alert.setContentText(erros.toString());
            alert.show();
            return;
        }

        // Adicionando o então criado usuário ao nosso "banco de dados"
        usuarios.add(new Usuario(
                nome, cpf, telefone, endereco, dataNascimento, senha, new ArrayList<Ingresso>()
        ));

        salvarUsuarios();

        // Limpando os campos da interface
        controller.fieldNomeCadastro.clear();
        controller.fieldSenhaCadastro.clear();
        controller.fieldCPFCadastro.clear();
        controller.fieldEnderecoCadastro.clear();
        controller.fieldTelefoneCadastro.clear();
        controller.fieldDataCadastro.getEditor().clear();
    }

    public void loginAction(ActionEvent actionEvent) throws Exception {
        String cpf = controller.fieldCPFLogin.getText();
        String senha = controller.fieldSenhaLogin.getText();

        for (Usuario usuario : usuarios) {
            if (usuario.getCPF().equals(cpf) && usuario.getSenha().equals(senha)) {
                usuarioLogado = usuario;
                break;
            }
        }
        if (usuarioLogado == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro - Login");
            alert.setHeaderText("Não foi possivel realizar o Login:");
            alert.setContentText("Houve um erro.");
            alert.show();
            return;
        }

        if (usuarioLogado.getIngressos() == null) {
            usuarioLogado.setIngressos(new ArrayList<>());
        }

        if (usuarioLogado.getNome().equals("ADMIN")) {
            /*Caso seja o ADMIN é carregado a view de admin, não a de compra.*/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminView.fxml"));
            Parent root = loader.load();
            AdminMenu adminMenu = new AdminMenu(loader.getController());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            adminMenu.start(stage);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompraView.fxml"));
            Parent root = loader.load();
            CompraMenu compraMenu = new CompraMenu(loader.getController());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            compraMenu.start(stage);
        }
    }

    @Override
    public void stop() {
        Main.salvarUsuarios();
    }
}
