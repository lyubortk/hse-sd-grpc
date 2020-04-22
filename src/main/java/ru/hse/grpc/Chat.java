package ru.hse.grpc;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.beans.EventHandler;

public class Chat {
    public void start(Stage stage) {

        ObservableList<String> seasonList = FXCollections.<String>observableArrayList("Spring", "Summer", "Fall", "Winter");

        ListView<String> seasons = new ListView<>(seasonList);
        seasons.setOrientation(Orientation.VERTICAL);

        GridPane grid = new GridPane();

        TextField message = new TextField();
        Button send = new Button("send");
        send.setOnAction(e -> {
            seasonList.add(message.getCharacters().toString());
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

}