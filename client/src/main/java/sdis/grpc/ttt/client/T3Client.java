package sdis.grpc.ttt.client;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

import static sdis.grpc.ttt.T3.*;
import static sdis.grpc.ttt.T3ServiceGrpc.T3ServiceBlockingStub;
import static sdis.grpc.ttt.T3ServiceGrpc.newBlockingStub;

public class T3Client {

    private Winner winner = Winner.WinnerNone;
    private Player player = Player.Player1;

    private Scanner input;
    private T3ServiceBlockingStub ttt;

    private char playerSymbol0;
    private char playerSymbol1;

    public T3Client(Channel channel) {
        input = new Scanner(System.in);
        ttt = newBlockingStub(channel);
    }

    private String readSymbol(int player) {
        String symbol;

        System.out.println("\nPlayer " + player + ", please type your symbol (only the first character will be used):\n");
        symbol = input.next();

        return symbol;
    }

    private void setSymbols(char symbol0, char symbol1) {
        playerSymbol0 = symbol0;
        playerSymbol1 = symbol1;
    }

    private int readPlay() {
        int play;
        do {
            System.out.printf(
                "\nPlayer %c, please enter the number of the square where you want to place your %c (or 0 to refresh the board): \n",
                (player == Player.Player1) ? '1' : '0',
                (player == Player.Player1) ? playerSymbol1 : playerSymbol0
            );
            play = input.nextInt();
        } while (play > 9 || play < 0);
        return play;
    }

    private void playGame() {
        int play;
        boolean playAccepted = false;

        String tmpSymbol0 = readSymbol(0);
        String tmpSymbol1 = readSymbol(1);

        setSymbols(tmpSymbol0.charAt(0), tmpSymbol1.charAt(0));

        chooseSymbolRequest symbolRequest = chooseSymbolRequest.newBuilder()
            .setSymbolPlayer0(tmpSymbol0)
            .setSymbolPlayer1(tmpSymbol1)
            .build();

        ttt.setSymbols(symbolRequest);

        do {
            player = player == Player.Player1 ? Player.Player0: Player.Player1;
            do {
                System.out.println(ttt.currentBoard(CurrentBoardRequest.getDefaultInstance()).getBoard());

                play = readPlay();
                if (play == 0) {
                    continue;
                }

                play--;

                PlayRequest playReq = PlayRequest.newBuilder()
                    .setPlayer(player)
                    .setCol(play % 3)
                    .setRow(play / 3)
                    .build();

                playAccepted = ttt.play(playReq).getValid();
                if (!playAccepted) {
                    System.out.println("Invalid play! Try again.");
                }

            } while (!playAccepted);
            winner = ttt.checkWinner(CheckWinnerRequest.getDefaultInstance()).getWinner();
        } while (winner == Winner.WinnerNone);
    }

    private void congratulate() {
        if (winner == Winner.WinnerDraw) {
            System.out.print("\nHow boring, it is a draw\n");
        } else {
            System.out
                .printf("\nCongratulations, player %c, YOU ARE THE WINNER!\n", winner == Winner.Winner1 ? '1' : '0');
        }
    }


    public static void main(String[] args) {
        System.out.println(T3Client.class.getSimpleName());

        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s host port%n", T3Client.class.getName());
            return;
        }

        String target = String.format("%s:%d", args[0], Integer.parseInt(args[1]));

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        T3Client client = new T3Client(channel);

        client.playGame();
        client.congratulate();

        channel.shutdownNow();
    }

}
