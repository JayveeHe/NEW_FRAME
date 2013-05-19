package fyy.ygame_map.logic;

import fyy.ygame_map.data.YAMapData;

final class YMapLogicNative
{
	static
	{
		System.loadLibrary("YMapLogicNative");
	}

	/**
	 * 利用地图数据，在本地生成一个地图数据的本地对象，便于以后查阅<br>
	 * 注意的是，该本地对象使用完之后需要被释放
	 * 
	 * @param mapData
	 *                地图数据
	 */
	YMapLogicNative(YAMapData mapData, int iTileSideLen, int iMapWidth,
			int iMapHeight)
	{
		init(mapData.iMapRowSum, mapData.iMapColumnSum, iTileSideLen,
				iMapWidth, iMapHeight);
		for (int i = 0; i < mapData.iMapRowSum; i++)
			for (int j = 0; j < mapData.iMapColumnSum; j++)
				initLandscape(i,
						j,
						mapData.tl_arr2[i][j].iLandScape);
	}

	/**
	 * 初始化，在本地分配一个二维数组，用于标识地图
	 * 
	 * @param iRowSum
	 *                地图行数（从1计数）
	 * @param iColSum
	 *                地图列数（从1计数）
	 * @param iTileSideLen
	 *                图元边长
	 */
	private native void init(int iRowSum, int iColSum, int iTileSideLen,
			int iMapWidth, int iMapHeight);

	/**
	 * 在本地对象初始化地形数据
	 * 
	 * @param iRow
	 *                地图格所在行
	 * @param iCol
	 *                地图格所在列
	 * @param iLandscape
	 *                地图格地形
	 */
	private native void initLandscape(int iRow, int iCol, int iLandscape);

	native void free();

	native int howFarIsBarrierInMyRight(int iX, int iY, int iTestDistance,
			int iLeftObturate, int iRightObturate);

	native int howFarIsBarrierInMyBottom(int iX, int iY, int iTestDistance,
			int iTopObturate, int iBottomObturate);

	native int howFarIsBarrierInMyLeft(int iX, int iY, int iTestDistance,
			int iLeftObturate, int iRightObturate);

	native int howFarIsBarrierInMyTop(int iX, int iY, int iTestDistance,
			int iTopObturate, int iBottomObturate);
}
