package fyy.ygame_map.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import fyy.ygame_frame.base.YABaseDomainView;
import fyy.ygame_frame.base.YDrawInformation;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_frame.util.YImageUtil;
import fyy.ygame_map.data.YAMapData;
import fyy.ygame_map.extra.DigitalTile;

/**
 * @author fei yiyun
 * 
 */
public abstract class YAMapView extends YABaseDomainView<YAMapData> {
	/**
	 * ��ͼ��Ϣ��int�����±�Լ��<br>
	 * <p>
	 * ��ͼ�Ļ�ͼ��Ϣ������
	 * <li>ǰ��ͼ������ʼ�����ꣻ</li>����{@link YDrawInformation#iX}��
	 * {@link YDrawInformation#iY}
	 * <li>ͼԪ������Ϣint���飻</li>����{@link YDrawInformation#objExtra}ָ���int���� ����ʱ
	 * {@link YDrawInformation#objExtra}
	 * ָ��һ����������Ԫ�ص��ڴ�������һ��Ϊint�����飬�ڶ���Ϊboolean�����飩
	 * <li>��Ե��ʶboolean���飻</li>����{@link YDrawInformation#objExtra}ָ��ĵڶ�������<br>
	 * Լ����int�����±�Ϊ{@link #iSubscript_ColStartDraw}��Ԫ�أ�<b>��ǰ���Ƶ���ʼ��</b><br>
	 * int�����±�Ϊ{@link #iSubscript_RowStartDraw}��Ԫ�أ�<b>��ǰ���Ƶ���ʼ��</b><br>
	 * int�����±�Ϊ{@link #iSubscript_XDraw}��Ԫ�أ�<b>��ʼͼԪ������ͼ�ϵĺ�����</b><br>
	 * int�����±�Ϊ{@link #iSubscript_YDraw}��Ԫ�أ�<b>��ʼͼԪ������ͼ�ϵ�������</b><br>
	 * int�����±�Ϊ{@link #iSubscript_FlagEdge}��Ԫ�أ�<b>�Ƿ����������С���</b><br>
	 * </p>
	 */
	static final int iSubscript_ColStartDraw = 0;

	/**
	 * ��ͼ��Ϣ��int�����±�Լ���������±�Ϊ��ֵ��Ԫ�أ�<b>��ʼͼԪ������ͼ�ϵĺ�����</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_XDraw = 1;

	/**
	 * ��ͼ��Ϣ��int�����±�Լ���������±�Ϊ��ֵ��Ԫ�أ�<b>��ǰ���Ƶ���ʼ��</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_RowStartDraw = 2;

	/**
	 * ��ͼ��Ϣ��int�����±�Լ���������±�Ϊ��ֵ��Ԫ�أ�<b>��ʼͼԪ������ͼ�ϵ�������</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_YDraw = 3;

	// /**
	// * ��ͼ��Ϣ��int�����±�Լ���������±�Ϊ��ֵ��Ԫ�أ�<b>������ͼ�Ƿ񵽴��Ե</b><br>
	// *
	// * ��Ӧ��Ԫ��Ϊ<b>��Ե��ʶ</b>��Լ�������λ��ʶ����
	// �Ӹߵ�������Ϊ���������ң�0Ϊ��ͼû�������ñ�Ե��1Ϊ��ͼ�����ñ�Ե<br>
	// * �磺0b0000��ʾ��ͼ�ĸ�����û�е���Ե��0b1010��ʾ��ͼ���������Ե����
	// *
	// * @see YAMapView#iSubscript_ColStartDraw
	// */
	// static final int iSubscript_FlagEdge = 4;
	/**
	 * ��ͼ��Ϣ��boolean�����±�Լ����boolean�����±�Ϊ��ֵ��Ԫ�أ�<b>��ͼ�Ƿ�ﵽ�ϱ�Ե</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Top = 0;

	/**
	 * ��ͼ��Ϣ��boolean�����±�Լ����boolean�����±�Ϊ��ֵ��Ԫ�أ�<b>��ͼ�Ƿ�ﵽ�±�Ե</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Bottom = 1;

	/**
	 * ��ͼ��Ϣ��boolean�����±�Լ����boolean�����±�Ϊ��ֵ��Ԫ�أ�<b>��ͼ�Ƿ�ﵽ���Ե</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Left = 3;

	/**
	 * ��ͼ��Ϣ��boolean�����±�Լ����boolean�����±�Ϊ��ֵ��Ԫ�أ�<b>��ͼ�Ƿ�ﵽ�ұ�Ե</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Right = 4;

	/** ��־��ǩ */
	private static final String strTag = "YAMapView";
	/** ��Ļ������ͼԪ���飬���ڻ��� */
	private Bitmap[] btmp_arrStretched;

