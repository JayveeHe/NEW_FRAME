package fyy.ygame_map.view;

import android.graphics.Canvas;
import android.util.Log;
import fyy.ygame_frame.base.YDrawInformation;
import fyy.ygame_map.data.YAMapData;

class YNormalPlaneMap extends YAMapView
{

	/** 日志标签 */
	private static final String strTag = "YNormalPlaneMap";

	YNormalPlaneMap(YAMapData mapData, int iIndexPicId, int iRowDisplay)
	{// iRowDislay已经经过合法性检查
		super(mapData, iIndexPicId);
		this.iRowSum = iRowDisplay;
	}

	@Override
	protected void adaptGameView(int iWidth, int iHeight)
	{
		iGridSideLength = iHeight / iRowSum;
		iYBlank = 0;
		iYRest = iHeight % iRowSum;
		if (iYRest > iGridSideLength)
		{
			System.out.println("异常：指定显示行数-iRowSum过大");
			Log.e(strTag, "指定显示行数-iRowSum过大");
			throw new RuntimeException("屏幕适配异常");
		}
		// 适配下来，如果地图总列数小于等于游戏视图能够显示的列数 ，那么完全显示出地图
		// （此时游戏视图横向上不能被完全填充）；如果地图列数大于游戏视图所能显示的列数，
		// 那么多加一列显示列，以便地图视图横向填充整个游戏视图
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
		int iX = Math.abs(drawInformation.iX);
		int iFirstTileColumn = iX / iGridSideLength + 1;
		int iInitX = -iX % iGridSideLength;

		int iY = Math.abs(drawInformation.iY);
		int iFirstTileRow = iY / iGridSideLength + 1;
		int iInitY = -iY % iGridSideLength;

		drawPiece(canvas, iFirstTileRow, iFirstTileColumn, iInitX,
				iInitY);
	}
}
