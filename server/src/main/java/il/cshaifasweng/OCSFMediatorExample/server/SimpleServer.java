package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;


public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private int[][] board = new int[3][3];
	int turn;

	public SimpleServer(int port) {
		super(port);

	}

	public void start(){
		//init board
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				board[i][j] = 2;
			}
		}
		Random rand = new Random();
		turn = rand.nextInt(2);
		String startMessage = "the game started, ";
		if(turn == 0){
			startMessage += "O";
		}
		else{
			startMessage += "X";
		}
		startMessage += " plays first";
		sendToAllClients(startMessage);
	}
	private boolean checkLine(int cell1, int cell2, int cell3, int player) {
        return cell1 == cell2 && cell2 == cell3 && cell3 == player;
    }

	private boolean checkWin(int player){
		// check if there is a win in a row or column
		for (int i = 0; i < 3; i++) {
			if (checkLine(board[i][0], board[i][1], board[i][2], player)){
				 return true;
			}
			if (checkLine(board[0][i], board[1][i], board[2][i], player)){
				return true;
			}
		}
		//check if there is a win in diagonal
		if (checkLine(board[0][0], board[1][1], board[2][2], player)){
			return true;
		}
		if (checkLine(board[0][2], board[1][1], board[2][0], player)){
			return true;
		}
		return false;
	}

	private boolean checkTie(){
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				if(board[i][j] == 2){
					return false;
				}
			}
		}
		return !checkWin(0) && !checkWin(1);
	}


	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				String message = "client added successfully";
				if(SubscribersList.size()==2){
					client.sendToClient(message + "you will play X, the game will start soon");
					start();
				}
				else{
					client.sendToClient(message + "you will play O, please wait for more players");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
		// in game messages
		else{
			// checking that the current client is the one who sent the message
			if(SubscribersList.get(turn).getClient().equals(client)){
				int row = Integer.parseInt(msgString.substring(0,1));
				int col = Integer.parseInt(msgString.substring(2));
				board[row][col] = turn;
				turn = (turn+1)%2;
				try {
					SubscribersList.get(turn).getClient().sendToClient("add" + row + "," + col);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				for(int j=0; j<3; j++){
					for(int i=0; i<3; i++){
						System.out.println(board[i][j] + " ");
					}
					System.out.println("");
				}
				if(checkWin((turn+1)%2)){
					sendToAllClients("Win");
				}
				else if(checkTie()){
					sendToAllClients("Tie");
				}
			}
		}
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}