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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.util.Arrays;
import java.util.Collections;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ObservableList<String> seasonList = FXCollections.<String>observableArrayList(getParameters().getRaw());

        ListView<String> seasons = new ListView<>(seasonList);
        seasons.setOrientation(Orientation.VERTICAL);
        Button send = new Button("send");
        send.setOnAction(e -> {
            seasonList.add("kek");
        });

        BorderPane border = new BorderPane();
        border.setBottom(send);
        border.setCenter(seasons);

        Scene scene = new Scene(border, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void printUsage() {
        System.err.println("Usage: ./gradlew run "); //TODO
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
        }

        try {
            if (args.length == 2) {
                ChatServer.run(args[0], Integer.parseInt(args[1]));
            } else if (args.length == 3) {
                ChatClient.run(args[0], args[1], Integer.parseInt(args[2]));
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Started UI");
        launch(args);
    }

}