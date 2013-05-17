package fyy.ygame_map.logic;


import java.util.List;

import android.util.Log;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;
import fyy.ygame_map.YMapConstant;
import fyy.ygame_map.data.YAMapData;

public class YMapLogic extends YABaseDomainLogic<YAMapData>
// public class YMapLogic extends YABaseDomainLogic<YAMapData, YMapModel>
{

	/** 走一格需要的步数 */
	private int iStepsPerTile = 5;

	/** 日志标签 */
	private static final String strTag = "YMapLogic";

	/** 广播消息 */
	public static final int iMSG_MapLogicConfirmed = -427091;

	/** 一步需要走多少像素，屏幕适配后得知 */
	public int iPixelsPerStep;

	/** 地图初始化时绘制的第一列，从1计数 */
	int iInitColumn = 1;
	/** 地图初始化时绘制的第一行，从1计数 */
	int iInitRow = 1;

	/** 远景图起始横坐标 */
	int iCurPerspectiveX;
	/** 移动一步，远景图所移动的像素 */
	int iPerspectiveStep = 1;

	/** 地图能绘制的最末列 */
	int iCol_StartDraw_Max;
	/** 地图能绘制的最末行 */
	int iRow_StartDraw_Max;

	/** 地图数据 */
	final YAMapData mapData;

	/** 远景图移动速度，<b>注：相对于图元移动的比例</b> */
	private float fPerspectiveVelocity = 0.5f;

	/** 地图上边缘标识 */
	boolean bTop;
	/** 地图下边缘标识 */
	boolean bBottom;
	/** 地图左边缘标识 */
	boolean bLeft;
	/** 地图右边缘标识 */
	boolean bRight;

	/** 视图布局是否完成标识 */
	private boolean bLogicConfirmed;

	/**
	 * x方向上已经偏移了多少像素（为负值），指的是整个地图，不是地图视图<br>
	 * <b>{@link #iMaxXCanMove}</b><=<b>iCurX</b><=<b>0</b>
	 */
	int iCurX;
	/**
	 * y方向上已经偏移了多少像素（为负值），指的是整个地图，不是地图视图<br>
	 * <b>{@link #iMaxYCanMove}</b><=<b>iCurY</b><=<b>0</b>
	 */
	int iCurY;
	/** 地图能够移动的最大位置，x坐标 （{@link #iCurX}最大值） */
	int iMaxXCanMove;
	/** 地图能够移动的最大位置，y坐标 （{@link #iCurY}最大值） */
	int iMaxYCanMove;

	// /** 全地图列总数，以0计数 */
	// private int iColumnSum;
	// /** 全地图行总数，以0计数 */
	// private int iRowSum;

	/** 用于障碍测试返回模糊障碍距离，其值为2 * {@link YAMapData#iViewGridSideLength} */
	public static final int iDISTANCE_INFINITY = Integer.MAX_VALUE;

	/** 地图总长度（指的不是地图视图，而是全部地图） */
	private int iMapWidth;
	/** 地图总高度（指的不是地图视图，而是全部地图） */
	private int iMapHeight;

	/** 地图四个方向封闭标识，化为四位二进制数，顺序为上下左右，1标识封闭，0标识开放。 默认全封闭 */
	private int iFlagObturate = 15;

	/** 标识地图移动是否为步进式 */
	private boolean bStepByStep;

	YITask[] tasksScrollLeft;
	YITask[] tasksScrollRight;
	YITask[] tasksNoScroll;

	YITask[] tasksScrollUp;
	YITask[] tasksScrollDown;

	/* 处理请求相关 */
	private static final int iState_LeftScrolling = -1;
	private static final int iState_RightScrolling = 1;
	private static final int iState_NoScrolling = 0;
	private static final int iState_UpScrolling = -2;
	private static final int iState_DownScrolling = 2;

	private static final int iERROR_IN_C = Integer.MAX_VALUE - 1;
	private int iState;

	private YMapLogicNative mapLogicNative;

	public YMapLogic(YAMapData domainData) {
		super(domainData);
		this.mapData = domainData;

		tasksScrollLeft = new YITask[] { new ScrollLeftTask(this) };
		tasksScrollRight = new YITask[] { new ScrollRightTask(this) };
		tasksNoScroll = new YITask[] { new NoScrollTask(this) };
		tasksScrollUp = new YITask[] { new ScrollUpTask(this) };
		tasksScrollDown = new YITask[] { new ScrollDownTask(this) };
	}

	@Override
	protected YITask[] onSubmitCurrentTasks() {
		YITask[] tasks = null;
		switch (iState) {
		case iState_LeftScrolling:
			tasks = tasksScrollLeft;
			break;

		case iState_RightScrolling:
			tasks = tasksScrollRight;
			break;

		case iState_UpScrolling:
			tasks = tasksScrollUp;
			break;

		case iState_DownScrolling:
			tasks = tasksScrollDown;
			break;

		case iState_NoScrolling:
			tasks = tasksNoScroll;
			break;

		default:
			tasks = null;
			break;
		}

		if (bStepByStep)
			iState = iState_NoScrolling;

		return tasks;
	}

	@Override
	protected int onDealRequest(YRequest request) {
		switch (request.iKey) {
		case YMapConstant.TaskKey.iScrollLeft:
			if (request.bStart)
				iState = iState_LeftScrolling;
			else {
				if (!bStepByStep)
					iState = iState_NoScrolling;
			}
			break;

		case YMapConstant.TaskKey.iScrollRight:
			if (request.bStart)
				iState = iState_RightScrolling;
			else {
				if (!bStepByStep)
					iState = iState_NoScrolling;
			}
			break;

		case YMapConstant.TaskKey.iScrollUp:
			if (request.bStart)
				iState = iState_UpScrolling;
			else {
				if (!bStepByStep)
					iState = iState_NoScrolling;
			}
			break;

		case YMapConstant.TaskKey.iScrollDown:
			if (request.bStart)
				iState = iState_DownScrolling;
			else {
				if (!bStepByStep)
					iState = iState_NoScrolling;
			}
			break;

		default:
			break;
		}
		return 0;
	}

	public void onReceiveBroadcastMsg(int arg0, Object arg1) {
		switch (arg0) {
		case YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED:
			confirmMoveParam();
			confirmBoundary();
			confirmInitPosition();
			bLogicConfirmed = true;
			mapLogicNative = new YMapLogicNative(mapData,
					domainData.iViewGridSideLength, iMapWidth, iMapHeight);
			System.out.println(this);
			broadcastLogic.send(YMapLogic.iMSG_MapLogicConfirmed, null, this);
			break;

		default:
			break;
		}
	}

	/** 确定地图的初始（显示）位置 */
	private void confirmInitPosition() {/*
										 * 该函数完成： 1.检查初始行列是否合法；
										 * 2.计算iCurX、iCurY；3.远景图初始位置 ；4.置方向标识
										 */
		// 1.
		if (iInitColumn > iCol_StartDraw_Max || iInitRow > iRow_StartDraw_Max)// 最小值已经在设置时检查
		{
			System.out
					.println("异常：initializeMapPosition(int iLeftColumn, int iTopRow)中"
							+ "参数不合法。"
							+ "iLeftColumn范围为:1~"
							+ iCol_StartDraw_Max
							+ "  iTopRow范围为:1~"
							+ iRow_StartDraw_Max);
			Log.e(strTag,
					"initializeMapPosition(int iLeftColumn, int iTopRow)中"
							+ "参数不合法。" + "iLeftColumn范围为:1~"
							+ iCol_StartDraw_Max + "  iTopRow范围为:1~"
							+ iRow_StartDraw_Max);
			throw new IllegalArgumentException("初始化地图位置异常");
		}
		// 2.
		iCurX = -(iInitColumn - 1) * domainData.iViewGridSideLength;
		iCurY = -(iInitRow - 1) * domainData.iViewGridSideLength;
		// 3.
		if (iMaxXCanMove == iCurX)
			bRight = true;
		if (0 == iCurX)
			bLeft = true;
		if (iMaxYCanMove == iCurY)
			bBottom = true;
		if (0 == iCurY)
			bTop = true;
		// 4.
		iCurPerspectiveX += (iInitColumn - 1) * iStepsPerTile
				* iPerspectiveStep;
	}

	/** 确定地图移动边界条件 */
	private void confirmBoundary() {
		iCol_StartDraw_Max = domainData.iMapColumnSum
				- domainData.iMapViewColumn + 1;
		iRow_StartDraw_Max = domainData.iMapRowSum - domainData.iMapViewRow + 1;

		iMapWidth = domainData.iViewGridSideLength * domainData.iMapColumnSum;
		iMapHeight = domainData.iViewGridSideLength * domainData.iMapRowSum;

		// iColumnSum = domainData.iMapColumnSum - 1;
		// iRowSum = domainData.iMapRowSum - 1;

		iMaxXCanMove = -(domainData.iMapColumnSum
				* domainData.iViewGridSideLength - domainData.iMapViewWidth);
		iMaxYCanMove = -(domainData.iMapRowSum * domainData.iViewGridSideLength - domainData.iMapViewHeight);
	}

	/** 确定地图移动参数 */
	private void confirmMoveParam() {/* 该函数完成：1.近景图一步移动多少像素；2.远景图一步移动多少像素 */
		iPixelsPerStep = domainData.iViewGridSideLength / iStepsPerTile;
		iPerspectiveStep = (int) (iPixelsPerStep * fPerspectiveVelocity);
		if (0 == iPixelsPerStep || 0 == iPerspectiveStep) {
			Log.w(strTag, "地图移动参数为零。iPixelsPerStep:" + iPixelsPerStep
					+ "  iPerspectiveStep:" + iPerspectiveStep
					+ "建议：方法_setStepsPerTile(int)设置较小参数值；\n"
					+ "方法_setPerspectiveVelocity(int)设置较大参数值。");
			System.out.println("地图移动参数为零。iPixelsPerStep:" + iPixelsPerStep
					+ "  iPerspectiveStep:" + iPerspectiveStep
					+ "建议：方法_setStepsPerTile(int)设置较小参数值；\n"
					+ "方法_setPerspectiveVelocity(int)设置较大参数值。");

		}
	}

	@Override
	protected void onSendRequests(
			java.util.Map<Integer, fyy.ygame_frame.base.YABaseDomainLogic<?>> domainLogics) {
	};

	/**
	 * 是否到达地图<b>左边缘</b><br>
	 * 注：<b>左边缘</b>的含义：地图移至第一列，并且x方向偏移为零，则认为处在左边缘
	 * 
	 * @return 到达返回true，否则返回false
	 */
	public final boolean isLeftEdge() {
		return bLeft;
	}

	/**
	 * 是否到达地图<b>右边缘</b><br>
	 * 注：<b>右边缘</b>的含义：地图移至最末列，并且x方向偏移也已经移满，则认为处在右边缘
	 * 
	 * @return 到达返回true，否则返回false
	 */
	public final boolean isRightEdge() {
		return bRight;
	}

	/**
	 * 是否到达地图<b>上边缘</b><br>
	 * 注：<b>上边缘</b>的含义：地图移至第一行，并且y方向偏移为零，则认为处在上边缘
	 * 
	 * @return 到达返回true，否则返回false
	 */
	public final boolean isTopEdge() {
		return bTop;
	}

	/**
	 * 是否到达地图<b>下边缘</b><br>
	 * 注：<b>下边缘</b>的含义：地图移至最末行，并且y方向偏移也已经移满，则认为处在下边缘
	 * 
	 * @return 到达返回true，否则返回false
	 */
	public final boolean isBottomEdge() {
		return bBottom;
	}

	/**
	 * 初始化地图显示位置
	 * 
	 * @param iTopRow
	 *            地图视图第一行的标号
	 * @param iLeftColumn
	 *            地图视图第一列的标号
	 */
	public final void initializeMapPosition(int iTopRow, int iLeftColumn) {
		if (iLeftColumn <= 0 || iTopRow <= 0) {
			System.out.println("异常：地图初始显示列数和行数应该大于零");
			Log.e(strTag, "地图初始显示列数和行数应该大于零");
			throw new IllegalArgumentException("地图设置初始显示位置参数异常");
		}
		iInitColumn = iLeftColumn;
		iInitRow = iTopRow;
	}

	/**
	 * 设置移动一格所需要的步数
	 * 
	 * @param iStepsPerTile
	 *            移动一格所需要的步数
	 */
	public final void setStepsPerTile(int iStepsPerTile) {
		this.iStepsPerTile = iStepsPerTile;
	}

	/**
	 * 获取移动一格所需要的步数
	 * 
	 * @return 移动一格所需要的步数
	 */
	public final int getStepsPerTile() {
		return iStepsPerTile;
	}

	/**
	 * 设置远景图移动速度
	 * 
	 * @param iPerspectiveVelocity
	 *            远景图移动速度<b> 注：该参数是相对于图元（近景图）的移动速度， 建议设置在0~1之间。然而即使如此，
	 *            图元（近景图）移动速度（即setStepsPerTile(int)中参数）过小， 也可能导致该速度为零</b>
	 */
	public final void setPerspectiveVelocity(float fPerspectiveVelocity) {
		this.fPerspectiveVelocity = fPerspectiveVelocity;
	}

	/**
	 * 测试给定点右边障碍格的距离
	 * <hr>
	 * <b>注：
	 * <li>距离指的是：给定点的像素小格右边缘距离障碍格左边缘的距离
	 * <li>给定点的坐标指的是地图上的实际坐标，而非视图上的坐标
	 * <li>仅当给定点距离障碍格小于一格距离，得到的距离才是真实距离</b>
	 * 
	 * @param iX
	 *            给定点横坐标
	 * @param iY
	 *            给定点纵坐标
	 * @return 给定点距离障碍格大于一格的距离，返回一格距离的两倍；如果距离障碍格小于一格的距离，则返回距离障碍格左边缘的实际距离。
	 */
	public final int howFarIsBarrierInMyRight(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyRight(iX, iY,
				iTestTileNumInRight, 2 & iFlagObturate, 1 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "所给点不能处在地图之外的上方或地图之外的下方，" + "所给点当前Y坐标为：" + iY);
			throw new IllegalArgumentException("所给点坐标非法");
		} else
			return iRes;
	}

	/**
	 * 测试给定点下边障碍格的距离
	 * <hr>
	 * <b>注：
	 * <li>距离指的是：给定点的像素小格下边缘距离障碍格上边缘的距离
	 * <li>给定点的坐标指的是地图上的实际坐标，而非视图上的坐标
	 * <li>仅当给定点距离障碍格小于一格距离，得到的距离才是真实距离</b>
	 * 
	 * @param iX
	 *            给定点横坐标
	 * @param iY
	 *            给定点纵坐标
	 * @return 给定点距离障碍格大于一格的距离，返回一格距离的两倍；如果距离障碍格小于一格的距离，则返回距离障碍格上边缘的实际距离。
	 */
	public final int howFarIsBarrierInMyBottom(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyBottom(iX, iY,
				iTestTileNumInBottom, 8 & iFlagObturate, 4 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "所给点不能处在地图之外的左边或地图之外的右边" + "所给点当前X坐标为：" + iX);
			throw new IllegalArgumentException("所给点坐标非法");
		} else
			return iRes;

	}

	/**
	 * 测试给定点左边障碍格的距离
	 * <hr>
	 * <b>注：
	 * <li>距离指的是：给定点的像素小格左边缘距离障碍格右边缘的距离
	 * <li>给定点的坐标指的是地图上的实际坐标，而非视图上的坐标
	 * <li>仅当给定点距离障碍格小于一格距离，得到的距离才是真实距离</b>
	 * 
	 * @param iX
	 *            给定点横坐标
	 * @param iY
	 *            给定点纵坐标
	 * @return 给定点距离障碍格大于一格的距离，返回一格距离的两倍；如果距离障碍格小于一格的距离，则返回距离障碍格右边缘的实际距离。
	 */
	public final int howFarIsBarrierInMyLeft(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyLeft(iX, iY,
				iTestTileNumInLeft, 2 & iFlagObturate, 1 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "所给点不能处在地图之外的上边或地图之外的下边" + "所给点当前Y坐标为：" + iY);
			throw new IllegalArgumentException("所给点坐标非法");
		} else
			return iRes;
	}

	/**
	 * 测试给定点上边障碍格的距离
	 * <hr>
	 * <b>注：
	 * <li>距离指的是：给定点的像素小格上边缘距离障碍格下边缘的距离
	 * <li>给定点的坐标指的是地图上的实际坐标，而非视图上的坐标
	 * <li>仅当给定点距离障碍格小于一格距离，得到的距离才是真实距离</b>
	 * 
	 * @param iX
	 *            给定点横坐标
	 * @param iY
	 *            给定点纵坐标
	 * @return 给定点距离障碍格大于一格的距离，返回一格距离的两倍；如果距离障碍格小于一格的距离，则返回距离障碍格下边缘的实际距离。
	 */
	public final int howFarIsBarrierInMyTop(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyTop(iX, iY,
				iTestTileNumInTop, 8 & iFlagObturate, 4 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "所给点不能处在地图之外的左边或地图之外的右边" + "所给点当前X坐标为：" + iX);
			throw new IllegalArgumentException("所给点坐标非法");
		} else
			return iRes;
	}

	/**
	 * 获取地图总高度（以像素为单位），待屏幕适配后得知
	 * 
	 * @return 地图总高度
	 */
	public int getMapHeightByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "应等到地图布局完成才能获取该值");
			System.out.println("异常：应等到地图布局完成才能获取该值");
			throw new IllegalAccessError("地图布局尚未完成，非法访问该值");
		}
		return iMapHeight;
	}

	/**
	 * 获取地图图元边长（以像素为单位），待屏幕适配后得知
	 * 
	 * @return 地图图元边长
	 */
	public int getTileSideLengthByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "应等到地图布局完成才能获取该值");
			System.out.println("异常：应等到地图布局完成才能获取该值");
			throw new IllegalAccessError("地图布局尚未完成，非法访问该值");
		}
		return domainData.iViewGridSideLength;
	}

	/**
	 * 获取地图宽高度（以像素为单位），待屏幕适配后得知
	 * 
	 * @return 地图宽高度
	 */
	public int getMapWidthByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "应等到地图布局完成才能获取该值");
			System.out.println("异常：应等到地图布局完成才能获取该值");
			throw new IllegalAccessError("地图布局尚未完成，非法访问该值");
		}

		return iMapWidth;
	}

	/**
	 * 获取地图滚动后的X轴偏移量
	 * 
	 * @return 地图滚动后的X轴偏移量, iOffsetX
	 * @author Jayvee
	 */
	public int getiOffsetX() {
		return iCurX;
	}

	/**
	 * 获取地图滚动后的Y轴偏移量
	 * 
	 * @return 地图滚动后的Y轴偏移量, iOffsetY
	 * @author Jayvee
	 */
	public int getiOffsetY() {
		return iCurY;
	}

	/**
	 * 设置地图边界封闭性，封闭视为有障碍、不可通过；开放视为无障碍、可通过。默认为四个方向全封闭。
	 * 
	 * @param iObturate
	 *            封闭标识，取值为大于等于0，小于等于15；为四位二进制数，
	 *            顺序从高位到低位依次标识地图上下左右的封闭性，1标识封闭，0标识开放
	 */
	public void setObturate(int iObturate) {
		if (iObturate > 15 || iObturate < 0) {
			Log.e(strTag, "封闭标识范围应大于等于0，小于等于15，但当前值为：" + iObturate);
			System.out.println("异常：封闭标识范围应大于等于0，小于等于15，但当前值为：" + iObturate);
			throw new IllegalArgumentException("封闭标识范围不合法");
		}

		iFlagObturate = iObturate;
	}

	public void setStepByStepScroll(boolean bStepByStep) {
		this.bStepByStep = bStepByStep;
	}

	private int iTestTileNumInBottom = 1;
	private int iTestTileNumInTop = 1;
	private int iTestTileNumInLeft = 1;
	private int iTestTileNumInRight = 1;

	/**
	 * 设置检测点检测障碍时拓展的地图格数目（默认为1）<br>
	 * 建议：如果不需要精确检测地图障碍格距离的话，不用设置，保持默认即可
	 * 
	 * @param iTestTileNumInTop
	 *            检测上方障碍时的拓展格数
	 * @param iTestTileNumInBottom
	 *            检测下方障碍时的拓展格数
	 * @param iTestTileNumInLeft
	 *            检测左方障碍时的拓展格数
	 * @param iTestTileNumInRight
	 *            检测右方障碍时的拓展格数
	 */
	public void setTestDistance(int iTestTileNumInTop,
			int iTestTileNumInBottom, int iTestTileNumInLeft,
			int iTestTileNumInRight) {
		if (iTestTileNumInBottom <= 0 || iTestTileNumInLeft <= 0
				|| iTestTileNumInRight <= 0 || iTestTileNumInTop <= 0) {
			Log.e(strTag, "非法参数，障碍检测拓展格数应该大于等于1");
			throw new IllegalArgumentException("障碍检测拓展格数非法");
		}
		this.iTestTileNumInBottom = iTestTileNumInBottom;
		this.iTestTileNumInLeft = iTestTileNumInLeft;
		this.iTestTileNumInRight = iTestTileNumInRight;
		this.iTestTileNumInTop = iTestTileNumInTop;
	}

	/**
	 * 处理攻击等伤害信息的对象数组
	 * 
	 * @author Jayvee
	 */
	// BuffInfo bf =
	// new BuffInfo();

	// BuffInfo[][] buffinfo = null;

	BuffInfo[][] buffinfo = new BuffInfo[domainData.iMapRowSum][domainData.iMapColumnSum];

	public void getBuffInfo(int Row, int Col) {

		// a = buffinfo[0][0].getDamage();

	}

	// buffinfo[0][0].
	// new BuffInfo[domainData.iMapRowSum][domainData.iMapColumnSum]
	// buffinfo[0][0] = new BuffInfo();
	// if (buffinfo!=null)
	// {
	// System.out.println("ttttttttttttttt"+ domainData.iMapRowSum);
	// }
	//

	@Override
	public String toString() {
		String str = "____________________\n" + "MapLogic:\n" + "起始列："
				+ iInitColumn + "  起始行：" + iInitRow + "\n起始列最大列数："
				+ iCol_StartDraw_Max + "  起始行最大行数：" + iRow_StartDraw_Max
				+ "\n移动一格所需步数：" + iStepsPerTile + "  一步所走像素数：" + iPixelsPerStep
				+ "\n远景图移动速度（相对于近景图）：" + fPerspectiveVelocity + "  一步所走像素数："
				+ iPerspectiveStep;
		return str;
	}

	/**
	 * 在地图指定位置，标记某<b>领域逻辑</b>{@link YABaseDomainLogic}占据了该位置<br>
	 * <hr>
	 * 注：
	 * <li>该方法需要在地图视图适配屏幕之后才能调用<br>
	 * <li>应该保证传入的iX、iY坐标合法（在地图逻辑之内）<br>
	 * <li>发起标记的领域逻辑，应该在离开该位置的时候调用
	 * {@link #removeMark(int, int, YABaseDomainLogic)}移除该标记
	 * 
	 * @param iX
	 *            领域逻辑在地图中所处位置的横坐标（不是视图坐标，是逻辑坐标）
	 * @param iY
	 *            领域逻辑在地图中所处位置的纵坐标（不是视图坐标，是逻辑坐标）
	 * @param domainLogic
	 *            标记该格的领域逻辑
	 */
	@Deprecated
	public void mark(int iX, int iY, YABaseDomainLogic<?> domainLogic) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		domainData.tl_arr2[iRow][iCol].addDomainLogic(domainLogic);
	}

	/**
	 * 在地图指定位置，移除某<b>领域逻辑</b>{@link YABaseDomainLogic}<br>
	 * 注：<li>该方法需要在地图视图适配屏幕之后才能调用 <li>应该保证传入的iX、iY坐标合法（在地图逻辑之内）
	 * 
	 * @param iX
	 *            领域逻辑在地图中所处位置的横坐标（不是视图坐标，是逻辑坐标）
	 * @param iY
	 *            领域逻辑在地图中所处位置的纵坐标（不是视图坐标，是逻辑坐标）
	 * @param domainLogic
	 *            要移除的领域逻辑
	 */
	@Deprecated
	public void removeMark(int iX, int iY, YABaseDomainLogic<?> domainLogic) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		domainData.tl_arr2[iRow][iCol].removeDomainLogic(domainLogic);
	}

	/**
	 * 获取地图上指定位置上的所有<b>领域逻辑</b>{@link YABaseDomainLogic}<br>
	 * 注：<li>该方法需要在地图视图适配屏幕之后才能调用 <li>应该保证传入的iX、iY坐标合法（在地图逻辑之内） <li>
	 * 获取到的是一个集合，而非单个领域逻辑
	 * 
	 * @param iX
	 *            指定位置的横坐标（不是视图坐标，是逻辑坐标）
	 * @param iY
	 *            指定位置的纵坐标（不是视图坐标，是逻辑坐标）
	 */
	@Deprecated
	public List<YABaseDomainLogic<?>> getMarks(int iX, int iY) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		return domainData.tl_arr2[iRow][iCol].getDomainLogic();
	}

}
