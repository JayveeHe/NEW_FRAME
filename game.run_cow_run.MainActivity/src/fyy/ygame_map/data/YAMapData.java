package fyy.ygame_map.data;

import fyy.ygame_frame.base.YABaseDomainData;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_map.extra.DigitalTile;
import android.content.res.Resources;

public abstract class YAMapData extends YABaseDomainData
{
	


	/** ��ͼ������������1��ʼ */
	public final int iMapColumnSum;
	/** ��ͼ�������� ����1��ʼ */
	public final int iMapRowSum;
	/** ����ͼԪ������ �������±��ʶ�˸�ͼԪ�ڵ�ͼ��������<b>������</b>���˴���������0���� */
	public final DigitalTile[][] tl_arr2;
	/** ����ͼ������ ����1��ʼ */
	public final int iIndexPicColumnSum;
	/** ����ͼ������ ����1��ʼ */
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
	 * ��ȡ����ͼ����
	 * 
	 * @return ����ͼ����
	 */
	protected abstract int getIndexPicRowSum();

	/**
	 * ��ȡ����ͼ����
	 * 
	 * @return ����ͼ����
	 */
	protected abstract int getIndexPicColumnSum();

	/**
	 * ��ȡ����ͼԪ��ά����
	 * 
	 * @return ����ͼԪ��ά����
	 */
	protected abstract DigitalTile[][] getDigitalTiles();

	/** @return ��ͼ������ */
	protected abstract int getMapColumnSum();

	/** @return ��ͼ������ */
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
		str += "��ͼ������:" + iMapColumnSum + "\n";
		str += "��ͼ������:" + iMapRowSum + "\n";
		str += "����ͼ����:" + iIndexPicColumnSum + "\n";
		str += "����ͼ����:" + iIndexPicRowSum + "\n";
		return str;
	}
}
