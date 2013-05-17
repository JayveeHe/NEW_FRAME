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
	 * 绘图信息的int数组下标约定<br>
	 * <p>
	 * 地图的绘图信息包括：
	 * <li>前景图绘制起始点坐标；</li>存于{@link YDrawInformation#iX}、
	 * {@link YDrawInformation#iY}
	 * <li>图元绘制信息int数组；</li>存于{@link YDrawInformation#objExtra}指向的int数组 （此时
	 * {@link YDrawInformation#objExtra}
	 * 指向一个含有两个元素的内存区，第一个为int型数组，第二个为boolean型数组）
	 * <li>边缘标识boolean数组；</li>存于{@link YDrawInformation#objExtra}指向的第二个数组<br>
	 * 约定：int数组下标为{@link #iSubscript_ColStartDraw}的元素：<b>当前绘制的起始列</b><br>
	 * int数组下标为{@link #iSubscript_RowStartDraw}的元素：<b>当前绘制的起始行</b><br>
	 * int数组下标为{@link #iSubscript_XDraw}的元素：<b>起始图元绘在视图上的横坐标</b><br>
	 * int数组下标为{@link #iSubscript_YDraw}的元素：<b>起始图元绘在视图上的纵坐标</b><br>
	 * int数组下标为{@link #iSubscript_FlagEdge}的元素：<b>是否修正残余行、列</b><br>
	 * </p>
	 */
	static final int iSubscript_ColStartDraw = 0;

	/**
	 * 绘图信息的int数组下标约定，数组下标为该值的元素：<b>起始图元绘在视图上的横坐标</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_XDraw = 1;

	/**
	 * 绘图信息的int数组下标约定，数组下标为该值的元素：<b>当前绘制的起始行</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_RowStartDraw = 2;

	/**
	 * 绘图信息的int数组下标约定，数组下标为该值的元素：<b>起始图元绘在视图上的纵坐标</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_YDraw = 3;

	// /**
	// * 绘图信息的int数组下标约定，数组下标为该值的元素：<b>描述地图是否到达边缘</b><br>
	// *
	// * 对应的元素为<b>边缘标识</b>，约定最低四位标识方向，
	// 从高到低依次为：上下左右，0为地图没有移至该边缘，1为地图移至该边缘<br>
	// * 如：0b0000表示地图四个方向都没有到边缘；0b1010表示地图移至上左边缘……
	// *
	// * @see YAMapView#iSubscript_ColStartDraw
	// */
	// static final int iSubscript_FlagEdge = 4;
	/**
	 * 绘图信息的boolean数组下标约定，boolean数组下标为该值的元素：<b>地图是否达到上边缘</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Top = 0;

	/**
	 * 绘图信息的boolean数组下标约定，boolean数组下标为该值的元素：<b>地图是否达到下边缘</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Bottom = 1;

	/**
	 * 绘图信息的boolean数组下标约定，boolean数组下标为该值的元素：<b>地图是否达到左边缘</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Left = 3;

	/**
	 * 绘图信息的boolean数组下标约定，boolean数组下标为该值的元素：<b>地图是否达到右边缘</b>
	 * 
	 * @see YAMapView#iSubscript_ColStartDraw
	 */
	static final int iSubscript_Right = 4;

	/** 日志标签 */
	private static final String strTag = "YAMapView";
	/** 屏幕适配后的图元数组，用于回收 */
	private Bitmap[] btmp_arrStretched;

	/** 视图宽度 */
	protected int iWidth;
	/** 视图高度 */
	protected int iHeight;

	/** 地图视图绘图的列数，自1算起 */
	protected int iColumnSum;
	/** 地图视图绘图行数 ，自1算起 */
	protected int iRowSum;
	/** 一个绘画图元的边长 */
	protected int iGridSideLength;
	/**
	 * 适配参数：屏幕适配之后X方向上剩余的长度 ，该长度还存在地图元去填充，但又未填满一列 。<br>
	 * <b>重要的，这部分是地图视图的部分</b>
	 */
	protected int iXRest = -530;
	/**
	 * 适配参数：屏幕适配之后Y方向上剩余的长度，该长度还能存在地图元填充，但又未填满一行。 <br>
	 * <b>重要的，这部分是地图视图的部分</b>
	 */
	protected int iYRest = -530;
	/**
	 * 适配参数：屏幕适配之后X方向上多出的空白部分，已无地图元填充 。 <br>
	 * <b>重要的，这部分不是地图视图的部分，是游戏视图的部分</b>
	 */
	protected int iXBlank = -530;
	/**
	 * 屏幕适配之后Y方向上多出的空白部分，已无地图元填充。<br>
	 * <b>重要的，这部分不是地图视图的部分，是游戏视图的部分</b>
	 */
	protected int iYBlank = -530;

	/** 地图图元附着图片的位图对象数组，第几行第几列第几层附着什么样的位图 */
	protected Bitmap[][][] btmp_arr3TilePic;
	/** 地图图元附着图片序号数组，第几行第几列第几层附着序号为几的图片 */
	private int[][][] i_arr3TilePic;

	/** 索引图资源id（配置在R文件中的那个） */
	private int iIndexPicId;

	/** 远景图资源id */
	protected int iPerspectivePicId;
	/** 远景图 */
	protected Bitmap btmpPerspective;

	/**
	 * 产生显示所有地图行的地图视图（不可上下移动、但可以左右移动的横版地图视图）
	 * 
	 * @param mapData
	 *            地图数据
	 * @param iIndexPicId
	 *            地图索引图id
	 * @param iPerspectivePicId
	 *            地图背景图id
	 * @return 地图视图
	 */
	public static YAMapView createSimpleHorizonMap(YAMapData mapData,
			int iIndexPicId) {
		return new YSimpleHorizonMap(mapData, iIndexPicId);
	}

	/**
	 * 产生指定显示行数的地图视图
	 * 
	 * @param mapData
	 *            地图数据
	 * @param iIndexPicId
	 *            地图索引图id
	 * @param iRowDisplay
	 *            指定显示的行数
	 * @return 地图视图
	 */
	public static YAMapView createNormalPlaneMap(YAMapData mapData,
			int iIndexPicId, int iRowDisplay) {// iRowDisplay合法范围：1~mapData.iMapRowSum（闭区间）
		if (iRowDisplay < 1 || iRowDisplay > mapData.iMapRowSum) {// 非法参数
			System.out.println("异常：普通平面地图指定显示行数-iRowDisplay范围应该为1~"
					+ mapData.iMapRowSum);
			Log.e(strTag, "普通平面地图指定显示行数-iRowDisplay范围应该为1~"
					+ mapData.iMapRowSum);
			throw new IllegalArgumentException("普通平面地图指定显示行数异常");
		}
		// 经过查验之后，iRowDisplay范围一定在1~iRowDisplay
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
		// 适配GameView
		adaptGameView(iWidth, iHeight);

		// 确定地图视图高宽
		if (-530 == iXBlank || -530 == iYBlank) {
			System.out.println("异常：iXBlank或iYBlank没有获取到适配值");
			Log.e(strTag, "iXBlank或iYBlank没有获取到适配值");
			throw new RuntimeException("适配参数获取异常");
		}
		this.iWidth = iWidth - iXBlank;
		this.iHeight = iHeight - iYBlank;
		System.out.println(this);

		// 将索引图拆分、包装为可绘画的图元
		// 拆分索引图，获取图元对象
		Bitmap[] btmp_arr = YImageUtil.splitImage(domainData.iIndexPicRowSum,
				domainData.iIndexPicColumnSum,
				BitmapFactory.decodeResource(resources, iIndexPicId));

		// 拉伸图元对象，使其适应屏幕
		btmp_arrStretched = YImageUtil.stretchImageArray(btmp_arr,
				iGridSideLength);

		// 获取图元附着图片的位图对象数组
		btmp_arr3TilePic = fetchBitmapArray3(domainData.tl_arr2,
				btmp_arrStretched);

		// 创建远景图
		createPerspective(resources, iWidth, iHeight);

		// 将地图布局完成消息发送，地图数据接收后完成对数据的填充
		// 注意约定：int数组顺序为：地图视图宽度、地图视图高度、一格边长（都是像素为单位）、
		// 地图视图的行数， 地图视图的列数
		// broadcast.send(YBroadcast.iMsgViewLayouted, new int[]
		// { this.iWidth, this.iHeight, iGridSideLength, iRowSum,
		// iColumnSum }, this);
		int[] iParams = new int[] { this.iWidth, this.iHeight, iGridSideLength,
				iRowSum, iColumnSum };
		broadcastDomain.send(YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED, iParams, this);

		broadcastView.send(YGameEnvironment.BroadcastMsgKey.MSG_MAP_VIEW_LAYOUTED, iParams, this);
	}

	/**
	 * 根据游戏视图{@link YAGameView}高宽，适配出地图视图的参数{@link #iRowSum}、
	 * {@link #iGridSideLength}、 {@link #iColumnSum}、{@link #iYRest}
	 * 
	 * @param iWidth
	 *            游戏视图宽
	 * @param iHeight
	 *            游戏视图高
	 */
	abstract protected void adaptGameView(int iWidth, int iHeight);

	/**
	 * 创建远景图
	 * 
	 * @param resources
	 *            资源引用
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
			Log.w(strTag, "没有设置背景图");
	}

	/**
	 * 将数字图元数组与位图数组整合为位图的三元数组，便于地图绘制
	 * 
	 * @param dgtl_tl_arr
	 *            包含数字信息的地图图元数组
	 * @param btmp_arr
	 *            索引图拆分后得到的位图对象数组
	 * @return 高效的、方便地图绘制的三维地图数组
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
				if (0 != i_arrBitmap[0]) {// 若非空，在对应的三维地图数组元素中填充对应图像
					btmp_arr3[i][j][0] = btmp_arr[i_arrBitmap[0] - 1];
				}
				if (0 != i_arrBitmap[1]) {// 同上
					btmp_arr3[i][j][1] = btmp_arr[i_arrBitmap[1] - 1];
				}
			}
		}
		return btmp_arr3;
	}

	/**
	 * 从指定的图元，绘制一块，块的长度和高度自适应地图视图
	 * 
	 * @param canvas
	 *            画布
	 * @param iFirstTileRow
	 *            指定行的首图元在地图里的行数，以1开始计数
	 * @param iFirstTileColumn
	 *            指定行的首图元在地图里的列数，以1开始计数
	 * @param iX
	 *            x偏移
	 * @param iY
	 *            y偏移
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
	 * 从指定的图元，绘制一行，行的长度自适应地图视图宽度
	 * 
	 * @param canvas
	 *            画布
	 * @param iFirstTileRow
	 *            指定行的首图元在地图里的行数，以1开始计数
	 * @param iFirstTileColumn
	 *            指定行的首图元在地图里的列数，以1开始计数
	 * @param iX
	 *            x偏移
	 * @param iY
	 *            y偏移
	 */
	private void drawOneRow(Canvas canvas, int iFirstTileRow,
			int iFirstTileColumn, int iInitX, int iInitY) {
		// 1计数到0计数转换
		int iRow = iFirstTileRow - 1;
		int iColumn = iFirstTileColumn - 1;
		// 从地图中选中要绘的该行
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
	 * 设置远景图
	 * 
	 * @param iPerspectivePicId
	 *            背景图id
	 */
	public void setPerspectivePic(int iPerspectivePicId) {
		this.iPerspectivePicId = iPerspectivePicId;
	}

	@Override
	public String toString() {
		return "____________________\n" + "MapView:\n" + "宽度:" + iWidth + "像素；"
				+ "  高度:" + iHeight + "像素" + "\n显示" + iColumnSum + "列，余"
				+ iXRest + "像素，" + "空白" + iXBlank + "像素" + "\n显示" + iRowSum
				+ "行，余" + iYRest + "像素，" + "空白" + iYBlank + "像素" + "\n图元边长:"
				+ iGridSideLength + "像素";
	}

	/**
	 * 获取游戏视图的宽度（像素）
	 * 
	 * @return iWidth
	 * @author Jayvee
	 */
	public int getViewWidth() {
		return iWidth;
	}

	/**
	 * 获取游戏视图的宽度（像素）
	 * 
	 * @return iWidth
	 * @author Jayvee
	 */
	public int getViewHeight() {
		return iHeight;
	}

}
