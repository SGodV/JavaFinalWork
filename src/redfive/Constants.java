package redfive;
//实现一个红五游戏程序 实现：
//
//		1）游戏由两副牌构成
//		2）4个玩家
//		3）系统能随机洗牌后模拟真实情况分别向4个玩家发牌，并剩下8张底牌
//		4）程序能优美的方式输出四个玩家手上的牌和底牌（按照人类合理的理牌顺序输出）
//		5）主控线程负责洗牌和获取底牌
//		6）4个玩家为4个独立线程，主控线程分别发牌给每个玩家线程
//		7）能够根据红五的规则（可以适当的简化，实习单张、两张牌的接牌方式，拖拉机的方式可以不需要）出牌
//		8）实现每位玩家的记分功能
//
//		需要采用多线程技术、各终端间的通讯、泛型、集合等相关的JAVA技术实现
public interface Constants {
	public static int PLAYER1 = 1;
	public static int PLAYER2 = 2;
	public static int PLAYER3 = 3;
	public static int PLAYER4 = 4;
	public static int PLAYER1_WON = 1;
	public static int PLAYER2_WON = 2;
	public static int PLAYER3_WON = 3;
	public static int PLAYER4_WON = 4;
	public static int DRAW = 3;
	public static int CONTINUE = 4;
}
