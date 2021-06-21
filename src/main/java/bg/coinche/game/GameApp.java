package bg.coinche.game;

import bg.coinche.MainApp;
import bg.coinche.gfx.Assets;
import bg.coinche.gfx.Board;
import bg.coinche.gfx.LeftRightPane;
import bg.coinche.gfx.TopBottomPane;
import bg.coinche.lang.Language;
import bg.coinche.popup.MyAlert;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import bg.coinche.model.*;
import shared.RoomPosition;
import shared.coinche.CoincheComm;
import shared.coinche.CoincheMsg;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class GameApp extends BorderPane {

    // NETWORK
    private int yourPoints, opponentPoints;
    private int playerID;
    private final GameClient gameClient;
    private boolean playable = false;
    private final Map<RoomPosition, ArrayList<Combination>> decl;
    // GUI
    private AnimationTimer timer;
    private final Handler handler;
    private Player bottomPlayer, leftPlayer, topPlayer, rightPlayer;
    private RoomPosition dealer;
    private RoomPosition currentPosition;
    private Result result;
    private MyAlert results_alert;
    private Card selectedCard;
    private LeftRightPane leftPane, rightPane;
    private TopBottomPane topPane, bottomPane;
    private Board board;
    public int parties_won, parties_lost;
    private int yourDec;
    private int opDec;
    boolean tabClicked = false;

    public GameApp(Socket socket) {
        decl = new HashMap<>();
        handler = new Handler(this);
        gameClient = new GameClient(socket);
        gameClient.handshake();
        createGUI();
        gameClient.first_game();
        gameClient.start();
    }

    private void createGUI() {
        Assets.init_cards();
        initPanes();
        initPlayers();
    }

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
    }

    public boolean isFinished() {
        boolean finished = yourPoints >= 3000 || opponentPoints >= 3000;
        if (finished) {
            printResults(false);
        }
        return finished;
    }

    public void printResults(boolean shortcut) {
        if (results_alert != null && results_alert.isShowing())
            if (shortcut) return;
            else results_alert.close();
        int tempyourPoints = yourPoints, tempopponentPoints = opponentPoints;
        Platform.runLater(() -> {
            StringProperty header;
            if (shortcut) header = Language.CURRENT_SC;
            else {
                if (yourPoints > opponentPoints) {
                    header = Language.YOU_WON;
                    parties_won++;
                } else if (yourPoints < opponentPoints) {
                    header = Language.YOU_LOST;
                    parties_lost++;
                } else {
                    header = Language.CURRENT_SC;
                }
            }
            String text;
            text = Language.YOUR_PTS.getValue() + tempyourPoints + "\n";
            text += Language.OP_PTS.getValue() + tempopponentPoints + "\n\n";
            text += Language.TOTAL_PT_WON.getValue() + "\n";
            text += Language.YOUR_TEAM.getValue() + parties_won + "\n";
            text += Language.OP_TEAM.getValue() + parties_lost;
            if (results_alert == null)
                results_alert = new MyAlert(Alert.AlertType.INFORMATION, Language.GR_H, header, text);
            else results_alert.update(header, text);
            results_alert.show();
        });
    }

    public void setup_scene_events() {
        Scene scene = getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) tabClicked = true;
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) tabClicked = false;
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                board.show(tabClicked);
            }
        };
        timer.start();
    }

    public void disable_animation_timer() {
        timer.stop();
        board.show(false);
    }

    public void enable_animatio_timer() {
        timer.start();
    }

    private void initPlayers() {
        bottomPlayer = new Player(RoomPosition.BOTTOM);
        leftPlayer = new Player(RoomPosition.LEFT);
        topPlayer = new Player(RoomPosition.TOP);
        rightPlayer = new Player(RoomPosition.RIGHT);
        bottomPane.setPlayer(bottomPlayer);
        leftPane.setPlayer(leftPlayer);
        topPane.setPlayer(topPlayer);
        rightPane.setPlayer(rightPlayer);
    }

    public void loadCards(ArrayList<Card> playerCards) {
        bottomPlayer.setCards(playerCards);
        bottomPane.LoadCards();
    }

    public void removeCard() {
        if (currentPosition == RoomPosition.BOTTOM) {
            bottomPane.removeCard(selectedCard);
        }
    }

    protected void initPanes() {
        result = new Result();
        result.setOnMouseClicked(e -> board.set_buy_if_buying());
        board = new Board(handler);
        setCenter(board);
        bottomPane = new TopBottomPane(handler, RoomPosition.BOTTOM);
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        leftPane = new LeftRightPane(handler, RoomPosition.LEFT);
        leftPane.setAlignment(Pos.CENTER_LEFT);
        topPane = new TopBottomPane(handler, RoomPosition.TOP);
        topPane.setAlignment(Pos.TOP_CENTER);
        rightPane = new LeftRightPane(handler, RoomPosition.RIGHT);
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        setRight(rightPane);
        setLeft(leftPane);
        setTop(topPane);
        setBottom(bottomPane);
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
        if (bid.youBought()) {
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
        if (bid.youBought()) { // YOU BOUGHT
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

            if (didnt_reach_bid || capot_bid_out_of_reach) reasons.add(Language.REASONS_1.getValue());
            if (defender_got_more_pts) reasons.add(Language.REASONS_2.getValue());
            if (capot_fail) reasons.add(Language.REASONS_3.getValue());
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
        startAnotherGame();
        Platform.runLater(() -> {
            result.update(bid, won, finalFail_reasons, finalDedans,
                    your_fold_pts, op_fold_pts,
                    fYround_total, fOPround_total,
                    yourPlies_count == 0, opPlies_count == 0,
                    yourDec, opDec,
                    get_dec_detail(your_dec_map), get_dec_detail(op_dec_map),
                    yourPoints, opponentPoints);
            board.getScore().add(bid, yourPoints, opponentPoints);
            board.getChildren().clear();
            board.getChildren().add(result);
            reset_panes();
            if (isFinished()) startNewMatch();
            initBuyPhase();
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
    }

    public void closeGameApp() {
        gameClient.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    public void initPlayPhase() {
        decl.clear();
        Suit trump = board.getLatestBid().getTrump();
        if (trump == Suit.TA || trump == Suit.SA) setPlayable(true);
        else if (playerID == 1) {
            gameClient.send_msg(new CoincheMsg(CoincheComm.TRUMP,
                    new Object[]{trump.ordinal()}));
        }
    }

    public void bought(int suit, int bid, boolean capot) {
        gameClient.send_msg(new CoincheMsg(CoincheComm.BOUGHT, new Object[]{playerID, suit, bid, capot}));
    }

    public void coinched() {
        gameClient.send_msg(new CoincheMsg(CoincheComm.COINCHED, new Object[]{playerID}));
    }

    public void surcoinched() {
        gameClient.send_msg(new CoincheMsg(CoincheComm.SURCOINCHED));
    }

    public void passed() {
        gameClient.send_msg(new CoincheMsg(CoincheComm.PASSED));
    }

    public void showDecs_setPlayable() {
        if (!decl.isEmpty()) board.announceDeclarations(
                bottomPlayer.getDeclarations(), rightPlayer.getDeclarations(),
                topPlayer.getDeclarations(), leftPlayer.getDeclarations());
        else setPlayable(true);
    }

    public void Iplayed(int suit, int rank) {
        setPlayable(false);
        gameClient.send_msg(new CoincheMsg(CoincheComm.PLAYED, new Object[]{suit, rank}));
    }

    public void startAnotherGame() {
        if (playerID == 1) gameClient.send_msg(new CoincheMsg(CoincheComm.GAME_END));
    }

    private class GameClient extends Thread {
        private Socket socket;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;
        private RoomPosition your_pos;

        public GameClient(Socket socket) {
            try {
                this.socket = socket;
                this.socket.setSoTimeout(0);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ignore) {
            }
        }

        public void handshake() {
            try {
                playerID = objIn.readInt();
                int D = objIn.readInt();
                your_pos = RoomPosition.getPositionByPlayerID(playerID);
                dealer = RoomPosition.getCurrentPositionByPlayerID(your_pos, (D) == 1 ? 4 : (D - 1) % 4);
            } catch (IOException ignore) {
            }
        }

        private void first_game() {
            try {
                CoincheMsg msg = (CoincheMsg) objIn.readObject();
                loadCards(Card.to_list(handler, (Integer[]) msg.adt_data));
                initBuyPhase();
                board.set_buy_gui();
            } catch (IOException | ClassNotFoundException ignore) {
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    CoincheMsg msg = (CoincheMsg) objIn.readObject();
                    CoincheComm msg_comm = CoincheComm.values()[msg.comm];
                    switch (msg_comm) {
                        case PASSED: {
                            board.pass();
                            break;
                        }
                        case BOUGHT: {
                            RoomPosition playerPosition = RoomPosition
                                    .getCurrentPositionByPlayerID(your_pos, (int) msg.adt_data[0]);
                            board.buy(playerPosition, Suit.values()[(int) msg.adt_data[1]], (int) msg.adt_data[2], (boolean) msg.adt_data[3]);
                            break;
                        }
                        case COINCHED: {
                            RoomPosition playerPosition = RoomPosition
                                    .getCurrentPositionByPlayerID(your_pos, (int) msg.adt_data[0]);
                            board.coinche(playerPosition);
                            break;
                        }
                        case SURCOINCHED: {
                            board.surcoinche();
                            break;
                        }
                        case PLAYED: {
                            board.play(new Card(handler, MainApp.announcePlayProperty, (int) msg.adt_data[0], (int) msg.adt_data[1], currentPosition));
                            break;
                        }
                        case NEW_GAME: {
                            setPlayable(false);
                            loadCards(Card.to_list(handler, (Integer[]) msg.adt_data));
                            break;
                        }
                        case DECLARATIONS: {
                            if (msg.adt_data.length == 0) {
                                RoomPosition temp_pos = this.your_pos;
                                bottomPlayer.setDeclarations(decl.get(temp_pos));
                                temp_pos = temp_pos.next();
                                rightPlayer.setDeclarations(decl.get(temp_pos));
                                temp_pos = temp_pos.next();
                                topPlayer.setDeclarations(decl.get(temp_pos));
                                temp_pos = temp_pos.next();
                                leftPlayer.setDeclarations(decl.get(temp_pos));
                                showDecs_setPlayable();
                            } else {
                                RoomPosition position = RoomPosition.values()[(int) msg.adt_data[0]];
                                decl.put(position, Combination.to_list(position, (Integer[]) msg.adt_data));
                            }
                            break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException ignore) {
            }
        }

        private void send_msg(CoincheMsg msg) {
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException ignore) {
            }
        }

        public void closeConn() {
            try {
                objOut.close();
                objIn.close();
                socket.close();
            } catch (IOException ignore) {
            }
        }
    }

    public void initBuyPhase() {
        dealer = dealer.next();
        currentPosition = dealer;
        updateDealerGUI();
        switchTurn();
        board.setupBuyPhase();
    }

    public void clearCards() {
        bottomPane.clearCards();
    }

    public void reset_panes() {
        bottomPane.reset_bid_decs();
        rightPane.reset_bid_decs();
        topPane.reset_bid_decs();
        leftPane.reset_bid_decs();
    }

    public void hide_pass_bids() {
        bottomPane.hide_pass_bids();
        rightPane.hide_pass_bids();
        topPane.hide_pass_bids();
        leftPane.hide_pass_bids();
    }

}