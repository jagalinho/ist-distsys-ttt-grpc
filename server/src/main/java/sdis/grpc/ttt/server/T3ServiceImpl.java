package sdis.grpc.ttt.server;

import io.grpc.stub.StreamObserver;
import sdis.grpc.ttt.T3ServiceGrpc.T3ServiceImplBase;

import static sdis.grpc.ttt.T3.*;

public final class T3ServiceImpl extends T3ServiceImplBase {

    private final TTT ttt = new TTT();

    public void setSymbols(chooseSymbolRequest req, StreamObserver<chooseSymbolResponse> obs) {
        ttt.setSymbols(req.getSymbolPlayer0().charAt(0), req.getSymbolPlayer1().charAt(0));

        chooseSymbolResponse resp = chooseSymbolResponse.newBuilder().build();

        obs.onNext(resp);
        obs.onCompleted();
    }

    @Override
    public void play(PlayRequest req, StreamObserver<PlayResponse> obs) {
        boolean valid = ttt.play(req.getRow(), req.getCol(), req.getPlayer().getNumber());

        PlayResponse resp = PlayResponse
            .newBuilder()
            .setValid(valid)
            .build();

        obs.onNext(resp);
        obs.onCompleted();
    }

    @Override
    public void checkWinner(CheckWinnerRequest request, StreamObserver<CheckWinnerResponse> obs) {
        Winner winner = Winner.forNumber(ttt.checkWinner());

        CheckWinnerResponse resp = CheckWinnerResponse
            .newBuilder()
            .setWinner(winner)
            .build();

        obs.onNext(resp);
        obs.onCompleted();
    }

    @Override
    public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> obs) {
        String board = ttt.currentBoard();

        CurrentBoardResponse resp = CurrentBoardResponse
            .newBuilder()
            .setBoard(board)
            .build();

        obs.onNext(resp);
        obs.onCompleted();
    }
}
