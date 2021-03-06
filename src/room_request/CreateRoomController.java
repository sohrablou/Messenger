package room_request;

import Client.RoomHandler;
import Server.Room;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import objects.MasterClass;
import objects.User;
import objects.UserLabel;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by Adam on 8/23/2016.
 */
public class CreateRoomController implements Initializable {

    private ClassLoader classLoader = this.getClass().getClassLoader();

    private HashSet<User> onlineList;
    private HashSet<User> inviteList = new HashSet<>();

    private Room room;

    int key;

    String title = "";
    String password = "";
    String description = "";

    private boolean isPublic = false;
    private boolean isStatic = false;
    private boolean passwordProtected = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setGraphics();
        setPreferences();
        createListeners();
        populateList();

    }

    public void start(HashSet<User> onlineList){
        this.onlineList = onlineList;
        MasterClass.client.disable();
    }

    private void setGraphics() {
        ImageView closeIcon = new ImageView();
        closeIcon.setImage(new Image(classLoader.getResourceAsStream("Images/closeIcon.png")));
        closeIcon.setFitWidth(15);
        closeIcon.setFitHeight(15);
        closeIcon.setBlendMode(BlendMode.EXCLUSION);
        closeButton.setGraphic(closeIcon);

        ImageView minimizeIcon = new ImageView();
        minimizeIcon.setImage(new Image(classLoader.getResourceAsStream("Images/minimizeIcon.png")));
        minimizeIcon.setFitWidth(15);
        minimizeIcon.setFitHeight(15);
        minimizeIcon.setBlendMode(BlendMode.EXCLUSION);
        minimizeButton.setGraphic(minimizeIcon);

        bodyPanel.setBackground(new Background(new BackgroundFill(Color.rgb(235, 235, 235), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void setPreferences() {

        passwordField.setDisable(true);
        keyField.setDisable(true);




    }

    private void populateList(){
        Thread thread = new Thread(() ->{

            onlineList.forEach(child ->{
                UserLabel label = new UserLabel((User) child);
                label.setOnMouseClicked(Event ->{
                    if(Event.getClickCount() == 2){
                        System.out.println(((UserLabel) Event.getSource()).getUser().getAlias());
                        addToInviteList(((UserLabel) Event.getSource()).getUser());
                    }
                });

                currentlyOnlineBox.getChildren().add(label);

            });
            onlineLabel.setText("Currently Online: [" + onlineList.size() + "]");
        });
        Platform.runLater(thread);
    }

    private void addToInviteList(User user){
        if(!inviteList.contains(user)){
            inviteList.add(user);

            Thread thread = new Thread(() ->{
                UserLabel label = new UserLabel(user);
                label.setOnMouseClicked(Event ->{
                    if(Event.getClickCount() == 2) {
                        removeFromInviteList(label.getUser());
                        invitedBox.getChildren().remove(label);
                    }
                });
                invitedBox.getChildren().add(label);
                invitedLabel.setText("Pending Invites: [" + inviteList.size() + "]");
            });
            Platform.runLater(thread);
        }


    }

    private void removeFromInviteList(User user){
        inviteList.remove(user);
        invitedLabel.setText("Pending Invites: [" + inviteList.size() + "]");


    }



    private void createListeners() {

        closeButton.setOnAction(Event ->{
            closeWindow();
        });

        createRoomButton.setOnAction(Event ->{
            createRoom();
        });

        passwordCheckBox.setOnAction(Event ->{
            passwordProtected = passwordCheckBox.isSelected();
            passwordField.setDisable(!passwordProtected);
        });

        staticCheckBox.setOnAction(Event ->{
            isStatic = staticCheckBox.isSelected();
            keyField.setDisable(!isStatic);
        });

        publicCheckBox.setOnAction(Event ->{
            isPublic = publicCheckBox.isSelected();
        });

        keyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) {

                try {
                    int value = Integer.parseInt(newValue);
                    if (value > 50000 || value < -50000 || value == 0) {
                        keyField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    return;
                }

            } else {
                keyField.setText(oldValue);
            }

        });

    }

    private void saveAndClose(Room room) {
        RoomHandler.requestRoom(room);
        if(inviteList.size() > 0){
            RoomHandler.inviteGroup(inviteList, room);
        }


        closeWindow();
    }

    private void createRoom(){


        title = titleField.getText().trim();
        description = descriptionField.getText().trim();
        if(title.length() < 5){
            helpLabel.setText("Title must be at least 5 Characters.");
            titleField.requestFocus();
            return;
        }
        else{
            room = new Room(title);
            room.setStatic(isStatic);
            room.setPasswordProtected(passwordProtected);
            room.setPublic(isPublic);
            room.setDescription(description);

            if(passwordProtected){
                if(passwordField.getText().length() < 5){
                    helpLabel.setText("Password must be at least 5 Characters.");
                    passwordField.requestFocus();
                    return;
                }
                else{
                    password = passwordField.getText();
                    room.setPassword(password);
                }

            }

            if(isStatic){

                if(keyField.getText().length() < 3){
                    helpLabel.setText("Static Rooms require a unique key of at least 3 digits");
                    keyField.requestFocus();
                    return;
                }

                else {
                    key = Integer.parseInt(keyField.getText());
                    room.setKey(key);
                }

            }
            room.setOwner(MasterClass.user);
            saveAndClose(room);
        }




    }

    private void closeWindow() {
        MasterClass.client.enable();
        ((Stage) mainPanel.getScene().getWindow()).close();
    }



    //----------------------------------------------JAVAFX INITIALIZATION-----------------------------------------------

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label helpLabel;

    @FXML
    private Label onlineLabel;

    @FXML
    private Label invitedLabel;

    @FXML
    private AnchorPane bodyPanel;

    @FXML
    private Button closeButton;

    @FXML
    private Button createRoomButton;

    @FXML
    private VBox currentlyOnlineBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private VBox invitedBox;

    @FXML
    private TextField keyField;

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Button minimizeButton;

    @FXML
    private CheckBox passwordCheckBox;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox publicCheckBox;

    @FXML
    private CheckBox staticCheckBox;

    @FXML
    private ToolBar titleBar;

    @FXML
    private TextField titleField;

    @FXML
    private ImageView titleImageView;


    @FXML
    void initialize() {
        assert bodyPanel != null : "fx:id=\"bodyPanel\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert createRoomButton != null : "fx:id=\"createRoomButton\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert currentlyOnlineBox != null : "fx:id=\"currentlyOnlineBox\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert descriptionField != null : "fx:id=\"descriptionField\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert invitedBox != null : "fx:id=\"invitedBox\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert keyField != null : "fx:id=\"keyField\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert mainPanel != null : "fx:id=\"mainPanel\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert minimizeButton != null : "fx:id=\"minimizeButton\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert passwordCheckBox != null : "fx:id=\"passwordCheckBox\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert publicCheckBox != null : "fx:id=\"publicCheckBox\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert staticCheckBox != null : "fx:id=\"staticCheckBox\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert titleBar != null : "fx:id=\"titleBar\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert titleField != null : "fx:id=\"titleField\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";
        assert titleImageView != null : "fx:id=\"titleImageView\" was not injected: check your FXML file 'RoomRequestFXML.fxml'.";

    }

}
