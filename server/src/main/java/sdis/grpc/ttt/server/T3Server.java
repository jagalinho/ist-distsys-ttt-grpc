package sdis.grpc.ttt.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class T3Server {

    public static void main(String[] args) throws Exception {
        System.out.println(T3Server.class.getSimpleName());

        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s port%n", Server.class.getName());
            return;
        }

        Server server = ServerBuilder
            .forPort(Integer.parseInt(args[0]))
            .addService(new T3ServiceImpl())
            .build();

        server.start();
        System.out.println("Server started");
        server.awaitTermination();
    }

}
