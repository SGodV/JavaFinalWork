package redfive;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Date;

public class Server extends Application implements Constants {
	int sessionNo = 1;

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		TextArea taLog = new TextArea();

		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
		primaryStage.setTitle("RedFiveServer"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		new Thread(() -> {
			try {
				// Create a server socket
				ServerSocket serverSocket = new ServerSocket(8000);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						taLog.appendText(new Date() +
								": Server started at socket 8000\n");
					}
				});

				// Ready to create a session for every two players
				while (true) {
					Platform.runLater(() -> taLog.appendText(new Date() +
							": Wait for players to join session " + sessionNo + '\n'));

					// Connect to player 1
					Socket player1 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() + ": Player 1 joined session "
								+ sessionNo + '\n');
						taLog.appendText("Player 1's IP address" +
								player1.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 1
					new DataOutputStream(
							player1.getOutputStream()).writeInt(PLAYER1);

					// Connect to player 2
					Socket player2 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() +
								": Player 2 joined session " + sessionNo + '\n');
						taLog.appendText("Player 2's IP address" +
								player2.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 3
					new DataOutputStream(
							player2.getOutputStream()).writeInt(PLAYER2);

					// Connect to player 3
					Socket player3 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() +
								": Player 3 joined session " + sessionNo + '\n');
						taLog.appendText("Player 3's IP address" +
								player3.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 3
					new DataOutputStream(
							player3.getOutputStream()).writeInt(PLAYER3);

					// Connect to player 4
					Socket player4 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() +
								": Player 4 joined session " + sessionNo + '\n');
						taLog.appendText("Player 4's IP address" +
								player4.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 4
					new DataOutputStream(
							player4.getOutputStream()).writeInt(PLAYER4);

					// Display this session and increment session number
					Platform.runLater(() ->
							taLog.appendText(new Date() +
									": Start a thread for session " + sessionNo++ + '\n'));

					// Launch a new thread for this session of two players
					new Thread(new HandleASession(player1, player2, player3, player4)).start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	class HandleASession implements Runnable, Constants {
		private Socket[] player = new Socket[5];
		//牌
		private boolean continueToPlay = true;
		private String state = "本轮出牌情况:\n";
		private pai[][] playPai = new pai[4][30];

		public HandleASession(Socket player1, Socket player2, Socket player3, Socket player4) {
			this.player[1] = player1;
			this.player[2] = player2;
			this.player[3] = player3;
			this.player[4] = player4;
			Main.main(null);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 25; j++) {
					playPai[i][j] = Main.play[i][j];
					try {
						new DataOutputStream(player[i + 1].getOutputStream()).writeUTF(playPai[i][j].getId());
						new DataOutputStream(player[i + 1].getOutputStream()).writeInt(playPai[i][j].getVal());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			state = "";
		}

		@Override
		public void run() {
			try {
				DataInputStream[] fromPlayer = new DataInputStream[5];
				DataOutputStream[] toPlayer = new DataOutputStream[5];
				fromPlayer[1] = new DataInputStream(player[1].getInputStream());
				toPlayer[1] = new DataOutputStream(player[1].getOutputStream());
				fromPlayer[2] = new DataInputStream(player[2].getInputStream());
				toPlayer[2] = new DataOutputStream(player[2].getOutputStream());
				fromPlayer[3] = new DataInputStream(player[3].getInputStream());
				toPlayer[3] = new DataOutputStream(player[3].getOutputStream());
				fromPlayer[4] = new DataInputStream(player[4].getInputStream());
				toPlayer[4] = new DataOutputStream(player[4].getOutputStream());
				firstPut(toPlayer, 0, "", "", 0);
				toPlayer[1].writeUTF(state);
//			toPlayer[1].writeInt(1);
				int now = 1;
				int[] point = new int[5];
				int[] painum = {0, 25, 25, 25, 25};
				String[][] paiout = new String[5][3];
				int[] nowPoint = new int[5];
				int paiPoint = 0;
				while (true) {
					for (int i = 0; i < 4; i++) {
						paiout[now][1] = fromPlayer[now].readUTF();
						paiPoint = fromPlayer[now].readInt();
						paiout[now][2] = fromPlayer[now].readUTF();
						painum[now] = fromPlayer[now].readInt();
						System.out.println(paiout[now][1] + " " + paiout[now][2]);
						state += "玩家" + now + ":" + paiout[now][1] + " " + paiout[now][2] + "| ";
						if (i != 3) {
							firstPut(toPlayer, now, paiout[(now + 3 - i) % 4 + 1][1], paiout[(now + 3 - i) % 4 + 1][2], paiPoint);
							sendPut(toPlayer, now, state);
						}
						if (paiout[now][2].equals("") || paiout[now][1].equals(paiout[now][2])) {
							nowPoint[now] = -paiPoint;
						} else nowPoint[now] = -3000;
						now = now % 4 + 1;
//					if(i != 3) toPlayer[now].writeInt(CONTINUE);
					}
					//统计分数
					int winner = 0;
					if (nowPoint[1] >= nowPoint[2] && nowPoint[1] >= nowPoint[3] && nowPoint[1] >= nowPoint[4]) {
						point[1] += countNum(paiout);
//					toPlayer[1].writeInt(CONTINUE);
						winner = 1;
					} else if (nowPoint[2] >= nowPoint[1] && nowPoint[2] >= nowPoint[3] && nowPoint[2] >= nowPoint[4]) {
						point[2] += countNum(paiout);
//					toPlayer[2].writeInt(CONTINUE);
						winner = 2;
					} else if (nowPoint[3] >= nowPoint[1] && nowPoint[3] >= nowPoint[2] && nowPoint[3] >= nowPoint[4]) {
						point[3] += countNum(paiout);
//					toPlayer[3].writeInt(CONTINUE);
						winner = 3;
					} else if (nowPoint[4] >= nowPoint[1] && nowPoint[4] >= nowPoint[2] && nowPoint[4] >= nowPoint[3]) {
						point[4] += countNum(paiout);
//					toPlayer[4].writeInt(CONTINUE);
						winner = 4;
					}
					String logPoint = "当前比分为: 玩家1:" + point[1] + " |玩家2:" + point[2] + " |玩家3:" + point[3] + " |玩家4:" + point[4];
					logPoint += "\n剩余牌数为:" + painum[1];
					for (int i = 1; i <= 4; i++) {
						toPlayer[i].writeUTF(state);
						toPlayer[i].writeUTF(logPoint);
					}
					state = "";
					System.out.println(logPoint);
					System.out.println("本轮胜者:" + winner);
					now = winner;

					if (painum[1] == 0 && painum[2] == 0 && painum[3] == 0 && painum[4] == 0) {
						int winer = 0;
						if (win(point[1], point[2], point[3], point[4]) == 1) winer = PLAYER1_WON;
						if (win(point[1], point[2], point[3], point[4]) == 2) winer = PLAYER2_WON;
						if (win(point[1], point[2], point[3], point[4]) == 3) winer = PLAYER3_WON;
						if (win(point[1], point[2], point[3], point[4]) == 4) winer = PLAYER4_WON;
						if (win(point[1], point[2], point[3], point[4]) == 0) winer = DRAW;
						for (int i = 1; i <= 4; i++) {
							toPlayer[i].writeInt(winer);
						}
						break;
					} else {
						for (int i = 1; i <= 4; i++) {
							toPlayer[i].writeInt(CONTINUE);
						}
						toPlayer[winner].writeUTF("");
						toPlayer[winner].writeUTF("");
						toPlayer[winner].writeInt(0);
						toPlayer[winner].writeUTF(state);
					}
				}
			} catch (IOException ex) {
//			System.err.println(ex);
				ex.printStackTrace();
			}
		}

		private void firstPut(DataOutputStream player[], int now, String pai1, String pai2, int value) throws IOException {
//		for(int i = 1; i <= 4; i++) {
//			if(i != now) {
//				player[i].writeUTF(pai1);
//				player[i].writeUTF(pai2);
//			}
//		}
			now = now % 4 + 1;
			player[now].writeUTF(pai1);
			player[now].writeUTF(pai2);
			player[now].writeInt(value);
		}

		private void sendPut(DataOutputStream player[], int now, String state) throws IOException {
//		for(int i = 1; i <= 4; i++) {
//			if(i != now) {
//				player[i].writeUTF(state);
//			}
//		}
			now = now % 4 + 1;
			player[now].writeUTF(state);
		}

		private int win(int a, int b, int c, int d) {
			if (a > b && a > c && a > d) return 1;
			else if (b > a && b > c && b > d) return 2;
			else if (c > a && c > b && c > d) return 3;
			else if (d > a && d > b && d > c) return 4;
			else return 0;
		}

		private int countNum(String[][] paiout) {
			int sum = 0;
			for (int i = 1; i <= 4; i++) {
				for (int j = 1; j <= 2; j++) {
					if (paiout[i][j].equals("") || paiout[i][j].equals("大王   ") || paiout[i][j].equals("小王   ")) ;
					else {
						int len = paiout[i][j].length();
						char p = paiout[i][j].charAt(len - 1);
						if (p == 'J' || p == 'Q' || p == 'K' || p == '0') sum += 10;
						else if (p == 'A') sum += 1;
						else sum += p - '0';
					}
				}
			}
			return sum;
		}
	}
}