	/** ��ͼ��� */
	protected int iWidth;
	/** ��ͼ�߶� */
	protected int iHeight;

	/** ��ͼ��ͼ��ͼ����������1���� */
	protected int iColumnSum;
	/** ��ͼ��ͼ��ͼ���� ����1���� */
	protected int iRowSum;
	/** һ���滭ͼԪ�ı߳� */
	protected int iGridSideLength;
	/**
	 * �����������Ļ����֮��X������ʣ��ĳ��� ���ó��Ȼ����ڵ�ͼԪȥ��䣬����δ����һ�� ��<br>
	 * <b>��Ҫ�ģ��ⲿ���ǵ�ͼ��ͼ�Ĳ���</b>
	 */
	protected int iXRest = -530;
	/**
	 * �����������Ļ����֮��Y������ʣ��ĳ��ȣ��ó��Ȼ��ܴ��ڵ�ͼԪ��䣬����δ����һ�С� <br>
	 * <b>��Ҫ�ģ��ⲿ���ǵ�ͼ��ͼ�Ĳ���</b>
	 */
	protected int iYRest = -530;
	/**
	 * �����������Ļ����֮��X�����϶���Ŀհײ��֣����޵�ͼԪ��� �� <br>
	 * <b>��Ҫ�ģ��ⲿ�ֲ��ǵ�ͼ��ͼ�Ĳ��֣�����Ϸ��ͼ�Ĳ���</b>
	 */
	protected int iXBlank = -530;
	/**
	 * ��Ļ����֮��Y�����϶���Ŀհײ��֣����޵�ͼԪ��䡣<br>
	 * <b>��Ҫ�ģ��ⲿ�ֲ��ǵ�ͼ��ͼ�Ĳ��֣�����Ϸ��ͼ�Ĳ���</b>
	 */
	protected int iYBlank = -530;

	/** ��ͼͼԪ����ͼƬ��λͼ�������飬�ڼ��еڼ��еڼ��㸽��ʲô����λͼ */
	protected Bitmap[][][] btmp_arr3TilePic;
	/** ��ͼͼԪ����ͼƬ������飬�ڼ��еڼ��еڼ��㸽�����Ϊ����ͼƬ */
	private int[][][] i_arr3TilePic;

	/** ����ͼ��Դid��������R�ļ��е��Ǹ��� */
	private int iIndexPicId;

	/** Զ��ͼ��Դid */
	protected int iPerspectivePicId;
	/** Զ��ͼ */
	protected Bitmap btmpPerspective;

	/**
	 * ������ʾ���е�ͼ�еĵ�ͼ��ͼ�����������ƶ��������������ƶ��ĺ���ͼ��ͼ��
	 * 
	 * @param mapData
	 *            ��ͼ����
	 * @param iIndexPicId
	 *            ��ͼ����ͼid
	 * @param iPerspectivePicId
	 *            ��ͼ����ͼid
	 * @return ��ͼ��ͼ
	 */
	public static YAMapView createSimpleHorizonMap(YAMapData mapData,
			int iIndexPicId) {
		return new YSimpleHorizonMap(mapData, iIndexPicId);
	}

	/**
	 * ����ָ����ʾ�����ĵ�ͼ��ͼ
	 * 
	 * @param mapData
	 *            ��ͼ����
	 * @param iIndexPicId
	 *            ��ͼ����ͼid
	 * @param iRowDisplay
	 *            ָ����ʾ������
	 * @return ��ͼ��ͼ
	 */
	public static YAMapView createNormalPlaneMap(YAMapData mapData,
			int iIndexPicId, int iRowDisplay) {// iRowDisplay�Ϸ���Χ��1~mapData.iMapRowSum�������䣩
		if (iRowDisplay < 1 || iRowDisplay > mapData.iMapRowSum) {// �Ƿ�����
			System.out.println("�쳣����ͨƽ���ͼָ����ʾ����-iRowDisplay��ΧӦ��Ϊ1~"
					+ mapData.iMapRowSum);
			Log.e(strTag, "��ͨƽ���ͼָ����ʾ����-iRowDisplay��ΧӦ��Ϊ1~"
					+ mapData.iMapRowSum);
			throw new IllegalArgumentException("��ͨƽ���ͼָ����ʾ�����쳣");
		}
		// ��������֮��iRowDisplay��Χһ����1~iRowDisplay
		if (iRowDisplay == mapData.iMapRowSum)
			return new YSimpleHorizonMap(mapData, iIndexPicId);
		else
			return new YNormalPlaneMap(mapData, iIndexPicId, iRowDisplay);
	}

