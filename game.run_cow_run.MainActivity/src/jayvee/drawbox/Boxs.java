package jayvee.drawbox;

import android.annotation.SuppressLint;
import java.util.Random;

/**
 * 箱子类说明：
 * @param iX
 * 箱子的绘图X坐标
 * @param iY
 * 箱子绘图Y坐标
 * @param iKind
 * 箱子的种类编号
 * @param isCreated
 * 该箱子是否需要绘制出来，为零时不绘制，非零时绘制
 * @author Jayvee
 * 
 */
public class Boxs {
	public int iX;
	public int iY;
	public int iKind;
	public int isCreated;
	
/**
 * 箱子类的构造函数
 * @param fRate
 * 设置该箱子不绘制出来的概率（0到1之间的浮点数）
 * @author Jayvee
 */
//	@SuppressLint("FloatMath")
	public Boxs(float fRate) {
		Random rd = new Random();
		// rd.nextInt(11);//生成0到10的随机数
		isCreated = (int) Math.floor(rd.nextInt(11) / (10 * fRate));
		iX = -70;
		iY = 0;
		iKind = rd.nextInt(4);
	}
/**
 * 该箱子的X坐标增加函数
 * @param x
 * int型增加绘图原点X坐标值
 * @author Jayvee
 */
	public void addX(int x) {
		this.iX += x;
	}
	/**
	 * 该箱子的Y坐标增加函数
	 * @param y
	 * int型增加绘图原点Y坐标值
	 * @author Jayvee
	 */
	public void addY(int y) {
		this.iY += y;
	}

}
