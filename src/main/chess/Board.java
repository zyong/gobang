package main.chess;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.AWTEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/* 棋盘定义
 * 
 */

public class Board extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Color bgColor = new Color(0xFFD700);

	// socreCache[role][dir][row][column]
	public int[][][][] scoreCache = new int[3][6][GameModel.N][GameModel.N];

	private int rule = Position.COMPUTER; // 1人和人下，2人和电脑下，3电脑和电脑下
	// 说明当前游戏的状态是否还在继续
	private boolean isActive = false;

	private Rectangle square; // 中间广场
	private int cell; // 单元格大小
	private static final int N = GameModel.N;
	private int[] baseH = new int[GameModel.N + 1]; // 水平方向, 11条经线的位置
	private int[] baseV = new int[GameModel.N + 1]; // 竖起方向, 11条纬线的位置

	// 游戏状态数据
	private int whoIsNow = Position.COMPUTER; // 黑方先行
	private GameModel model = new GameModel();
	private EvaluatePoint ep = new EvaluatePoint();

	// 焦点提示
	private Position focus = new Position(Position.BLACK, -1, -1);

	// 电脑选手
	private Robots robotA;

	// 重新开始按钮的位置
	private Rectangle restartButton = new Rectangle();

	// 人与机器对战按钮的位置
	private Rectangle humandrobotButton = new Rectangle();

	private int[][] comScore = new int[N][N];

	private int[][] humScore = new int[N][N];

	/**
	 * 业务逻辑部分 1.初始化棋盘 1.定义棋盘大小，初始化棋盘事件，初始化棋子 2.下棋数据操作 1.添加棋子，删除棋子，悔棋，前进 3.位置分值计算
	 */

	public Board() {
		// 设置背景是否透明
		this.setOpaque(false);

		// 鼠标点击事件
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public GameModel getModel() {
		return model;
	}

	public Robots getRobot() throws Exception {
		if (robotA != null) {
			return robotA;
		}
		throw new Exception("RobotA is null");
	}

	public void put(Position p) {
		if (Config.DEBUG) {
			System.out.printf("put point px:%d py:%d role:%d%n", p.px, p.py, p.role);
		}
		model.update(p.px, p.py, p.role);
		repaint();
	}

	public void remove(Position p) {
		if (Config.DEBUG) {
			System.out.printf("remove point px:%d py:%d role:%d%n", p.px, p.py, p.role);
		}

		model.remove(p);
		repaint();
	}

	/**
	 * 棋局的开局设置
	 * 1、设置初始棋子的位置（可以有多中变化）
	 * 2、设置变量的初始值
	 */
	public void init() {
		initScore();
	}


	public void initScore() {
		for (int i=0; i<model.matrix.length;i++) {
			for (int j=0; j<model.matrix.length; j++) {
				if (model.matrix[i][j].role == Position.EMPTY) {
					if (hasNeighbor(i,j,1,1)) {
						int cs = ep.scorePoint(model, i, j, Position.COMPUTER);
						int hs = ep.scorePoint(model, i, j, Position.HUMAN);
						comScore[i][j] = cs;
						humScore[i][j] = hs;
					}
				} else if (model.matrix[i][j].role == Position.COMPUTER) {
					comScore[i][j] = ep.scorePoint(model, i, j, Position.COMPUTER);
					humScore[i][j] = 0;
				} else if (model.matrix[i][j].role == Position.HUMAN) {
					humScore[i][j] = ep.scorePoint(model, i, j, Position.HUMAN);
			        comScore[i][j] = 0;
				}
			}
		}
	}

	private boolean hasNeighbor(int x, int y, int distance, int count) {
		int len = model.matrix.length;
		int startX = x - distance;
		int endX = x+distance;
		int startY = y - distance;
		int endY = y + distance;

		for (int i=startX;i<=endX; i++) {
			if (i<0 ||i>=len) continue;
			for (int j=startY;j<=endY;j++) {
				if (j<0 || j>=len) continue;
				if (i==x && j==y) continue;
				if (model.matrix[i][j].role == Position.EMPTY) {
					count--;
					if (count <= 0) 
						return true;
				}
			}
		}

		return false;
	}


	/**
	 * 展现相关部分
	 * 1、计算棋局的坐标，包括board，开始按钮，经纬线等
	 * 2、画棋局
	 */


	/**
	 * 计算逻辑
	 */
	private void calculate() {
		int w = getWidth();
		int h = getHeight();

		// 计算中央的方形广场
		int size = w < h ? w : h;
		size -= 76; // padding
		int x = (w - size) / 2;
		int y = (h - size) / 2;

		// 计算经线和纬线的位置
		cell = size / (N-1);
		for (int i = 0; i <= N; i++) {
			baseH[i] = x + i * cell;
			baseV[i] = y + i * cell;
		}

		// 广场的精确位置
		square = new Rectangle(x, y, (N-1) * cell, (N-1) * cell);

		// 重启按钮的位置 ( 位于广场下方 )
		restartButton.width = 120;
		restartButton.height = 40;
		restartButton.x = square.x + (square.width - restartButton.width) / 2;
		restartButton.y = square.y + square.height + 20;

		// 人机对战按钮的位置 ( 位于广场下方 )
		humandrobotButton.width = 120;
		humandrobotButton.height = 40;
		humandrobotButton.x = square.x;
		humandrobotButton.y = square.y + square.height + 20;
	}

	private void paintHit(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		// 鼠标移动时，绘制焦点
		if (focus.px >= 0 && focus.py >= 0) {
			int x = square.x + cell * focus.px;
			int y = square.y + cell * focus.py;
			int r = (int) (cell * 0.43);
			//
			Color fillColor = new Color(0x80FCFCFC, true);// 半透明颜色 ARGB
			if (whoIsNow == Position.COMPUTER)
				fillColor = new Color(0x80202020, true); // 半透明颜色 ARGB

			g2d.setPaint(fillColor);
			g2d.fillOval(x - r, y - r, r * 2, r * 2);
		}
	}
	

	protected void paintTip(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		// 先手权提示
		if (true) {
			int cx = square.x + cell * N / 2 + 30; // 中间偏右的位置
			int cy = square.y - cell;
			drawChess(
				g2d, 
				cx, 
				cy, 
				whoIsNow == Position.COMPUTER, 
				0
			);
			g2d.setPaint(new Color(0x202020));
			g2d.setFont(g2d.getFont().deriveFont(18.0f));
			g2d.drawString("当前先手:", cx - 100, cy + 7);
		}
	}

	protected void paintButton(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// 绘制重启按钮 ( 此处并未仔细计算，可以参考Swing高级篇第3章作精细计算)
		if (restartButton != null) {
			Rectangle rect = restartButton;

			// 画圆角矩形
			g2d.setPaint(new Color(0xFFD700));
			g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);

			// 画文字
			g2d.setPaint(new Color(0x303030));
			g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			String text = "再来一局";

			FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
			int fontSize = fm.getHeight(); // 字高
			int textWidth = fm.stringWidth(text);
			int leading = fm.getLeading();
			int descent = fm.getDescent(); // bottom->baseline 的高度

			int x = 0;
			int y = 0;
			x = rect.x + (rect.width - textWidth) / 2; // 水平居中
			y = rect.y + rect.height / 2 + (fontSize - leading) / 2 - descent; // 竖直居中
			g2d.drawString(text, x, y);
		}
	}

	protected void paintHuman(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// 绘制人机对战按钮 ( 此处并未仔细计算，可以参考Swing高级篇第3章作精细计算)
		if (humandrobotButton != null) {
			Rectangle rect = humandrobotButton;

			// 画圆角矩形
			g2d.setPaint(new Color(0xFFD700));
			g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);

			// 画文字
			g2d.setPaint(new Color(0x303030));
			g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			String text = "人机对战";

			FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
			int fontSize = fm.getHeight(); // 字高
			int textWidth = fm.stringWidth(text);
			int leading = fm.getLeading();
			int descent = fm.getDescent(); // bottom->baseline 的高度

			int x = 0;
			int y = 0;
			x = rect.x + (rect.width - textWidth) / 2; // 水平居中
			y = rect.y + rect.height / 2 + (fontSize - leading) / 2 - descent; // 竖直居中
			g2d.drawString(text, x, y);
		}
	}



	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		//
		Graphics2D g2d = (Graphics2D) g;

		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 确保已经calculate()
		calculate();

		// 背景绘制
		g2d.setPaint(bgColor);
		g2d.fill(square);

		// 绘制经纬线
		g2d.setPaint(new Color(0x606060));
		g2d.setStroke(new BasicStroke(1.2f));
		for (int i = 0; i < N; i++) {
			String str = String.valueOf(i);
			int length = str.length();
			g2d.drawChars(str.toCharArray() , 0, length, baseH[0] - 20, baseV[i] + 7);
			g2d.drawLine(baseH[0], baseV[i], baseH[N-1], baseV[i]);
			g2d.drawChars(str.toCharArray() , 0, length, baseH[i] - 5 , baseV[0] - 10);
			g2d.drawLine(baseH[i], baseV[0], baseH[i], baseV[N-1]);
		}

		// 绘制游戏数据：模子
		for (int i = 0; i < N; i++) {
			for (int k = 0; k < N; k++) {
				Position p = model.at(i, k);
				if (p == null)
					continue; // 此位置没有数据

				if (p.role != Position.EMPTY) {
					// 棋子是一个圆形, x,y为圆心, r为半径
					if (p.mark > 0 && p.mark < model.time) {
						p.mark = 0;
					}
					int x = baseH[p.px];
					int y = baseV[p.py];
					drawChess(g2d, x, y, p.role == Position.BLACK, p.mark);
				}
			}
		}

		paintHit(g);

		paintTip(g);

		paintHuman(g);

		paintButton(g);

	}

	// x,y: 棋子的坐标 (px)
	private void drawChess(Graphics2D g2d, int x, int y, boolean black, int mark) {
		int r = (int) (cell * 0.43);

		// 设定黑棋和白棋的颜色
		Color lineColor = null;
		Color fillColor = null;
		if (black) {
			fillColor = new Color(0x202020);
			lineColor = new Color(0x303030);
		} else {
			fillColor = new Color(0xFCFCFC);
			lineColor = new Color(0x808080);
		}
		// 绘制
		g2d.setPaint(fillColor);
		g2d.fillOval(x - r, y - r, r * 2, r * 2);
		g2d.setPaint(lineColor);
		g2d.drawOval(x - r, y - r, r * 2, r * 2);

		// 胜利时的特殊标识
		if (mark > 0) {
			Path2D path = new Path2D.Double();
			// 通过路径来画一个图形，在路径里面填充一个颜色,然后用g2d来实现path
			path.moveTo((double)x - 5, (double)y + 4);
			path.lineTo((double)x + 6, (double)y + 4);
			path.lineTo((double)x, (double)y - 4);
			path.closePath();
			g2d.setPaint(Color.GREEN);
			g2d.fill(path);
		}
	}


	/**
	 * 交互逻辑部分的开始
	 * 1、鼠标点击操作
	 *  1.点击按钮，开始按钮，人机大战按钮
	 *  2.下棋，判断输赢，画棋局，通知电脑下棋
	 * 2、鼠标移动操作
	 *  1.绘制阴影效果
	 */

	/**
	 *  根据鼠标位置，判断所在的点位
	 * @param x
	 * @param y
	 * @return
	 */
	private Position testPosition(int x, int y) {
		// 归纳到最近的整数 (点击在交叉点附近，都会被归纳到交叉点)
		int px = Math.round((float) (x - square.x) / cell);
		int py = Math.round((float) (y - square.y) / cell);

		return model.at(px, py);
	}

	/*
	 * 鼠标点击事件 
	 * 1.设置用户下的棋子
	 * 2.画用户棋子 
	 * 3.通知电脑下棋
	 * 2.点击特定按钮的操作
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			// 是否点击了‘重新开始’按钮
			if (restartButton.contains(e.getPoint())) {
				clickRestartButton(e);
			}

			if (humandrobotButton.contains(e.getPoint())) {
				setRule(Position.COMPUTER);
				model.reset();
				robotA.start();
				this.isActive = true;
				repaint();
				whoIsNow = Position.HUMAN;
				return;
			}

			// 如果游戏结束了就不需要再画界面了
			if (!isActive) {
				return;
			}

			// 人下了一子
			Position p = testPosition(e.getX(), e.getY());
			if (p != null && p.role == Position.EMPTY) {
				model.update(p.px, p.py, Position.HUMAN);
				whoIsNow = 0 - whoIsNow; // 交换先手
				checkWin();

				// if human vs computer, notify the competitor
				if (isActive && rule == Position.COMPUTER) {
					// 鼠标点击事件
					disableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
					focus.px = -1;
					focus.py = -1;
					notifyRobots();
				}
			}
		}
		super.processMouseEvent(e);
	}

	/**
	 * 设置游戏规则初始化
	 * @param rule
	 */
	public void setRule(int rule) {
		this.rule = rule;

		// 根据棋局设置添加robots
		if (rule == Position.COMPUTER) {
			this.robotA = new Robots(this);
		}
	}

	/*
	 *  点击重启按钮操作
	 * 
	 */
	private void clickRestartButton(MouseEvent e) {
		model.reset();
		setRule(Position.COMPUTER);
		whoIsNow = Position.WHITE;
		isActive = true;
		repaint();
		return;
	}

	/*
	 * 通知电脑进行计算下一个棋子位置
	 */
	protected void notifyRobots() {
		Runnable runx = new Runnable() {
			public void run() {
				try {
					robotA.put();
				} catch (Exception e) {
					e.printStackTrace();
				}
				checkWin();
				// 鼠标点击事件
				enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
				whoIsNow = 0 - robotA.role;
				repaint();
			}
		};
		SwingUtilities.invokeLater(runx);
	}

	/*
	 * 判断棋局是否已经结束，通知双方输赢
	 */
	protected void checkWin() {
		// 检查输赢
		int result = model.checkWin();
		if (result != Position.EMPTY) {
			System.out.println("游戏结束!");

			// 提示一个对话框
			if (result != Position.COMPUTER)
				new InfoDialog(this, "You Win").exec();
			else
				new InfoDialog(this, "You Lose").exec();

			isActive = false;
		}
		repaint();
	}

	// 鼠标移动的时候增加阴影效果
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		// 如果游戏结束了就不需要再画界面了
		if (!isActive) {
			return;
		}
		if (e.getID() == MouseEvent.MOUSE_MOVED) {
			Position p = testPosition(e.getX(), e.getY());
			int px = -1;
			int py = -1;
			if (p != null && p.role == Position.EMPTY) {
				px = p.px;
				py = p.py;
			}
			// 当前焦点位置
			if (px != focus.px || py != focus.py) {

				// 只有焦点改变时才重新绘制,避免在同一个点位反复重绘
				focus.px = px;
				focus.py = py;
				repaint();
			}
		}
		super.processMouseEvent(e);
	}

}
