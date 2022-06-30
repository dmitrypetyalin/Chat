package ru.gb.chat.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gb.chat.client.net.MessageProcessor;
import ru.gb.chat.client.net.NetworkService;
import ru.gb.chat.enums.Command;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static ru.gb.chat.constants.MessageConstants.REGEX;
import static ru.gb.chat.enums.Command.*;

/**
 * 24.05.2022 19:43
 *
 * @author PetSoft
 */
public class ChatController implements Initializable, MessageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);
    private static final String BROADCAST_CONTACT = "ALL";
    @FXML
    private VBox changeNickPanel;
    @FXML
    private TextField newNickField;
    @FXML
    private VBox changePasswordPanel;
    @FXML
    private PasswordField oldPassField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private VBox loginPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox mainPanel;
    @FXML
    private TextArea chatArea;
    @FXML
    private ListView contacts;
    @FXML
    private TextField inputField;
    @FXML
    private Button btnSend;
    private NetworkService networkService;
    private String user;

    public void mockAction(ActionEvent actionEvent) {
        System.out.println("mock");
    }

    public void closeApplication(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            String text = inputField.getText();
            if (text == null || text.isBlank()) {
                return;
            }
            String selectedItem = String.valueOf(contacts.getSelectionModel().getSelectedItems());
            String recipient = selectedItem.substring(1, selectedItem.length() - 1);
            if (recipient.equals(BROADCAST_CONTACT)) {
                networkService.sendMessage(BROADCAST_MESSAGE.getCommand() + REGEX + text);
            } else {
                networkService.sendMessage(PRIVATE_MESSAGE.getCommand() + REGEX + recipient + REGEX + text);
            }
            inputField.clear();
        } catch (IOException e) {
            showError("Network error");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
        alert.showAndWait();
    }

    public void helpAction(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(URI.create("https://github.com/dmitrypetyalin/Chat/blob/chat-client/Help.txt"));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void aboutAction(ActionEvent actionEvent) {
        String message = "\tPetsoft\n\t" + "Version 1.0(Beta)\n\t" +
                "This product is licensed under the Petsoft Software License to: \n\t" +
                System.getProperty("user.name");

        final Stage dialog = new Stage();
        dialog.setTitle("About Chat");
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(30);
        dialogVbox.getChildren().add(new Text(message));
        dialogVbox.setAlignment(Pos.CENTER_LEFT);
        Scene dialogScene = new Scene(dialogVbox, 500, 100);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkService = new NetworkService(this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseMessage(message));
    }

    private void parseMessage(String message) {
        String[] split = message.split(REGEX);
        Command command = Command.getByCommand(split[0]);
        switch (command) {
            case AUTH_OK -> authOk(split);
            case ERROR_MESSAGE -> showError(split[1]);
            case LIST_USERS -> parseUsers(split);
            case CHANGE_NICK_OK -> handleChangeNick(split[1]);
            default -> chatArea.appendText(split[1] + System.lineSeparator());
        }
    }

    private void handleChangeNick(String newNick) {
        user = newNick;
        returnToChat(null);
    }

    private void parseUsers(String[] split) {
        List<String> contact = new ArrayList<>(Arrays.asList(split));
        contact.set(0, BROADCAST_CONTACT);
        contacts.setItems(FXCollections.observableList(contact));
        contacts.getSelectionModel().selectFirst();
    }

    private void authOk(String[] split) {
        user = split[1];
        loginPanel.setVisible(false);
        mainPanel.setVisible(true);
    }

    public void sendChangeNick(ActionEvent actionEvent) {
        String newNick = newNickField.getText();
        if(newNick.isBlank()) {
            return;
        }
        try {
            networkService.sendMessage(CHANGE_NICK.getCommand() + REGEX + newNick);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Network error");
        }
    }

    public void returnToChat(ActionEvent actionEvent) {
        mainPanel.setVisible(true);
        changeNickPanel.setVisible(false);
    }

    public void sendChangePass(ActionEvent actionEvent) {
        //TODO
    }

    public void sendAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        String msg = AUTH_MESSAGE.getCommand() + REGEX + login + REGEX + password;
        try {
            if (!networkService.isConnected()) {
                networkService.connect();
            }
            networkService.sendMessage(msg);
        } catch (IOException e) {
            showError("Network error");
        }
    }

    public void showChangeNickPanel(ActionEvent actionEvent) {
        changeNickPanel.setVisible(true);
        mainPanel.setVisible(false);
    }
}
