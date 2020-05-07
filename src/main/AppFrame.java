package main;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import main.chess.Board;


public class AppFrame extends JFrame
{		
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AppFrame()
	{
		super("Swing实战篇 - 五子棋");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(600, 1080);
		setResizable(false);
		
		// root
		JPanel root = new RootPanel();
		root.setLayout(new BorderLayout());		
		this.setContentPane(root);

		// 棋盘
		Board board = new Board();
		root.add(board, BorderLayout.CENTER);

	}
	
	
}
