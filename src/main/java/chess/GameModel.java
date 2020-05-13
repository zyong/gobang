package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/* 游戏数据模型
 * 
 */
public class GameModel
{


	public static final int N = Config.N;
	
	// 点位矩阵
	Position[][] matrix = new Position[N][N];
		
	// 所有的经纬线、斜线
	// 每条线由该直线上经过的点位组成
	List<Position[]> lines = new ArrayList<>();

	EvaluatePoint ep = new EvaluatePoint();

	private LinkedList<Position> pre = new LinkedList<>();
	private LinkedList<Position> next = new LinkedList<>();


	// 下棋次数
	int time = 0;
	
	public GameModel()
	{
		// 创建点位
		// 二维数组的使用：和一维差不多，相当于一间大教室，11排11列
		for(int i=0; i<N;i++)
		{
			for(int k=0; k<N; k++)
			{
				matrix[i][k] = new Position(Position.EMPTY, i, k);
			}
		}
		
		// 计算出所有的经纬线、斜线所包含的点位
		// 横线(纬线)
		for(int i=0; i<N; i++)
		{
			Position[] line = new Position[N];
			for(int k=0;k<N; k++)
			{				
				line[k] = matrix[k][i];							
			}		
			lines.add( line );	
			printLine("横线", line );
		}
		// 竖线(经线)
		for(int i=0; i<N; i++)
		{
			Position[] line = new Position[N];
			line = Arrays.copyOf(matrix[i], N);					
			lines.add( line );	
			printLine("竖线", line );
		}
		// 左斜线
		for(int i=0; i<N; i++)
		{
			Position[] topLeft = new Position[i+1];
			Position[] bottomRight = new Position[i+1];
			for(int k=0;k<=i; k++)
			{		
				// i=0时, (0,0)
				// i=1时, (0,1) (1,0)
				// i=2时, (0,2), (1,1), (2,0)
				topLeft[k] = matrix[k][i-k];
				// i=0时, (10,10)
				bottomRight[k] =  matrix[N-k-1][N-i+k-1];
			}		
			lines.add( topLeft );
			lines.add( bottomRight );
			printLine("斜线", topLeft );
			printLine("斜线", bottomRight );
		}
		
		// 右斜线
		for(int i=0; i<N; i++)
		{
			Position[] topRight = new Position[i+1];
			Position[] bottomLeft = new Position[i+1];
			for(int k=0;k<=i; k++)
			{		
				// i=0时, (10,0)
				// i=1时, (9,0) (10,1)
				// i=2时, (8,0) (9,1) (10,2)
				topRight[k] = matrix[N-i+k-1][k];
				// i=0时, (0,10)
				// i=1时, (0,9), (1,10)
				// i=2时, (0,8), (1,9), (2,10)
				bottomLeft[k] =  matrix[k][N-i+k-1];
			}		
			lines.add( topRight );
			lines.add( bottomLeft );
			printLine("反斜线", topRight );
			printLine("反斜线", bottomLeft );
		}
	}
	
	// 根据位置，取得点位的值
	public Position at(int px, int py)
	{
		if ((px < 0 || px >= N) || (py < 0 || py >= N)) {
			return null;
		}
		return matrix[px][py];
	}
	
	// 打印出该行内所有的点位
	public void printLine(String name, Position[] line)
	{
		return;
	}
	
	// 检查所有直线上，是否存在五子连珠。
	public int checkWin()
	{
		for(Position[] line : lines)
		{
			Boolean result = checkWin(line, Position.COMPUTER);
			if (Boolean.TRUE.equals(result)) {
				return Position.COMPUTER;
			}

			result = checkWin(line, Position.HUMAN);
			if (Boolean.TRUE.equals(result)) {
				return Position.HUMAN;
			}
		}
		return Position.EMPTY;
	}
	
	// 检查一条直线上，是否存在五子连珠。如果 存在，则返回这五个连续的点位
	public Boolean checkWin(Position[] line, int role)
	{
		int start = 0;

		for(int i=0; i<line.length;i++)
		{
			Position p = line[i];
			if(p.role == role) {
				start++;

				if ((i + 4) < line.length) {
					// 比如 0,1,2,3,4 全白
					for(int k=1; k<=5;k++) {
						if (line[k+i].role == p.role) {
							start++;
							if (start == 5) {
								return true;
							}
							continue;
						} else {
							break;
						}
					}
					start = 0;
				}
				
			}
		}
		
		return false;
	}
	
	// 重置为初始状态
	public void reset()
	{
		for(int i=0; i<N;i++)
		{
			for(int k=0; k<N; k++)
			{
				Position p = matrix[i][k];
				p.role = Position.EMPTY;
				p.mark = 0;
			}
		}
		time = 0;
		pre = new LinkedList<>();
		next = new LinkedList<>();
	}

	public void update(int x, int y, int role)
	{
		if (Config.DEBUG) {
			System.out.printf("put point px:%d py:%d role:%d%n", x, y, role);
		}
		matrix[x][y].role = role;
		matrix[x][y].mark = ++time;
		Position p = matrix[x][y];
		if (!pre.contains(p) && p.role != Position.EMPTY) {
			pre.addFirst(matrix[x][y]);
		}
	}