	YAMapView(YAMapData mapData, int iIndexPicId) {
		super(mapData);
		this.iIndexPicId = iIndexPicId;
	}

	@Override
	protected void onLoadBitmaps(Resources resources, int iWidth, int iHeight,
			int[] iMapLayoutParams) {
		// TODO Auto-generated method stub
		// ����GameView
		adaptGameView(iWidth, iHeight);

		// ȷ����ͼ��ͼ�߿�
		if (-530 == iXBlank || -530 == iYBlank) {
			System.out.println("�쳣��iXBlank��iYBlankû�л�ȡ������ֵ");
			Log.e(strTag, "iXBlank��iYBlankû�л�ȡ������ֵ");
			throw new RuntimeException("���������ȡ�쳣");
		}
		this.iWidth = iWidth - iXBlank;
		this.iHeight = iHeight - iYBlank;
		System.out.println(this);

		// ������ͼ��֡���װΪ�ɻ滭��ͼԪ
		// �������ͼ����ȡͼԪ����
		Bitmap[] btmp_arr = YImageUtil.splitImage(domainData.iIndexPicRowSum,
				domainData.iIndexPicColumnSum,
				BitmapFactory.decodeResource(resources, iIndexPicId));

		// ����ͼԪ����ʹ����Ӧ��Ļ
		btmp_arrStretched = YImageUtil.stretchImageArray(btmp_arr,
				iGridSideLength);

		// ��ȡͼԪ����ͼƬ��λͼ��������
		btmp_arr3TilePic = fetchBitmapArray3(domainData.tl_arr2,
				btmp_arrStretched);

		// ����Զ��ͼ
		createPerspective(resources, iWidth, iHeight);

		// ����ͼ���������Ϣ���ͣ���ͼ���ݽ��պ���ɶ����ݵ����
		// ע��Լ����int����˳��Ϊ����ͼ��ͼ��ȡ���ͼ��ͼ�߶ȡ�һ��߳�����������Ϊ��λ����
		// ��ͼ��ͼ�������� ��ͼ��ͼ������
		// broadcast.send(YBroadcast.iMsgViewLayouted, new int[]
		// { this.iWidth, this.iHeight, iGridSideLength, iRowSum,
		// iColumnSum }, this);
		int[] iParams = new int[] { this.iWidth, this.iHeight, iGridSideLength,
				iRowSum, iColumnSum };
		broadcastDomain.send(YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED, iParams, this);

		broadcastView.send(YGameEnvironment.BroadcastMsgKey.MSG_MAP_VIEW_LAYOUTED, iParams, this);
	}

	/**
	 * ������Ϸ��ͼ{@link YAGameView}�߿��������ͼ��ͼ�Ĳ���{@link #iRowSum}��
	 * {@link #iGridSideLength}�� {@link #iColumnSum}��{@link #iYRest}
	 * 
	 * @param iWidth
	 *            ��Ϸ��ͼ��
	 * @param iHeight
	 *            ��Ϸ��ͼ��
	 */
	abstract protected void adaptGameView(int iWidth, int iHeight);

	/**
	 * ����Զ��ͼ
	 * 
	 * @param resources
	 *            ��Դ����
	 */
	private void createPerspective(Resources resources, int iWidth, int iHeigt) {
		if (0 != iPerspectivePicId) {
			Bitmap bitmapTemp = BitmapFactory.decodeResource(resources,
					iPerspectivePicId);
			btmpPerspective = Bitmap.createScaledBitmap(bitmapTemp, iWidth,
					iHeight, true);
			if (!bitmapTemp.isRecycled())
				bitmapTemp.recycle();
		} else
			Log.w(strTag, "û�����ñ���ͼ");
	}

	/**
	 * ������ͼԪ������λͼ��������Ϊλͼ����Ԫ���飬���ڵ�ͼ����
	 * 
	 * @param dgtl_tl_arr
	 *            ����������Ϣ�ĵ�ͼͼԪ����
	 * @param btmp_arr
	 *            ����ͼ��ֺ�õ���λͼ��������
	 * @return ��Ч�ġ������ͼ���Ƶ���ά��ͼ����
	 */
	private Bitmap[][][] fetchBitmapArray3(DigitalTile[][] dgtl_tl_arr,
			Bitmap[] btmp_arr) {
		Bitmap[][][] btmp_arr3 = new Bitmap[dgtl_tl_arr.length][dgtl_tl_arr[0].length][2];
		i_arr3TilePic = new int[dgtl_tl_arr.length][dgtl_tl_arr[0].length][2];
		for (int i = 0; i < dgtl_tl_arr.length; i++) {
			for (int j = 0; j < dgtl_tl_arr[0].length; j++) {
				int[] i_arrBitmap = dgtl_tl_arr[i][j].iBitmapNum;
				i_arr3TilePic[i][j][0] = i_arrBitmap[0];
				i_arr3TilePic[i][j][1] = i_arrBitmap[1];
				if (0 != i_arrBitmap[0]) {// ���ǿգ��ڶ�Ӧ����ά��ͼ����Ԫ��������Ӧͼ��
					btmp_arr3[i][j][0] = btmp_arr[i_arrBitmap[0] - 1];
				}
				if (0 != i_arrBitmap[1]) {// ͬ��
					btmp_arr3[i][j][1] = btmp_arr[i_arrBitmap[1] - 1];
				}
			}
		}
		return btmp_arr3;
	}

