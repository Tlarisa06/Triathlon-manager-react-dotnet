package ro.mpp.triathlon.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.mpp.triathlon.dto.ParticipantDTO;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.services.ITriathlonObserver;
import ro.mpp.triathlon.services.ITriathlonServices;
import java.util.List;

public class MainController implements ITriathlonObserver {
    private ITriathlonServices service;
    private Referee loggedReferee;

    @FXML private Label welcomeLabel;
    @FXML private TableView<ParticipantDTO> allParticipantsTable;
    @FXML private TableColumn<ParticipantDTO, String> colName;
    @FXML private TableColumn<ParticipantDTO, Integer> colTotalPoints;
    @FXML private TextField pointsField;

    @FXML private TableView<ParticipantDTO> reportTable;
    @FXML private TableColumn<ParticipantDTO, String> colReportName;
    @FXML private TableColumn<ParticipantDTO, Integer> colReportPoints;

    private ObservableList<ParticipantDTO> participantsModel = FXCollections.observableArrayList();
    private ObservableList<ParticipantDTO> reportModel = FXCollections.observableArrayList();

    public void setService(ITriathlonServices service, Referee referee) {
        this.service = service;
        this.loggedReferee = referee;
        welcomeLabel.setText("Arbitru: " + referee.getName());
        initData();
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTotalPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        allParticipantsTable.setItems(participantsModel);

        colReportName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colReportPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        reportTable.setItems(reportModel);
    }

    private void initData() {
       Platform.runLater(() -> {
            try {
                // 1. Toți participanții (alfabetic)
                List<ParticipantDTO> all = service.getParticipantsByEvent(-1);
                if (all != null) participantsModel.setAll(all);

                // 2. Raport proba mea
                List<ParticipantDTO> report = service.getParticipantsByEvent(loggedReferee.getIdEvent());
                if (report != null) reportModel.setAll(report);
            } catch (Exception e) {
                System.err.println("Eroare la încărcarea datelor: " + e.getMessage());
            }
        });
    }

    @FXML
    public void handleAddResult() {
        ParticipantDTO selected = allParticipantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Selectați un participant!").show();
            return;
        }

        try {
            int points = Integer.parseInt(pointsField.getText());
            service.addResult(loggedReferee.getId(), selected.getIdParticipant(), points);
            pointsField.clear();
            } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Punctajul trebuie să fie un număr!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            service.logout(loggedReferee, this);
            welcomeLabel.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateReceived() {
       Platform.runLater(this::initData);
    }
}