package controller.professor;

import controller.exception.AllertExceptionController;
import model.DAO.professor.ProfessorDAOImpl;
import model.dominio.Professor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;


public class ControllerProfessor implements Initializable {
    @FXML private TableView<Professor> tableViewProfessor;

    @FXML private TableColumn<Professor, String> tabelaColunaProfessorNome;
    @FXML private TableColumn<Professor, String> tabelaColunaProfessorCpf;

    @FXML private Label labelProfessorNome;
    @FXML private Label labelProfessorMatricula;
    @FXML private Label labelProfessorCpf;
    @FXML private Label labelProfessorEmail;
    @FXML private Label labelProfessorDataNascimento;
    @FXML private Label labelProfessorRg;

    private List<Professor> listProfessor;
    private ObservableList<Professor> observableListProfessor;

    private ProfessorDAOImpl pDAO;

    public void initialize(URL location, ResourceBundle resources) {
        pDAO = new ProfessorDAOImpl();

        listProfessor = pDAO.listar(Professor.class);

        carregaTableViewProfessor();

        // Abaixo temos uma função lambda em java. Do lado esquerdo é os parâmetros e do lado direito é a função.
        tableViewProfessor.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionaItemViewProfessor(newValue)
        );

    }

    private void carregaTableViewProfessor() {
        tabelaColunaProfessorNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        tabelaColunaProfessorCpf.setCellValueFactory(new PropertyValueFactory<>("Cpf"));

        observableListProfessor = FXCollections.observableArrayList(listProfessor);
        tableViewProfessor.setItems(observableListProfessor);

//        for (Disciplina disciplina : listProfessor.get(0).getDisciplinas()) {
//            if (disciplina != null)
//                System.out.println("O professor " + listProfessor.get(0).getNome() + " Leciona: " + disciplina.getNome());
//        }
    }

    // Recebe um professor, pois o tableView é do tipo Professor.
    private void selecionaItemViewProfessor(Professor professor) {

        if (professor == null) {
            professor = new Professor();
        }

        labelProfessorNome.setText(professor.getNome());
        labelProfessorEmail.setText(professor.getEmail());
        labelProfessorDataNascimento.setText(professor.getDataNascimento());
        labelProfessorRg.setText(professor.getRg());
        labelProfessorCpf.setText(professor.getDecoratedCpf());
        labelProfessorMatricula.setText(professor.getMatricula());
    }

    @FXML
    public void handleButtonCadastrar () throws IOException, SQLException, ClassNotFoundException {
        Professor professor = new Professor();
        boolean btnSalvarClicado = showOpenCadastroProfessorDialog(professor, "Cadastro");

        if (btnSalvarClicado) {
            // System.out.println("Salvando professor no banco de dados.");

            listProfessor.add(professor);
            pDAO.inserir(professor);

            // Recarrega a página de cadastro de professor.
            carregaTableViewProfessor();
        }
    }

    @FXML
    public void handleButtonEditar () throws IOException, SQLException, ClassNotFoundException {
        Professor professor = tableViewProfessor.getSelectionModel().getSelectedItem();

        if (professor != null) {
            boolean btnSalvarClicado = showOpenCadastroProfessorDialog(professor, "Edicao");

            if (btnSalvarClicado) {
                System.out.println("Alterando professor no banco de dados.");

                // Recarrega a página de cadastro de professor.

                pDAO.alterar(professor);

                observableListProfessor.clear();
                carregaTableViewProfessor();

            }
        }
        else {
            AllertExceptionController.erro("Por favor, selecione um professor.");
        }

    }

    @FXML
    public void handleButtonExcluir() throws SQLException, ClassNotFoundException {
        Professor professor = tableViewProfessor.getSelectionModel().getSelectedItem();

        if (professor != null) {
            boolean deletar;
            deletar = AllertExceptionController.confirmation("Atencao. Esse professor sera deletado. Deseja mesmo fazer isso?");

            if (deletar) {
                System.out.println("Deletando professor no banco de dados.");

                listProfessor.remove(professor);
                pDAO.deletar(professor);


                // Recarrega a página de cadastro de professor.
                carregaTableViewProfessor();
            }
        }
        else {
            AllertExceptionController.erro("Por favor, selecione um professor.");
        }
    }

    private boolean showOpenCadastroProfessorDialog(Professor professor, String action) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ControllerProfessorDialogInsercao.class.getClassLoader().getResource("professor/insercao_professor.fxml"));

        AnchorPane paginaDialogoCadastro = fxmlLoader.load();

        // Cria um Estágio de Diálogo.
        Stage dialogStage = new Stage();
        dialogStage.setTitle(action + " de Professor");
        Scene scene = new Scene(paginaDialogoCadastro);
        dialogStage.setScene(scene);

        // Seta a caixa de dialogo no controller e o professor.
        ControllerProfessorDialogInsercao controller = fxmlLoader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProfessor(professor);

        // Mostra a tela de dialogo que foi toda carregada e espera ser fechada.
        dialogStage.showAndWait();

        return controller.isBtnSalvarClicado();

    }


}
