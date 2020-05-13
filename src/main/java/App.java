



import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;

public class App {
	private static void createGUI() throws IOException
	{
		JFrame frame = new AppFrame();
		frame.setVisible(true);

		// 居中显示
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		int x = ( screenSize.width - frame.getWidth())/2;
		int y = ( screenSize.height - frame.getHeight())/2;
		frame.setLocation(x,  y);
		
	}

	public static void main(String[] args)
	{

		// 此段代码间接地调用了 createGUI()，具体原理在 Swing高级篇 里讲解
		// 初学者先照抄此代码框架即可
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				try {
					createGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}
}
