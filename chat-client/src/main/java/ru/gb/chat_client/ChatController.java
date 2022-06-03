package ru.gb.chat_client;

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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 24.05.2022 19:43
 *
 * @author PetSoft
 */
public class ChatController implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);
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

    public void mockAction(ActionEvent actionEvent) {
        System.out.println("mock");
    }

    public void closeApplication(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sendMessage(ActionEvent actionEvent) {
        String text = inputField.getText();
        if (text == null || text.isBlank()) {
            return;
        }

        String contact = String.valueOf(contacts.getSelectionModel().getSelectedItems());
        if (contact.equals("[]")) {
            contact = "Broadcast:";
        }

        chatArea.appendText(contact + text.trim() + System.lineSeparator());
        inputField.clear();
    }

    public void helpAction(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(URI.create("https://github.com/dmitrypetyalin/Chat/blob/chat-client/Help.txt"));
        } catch(IOException e) {
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
        List<String> names = List.of("Bob", "Bill", "Joe", "Keaton");
        contacts.setItems(FXCollections.observableList(names));
    }
}
