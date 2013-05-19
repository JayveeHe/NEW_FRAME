package fyy.ygame_map.logic;

import fyy.ygame_map.data.YAMapData;

final class YMapLogicNative
{
	static
	{
		System.loadLibrary("YMapLogicNative");
	}

	/**
	 * ���õ�ͼ���ݣ��ڱ�������һ����ͼ���ݵı��ض��󣬱����Ժ����<br>
	 * ע����ǣ��ñ��ض���ʹ����֮����Ҫ���ͷ�
	 * 
	 * @param mapData
	 *                ��ͼ����
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
	 * ��ʼ�����ڱ��ط���һ����ά���飬���ڱ�ʶ��ͼ
	 * 
	 * @param iRowSum
	 *                ��ͼ��������1������
	 * @param iColSum
	 *                ��ͼ��������1������
	 * @param iTileSideLen
	 *                ͼԪ�߳�
	 */
	private native void init(int iRowSum, int iColSum, int iTileSideLen,
			int iMapWidth, int iMapHeight);

	/**
	 * �ڱ��ض����ʼ����������
	 * 
	 * @param iRow
	 *                ��ͼ��������
	 * @param iCol
	 *                ��ͼ��������
	 * @param iLandscape
	 *                ��ͼ�����
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
