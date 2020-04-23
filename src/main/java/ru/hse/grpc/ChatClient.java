package ru.hse.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    public static void run(String name, String address, int port, Ui ui) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        ChatGrpc.ChatStub service = ChatGrpc.newStub(channel);
        CountDownLatch finishedLatch = new CountDownLatch(1);

        AtomicBoolean finished = new AtomicBoolean(false);

        StreamObserver<Model.ChatMessage> observer = service.stream(new StreamObserver<Model.ChatMessage>() {
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
                System.out.println("on completed");
                finishedLatch.countDown();
            }
        });
        ui.setCallback((userName, message) -> observer.onNext(Model.ChatMessage.newBuilder()
                .setName(userName)
                .setText(message)
                .setTimestamp(System.currentTimeMillis())
                .build()
        ));
    }
}
