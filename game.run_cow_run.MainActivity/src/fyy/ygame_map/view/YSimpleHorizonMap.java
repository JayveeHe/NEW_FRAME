package fyy.ygame_map.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import fyy.ygame_frame.base.YDrawInformation;
import fyy.ygame_map.data.YAMapData;

class YSimpleHorizonMap extends YAMapView
{
	private static final String strTag = "YSimpleHorizonMap";

	YSimpleHorizonMap(YAMapData mapData, int iIndexPicId)
	{
		super(mapData, iIndexPicId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void adaptGameView(int iWidth, int iHeight)
	{
		iRowSum = domainData.iMapRowSum;
		iGridSideLength = iHeight / iRowSum;
		
		iYRest = 0;
		iYBlank = iHeight % iRowSum;
		if (0 != iYBlank)
			Log.w(strTag, "地图与游戏视图不能完全匹配，y方向剩余" + iYBlank
					+ "像素不能被地图元填充");
		// 适配下来，如果地图总列数小于等于游戏视图能够显示的列数 ，那么完全显示出地图
		// （此时游戏视图横向上不能被完全填充）；
		if ((iColumnSum = iWidth / iGridSideLength) > domainData.iMapColumnSum)
		{// 适配得到游戏视图能够显示的列数比实际地图列数还多（地图设计得太短、屏幕太长）
			iColumnSum = domainData.iMapColumnSum;// 重置视图显示列数
			iXRest = 0;
			iXBlank = iWidth - iGridSideLength * iColumnSum;
			Log.w(strTag, "地图不能填充整个游戏视图，x方向剩余" + iXBlank
					+ "像素不能被地图元填充");
		} else
		{// 地图能够填充整个游戏视图
			iXBlank = 0;
			iXRest = iWidth % iGridSideLength;
		}
	}

	@Override
	protected void onDraw(Canvas canvas, YDrawInformation drawInformation)
	{
		if (null != btmpPerspective)
			drawPerspectivePic(canvas, drawInformation.iPicIndex);
		int iX = Math.abs(drawInformation.iX);
		int iFirstTileColumn = iX / iGridSideLength + 1;
		int iInitX = -iX % iGridSideLength;
		drawPiece(canvas, 1, iFirstTileColumn, iInitX, iYBlank);
	}

	/**
	 * 绘制远景图
	 * 
	 * @param canvas
	 *                画布
	 * @param iX
	 *                远景图最左侧绘到屏幕上的x坐标
	 */
	private void drawPerspectivePic(Canvas canvas, int iX)
	{
		int iiX = iWidth - iX;
		canvas.drawBitmap(btmpPerspective, new Rect(iX, 0, iWidth,
				iHeight), new Rect(0, 0, iiX, iHeight), null);
		canvas.drawBitmap(btmpPerspective, new Rect(0, 0, iX, iHeight),
				new Rect(iiX, 0, iWidth, iHeight), null);
	}

}
