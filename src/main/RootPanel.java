package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class RootPanel extends JPanel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	Image bgImage;
	
	public RootPanel()
	{
		try {
			URL url = getClass().getResource("res/background.jpg");
			bgImage = ImageIO.read(url);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
				
		Graphics2D g2d = (Graphics2D)g;		
		int w = getWidth();
		int h = getHeight();
		
		// 渐变填充，参考 Swing高级篇，第2.4讲
		if(true)
		{
			Rectangle r = new Rectangle(0,0,w,h);

			Point2D start = new Point2D.Float(r.x, 0); // 起点
			Point2D end = new Point2D.Float(r.x, r.y + (float)r.height); // 终点(渐变的方向)
			float[] dist = { 0.0f, 1.0f };// 插入关键点
			Color[] colors = { new Color(0xFFFFFF),  new Color(0XF0F8FF)}; // 关键点的颜色值
			Paint paint = new LinearGradientPaint(start, end, dist, colors);

			g2d.setPaint(paint); 
			g2d.fill(r);
		}
		
		// 也可以绘制一张背景图片
		if(bgImage != null)
		{
			g2d.drawImage(bgImage, 0, 0, w, h, null);
		}
	}
}
