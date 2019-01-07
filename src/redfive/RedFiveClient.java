package redfive;

import java.io.*;
import java.net.*;
import java.util.*;

public class RedFiveClient implements Runnable, RedFiveConstants {
	private boolean myTurn = false;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private boolean continueToPlay = true;
	private String host = "localhost";
	private ArrayList<Card> myCard = new ArrayList<Card>();
	private String state = "";
	private String first1,first2;
	private int paiValue = 0;
	private int x,y;
	public static void main(String args[]) {
		new RedFiveClient().connectToServer();
	}
	private void connectToServer(){
		try {
			Socket socket = new Socket(host, 8000);
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run(){
		try {
			int player = fromServer.readInt();
			String myplayer = "";
			if (player == PLAYER1) {
				myplayer = "庄家";
			}
			else if (player == PLAYER2) {
				myplayer = "闲家1";
			}
			else if (player == PLAYER3) {
				myplayer = "闲家2";
			}
			else if (player == PLAYER4) {
				myplayer = "闲家3";
			}
			System.out.println("成功加入游戏，你是:"+myplayer+"! ");
			myCard.clear();
			for(int i=0;i<25;i++) {
				Card x = new Card();
				x.setId(fromServer.readUTF());
				x.setVal(fromServer.readInt());
				myCard.add(x);
			}
			prMypai();
			while (continueToPlay) {
				first1 = fromServer.readUTF();
				first2 = fromServer.readUTF();
				paiValue = fromServer.readInt();
				state = fromServer.readUTF();
				System.out.println(first1+" "+first2+" "+state);
				prMypai();
				System.out.println("本轮出牌情况: "+state);
				Init();
				state = fromServer.readUTF();
				System.out.println("本轮出牌情况: "+state);
				String logpoint = fromServer.readUTF();
				System.out.println(logpoint);
//				System.out.println(logpoint);
				receiveInfoFromServer();
			}
		} catch (Exception ex) {
		}
	}
	private void Init() {
		System.out.println("请出牌:(输入数字从1开始表示出的牌，如果出单张，请输入x 0. 前一个数字必须大于后一个数字)");
		Scanner in = new Scanner(System.in);
		while(true) {
//			int x = in.nextInt();
//			int y = in.nextInt();
			x = 0; y = 0;
			autoOut();
//			x = in.nextInt();
//			y = in.nextInt();

			if (y == 0){
				System.out.println(myCard.get(x-1).getId());
			}
			else {
				System.out.println(myCard.get(x-1).getId()+" "+ myCard.get(y-1).getId());
			}

				if(x > myCard.size() || y > myCard.size()) {
					System.out.println("输入数字超出手牌大小");
//					continue;
				}
				else if(x < y || x == 0) {
					System.out.println("输入不合法(大小顺序不对或者不出牌)");
//					continue;
				}
				else if(x == y) {
					System.out.println("不能打两张相同的牌");
//					continue;
				}
				else if(state.equals("") && y!=0 && myCard.get(x-1).getVal() != myCard.get(y-1).getVal()) {
					System.out.println("第一个出对子需要花色和数字均相同的牌");
//					continue;
				}
				else if(!state.equals("")){
//					continue;
					if (checkAfter(x, y)){
						try {
							toServer.writeUTF(myCard.get(x-1).getId());
							toServer.writeInt(myCard.get(x-1).getVal());
							if(y != 0) toServer.writeUTF(myCard.get(y-1).getId());
							else toServer.writeUTF("");

							myCard.remove(x-1);
							if(y != 0) myCard.remove(y-1);
							toServer.writeInt(myCard.size());

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				else {
					try {
						toServer.writeUTF(myCard.get(x-1).getId());
						toServer.writeInt(myCard.get(x-1).getVal());
						if(y != 0) toServer.writeUTF(myCard.get(y-1).getId());
						else toServer.writeUTF("");

						myCard.remove(x-1);
						if(y != 0) myCard.remove(y-1);
						toServer.writeInt(myCard.size());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}

		}
	}
	private void waitForPlayerAction() throws InterruptedException {
		Thread.sleep(500);
	}
	private boolean checkAfter(int x, int y) {
		String my1, my2;
		if(first2.equals("")) {
			my1 = myCard.get(x-1).getId();
			if(my1.substring(0, 2).equals(first1.substring(0, 2))) return true;
			else {
				if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
					for(int i = 0; i< myCard.size(); i++) {
						if(myCard.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
							System.out.println("必须出相同花色的单牌");
							return false;
						}
					}

				}
				if(paiValue < 0) {
					if(myCard.get(x-1).getVal() < 0) return true;
					for(int i = 0; i< myCard.size(); i++) {
						if(myCard.get(i).getVal() < 0) {
							System.out.println("主牌必须用主牌接");
							return false;
						}
					}

				}
				return true;
			}
		}
		else {
			if(y == 0) {
				System.out.println("必须出对子");
				return false;
			}
			if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
				int num1 = 0, num2 = 0;
//				if(myCard.get(x-1).getId().substring(0, 2).equals(first1.substring(0, 2)) && myCard.get(x-1).getVal() >= -600) num1 ++;
//				if(myCard.get(y-1).getId().substring(0, 2).equals(first1.substring(0, 2)) && myCard.get(y-1).getVal() >= -600) num1 ++;
				if(myCard.get(x-1).getId().substring(0, 2).equals(first1.substring(0, 2))) num1 ++;
				if(myCard.get(y-1).getId().substring(0, 2).equals(first1.substring(0, 2))) num1 ++;
				for(int i = 0; i< myCard.size(); i++) {
					if(myCard.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
						num2 ++;
					}
					if(num2 == 2) break;
				}
				if(num2 > num1) {
					System.out.println("必须出相同花色的牌");
					return false;
				}

			}
			if(paiValue < 0) {
				int num1 = 0, num2 = 0;
				if(myCard.get(x-1).getVal() < 0) num1 ++;
				if(myCard.get(y-1).getVal() < 0) num1 ++;
				for(int i = 0; i< myCard.size(); i++) {
					if(myCard.get(i).getVal() < 0) {
						num2 ++;
					}
					if(num2 == 2) break;
				}
				if(num2 > num1) {
					System.out.println("主牌必须用主牌来接");
					return false;
				}
			}
			return true;
		}
	}
	private void autoOut() {
		if(state.equals("")) {
			x = 1;
			y = 0;
			return;
		}
		if(first2.equals("")) {
			if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
				for(int i = 0; i< myCard.size(); i++) {
					if(myCard.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
						x = i+1;
						y = 0;
						return;
					}
				}
			}
			if(paiValue < 0) {
				for(int i = 0; i< myCard.size(); i++) {
					if(myCard.get(i).getVal() < 0) {
						x = i+1;
						y = 0;
						return;
					}
				}
			}
			x = myCard.size();
			y = 0;
			return;
		}
		else {
			ArrayList<Integer> out = new ArrayList<Integer>();
			if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
				for(int i = 0; i< myCard.size(); i++) {
					if(myCard.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
						out.add(i+1);
					}
					if(out.size() >= 2) {
						x = out.get(1);
						y = out.get(0);
						return;
					}
				}
			}
			if(paiValue < 0) {
				for(int i = myCard.size()-1; i>=0; i--) {
					if(myCard.get(i).getVal() < 0) {
						out.add(i+1);
					}
					if(out.size() >= 2) {
						x = out.get(1);
						y = out.get(0);
						return;
					}
				}
			}
			else {
				if(out.size() == 0) y = myCard.size();
				else y = out.get(0);
				if(out.size() == 0) x = myCard.size() - 1;
				else if(out.size() == 1) x = myCard.size();
				else x = out.get(1);
				return;
			}
		}
	}
	private void receiveInfoFromServer() throws IOException {
		int status = fromServer.readInt();
		if (status == PLAYER1_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,庄家胜利");
		}
		else if (status == PLAYER2_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,闲家1胜利");
		}
		else if (status == PLAYER3_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,闲家2胜利");
		}
		else if (status == PLAYER3_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,闲家3胜利");
		}
		else if (status == DRAW) {
			continueToPlay = false;
			System.out.println("\n比赛结束,平局");
		}
		else {
			System.out.print("\n等待中。。。");
		}
	}
	private void prMypai() {
		System.out.print("你的回合：\n当前手牌: ");
		for(int i = 0; i< myCard.size(); i++) {
			System.out.print(myCard.get(i).id+" ");
		}
		System.out.println();
	}
}
