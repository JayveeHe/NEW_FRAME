package fyy.ygame_map.data;

import fyy.ygame_frame.base.YABaseDomainData;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_map.extra.DigitalTile;
import android.content.res.Resources;

public abstract class YAMapData extends YABaseDomainData
{
	


	/** 地图的总列数，自1开始 */
	public final int iMapColumnSum;
	/** 地图的总行数 ，自1开始 */
	public final int iMapRowSum;
	/** 数字图元的数组 ，行列下标标识了该图元在地图中所处的<b>行列数</b>，此处的行列以0计数 */
	public final DigitalTile[][] tl_arr2;
	/** 索引图的列数 ，自1开始 */
	public final int iIndexPicColumnSum;
	/** 索引图的行数 ，自1开始 */
	public final int iIndexPicRowSum;
	public int iViewGridSideLength;
	public int iMapViewColumn;
	public int iMapViewWidth;
	public int iMapViewHeight;
	public int iMapViewRow;

	protected YAMapData(int iID, Resources resources, String strAssetsFileName) {
		super(iID, resources, strAssetsFileName);
		// TODO Auto-generated constructor stub
		startParser(resources, strAssetsFileName);
		iMapColumnSum = getMapColumnSum();
		iMapRowSum = getMapRowSum();
		tl_arr2 = getDigitalTiles();
		iIndexPicColumnSum = getIndexPicColumnSum();
		iIndexPicRowSum = getIndexPicRowSum();
		endParser();
		System.out.println(this);
	}


	protected abstract void startParser(Resources res,
			String strAssetsFileName);

	protected abstract void endParser();

	/**
	 * 获取索引图行数
	 * 
	 * @return 索引图行数
	 */
	protected abstract int getIndexPicRowSum();

	/**
	 * 获取索引图列数
	 * 
	 * @return 索引图列数
	 */
	protected abstract int getIndexPicColumnSum();

	/**
	 * 获取数字图元二维数组
	 * 
	 * @return 数字图元二维数组
	 */
	protected abstract DigitalTile[][] getDigitalTiles();

	/** @return 地图总列数 */
	protected abstract int getMapColumnSum();

	/** @return 地图总行数 */
	protected abstract int getMapRowSum();

	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg)
	{
		switch (iMsgKey)
		{
		case YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED:
			int[] iTemp = (int[]) objectDetailMsg;
			iMapViewWidth = iTemp[0];
			iMapViewHeight = iTemp[1];
			iViewGridSideLength = iTemp[2];
			iMapViewRow = iTemp[3];
			iMapViewColumn = iTemp[4];
			break;

		default:
			break;
		}
	}

	@Override
	public String toString()
	{
		String str = "____________________\n" + "YAMapData:\n";
		str += "地图总列数:" + iMapColumnSum + "\n";
		str += "地图总行数:" + iMapRowSum + "\n";
		str += "索引图列数:" + iIndexPicColumnSum + "\n";
		str += "索引图行数:" + iIndexPicRowSum + "\n";
		return str;
	}
}
