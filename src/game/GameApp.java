package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import gfx.Assets;
import gfx.Board;
import gfx.LeftRightPane;
import gfx.TopBottomPane;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import model.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import shared.RoomPosition;

public class GameApp extends BorderPane {

    // NETWORK
    private int yourPoints, opponentPoints;
    private int playerID;
    private final BuyThread buyThread;
    private final PlayThread playThread;
    private boolean playable = false;

    // GUI
    private AnimationTimer timer;
    private final Handler handler;
    private Player bottomPlayer, leftPlayer, topPlayer, rightPlayer;
    private RoomPosition dealer;
    private RoomPosition currentPosition;
    private ArrayList<Card> playerCards;
    private Result result;
    private Card selectedCard;
    private LeftRightPane leftPane, rightPane;
    private TopBottomPane topPane, bottomPane;
    private Board board;
    public int parties_won, parties_lost;
    private int yourDec;
    private int opDec;


    private void updateDealerGUI() {
        bottomPane.updateDealer();
        leftPane.updateDealer();
        topPane.updateDealer();
        rightPane.updateDealer();
    }

    public void switchTurn() {
        currentPosition = currentPosition.next();
    }

    public void startNewMatch() {
        yourPoints = 0;
        opponentPoints = 0;
        board.getScore().getItems().clear();
        startNewGame();
    }

    public boolean isFinished() {
        boolean finished = yourPoints >= 3000 || opponentPoints >= 3000;
        if (finished) {
            printResults();
        }
        return finished;
    }

