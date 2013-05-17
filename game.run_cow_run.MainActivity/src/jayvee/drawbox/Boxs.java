package jayvee.drawbox;

import android.annotation.SuppressLint;
import java.util.Random;

public class Boxs {
	public int iX;
	public int iY;
	public int iKind;
	public int isCreated;

	@SuppressLint("FloatMath")
	public Boxs() {
		Random rd = new Random();
		// rd.nextInt(11);//生成0到10的随机数
		isCreated = (int) Math.floor(rd.nextInt(11) / (10 * 0.6f));
		iX = -70;
		iY = 0;
		iKind = rd.nextInt(4);
	}

	public void addX(int x) {
		this.iX += x;
	}

	public void addY(int y) {
		this.iY += y;
	}

}
