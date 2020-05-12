package gobang.project;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import gobang.project.chess.Board;

public class AppFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AppFrame() throws IOException
	{
		super("五子棋人机大战");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(600, 1080);

		ImageIcon imageIcon = new ImageIcon("res/gobang.png");
		setIconImage(imageIcon.getImage());


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
