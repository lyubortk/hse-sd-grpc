package ru.hse.grpc;

public class Main {
    public static void printUsage() {
        System.err.println("Usage: ./gradlew run --args='username [host] port'"); //TODO
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
    }
}
