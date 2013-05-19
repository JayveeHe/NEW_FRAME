package fyy.ygame_frame.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
*<b>图像工具</b>
*
*<p>
*<b>概述</b>：
*处理图像方便的工具。
*
*@author FeiYiyun
*
*/
public final class YImageUtil
{
	private YImageUtil()
	{
	};

	/**根据传入的id数组返回对应的位图数组。
	*@param res 资源
	*@param iImgIds id数组
	*@return 位图数组
	*/
	public static Bitmap[] getBitmapArray(Resources res, int[] iImgIds)
	{
		Bitmap[] btmp_arr = new Bitmap[iImgIds.length];
		for (int i = 0; i < iImgIds.length; i++)
			btmp_arr[i] = BitmapFactory.decodeResource(res,
					iImgIds[i]);

		return btmp_arr;
	}

	/**拉伸或收缩正方形的位图数组。<br>
	 * <b>注</b>：原位图数组会被回收。
	*@param bitmaps 要被伸缩的位图数组
	*@param iSideLength 将要被拉伸成的边长
	*@return 被拉伸后的位图数组
	*/
	public static Bitmap[] stretchImageArray(Bitmap bitmaps[],
			int iSideLength)
	{
		Bitmap[] btmp_arrStretched = new Bitmap[bitmaps.length];

		for (int i = 0; i < bitmaps.length; i++)
		{
			btmp_arrStretched[i] = Bitmap.createScaledBitmap(
					bitmaps[i], iSideLength, iSideLength,
					true);
			if (null != bitmaps[i] && !bitmaps[i].isRecycled())
				bitmaps[i].recycle();
		}
		return btmp_arrStretched;
	}

	/**按照指定行列数拆分给定位图对象。<br>
	 * <b>注</b>：原位图对象会被回收。
	*@param iRowSum 指定行数
	*@param iColumnSum  指定列数
	*@param btmpSrc  指定位图对象
	*@return 拆分后的位图对象数组
	*/
	public static Bitmap[] splitImage(int iRowSum, int iColumnSum,
			Bitmap btmpSrc)
	{
		int iSideLength = btmpSrc.getHeight() / iRowSum;

		Bitmap[] btmp_arr = new Bitmap[iColumnSum * iRowSum];
		for (int i = 0; i < iRowSum; i++)
			for (int j = 0; j < iColumnSum; j++)
				btmp_arr[i * iColumnSum + j] = Bitmap
						.createBitmap(btmpSrc,
								j * iSideLength,
								i * iSideLength,
								iSideLength,
								iSideLength);

		if (!btmpSrc.isRecycled())
			btmpSrc.recycle();

		return btmp_arr;
	}

	/**回收位图对象数组的位图。
	*@param bitmaps 将要被回收位图的位图对象，可多个
	*/
	public static void recycleBitmapArray(Bitmap[]... bitmaps)
	{
		for (int i = 0; i < bitmaps.length; i++)
			for (int j = 0; j < bitmaps[i].length; j++)
				if (null != bitmaps[i][j]
						&& !bitmaps[i][j]
								.isRecycled())
					bitmaps[i][j].recycle();
	}
}
