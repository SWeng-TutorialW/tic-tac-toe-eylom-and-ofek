package il.cshaifasweng.OCSFMediatorExample.client;


import javafx.event.ActionEvent;
import javafx.application.Platform;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

	@FXML
	private GridPane boardGrid;
	@FXML
	private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
	@FXML
	private Label turnBox;

	private Button[][] board;
	private boolean turn;
	private String mark;
	private boolean gameOver = false;

	@FXML
	public void initialize() {
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			e.printStackTrace();
		}
		EventBus.getDefault().register(this);
		board = new Button[3][3];
		board[0][0] = btn1;
		board[0][1] = btn2;
		board[0][2] = btn3;
		board[1][0] = btn4;
		board[1][1] = btn5;
		board[1][2] = btn6;
		board[2][0] = btn7;
		board[2][1] = btn8;
		board[2][2] = btn9;
		turn  = false;
	}



	@FXML
	void btnPressed(ActionEvent event) {
		if(turn){
			Button clicked = (Button) event.getSource();
			int btnNumber = Integer.parseInt(clicked.getId().substring(3, 4));
			int row = (btnNumber-1)/3;
			int col = (btnNumber-1)%3;
			if(board[row][col].getText().isEmpty() && !gameOver){
				Platform.runLater(()->board[row][col].setText(mark));
				try{
					SimpleClient.getClient().sendToServer( + row + "," + col);
				}
				catch (IOException e){
					e.printStackTrace();
				}
				turn = false;
				Platform.runLater(()->turnBox.setText("opponent's turn"));
			}
		}
	}

	@Subscribe
	public void handleMessageFromServer(String message){
		if(message.startsWith("client added successfully")){
			if(message.contains("X")) {
				mark = "X";
			}
			else{
				mark = "O";
			}
			if(message.contains("more")){
				Platform.runLater(() -> turnBox.setText("waiting for another player"));
			}
		}
		else if(message.startsWith("add")) {
			int row = Integer.parseInt(message.substring(3, 4));
			int col = Integer.parseInt(message.substring(5));
			String opponentMark;
			if (mark.equals("X")) {
				opponentMark = "O";
			}
			else {
                opponentMark = "X";
            }
            Platform.runLater(() -> board[row][col].setText(opponentMark));
			turn = true;
			Platform.runLater(() -> turnBox.setText("your turn, you play " + mark));
		}
		else if(message.startsWith("the game started, ")) {
			String currentMark = message.substring(18, 19);
			if(currentMark.equals(mark)){
				turn  = true;
				Platform.runLater(() -> turnBox.setText("your turn, you play " + mark));
			}
			else{
				Platform.runLater(() -> turnBox.setText("opponent's turn"));
			}
		}
		else if(message.equals("Win")) {
			if(turn) {
				Platform.runLater(() -> turnBox.setText("You lost!"));
			}
			else{
				Platform.runLater(() -> turnBox.setText("You won!"));
			}
			gameOver = true;
		}
		else if(message.equals("Tie")) {
			Platform.runLater(() -> turnBox.setText("Tie!"));
			gameOver = true;
		}
		else{
			Platform.runLater(() -> turnBox.setText(message));
		}
	}




}