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

    public ChatServer(String name) {
        this.name = name;
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
                System.out.println("<" + time + ">: [" + value.getName() + "]: " + value.getText());
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

    public static void run(String name, int port) throws IOException, InterruptedException {
        ChatServer serverImpl = new ChatServer(name);
        Server server = ServerBuilder.forPort(port).addService(serverImpl).build();
        server.start();

        Scanner scanner = new Scanner(System.in);
        String line;
        while ((line = scanner.nextLine()) != null) {
            if (serverImpl.getFinished().get()) {
                break;
            }
            var observer = serverImpl.responseObserverReference.get();
            if (observer != null) {
                observer.onNext(Model.ChatMessage.newBuilder()
                        .setName(name)
                        .setText(line)
                        .setTimestamp(System.currentTimeMillis())
                        .build());
            }
        }
        var observer = serverImpl.responseObserverReference.get();
        if (observer != null) {
            observer.onCompleted();
        }
        server.shutdownNow();
        server.awaitTermination();
    }
}
