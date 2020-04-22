package ru.hse.grpc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.function.BiFunction;

public class Chat implements Ui {
    private final ObservableList<String> msgs = FXCollections.<String>observableArrayList();
    private final String userName;
    private BiFunction<String, String, Void> callback;

    public Chat(String userName) {
        this.userName = userName;
    }

    public void start(Stage stage) {
        ListView<String> seasons = new ListView<>(msgs);
        seasons.setOrientation(Orientation.VERTICAL);

        GridPane grid = new GridPane();

        TextField message = new TextField();
        Button send = new Button("send");
        send.setOnAction(e -> {
            msgs.add("[" + userName + "]" + ": " + message.getCharacters().toString());
            callback.apply(userName, message.getCharacters().toString());
            message.clear();
            message.requestFocus();
        });

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(80);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(10);

        grid.getColumnConstraints().addAll(column1, column2);

        grid.add(message, 0, 0);
        grid.add(send, 1, 0);

        BorderPane border = new BorderPane();
        border.setBottom(grid);
        border.setCenter(seasons);

        Scene scene = new Scene(border, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void displayMsg(Model.ChatMessage msg) {
        msgs.addAll(msg.getTimestamp() + "[" + msg.getName() + "]" + ": " + msg.getText());
    }

    @Override
    public void setCallback(BiFunction<String, String, Void> callback) {
        this.callback = callback;
    }
}