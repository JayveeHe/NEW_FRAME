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
			Log.w(strTag, "��ͼ����Ϸ��ͼ������ȫƥ�䣬y����ʣ��" + iYBlank
					+ "���ز��ܱ���ͼԪ���");
		// ���������������ͼ������С�ڵ�����Ϸ��ͼ�ܹ���ʾ������ ����ô��ȫ��ʾ����ͼ
		// ����ʱ��Ϸ��ͼ�����ϲ��ܱ���ȫ��䣩��
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
		if (null != btmpPerspective)
			drawPerspectivePic(canvas, drawInformation.iPicIndex);
		int iX = Math.abs(drawInformation.iX);
		int iFirstTileColumn = iX / iGridSideLength + 1;
		int iInitX = -iX % iGridSideLength;
		drawPiece(canvas, 1, iFirstTileColumn, iInitX, iYBlank);
	}

	/**
	 * ����Զ��ͼ
	 * 
	 * @param canvas
	 *                ����
	 * @param iX
	 *                Զ��ͼ�����浽��Ļ�ϵ�x����
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