	public void update(Position p) {
		matrix[p.px][p.py] = p;
		if (!pre.contains(p) && p.role != Position.EMPTY) {
			pre.addFirst(p);
		}
	}

	public boolean isBackward() {
		return !pre.isEmpty();
	}

	public Position backward() {
		int len;
		if (pre.size() >= 2) {
			len = 2;
		} else {
			len = 1;
		}

		Position p = null;
		for (int i=0; i<len; i++) {
			p = pre.removeFirst();
			next.addFirst(new Position(p));
			p.role = Position.EMPTY;
			update(p);
	
			if (Config.DEBUG) {
				System.out.printf("backward point px:%d py:%d role:%d%n", p.px, p.py, p.role);
			}
		}

		return p;
	}

	public boolean isForward() {
		return !next.isEmpty();
	}
	
	public Position forward() {
		int len;
		if (next.size() >= 2) {
			len = 2;
		} else {
			len = 1;
		}
		Position p = null;
		for (int i=0; i<len; i++) {
			p = next.removeFirst();
			p.mark = time;
			update(p);
			if (Config.DEBUG) {
				System.out.printf("forward point px:%d py:%d role:%d%n", p.px, p.py, p.role);
			}
		}

		return p;
	}

	public void verify(Position p) {
		matrix[p.px][p.py] = p;
	}



	public List<Position> genPosition(int deep) {
		ArrayList<Position> fives = new ArrayList<>();
        ArrayList<Position> fours = new ArrayList<>();
        ArrayList<Position> twothrees = new ArrayList<>();
        ArrayList<Position> threes = new ArrayList<>();
        ArrayList<Position> twos = new ArrayList<>();
		ArrayList<Position> neighbors = new ArrayList<>();
		ArrayList<Position> ans = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
				if (matrix[i][j].role == Position.EMPTY && 
					hasNeihbor(matrix[i][j], 1, 1)) {
						// 启发式搜索,先计算得分，然后按照得分排序
					int scoreHum = ep.scorePoint(this, i, j, Position.HUMAN);
					int scoreCom = ep.scorePoint(this, i, j, Position.COMPUTER);

					if (Config.DEBUG) {
						System.out.println("gen point x:" + i + " y:" + j);
						System.out.println("gen point scoreCom:" + scoreCom);
						System.out.println("gen point scoreHum:" + scoreHum);
					}
					// 分别查看电脑和玩家能否到5，4，3等分值
					/**
					 * 首先取自己得分高的位置，如果对方得分比自己高，证明对方棋局有优势要封堵，
					 * 封堵的办法是在对方得分高的位置进行设置棋子，这样对方就不能得势了； 
					 */
					if (scoreCom >= Position.FIVE) {
						ans.add(matrix[i][j]);
						return ans;
					} else if (scoreHum >= Position.FIVE) {
						// 不在这里返回的原因电脑也可能成5
						fives.add(matrix[i][j]);
					} else if (scoreCom >= Position.FOUR) {
						// 电脑的结果排在前面
						fours.add(0, matrix[i][j]);
					} else if (scoreHum >= Position.FOUR) {
						fours.add(matrix[i][j]);
						// 双三的原因是两边没有阻碍
					} else if (scoreCom >= 2*Position.THREE) {
						twothrees.add(0, matrix[i][j]);
					} else if (scoreHum >= 2*Position.THREE) {
						twothrees.add(matrix[i][j]);
					} else if (scoreCom >= Position.THREE) {
						threes.add(0, matrix[i][j]);
					} else if (scoreHum >= Position.THREE) {
						threes.add(matrix[i][j]);
					} else if (scoreCom >= Position.TWO) {
						twos.add(0, matrix[i][j]);
					} else if (scoreHum >= Position.TWO) {
						twos.add(matrix[i][j]);
					} else {
						neighbors.add(matrix[i][j]);
					}
				}
            }
		}
		
		
		if (!fives.isEmpty()) {
			ans.add(fives.get(0));
			return ans;
		}

		if (!fours.isEmpty()) {
			ans.add(fours.get(0));
			return ans;
		}

		if (!twothrees.isEmpty()) {
			ans.addAll(threes);
			return ans;
		}
		twos.addAll(neighbors);
		threes.addAll(twos);
		ans = threes;

		if (ans.size() > Config.countLimit) {
			return new ArrayList<>(ans.subList(0, Config.countLimit));
		}
			 
		return ans;
	}

	public boolean hasNeihbor(Position point, int distance, int count) {
		int len = matrix.length;
		int startX = point.px - distance;
		int endX = point.px + distance;
		int startY = point.py - distance;
		int endY = point.py + distance;

		for (int i=startX; i<= endX; i++) {
			if (i<0 || i>= len)  
				continue;
			for (int j = startY; j <= endY; j++) {
				if (j < 0 || j>=len) 
					continue;
				if (i == point.px && j == point.py) 
					continue;
				if (matrix[i][j].role != Position.EMPTY) {
					count--;
					if (count <= 0) 
						return true;
				}
			}
		
		}
		return false;
	}

}
