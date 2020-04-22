package ru.hse.grpc;

import java.util.function.BiFunction;

public interface Ui {
    void displayMsg(Model.ChatMessage msg);

    void setCallback(BiFunction<String, String, Void> callback);
}
