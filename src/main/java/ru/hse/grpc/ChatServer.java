package ru.hse.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ChatServer extends ChatGrpc.ChatImplBase {
    private final String name;
    private final AtomicReference<StreamObserver<Model.ChatMessage>> responseObserverReference = new AtomicReference<>();
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final Ui ui;

    public ChatServer(String name, Ui ui) {
        this.name = name;
        this.ui = ui;
    }

    public AtomicReference<StreamObserver<Model.ChatMessage>> getResponseObserverReference() {
        return responseObserverReference;
    }

    public AtomicBoolean getFinished() {
        return finished;
    }


    @Override
    public StreamObserver<Model.ChatMessage> stream(StreamObserver<Model.ChatMessage> responseObserver) {
        boolean success = responseObserverReference.compareAndSet(null, responseObserver);
        if (!success) {
            responseObserver.onCompleted();
        }

        return new StreamObserver<Model.ChatMessage>() {
            @Override
            public void onNext(Model.ChatMessage value) {
                long timeStamp = value.getTimestamp();
                DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
                Date result = new Date(timeStamp);
                String time = simple.format(result);
                ui.displayMsg("<" + time + ">: [" + value.getName() + "]: " + value.getText());
            }

            @Override
            public void onError(Throwable t) {
                finished.set(true);
                System.err.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                finished.set(true);
                System.out.println("Completed messaging with client");
            }
        };
    }

    public static void run(String name, int port, Ui ui) throws IOException, InterruptedException {
        ChatServer serverImpl = new ChatServer(name, ui);
        Server server = ServerBuilder.forPort(port).addService(serverImpl).build();
        server.start();

        ui.setCallback((userName, msg) -> {
            var observer = serverImpl.responseObserverReference.get();
            if (observer != null) {
                observer.onNext(Model.ChatMessage.newBuilder()
                        .setName(userName)
                        .setText(msg)
                        .setTimestamp(System.currentTimeMillis())
                        .build());
            }
        });
    }
}
