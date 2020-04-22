package ru.hse.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Scanner;

public class ChatServer extends ChatGrpc.ChatImplBase {
    private final String name;

    public ChatServer(String name) {
        this.name = name;
    }


    @Override
    public StreamObserver<Model.ChatMessage> stream(StreamObserver<Model.ChatMessage> responseObserver) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.nextLine()) != null) {
                responseObserver.onNext(Model.ChatMessage.newBuilder()
                        .setName(name)
                        .setText(line)
                        .setTimestamp(System.currentTimeMillis())
                        .build()
                );
            }
        }).start();

        return new StreamObserver<Model.ChatMessage>() {
            @Override
            public void onNext(Model.ChatMessage value) {
                System.out.println(value.getName() + " " + value.getText());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("On error" + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("On completed");
            }
        };
    }

    public static void run(String name, int port) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(port).addService(new ChatServer(name)).build();
        server.start();
        server.awaitTermination();
    }
}
