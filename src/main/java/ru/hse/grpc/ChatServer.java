package ru.hse.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ChatServer extends ChatGrpc.ChatImplBase {
    private final String name;
    private final AtomicReference<StreamObserver<Model.ChatMessage>> responseObserverReference = new AtomicReference<>();

    public ChatServer(String name) {
        this.name = name;
    }

    public AtomicReference<StreamObserver<Model.ChatMessage>> getResponseObserverReference() {
        return responseObserverReference;
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
                System.out.println(value.getName() + " " + value.getText());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed messaging with client");
            }
        };
    }

    public static void run(String name, int port) throws IOException, InterruptedException {
        ChatServer serverImpl = new ChatServer(name);

        Thread stdinReader = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.nextLine()) != null) {
                var observer = serverImpl.responseObserverReference.get();
                if (observer != null) {
                    observer.onNext(Model.ChatMessage.newBuilder()
                            .setName(name)
                            .setText(line)
                            .setTimestamp(System.currentTimeMillis())
                            .build());
                }
            }
        });

        Server server = ServerBuilder.forPort(port).addService(serverImpl).build();
        server.start();
        server.awaitTermination();
    }
}
