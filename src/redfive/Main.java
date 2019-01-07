package redfive;

import java.util.*;


public class Main {
	public static int huase;
	static Card[][] play = new Card[4][30];
	public static void main(String[] args) {
		Comparator cmp = new cmp1();
//		new Main().pr2(a);
		Random p = new Random();
		String x = null;
		huase = (p.nextInt());
		if(huase < 0) huase = -huase;
		huase %= 4;
		if(huase == 0) x = "黑桃";
		if(huase == 1) x = "草花";
		if(huase == 2) x = "方块";
		if(huase == 3) x = "红桃";
		System.out.println("主花色为:"+x);
		
		Card[] a = new Main().init();
		a = new Main().xipai(a);
		play = new Main().fapai(a);
		Arrays.sort(a,0,108, cmp);
		
//		try {
//			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("D:/zzzzzzz/main.txt")));
//			out.writeUTF("所有牌:");
//			for(int i=0;i<108;i++) {
//				out.writeUTF(a[i].getId()+" "+a[i].getVal()+" ");
//			}
//			out.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("D:/zzzzzzz/main.txt")));
//			in.readUTF();
//			for(int i=0;i<108;i++) {
//				a[i].setId(in.readUTF());
//			}
//			in.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		for(int i = 0;i < 4;i ++) {
			Arrays.sort(play[i],0,25,cmp);
			System.out.print("玩家"+(i+1)+"：");
			for(int j=0;j<25;j++) {
				System.out.print(play[i][j].getId()+" ");
			}
			System.out.println();
		}
		Arrays.sort(a,100,108,cmp);
		System.out.print("底牌：");
		for(int i=100;i<108;i++) {
			System.out.print(a[i].getId()+" ");
		}
		System.out.println();
		
//		try {
//			FileWriter fw = new FileWriter("D:/zzzzzzz/play1.txt");
//			fw.write("玩家1:");
//			for(int j=0;j<25;j++) {
//				fw.write(play[0][j].getId()+" ");
//			}
//	        fw.close();
//
//	        fw = new FileWriter("D:/zzzzzzz/play2.txt");
//			fw.write("玩家2:");
//			for(int j=0;j<25;j++) {
//				fw.write(play[1][j].getId()+" ");
//			}
//	        fw.close();
//	        
//	        fw = new FileWriter("D:/zzzzzzz/play3.txt");
//			fw.write("玩家3:");
//			for(int j=0;j<25;j++) {
//				fw.write(play[2][j].getId()+" ");
//			}
//	        fw.close();
//	        
//	        fw = new FileWriter("D:/zzzzzzz/play4.txt");
//			fw.write("玩家4:");
//			for(int j=0;j<25;j++) {
//				fw.write(play[3][j].getId()+" ");
//			}
//	        fw.close();
//	        
//	        fw = new FileWriter("D:/zzzzzzz/dipai.txt");
//			fw.write("底牌:");
//			for(int i=100;i<108;i++) {
//				fw.write(a[i].getId()+" ");
//			}
//	        fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public Card[] init() {
		Card[] a = new Card[200];
		for(int i = 0;i < 2;i ++) a[i] = new Card();
		int i = 0,val = 0;
		a[i++].setId("大王 ");a[0].setVal(-1999);
		a[i++].setId("小王 ");a[1].setVal(-1998);
		
		
		int color = 14;
		for( ;color >= 2;) {
			String x = "";
			if(color>10) { 
				if(color == 11) x+="J";
				if(color == 12) x+="Q";
				if(color == 13) x+="K";
				if(color == 14) x+="A";
			}
			else x+=color+"";
			
			a[i] = new Card();
			a[i].setId("黑桃"+x);
			a[i].setVal(val);
			i++;val++;
			
			a[i] = new Card();
			a[i].setId("草花"+x);
			a[i].setVal(val);
			i++;val++;
			
			a[i] = new Card();
			a[i].setId("方块"+x);
			a[i].setVal(val);
			i++;val++;
			
			if(color != 5) {
				a[i] = new Card();
				a[i].setId("红桃"+x);
				a[i].setVal(val);
				i++;val++;
			}
			else {
				a[i] = new Card();
				a[i].setId("红桃"+x);
				a[i].setVal(-2000);
				i++;val++;
			}
			
			color--;
		}
		
		int y = huase;
		a[54 + y - 8].setVal(a[54 + y - 8].getVal() - 900);
		if(y == 0) a[54 + y - 7].setVal(a[54 + y - 7].getVal() - 880);
		if(y == 1) a[54 + y - 9].setVal(a[54 + y - 9].getVal() - 880);
		if(y == 2) a[54 + y - 7].setVal(a[54 + y - 7].getVal() - 880);
		if(y == 3) a[54 + y - 9].setVal(a[54 + y - 9].getVal() - 880);
		
		a[54 + y - 4].setVal(a[54 + y - 4].getVal() - 800);
		if(y == 0) a[54 + y - 3].setVal(a[54 + y - 3].getVal() - 780);
		if(y == 1) a[54 + y - 5].setVal(a[54 + y - 5].getVal() - 780);
		if(y == 2) a[54 + y - 3].setVal(a[54 + y - 3].getVal() - 780);
		if(y == 3) a[54 + y - 5].setVal(a[54 + y - 5].getVal() - 780);
		
		for(int k = 2;k <= 42;k +=4) {
			a[k + y].setVal(a[k + y].getVal() - 500);
//			System.out.println(a[i+y].getId());
		}
		
		int j;
		for(j = 0;j < i;j ++) 
			a[j+i] = a[j];
		i+=j;
//		System.out.println(i);
		return a;
	}
	public Card[] xipai(Card[] a) {
		int i = 0;
		while(i < 108*2) {
			Random p = new Random();
			int x = i%108;
			int y = -1;
			while( y==-1||x==y ) {
				p = new Random();
				y = (p.nextInt()*1000);
				if(y < 0) y = -y;
				y%=108;
			}
//			System.out.println(x+" "+y);
//			System.out.println(a[x].getId()+" "+a[y].getId());
			Card tmp = a[x];
			a[x] = a[y];
			a[y] = tmp;
			i++;
		}
//		for(i=0;i<108;i++) System.out.print(a[i].getId()+" ");
		return a;
	}
	public Card[][] fapai(Card[] a) {
		Card[][] play = new Card[4][30];
		for(int i=0;i<100;i++) {
			int k = i/4;
			play[i%4][k] = new Card();
//			System.out.print(a[i].getId()+" ");
			play[i%4][k] = a[i];
		}
		return play;
	}
	public void pr(Card[][] play) {
		for(int i=0;i<4;i++) {
			for(int j=0;j<27;j++) {
				System.out.print(play[i][j].getId()+" ");
			}
			System.out.println();
		}
	}
	public void pr2(Card[] a) {
		for(int i=0;i<108;i++) {
			System.out.print(a[i].getId()+" ");
		}
		System.out.println(0);
	}
}
class cmp1 implements Comparator<Card> {
    public int compare (Card a, Card b) {
//		if(a.getVal()>b.getVal()) return 1;
//		else if(a.getVal()==b.getVal()) return 0;
//		else return -1;
    	return a.getVal()-b.getVal();
	}
}
