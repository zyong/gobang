package chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import af.swing.AfPanel;
import af.swing.layout.AfColumnLayout;
import af.swing.layout.AfRowLayout;

/* 消息提示对话框
 * 
 */
public class InfoDialog extends JDialog
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public JLabel label = new JLabel();
	public JButton okButton = new JButton("确定");
	
	public InfoDialog(JComponent owner, String message)
	{
		// 传递window对象，通过SwingUtilities的方法通过owner来获取window
		super(SwingUtilities.windowForComponent(owner), "");
		// 设置modal是不可以操作其他窗口，当前对话框窗口置顶
		this.setModal(true);

		// 不对窗口边框和标题栏装饰
		this.setUndecorated(true);

		// 对话框尺寸
		this.setSize(new Dimension(200,90));
		
		// 设置一个容器
		AfPanel root = new AfPanel();
		this.setContentPane(root);
		root.setLayout(new AfColumnLayout(4));
		root.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		root.padding(4);
		
		// 中间面板
		root.add(label, "30px");
		label.setText(message);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		//
		AfPanel bottom = new AfPanel();
		bottom.padding(4);
		bottom.setLayout(new AfRowLayout(4));
		// 解决布局占位问题，就是前面补充空白
		bottom.add(new JLabel(),"1w");
		bottom.add(okButton);
		bottom.add(new JLabel(),"1w");
		
		root.add(bottom, "40px");
		
		okButton.setFocusPainted(false);
		okButton.setOpaque(true);
		okButton.setBackground(new Color(0xFCFCFC));
		okButton.addActionListener( 
			e->setVisible(false) // mainDialog.this.setVisible(false)
		);
	}
	
	// 返回值为 true : 表示用户点了"确定"
	// 返回值为 false : 表示用户叉掉了窗口，或者点了”取消"
	public void exec()
	{
		// 相对owner居中显示
		Rectangle frmRect = this.getOwner().getBounds();
		int width = this.getWidth();
		int height = this.getHeight();
		int x = frmRect.x + (frmRect.width - width)/2;
		int y = frmRect.y + (frmRect.height - height)/2;
		this.setBounds(x,y, width, height);
		
		// 显示窗口 ( 阻塞 ，直接对话框窗口被关闭)
		this.setVisible(true);		
	}
	
	
}
