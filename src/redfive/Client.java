package redfive;

import java.awt.*;
import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.naming.InitialContext;
import javax.swing.JApplet;

public class Client implements Runnable, Constants{
	private boolean myTurn = false;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private boolean continueToPlay = true;
	private String host = "localhost";
	private ArrayList<pai> myPai = new ArrayList<pai>();
	private String state = "";
	private String first1,first2;
	private int paiValue = 0;
	private int x,y;
	public static void main(String args[]) {
		new Client().connectToServer();
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
				myplayer = "玩家1";
			}
			else if (player == PLAYER2) {
				myplayer = "玩家2";
			}
			else if (player == PLAYER3) {
				myplayer = "玩家3";
			}
			else if (player == PLAYER4) {
				myplayer = "玩家4";
			}
			System.out.println("成功加入游戏，你是:"+myplayer+"! 请记住!");
			myPai.clear();
			for(int i=0;i<25;i++) {
				pai x = new pai();
				x.setId(fromServer.readUTF());
				x.setVal(fromServer.readInt());
				myPai.add(x);
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
			if(x == 0 && y == 0) {
//				autoOut();
//				x = this.x;
//				y = this.y;
				x = in.nextInt();
				y = in.nextInt();
			}
			else {
				if(x > myPai.size() || y > myPai.size()) {
					System.out.println("输入数字超出手牌大小");
					continue;
				}
				if(x < y || x == 0) {
					System.out.println("输入不合法(大小顺序不对或者不出牌)");
					continue;
				}
				if(x == y) {
					System.out.println("不能打两张相同的牌");
					continue;
				}
				if(state.equals("") && y!=0 && myPai.get(x-1).getVal() != myPai.get(y-1).getVal()) {
					System.out.println("第一个出对子需要花色和数字均相同的牌");
					continue;
				}
				if(!state.equals("") && !checkAfter(x, y)){
					continue;
				}
			}
			try {
				System.out.println(x+" "+y);
				toServer.writeUTF(myPai.get(x-1).getId());
				toServer.writeInt(myPai.get(x-1).getVal());
				if(y != 0) toServer.writeUTF(myPai.get(y-1).getId());
				else toServer.writeUTF("");
				
				myPai.remove(x-1);
				if(y != 0) myPai.remove(y-1);
				toServer.writeInt(myPai.size());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
	private void waitForPlayerAction() throws InterruptedException {
		Thread.sleep(500);
	}
	private boolean checkAfter(int x, int y) {
		String my1, my2;
		if(first2.equals("")) {
			my1 = myPai.get(x-1).getId();
			if(my1.substring(0, 2).equals(first1.substring(0, 2))) return true;
			else {
				if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
					for(int i=0;i<myPai.size();i++) {
						if(myPai.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
							System.out.println("必须出相同花色的单牌");
							return false;
						}
					}
				}
				if(paiValue < 0) {
					if(myPai.get(x-1).getVal() < 0) return true;
					for(int i=0;i<myPai.size();i++) {
						if(myPai.get(i).getVal() < 0) {
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
				if(myPai.get(x-1).getId().substring(0, 2).equals(first1.substring(0, 2)) && myPai.get(x-1).getVal() >= -600) num1 ++;
				if(myPai.get(y-1).getId().substring(0, 2).equals(first1.substring(0, 2)) && myPai.get(y-1).getVal() >= -600) num1 ++;
				for(int i=0;i<myPai.size();i++) {
					if(myPai.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
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
				if(myPai.get(x-1).getVal() < 0) num1 ++;
				if(myPai.get(y-1).getVal() < 0) num1 ++;
				for(int i=0;i<myPai.size();i++) {
					if(myPai.get(i).getVal() < 0) {
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
				for(int i=0;i<myPai.size();i++) {
					if(myPai.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
						x = i+1;
						y = 0;
						return;
					}
				}
			}
			if(paiValue < 0) {
				for(int i=0;i<myPai.size();i++) {
					if(myPai.get(i).getVal() < 0) {
						x = i+1;
						y = 0;
						return;
					}
				}
			}
			x = myPai.size();
			y = 0;
			return;
		}
		else {
			ArrayList<Integer> out = new ArrayList<Integer>();
			if(!first1.substring(0, 2).equals("大王") && !first1.substring(0, 2).equals("小王") && !first1.equals("红桃5")) {
				for(int i=0;i<myPai.size();i++) {
					if(myPai.get(i).getId().substring(0, 2).equals(first1.substring(0, 2))) {
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
				for(int i=myPai.size()-1;i>=0;i--) {
					if(myPai.get(i).getVal() < 0) {
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
				if(out.size() == 0) y = myPai.size();
				else y = out.get(0);
				if(out.size() == 0) x = myPai.size() - 1;
				else if(out.size() == 1) x = myPai.size();
				else x = out.get(1);
				return;
			}
		}
	}
	private void receiveInfoFromServer() throws IOException {
		int status = fromServer.readInt();
		if (status == PLAYER1_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,玩家1胜利");
		}
		else if (status == PLAYER2_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,玩家2胜利");
		}
		else if (status == PLAYER3_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,玩家3胜利");
		}
		else if (status == PLAYER3_WON) {
			continueToPlay = false;
			System.out.println("\n比赛结束,玩家4胜利");
		}
		else if (status == DRAW) {
			continueToPlay = false;
			System.out.println("\n比赛结束,平局");
		}
		else {
			System.out.print("\n你的回合：");
		}
	}
	private void prMypai() {
		System.out.print("当前手牌: ");
		for(int i=0;i<myPai.size();i++) {
			System.out.print(myPai.get(i).id+" ");
		}
		System.out.println();
	}
}
