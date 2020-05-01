package my.chess;

import java.util.ArrayList;
import java.util.Arrays;
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
		return matrix[px][py];
	}
	
	// 打印出该行内所有的点位
	public void printLine(String name, Position[] line)
	{
		System.out.print(name);
		for(int i=0; i<line.length; i++)
		{
			Position p = line[i];
			System.out.printf("(%2d,%2d) ", p.px, p.py);
		}
		System.out.println();
	}
	
	// 检查所有直线上，是否存在五子连珠。
	public Position[] checkWin()
	{
		for(Position[] line : lines)
		{
			Position[] result = checkWin(line);
			if(result != null)
				return result;
		}
		return new Position[0];
	}
	
	// 检查一条直线上，是否存在五子连珠。如果 存在，则返回这五个连续的点位
	public Position[] checkWin(Position[] line)
	{
		int color = Position.EMPTY;
		int start = 0;
		for(int i=0; i<line.length;i++)
		{
			Position p = line[i];
			if(p.role != color)
			{
				start = i;
				color = p.role;
			}
			// 比如 0,1,2,3,4 全白
			if(color != Position.EMPTY && i-start>=4)
			{
				Position[] result = new Position[5];
				for(int k=0; k<5;k++)
					result[k] = line[start + k];
				return result;
			}
		}
		
		return new Position[0];
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
				p.mark = false;
			}
		}
	}

	public void update(int x, int y, int role)
	{
		lines.get(x)[y].role = role;
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
				if (matrix[i][j].role == Position.EMPTY && hasNeihbor(matrix[i][j], 2, 2)) {
					int scoreHum = scorePoint(matrix[i][j], Position.HUMAN);
					int scoreCom = scorePoint(matrix[i][j], Position.COMPUTER);
					// 分别查看电脑和玩家能否到5，4，3等分值
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
			return ans.subList(0, Config.countLimit);
		}
			 
		return ans;
	}

	/*
	 * 启发式评价函数
	 * 这个是专门给某一个空位打分的，不是给整个棋盘打分的
	 * 并且是只给某一个角色打分
	 */
	private int scorePoint(Position point, int role) {
		int result = 0;
		int count = 0;
		int block = 0;
		int empty = 0;

		int len = matrix.length;

		// 默认把当前位置当做己方棋子，就是这个棋子已经下了之后的分数
		count = 1;
		for (int i=point.py+1;;i++) {
			if (i >= len) {
				block++;
				break;
			}

			Position p = matrix[point.px][i];
			if (p.role == Position.EMPTY) {
				// 如果中间有空的情况
				if (empty == 0 && i < len-1 && matrix[p.px][i+1].role == role) {
					// 记录为空的位置
					empty = count;
					continue;
				} else {
					// 后面也没有子 or empty的值已经存在
					break;
				}
			}

			if (p.role == role) {
				count++;
				continue;
			} else {
				block++;
				break;
			}
		}

		// 计算左边的棋子
		for (int i=point.py-1;; i--) {
			if (i<0) {
				block++;
				break;
			}

			Position p = matrix[point.px][i];
			if (p.role == Position.EMPTY) {
				if (empty == 0 && i>0 && matrix[point.px][i-1].role == role) {
					empty = count;
					continue;
				} else {
					break;
				}
			}

			if (p.role == role) {
				count++;
				continue;
			} else {
				block++;
				break;
			}
		}
		// 得出横向得分
		result += score(count, block, empty);

		return result;
		
	}

	private int score(int count, int block, int empty) {
		if (empty == 0) {
			if (count >= 5) 
				return Position.FIVE;
			if (block == 0) {
				switch(count) {
					case 1: return Position.ONE;
					case 2: return Position.TWO;
					case 3: return Position.THREE;
					case 4: return Position.FOUR;
					default: break;

				}
			}

			if (block == 1) {
				switch(count) {
					case 1: return Position.BLOCKED_ONE;
					case 2: return Position.BLOCKED_TWO;
					case 3: return Position.BLOCKED_THREE;
					case 4: return Position.BLOCKED_FOUR;
					default: break;

				}
			}
			
		} else if (empty == 1 || empty == count - 1) {
			//
			if(count >= 6) {
				return Position.FIVE;
			}
			if(block == 0) {
				switch(count) {
					case 2: return Position.TWO;
					case 3:
					case 4: return Position.THREE;
					case 5: return Position.FOUR;
					default: break;
				}
			}
		} else if (empty == 2 || empty == count - 2) {
			if (count >= 7) {
				return Position.FIVE;
			}
			if (block == 0) {
				switch(count) {
					case 3:
					case 4:
					case 5: return Position.THREE;
					case 6: return Position.FOUR;
					default: break;

				}
			}

			if (block == 1) {
				switch(count) {
					case 3: return Position.BLOCKED_THREE;
					case 4: return Position.BLOCKED_THREE;
					case 5: return Position.BLOCKED_FOUR;
					case 6: return Position.FOUR;
					default: break;

				}
			}

			if (block == 2) {
				switch(count) {
					case 4:
					case 5:
					case 6: return Position.BLOCKED_FOUR;
					default: break;

				}
			}
		} else if (empty == 3 || empty == count - 3) {
			if (count >= 8) {
				return Position.FIVE;
			}
			if (block == 0) {
				switch(count) {
					case 4:
					case 5: return Position.THREE;
					case 6: return Position.THREE * 2;
					case 7: return Position.FOUR;
					default: break;

				}
			}

			if (block == 1) {
				switch(count) {
					case 4:
					case 5:
					case 6: return Position.BLOCKED_FOUR;
					case 7: return Position.FOUR;
					default: break;

				}
			}

			if (block == 2) {
				switch(count) {
					case 4:
					case 5:
					case 6:
					case 7: return Position.BLOCKED_FOUR;
					default: break;

				}
			}
		} else if(empty == 4 || empty == count - 4) {
			if (count >= 9) {
				return Position.FIVE;
			}
			if (block == 0) {
				switch(count) {
					case 5:
					case 6:
					case 7:
					case 8: return Position.FOUR;
					default: break;

				}
			}
			if (block == 1) {
				switch(count) {
					case 4:
					case 5:
					case 6:
					case 7: return Position.BLOCKED_FOUR;
					case 8: return Position.FOUR;
					default: break;

				}
			}

			if (block == 2) {
				switch(count) {
					case 5:
					case 6:
					case 7:
					case 8: return Position.BLOCKED_FOUR;
					default: break;

				}
			}
		} else if (empty == 5 || empty == count-5) {
			return Position.FIVE;
		}

		return 0;
	}

	private boolean hasNeihbor(Position point, int distance, int count) {
		ArrayList<Position> neibors = new ArrayList<>();
		
		// 找空位左右1格的位置
		for (int i = point.px-distance; i<=point.px+distance; i++) {
			if (i<=0 || i >= N) continue;
			for (int j=point.py - distance; j <= point.py + distance; j++) {
				if (j <= 0 || j >= N) continue;
				if (i == point.px && j == point.py) continue;
				// 找到一个节点
				if (matrix[i][j].role == Position.EMPTY) {
					neibors.add(matrix[i][j]);
					count--;
					if (count <= 0) 
						return false;
				}
			}
		}
		return true;
	}
}
