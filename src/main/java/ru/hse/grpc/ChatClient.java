package ru.hse.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ChatClient {
    public static void run(String name, String address, int port) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        ChatGrpc.ChatStub service = ChatGrpc.newStub(channel);
        CountDownLatch finishedLatch = new CountDownLatch(1);

        StreamObserver<Model.ChatMessage> observer = service.stream(new StreamObserver<Model.ChatMessage>() {
            @Override
            public void onNext(Model.ChatMessage value) {
                System.out.println("onNext from client");
                System.out.println(value.getName() + " " + value.getText());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("On error" + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("on completed");
                finishedLatch.countDown();
            }
        });
        Scanner scanner = new Scanner(System.in);
        String line;
        while ((line = scanner.nextLine()) != null) {
            observer.onNext(Model.ChatMessage.newBuilder()
                    .setName(name)
                    .setText(line)
                    .setTimestamp(System.currentTimeMillis())
                    .build()
            );
        }
        finishedLatch.await();
        observer.onCompleted();
    }
}
