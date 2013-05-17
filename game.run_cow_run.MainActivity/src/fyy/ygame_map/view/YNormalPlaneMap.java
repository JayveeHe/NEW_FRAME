package fyy.ygame_map.view;

import android.graphics.Canvas;
import android.util.Log;
import fyy.ygame_frame.base.YDrawInformation;
import fyy.ygame_map.data.YAMapData;

class YNormalPlaneMap extends YAMapView
{

	/** ��־��ǩ */
	private static final String strTag = "YNormalPlaneMap";

	YNormalPlaneMap(YAMapData mapData, int iIndexPicId, int iRowDisplay)
	{// iRowDislay�Ѿ������Ϸ��Լ��
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
			System.out.println("�쳣��ָ����ʾ����-iRowSum����");
			Log.e(strTag, "ָ����ʾ����-iRowSum����");
			throw new RuntimeException("��Ļ�����쳣");
		}
		// ���������������ͼ������С�ڵ�����Ϸ��ͼ�ܹ���ʾ������ ����ô��ȫ��ʾ����ͼ
		// ����ʱ��Ϸ��ͼ�����ϲ��ܱ���ȫ��䣩�������ͼ����������Ϸ��ͼ������ʾ��������
		// ��ô���һ����ʾ�У��Ա��ͼ��ͼ�������������Ϸ��ͼ
		if ((iColumnSum = iWidth / iGridSideLength) > domainData.iMapColumnSum)
		{// ����õ���Ϸ��ͼ�ܹ���ʾ��������ʵ�ʵ�ͼ�������ࣨ��ͼ��Ƶ�̫�̡���Ļ̫����
			iColumnSum = domainData.iMapColumnSum;// ������ͼ��ʾ����
			iXRest = 0;
			iXBlank = iWidth - iGridSideLength * iColumnSum;
			Log.w(strTag, "��ͼ�������������Ϸ��ͼ��x����ʣ��" + iXBlank
					+ "���ز��ܱ���ͼԪ���");
		} else
		{// ��ͼ�ܹ����������Ϸ��ͼ
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
