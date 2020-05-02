package my.chess;

public class Config {
    static int searchDeep = 4;  //搜索深度
    static double deepDecrease = 0.8; //按搜索深度递减分数，为了让短路径的结果比深路劲的分数高
    static int countLimit = 12; //gen函数返回的节点数量上限，超过之后将会按照分数进行截断
    static int checkmateDeep = 7;  //算杀深度

    public static final int N = 15; 	// 棋盘规模，与ChessBoard里的一致
    public static final boolean DEBUG = false;
}
