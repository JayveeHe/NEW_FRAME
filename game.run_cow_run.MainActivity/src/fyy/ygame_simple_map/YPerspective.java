package fyy.ygame_simple_map;

import fyy.ygame_frame.base.YGameView;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
*<b>远景图层</b>
*
*<p>
*<b>概述</b>：
*一张在游戏场景里循环播放的“远景图”，如山、天空、云彩等。
*
*<p>
*<b>注</b>：
*作为<b>远景图</b>的图片素材，应该循环播放时候没有缝隙。（即图片最左侧能够与图片最
*右侧无缝结合）
*
*@author FeiYiyun
*
*/
public final class YPerspective
{
	private static final String strTag = "YPerspective";

	/**<b>资源标识</b>*/
	private final int iR_drawable;
	/**<b>对应位图对象</b>*/
	private Bitmap bitmap;

	/**<b>比例步长</b>：相对于<b>游戏视图</b>{@link YGameView}宽度。*/
	float fStepLength = 1 / 20;
	/**<b>实际步长</b>*/
	int iStepLength;

	/**<b>远景图层宽度</b>：即<b>游戏视图</b>{@link YGameView}宽度。*/
	private int iWidth;
	/**<b>远景图层实际高度</b>*/
	private int iHeight;
	/**<b>远景图层权重</b>：即<b>远景图层</b>{@link YPerspective}比例高度（相对于
	*<b>游戏视图</b>{@link YGameView}）。*/
	private final float fWeight;

	/**<b>初始实际横坐标</b>*/
	int iInitX;
	/**<b>初始实际纵坐标</b>*/
	private int iInitY;
	/**<b>初始比例横坐标</b>*/
	private final float fInitX;
	/**<b>初始比例纵坐标</b>*/
	private final float fInitY;

	/**
	*下述的“比例变量”范围应为0~1，该比例相对于<b>游戏视图</b>{@link YGameView}。
	*@param iR_drawable 远景图的资源标识（R文件中drawable值）
	*@param fStepLength 远景图移动的比例步长（远景图移动一次的“比例长度”，相对
	*于<b>游戏视图</b>宽度），默认为1/20，范围大于0，小于1
	*@param fWeight 远景图权重（特指纵方向上），范围应该大于0，小于等于1
	*@param fInitX 远景图初始“比例横坐标”（相对于<b>游戏视图</b>宽度，范围
	*应该大于等于0 ， 小于等于1）
	*@param fInitY 远景图初始“比例纵坐标”（相对于<b>游戏视图</b>高度，范围
	*应该大于等于0 ， 小于等于1）
	*/
	public YPerspective(int iR_drawable, float fStepLength, float fWeight, float fInitX,
			float fInitY)
	{
		if (fStepLength >= 1 || fStepLength <= 0 || fWeight > 1 || fWeight <= 0
				|| fInitX > 1 || fInitX < 0 || fInitY > 1 || fInitY < 0)
		{
			Log.e(strTag, "比例步长范围应该大于0，小于1；权重范围应该大于0，小于等于1。初始比例"
					+ "横、纵坐标范围应该大于等于0 ， 小于等于1");
			throw new IllegalArgumentException("比例步长、权重或初始比例横纵坐标超出范围。");
		}
		this.iR_drawable = iR_drawable;
		this.fWeight = fWeight;
		this.fStepLength = fStepLength;
		this.fInitX = fInitX;
		this.fInitY = fInitY;
	}

	/**调用该函数的对象应该负责检查参数合法性。
	*@param fStepLength
	*/
	void setStepLenght(float fStepLength)
	{
		this.fStepLength = fStepLength;
		iStepLength = (int) (fStepLength * iWidth);
	}

	/**生成位图
	*@param res 资源
	*@param iViewWidth 视图宽度
	*@param iViewHeight 视图高度
	*/
	void generateBitmap(Resources res, int iViewWidth, int iViewHeight)
	{
		iInitX = (int) (iViewWidth * fInitX);
		iInitY = (int) (iViewHeight * fInitY);

		iWidth = iViewWidth;
		iHeight = (int) (iViewHeight * fWeight);

		iBottom = iHeight + iInitY;

		iStepLength = (int) (fStepLength * iViewWidth);

		Bitmap bitmapTem = BitmapFactory.decodeResource(res, iR_drawable);
		bitmap = Bitmap.createScaledBitmap(bitmapTem, iWidth, iHeight, true);
		if (!bitmapTem.isRecycled())
			bitmapTem.recycle();
	}

	private int iBottom;

	/**绘制远景图，应该在调用了{@link #generateBitmap(Resources, int, int)}之后调用该方法。
	*@param canvas 画布
	*@param iX 远景图画在屏幕最左侧的横坐标（该横坐标是位于远景图上的）
	*/
	void draw(Canvas canvas, int iX)
	{
		int iiX = iWidth - iX;
		canvas.drawBitmap(bitmap, new Rect(iX, 0, iWidth, iHeight), new Rect(0, iInitY,
				iiX, iBottom), null);
		canvas.drawBitmap(bitmap, new Rect(0, 0, iX, iHeight), new Rect(iiX, iInitY,
				iWidth, iBottom), null);
	}

	/**
	*回收位图
	*/
	void recycle()
	{
		if (null != bitmap && !bitmap.isRecycled())
			bitmap.recycle();
	}
}
