package br.eng.dgjl.teatro;

import br.eng.dgjl.teatro.classes.Usuario;
import br.eng.dgjl.teatro.ui.CompraMenu;
import br.eng.dgjl.teatro.ui.StartupMenu;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class Main extends Application {
    public static LinkedList<Usuario> usuarios = new LinkedList<>();
    public static Usuario usuarioLogado = null;
    public static Gson gson = new Gson();

    // Alterar isso daq
    public static final String usuariosJsonFilePath = "C:\\Users\\GuiSai\\Desktop\\aulas\\pi\\TeatroABC\\src\\main\\resources\\br\\eng\\dgjl\\teatro\\usuarios.json";

    public void start(Stage stage) throws Exception {
        /* Carregando os usuarios salvos no json e salvando na variável usuarios */
        File f = new File(usuariosJsonFilePath);
        if (f.exists() && !f.isDirectory()) {
            FileReader fileReader = new FileReader(usuariosJsonFilePath);
            usuarios.addAll(
                    Arrays.asList(gson.fromJson(
                            new BufferedReader(fileReader),
                            Usuario[].class)
                    )
            );
            fileReader.close();
            inciarMenuStart(stage);
        } else {
            System.out.println("Usuarios.json com caminho inválido: " + usuariosJsonFilePath);
        }
    }

    @Override
    public void stop() throws IOException {
        salvarUsuarios();
    }

    public static void salvarUsuarios(){
        /*
         * Função responsavel pela salvamento do usuarios.JSON
         * */
        if (usuarios.isEmpty()) return;
        try {
            String a = gson.toJson(usuarios);
            FileWriter myWriter = new FileWriter(usuariosJsonFilePath);
            myWriter.write(a);
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void inciarMenuStart(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartupMenu.class.getResource("StartupView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        StartupMenu startupMenu = new StartupMenu(fxmlLoader.getController());
        stage.setTitle("Teatro - Entrada");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull
                (CompraMenu.class.getResourceAsStream("images/Logo.png"))
        ));
        startupMenu.start(stage);
    }
}