	/**
	 * ��ָ����ͼԪ������һ�飬��ĳ��Ⱥ͸߶�����Ӧ��ͼ��ͼ
	 * 
	 * @param canvas
	 *            ����
	 * @param iFirstTileRow
	 *            ָ���е���ͼԪ�ڵ�ͼ�����������1��ʼ����
	 * @param iFirstTileColumn
	 *            ָ���е���ͼԪ�ڵ�ͼ�����������1��ʼ����
	 * @param iX
	 *            xƫ��
	 * @param iY
	 *            yƫ��
	 */
	void drawPiece(Canvas canvas, int iFirstTileRow, int iFirstTileColumn,
			int iInitX, int iInitY) {
		int iRow = iFirstTileRow;
		int iiY = iHeight + iInitY;

		for (int iY = iInitY; iY < iiY;) {
			drawOneRow(canvas, iRow, iFirstTileColumn, iInitX, iY);
			iRow++;
			iY += iGridSideLength;
		}
	}

	/**
	 * ��ָ����ͼԪ������һ�У��еĳ�������Ӧ��ͼ��ͼ���
	 * 
	 * @param canvas
	 *            ����
	 * @param iFirstTileRow
	 *            ָ���е���ͼԪ�ڵ�ͼ�����������1��ʼ����
	 * @param iFirstTileColumn
	 *            ָ���е���ͼԪ�ڵ�ͼ�����������1��ʼ����
	 * @param iX
	 *            xƫ��
	 * @param iY
	 *            yƫ��
	 */
	private void drawOneRow(Canvas canvas, int iFirstTileRow,
			int iFirstTileColumn, int iInitX, int iInitY) {
		// 1������0����ת��
		int iRow = iFirstTileRow - 1;
		int iColumn = iFirstTileColumn - 1;
		// �ӵ�ͼ��ѡ��Ҫ��ĸ���
		Bitmap[][] bitmaps = btmp_arr3TilePic[iRow];

		for (int iX = iInitX; iX < iWidth;) {
			if (null != bitmaps[iColumn][0])
				canvas.drawBitmap(bitmaps[iColumn][0], iX, iInitY, null);
			if (null != bitmaps[iColumn][1])
				canvas.drawBitmap(bitmaps[iColumn][1], iX, iInitY, null);
			iColumn++;
			iX += iGridSideLength;
		}
	};

	@Override
	protected void onRecycleBitmaps() {
		if (null != btmpPerspective && !btmpPerspective.isRecycled())
			btmpPerspective.recycle();
		YImageUtil.recycleBitmapArray(btmp_arrStretched);
	}

	public void onReceiveBroadcastMsg(int arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * ����Զ��ͼ
	 * 
	 * @param iPerspectivePicId
	 *            ����ͼid
	 */
	public void setPerspectivePic(int iPerspectivePicId) {
		this.iPerspectivePicId = iPerspectivePicId;
	}

	@Override
	public String toString() {
		return "____________________\n" + "MapView:\n" + "���:" + iWidth + "���أ�"
				+ "  �߶�:" + iHeight + "����" + "\n��ʾ" + iColumnSum + "�У���"
				+ iXRest + "���أ�" + "�հ�" + iXBlank + "����" + "\n��ʾ" + iRowSum
				+ "�У���" + iYRest + "���أ�" + "�հ�" + iYBlank + "����" + "\nͼԪ�߳�:"
				+ iGridSideLength + "����";
	}

	/**
	 * ��ȡ��Ϸ��ͼ�Ŀ�ȣ����أ�
	 * 
	 * @return iWidth
	 * @author Jayvee
	 */
	public int getViewWidth() {
		return iWidth;
	}

	/**
	 * ��ȡ��Ϸ��ͼ�Ŀ�ȣ����أ�
	 * 
	 * @return iWidth
	 * @author Jayvee
	 */
	public int getViewHeight() {
		return iHeight;
	}

}
