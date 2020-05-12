package gobang.project.chess;

/* 点位
 * 
 */
public class Position
{
	public static final int WHITE = 1;
	public static final int BLACK = 2;
	public static final int EMPTY = 0;

	public static final int HUMAN = 1;
	public static final int COMPUTER = 2;

	public static final int FIVE = 100000;
	public static final int FOUR = 10000;
	public static final int THREE = 1000;
	public static final int TWO = 100;
	public static final int ONE = 10;

	public static final int BLOCKED_FOUR = 1000;
	public static final int BLOCKED_THREE = 100;
	public static final int BLOCKED_TWO = 10;
	public static final int BLOCKED_ONE = 1;




	public int role = EMPTY; // 该点位的值
	public int px = 0; // 水平位置坐标 :0,1,2,...,10
	public int py = 0; // 竖直位置坐标 :0,1,2,...,10
	public int mark = 0; // 游戏结束时的标记,五子连珠的特殊显示标识
	public Double score = 0.0;

	
	public Position()
	{		
	}
	
	public Position(int role , int px, int py)
	{
		this.role = role;
		this.px = px;
		this.py = py;
	}

	public Position(Position p) {
		role = p.role;
		px = p.px;
		py = p.py;
		mark = p.mark;
		score = p.score;
	}

	public static int reverseRole(int role) {
		if (role == HUMAN) {
			return COMPUTER;
		} else {
			return HUMAN;
		}
	}

}