    private void printResults() {
        boolean youWon = yourPoints > opponentPoints;
        if (youWon) {
            parties_won++;
        } else {
            parties_lost++;
        }
        int tempyourPoints = yourPoints, tempopponentPoints = opponentPoints;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game");
            alert.setHeaderText("Results");
            String text = "Party's over, " + (youWon ? " You won!" : "You lost!") + "\n";
            text += "Your points : " + tempyourPoints + "\n";
            text += "Opponent points : " + tempopponentPoints + "\n\n";
            text += "Total parties won : " + "\n";
            text += "Your team : " + parties_won + "\n";
            text += "Opponents team : " + parties_lost;
            alert.setContentText(text);
            alert.show();
        });
    }

    boolean tabClicked = false;
    public boolean first_game = true;

    public GameApp(Socket buy_socket, Socket play_socket, Map<RoomPosition, String> players_names) {
        buyThread = new BuyThread(buy_socket);
        playThread = new PlayThread(play_socket);

        handler = new Handler(this);
        createGUI(players_names);
        startNewMatch();
    }

    private void createGUI(Map<RoomPosition, String> players_names) {
        Assets.init_cards();
        initPanes(players_names);
    }

    public void setup_scene_events() {
        Scene scene = getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                tabClicked = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                tabClicked = false;
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                board.show(tabClicked);
            }
        };
        timer.start();
    }

    public void disable_animatio_timer() {
        timer.stop();
        board.show(false);
    }

    public void enable_animatio_timer() {
        timer.start();
    }

    public void initPlayers() {
        bottomPlayer = new Player(RoomPosition.BOTTOM);
        leftPlayer = new Player(RoomPosition.LEFT);
        topPlayer = new Player(RoomPosition.TOP);
        rightPlayer = new Player(RoomPosition.RIGHT);
        bottomPane.setPlayer(bottomPlayer);
        leftPane.setPlayer(leftPlayer);
        topPane.setPlayer(topPlayer);
        rightPane.setPlayer(rightPlayer);
    }

    public void loadCards() {
        bottomPlayer.setCards(playerCards);
        bottomPane.LoadCards();
    }

    public void removeCard() {
        if (currentPosition == RoomPosition.BOTTOM) {
            bottomPane.removeCard(selectedCard);
        }
    }

    public void startNewGame() {
        board.resetPlies_count();
        dealer = dealer.next();
        currentPosition = dealer;
        clearCards();
        buyThread.getCards();
        updateDealerGUI();
        switchTurn();
        board.setup_first_turn();
    }

    private void clearCards() {
        bottomPane.removeCards();
    }

    protected void initPanes(Map<RoomPosition, String> players_names) {
        result = new Result();
        result.setOnMouseClicked(e -> {
            result.setDisable(true);
            playThread.startAnotherGame();
            if (isFinished())
                startNewMatch();
            else
                startNewGame();
        });
        board = new Board(handler);
        setCenter(board);
        bottomPane = new TopBottomPane(handler, RoomPosition.BOTTOM, players_names.get(RoomPosition.BOTTOM));
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        leftPane = new LeftRightPane(handler, RoomPosition.LEFT, players_names.get(RoomPosition.LEFT));
        leftPane.setAlignment(Pos.CENTER_LEFT);
        topPane = new TopBottomPane(handler, RoomPosition.TOP, players_names.get(RoomPosition.TOP));
        topPane.setAlignment(Pos.TOP_CENTER);
        rightPane = new LeftRightPane(handler, RoomPosition.RIGHT, players_names.get(RoomPosition.RIGHT));
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        setRight(rightPane);
        setLeft(leftPane);
        setTop(topPane);
        setBottom(bottomPane);
        setPrefSize(1280, 920);
    }

    public void checkWinner(boolean der, int yourPlies_count, int opPlies_count) {
        yourDec = 0;
        opDec = 0;
        List<Combination> current;
        Map<ComboType, Integer> your_dec_map = new HashMap<>(), op_dec_map = new HashMap<>();
        current = bottomPlayer.getDeclarations();
        if (current != null)
            for (Combination comb : current) {
                your_dec_map.merge(comb.getType(), 1, Integer::sum);
                yourDec += comb.getType().getValue();
            }
        current = topPlayer.getDeclarations();
        if (current != null)
            for (Combination comb : current) {
                your_dec_map.merge(comb.getType(), 1, Integer::sum);
                yourDec += comb.getType().getValue();
            }
        current = leftPlayer.getDeclarations();
        if (current != null)
            for (Combination comb : current) {
                op_dec_map.merge(comb.getType(), 1, Integer::sum);
                opDec += comb.getType().getValue();
            }
        current = rightPlayer.getDeclarations();
        if (current != null)
            for (Combination comb : current) {
                op_dec_map.merge(comb.getType(), 1, Integer::sum);
                opDec += comb.getType().getValue();
            }
        Bid bid = board.getLatestBid();
        boolean sa_ta = bid.getTrump() == Suit.SA || bid.getTrump() == Suit.TA;
        int your_fold_pts, op_fold_pts;
        if (board.getLatestBid().youBought()) {
            your_fold_pts = board.getYourPts(sa_ta, opPlies_count == 0, (der ? 10 : 0));
            op_fold_pts = board.getOpponentPts(sa_ta, yourPlies_count == 0, (!der ? 10 : 0));
        } else {
            your_fold_pts = board.getYourPts(sa_ta, opPlies_count == 0, (!der ? 10 : 0));
            op_fold_pts = board.getOpponentPts(sa_ta, yourPlies_count == 0, (der ? 10 : 0));
        }
        int Yround_total = your_fold_pts + yourDec, OPround_total = op_fold_pts + opDec;
        int dedans = (sa_ta ? 260 : 160);
        boolean won;
        boolean didnt_reach_bid, defender_got_more_pts, capot_fail, capot_bid_out_of_reach;
        if (board.getLatestBid().youBought()) { // YOU BOUGHT
            didnt_reach_bid = Yround_total < bid.getValue();
            defender_got_more_pts = OPround_total > Yround_total;
            capot_fail = bid.isCapot() && opPlies_count > 0;
            capot_bid_out_of_reach = bid.isCapot() && yourDec < bid.getValue();
            if (didnt_reach_bid || defender_got_more_pts || capot_fail || capot_bid_out_of_reach) { // LOST
                Yround_total = 0;
                OPround_total = bid.result(false, yourPlies_count == 0, op_fold_pts, yourDec + opDec);
                opponentPoints += OPround_total;
                won = false;
            } else { // WON
                Yround_total = bid.result(true, opPlies_count == 0, your_fold_pts, yourDec + (bid.isCorS() ? opDec : 0));
                yourPoints += Yround_total;
                if (!bid.isCorS()) {
                    int mode = OPround_total % 10;
                    OPround_total += -mode + (mode > 4 ? 10 : 0);
                    opponentPoints += OPround_total;
                    dedans = 0;
                } else {
                    OPround_total = 0;
                }
                won = true;
            }
        } else { // OP BOUGHT
            didnt_reach_bid = OPround_total < bid.getValue();
            defender_got_more_pts = Yround_total > OPround_total;
            capot_fail = bid.isCapot() && yourPlies_count > 0;
            capot_bid_out_of_reach = bid.isCapot() && opDec < bid.getValue();
            if (didnt_reach_bid || defender_got_more_pts || capot_fail || capot_bid_out_of_reach) { // LOST
                Yround_total = bid.result(false, opPlies_count == 0, your_fold_pts, yourDec + opDec);
                OPround_total = 0;
                yourPoints += Yround_total;
                won = false;
            } else { // WON
                OPround_total = bid.result(true, yourPlies_count == 0, op_fold_pts, opDec + (bid.isCorS() ? yourDec : 0));
                opponentPoints += OPround_total;
                if (!bid.isCorS()) {
                    int mode = Yround_total % 10;
                    Yround_total += -mode + (mode > 4 ? 10 : 0);
                    yourPoints += Yround_total;
                    dedans = 0;
                } else {
                    Yround_total = 0;
                }
                won = true;
            }
        }
        StringBuilder fail_reasons = new StringBuilder();
        if (!won) {
            List<String> reasons = new ArrayList<>();

            if (didnt_reach_bid || capot_bid_out_of_reach)
                reasons.add("Buyer didn't reach bid points!");
            if (defender_got_more_pts)
                reasons.add("Defender got more points!");
            if (capot_fail)
                reasons.add("Capot bid failed!");
            int i = reasons.size() - 1;
            while (i > -1) {
                fail_reasons.append(reasons.get(i));
                i--;
                if (i > -1) {
                    fail_reasons.append("\n");
                }
            }
        }

        int fYround_total = Yround_total, fOPround_total = OPround_total, finalDedans = dedans;
        String finalFail_reasons = fail_reasons.toString();
        Platform.runLater(() -> {
            board.getChildren().clear();
            result.setDisable(false);
            result.update(board.getLatestBid(), won, finalFail_reasons, finalDedans,
                    your_fold_pts, op_fold_pts,
                    fYround_total, fOPround_total,
                    yourPlies_count == 0, opPlies_count == 0,
                    yourDec, opDec,
                    get_dec_detail(your_dec_map), get_dec_detail(op_dec_map),
                    yourPoints, opponentPoints);
            board.getScore().add(bid, yourPoints, opponentPoints);
            board.getChildren().add(result);
        });
    }

    private String get_dec_detail(Map<ComboType, Integer> dec_map) {
        Iterator<Map.Entry<ComboType, Integer>> itr;
        StringBuilder dec_detail = new StringBuilder();
        dec_detail.append("(");
        itr = dec_map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<ComboType, Integer> entry = itr.next();
            dec_detail
                    .append(entry.getValue() > 1 ? entry.getValue() + " x " : "").append(entry.getKey().toString());
            if (itr.hasNext())
                dec_detail.append(" + ");
        }
        dec_detail.append(")");
        return dec_detail.toString();
    }

    // GETTERS SETTERS

    public boolean isYourTurn() {
        return currentPosition == RoomPosition.BOTTOM;
    }

    public boolean isPlayable() {
        return playable;
    }

    public RoomPosition getCurrentPosition() {
        return currentPosition;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public LeftRightPane getLeftPane() {
        return leftPane;
    }

    public LeftRightPane getRightPane() {
        return rightPane;
    }

    public TopBottomPane getTopPane() {
        return topPane;
    }

    public TopBottomPane getBottomPane() {
        return bottomPane;
    }

    public Board getBoard() {
        return board;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public RoomPosition getDealer() {
        return dealer;
    }

    public void setNextTurn(RoomPosition winnerPosition) {
        currentPosition = winnerPosition;
        board.adapt_turn();
    }

    public void closeGameApp() {
        buyThread.closeConn();
        playThread.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    public class BuyThread extends Thread {

        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private Socket socket;
        private Thread waitThread;

        public BuyThread(Socket socket) {
            try {
                this.socket = socket;
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                int D = dataIn.readInt();
                dealer = RoomPosition.getCurrentPositionByPlayerID(RoomPosition.getPositionByPlayerID(playerID),
                        (D) == 1 ? 4 : (D - 1) % 4);
            } catch (IOException ignore) {
            }
        }

        public void WaitForYourTurn() {
            if (waitThread == null) {
                waitThread = new Thread(() -> {
                    boolean finished;
                    do {
                        int[] coor = receive();
                        RoomPosition playerPosition = RoomPosition
                                .getCurrentPositionByPlayerID(RoomPosition.getPositionByPlayerID(playerID), coor[0]);
                        finished = board.buy(playerPosition, Suit.get(coor[1]), coor[2], coor[3] == 1);
                    } while (!finished && currentPosition != RoomPosition.BOTTOM);
                    waitThread = null;
                });
                waitThread.start();
            }
        }

        public void startAnotherGame() {
            try {
                dataOut.writeInt(-1);
                dataOut.flush();
            } catch (IOException ignore) {
            }
        }

        public void send(int suit, int value, boolean capot) {
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(suit);
                dataOut.writeInt(value);
                dataOut.writeInt(capot ? 1 : 0);
                dataOut.flush();
            } catch (IOException ignore) {
            }
        }

        public int[] receive() {
            int[] coor = new int[4];
            try {
                coor[0] = dataIn.readInt();
                coor[1] = dataIn.readInt();
                coor[2] = dataIn.readInt();
                coor[3] = dataIn.readInt();
            } catch (IOException ignore) {
            }
            return coor;
        }

        public void getCards() {
            Thread getCardsThread = new Thread(() -> {
                playerCards = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    try {
                        int cardSuit = dataIn.readInt();
                        int cardRank = dataIn.readInt();
                        playerCards.add(new Card(handler, cardSuit, cardRank, RoomPosition.BOTTOM));
                    } catch (IOException ignore) {
                    }
                }
                WaitForYourTurn();
                initFirstPhase();
            });
            getCardsThread.start();
        }

        public void closeConn() {
            try {
                dataOut.close();
                dataIn.close();
                socket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private void initFirstPhase() {
        if (!first_game) {
            bottomPane.reset_bid_decs();
            rightPane.reset_bid_decs();
            topPane.reset_bid_decs();
            leftPane.reset_bid_decs();
        }
        board.initFirstPhase();
    }

    public class PlayThread extends Thread {

        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private Thread waitThread;
        private Socket socket;

        public PlayThread(Socket socket) {
            try {
                this.socket = socket;
                dataOut = new DataOutputStream(socket.getOutputStream());
                dataIn = new DataInputStream(socket.getInputStream());
            } catch (IOException ignore) {
            }
        }

        public void startAnotherGame() {
            try {
                dataOut.writeInt(0);
                dataOut.flush();
            } catch (IOException ignore) {
            }
        }

        public void initSecondPhase() {
            Thread declarationThread = new Thread(() -> {
                try {
                    dataOut.writeInt(board.getLatestBid().getTrump().getIndex()); // giving trump index
                    dataOut.flush();
                } catch (IOException ignore) {
                }
                boolean thereIS = false;
                if (board.getLatestBid().getTrump() != Suit.TA && board.getLatestBid().getTrump() != Suit.SA) {
                    Map<RoomPosition, ArrayList<Combination>> decl = new HashMap<>();
                    RoomPosition position = RoomPosition.BOTTOM;
                    for (int i = 0; i < 4; i++) {
                        int suit = 0;
                        int rank = 0;
                        int order = 0;
                        ArrayList<Combination> combos = new ArrayList<>();
                        try {
                            suit = dataIn.readInt();
                            rank = dataIn.readInt();
                            order = dataIn.readInt();
                        } catch (IOException ignore) {
                        }
                        while (order != -1) {
                            thereIS = true;
                            Combination newComb = new Combination(position, suit, rank, order);
                            combos.add(newComb);
                            try {
                                suit = dataIn.readInt();
                                rank = dataIn.readInt();
                                order = dataIn.readInt();
                            } catch (IOException ignore) {
                            }
                        }
                        decl.put(position, combos);
                        position = position.next();
                    }
                    if (thereIS) { // there is at least one declaration
                        RoomPosition your_pos = RoomPosition.getPositionByPlayerID(playerID);
                        bottomPlayer.setDeclarations(decl.get(your_pos));
                        your_pos = your_pos.next();
                        rightPlayer.setDeclarations(decl.get(your_pos));
                        your_pos = your_pos.next();
                        topPlayer.setDeclarations(decl.get(your_pos));
                        your_pos = your_pos.next();
                        leftPlayer.setDeclarations(decl.get(your_pos));
                    }
                }
                showDecs_setPlayable(thereIS);
                WaitForYourTurn();
            });
            declarationThread.start();
        }

        public void showDecs_setPlayable(boolean thereIS) {
            if (thereIS)
                board.announceDeclarations(bottomPlayer.getDeclarations(), rightPlayer.getDeclarations(), topPlayer.getDeclarations(), leftPlayer.getDeclarations());
            else setPlayable(true);
        }

        boolean finished;

        public void WaitForYourTurn() {
            if (waitThread == null) {
                waitThread = new Thread(() -> {
                    finished = false;
                    for (int i = 0; i < 24; i++) { // 8 * 3
                        int[] coor = receive();
                        board.play(new Card(handler, coor[0], coor[1], currentPosition));
                    }
                    waitThread = null;
                });

                waitThread.start();
            }
        }

        public void send(int suit, int rank) {
            try {
                dataOut.writeInt(suit);
                dataOut.writeInt(rank);
                dataOut.flush();
            } catch (IOException ignore) {
            }
        }

        public int[] receive() {
            int[] coor = new int[2];
            try {
                coor[0] = dataIn.readInt();
                coor[1] = dataIn.readInt();
            } catch (IOException ignore) {
            }
            return coor;
        }

        public void closeConn() {
            try {
                dataOut.close();
                dataIn.close();
                socket.close();
            } catch (IOException ignore) {
            }
        }
    }

    public BuyThread getBuyThread() {
        return buyThread;
    }

    public PlayThread getPlayThread() {
        return playThread;
    }

    public void hide_pass_bids() {
        bottomPane.hide_pass_bids();
        rightPane.hide_pass_bids();
        topPane.hide_pass_bids();
        leftPane.hide_pass_bids();
    }

}