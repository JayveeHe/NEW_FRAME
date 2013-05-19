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

	/** ��һ����Ҫ�Ĳ��� */
	private int iStepsPerTile = 5;

	/** ��־��ǩ */
	private static final String strTag = "YMapLogic";

	/** �㲥��Ϣ */
	public static final int iMSG_MapLogicConfirmed = -427091;

	/** һ����Ҫ�߶������أ���Ļ������֪ */
	public int iPixelsPerStep;

	/** ��ͼ��ʼ��ʱ���Ƶĵ�һ�У���1���� */
	int iInitColumn = 1;
	/** ��ͼ��ʼ��ʱ���Ƶĵ�һ�У���1���� */
	int iInitRow = 1;

	/** Զ��ͼ��ʼ������ */
	int iCurPerspectiveX;
	/** �ƶ�һ����Զ��ͼ���ƶ������� */
	int iPerspectiveStep = 1;

	/** ��ͼ�ܻ��Ƶ���ĩ�� */
	int iCol_StartDraw_Max;
	/** ��ͼ�ܻ��Ƶ���ĩ�� */
	int iRow_StartDraw_Max;

	/** ��ͼ���� */
	final YAMapData mapData;

	/** Զ��ͼ�ƶ��ٶȣ�<b>ע�������ͼԪ�ƶ��ı���</b> */
	private float fPerspectiveVelocity = 0.5f;

	/** ��ͼ�ϱ�Ե��ʶ */
	boolean bTop;
	/** ��ͼ�±�Ե��ʶ */
	boolean bBottom;
	/** ��ͼ���Ե��ʶ */
	boolean bLeft;
	/** ��ͼ�ұ�Ե��ʶ */
	boolean bRight;

	/** ��ͼ�����Ƿ���ɱ�ʶ */
	private boolean bLogicConfirmed;

	/**
	 * x�������Ѿ�ƫ���˶������أ�Ϊ��ֵ����ָ����������ͼ�����ǵ�ͼ��ͼ<br>
	 * <b>{@link #iMaxXCanMove}</b><=<b>iCurX</b><=<b>0</b>
	 */
	int iCurX;
	/**
	 * y�������Ѿ�ƫ���˶������أ�Ϊ��ֵ����ָ����������ͼ�����ǵ�ͼ��ͼ<br>
	 * <b>{@link #iMaxYCanMove}</b><=<b>iCurY</b><=<b>0</b>
	 */
	int iCurY;
	/** ��ͼ�ܹ��ƶ������λ�ã�x���� ��{@link #iCurX}���ֵ�� */
	int iMaxXCanMove;
	/** ��ͼ�ܹ��ƶ������λ�ã�y���� ��{@link #iCurY}���ֵ�� */
	int iMaxYCanMove;

	// /** ȫ��ͼ����������0���� */
	// private int iColumnSum;
	// /** ȫ��ͼ����������0���� */
	// private int iRowSum;

	/** �����ϰ����Է���ģ���ϰ����룬��ֵΪ2 * {@link YAMapData#iViewGridSideLength} */
	public static final int iDISTANCE_INFINITY = Integer.MAX_VALUE;

	/** ��ͼ�ܳ��ȣ�ָ�Ĳ��ǵ�ͼ��ͼ������ȫ����ͼ�� */
	private int iMapWidth;
	/** ��ͼ�ܸ߶ȣ�ָ�Ĳ��ǵ�ͼ��ͼ������ȫ����ͼ�� */
	private int iMapHeight;

	/** ��ͼ�ĸ������ձ�ʶ����Ϊ��λ����������˳��Ϊ�������ң�1��ʶ��գ�0��ʶ���š� Ĭ��ȫ��� */
	private int iFlagObturate = 15;

	/** ��ʶ��ͼ�ƶ��Ƿ�Ϊ����ʽ */
	private boolean bStepByStep;

	YITask[] tasksScrollLeft;
	YITask[] tasksScrollRight;
	YITask[] tasksNoScroll;

	YITask[] tasksScrollUp;
	YITask[] tasksScrollDown;

	/* ����������� */
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

	/** ȷ����ͼ�ĳ�ʼ����ʾ��λ�� */
	private void confirmInitPosition() {/*
										 * �ú�����ɣ� 1.����ʼ�����Ƿ�Ϸ���
										 * 2.����iCurX��iCurY��3.Զ��ͼ��ʼλ�� ��4.�÷����ʶ
										 */
		// 1.
		if (iInitColumn > iCol_StartDraw_Max || iInitRow > iRow_StartDraw_Max)// ��Сֵ�Ѿ�������ʱ���
		{
			System.out
					.println("�쳣��initializeMapPosition(int iLeftColumn, int iTopRow)��"
							+ "�������Ϸ���"
							+ "iLeftColumn��ΧΪ:1~"
							+ iCol_StartDraw_Max
							+ "  iTopRow��ΧΪ:1~"
							+ iRow_StartDraw_Max);
			Log.e(strTag,
					"initializeMapPosition(int iLeftColumn, int iTopRow)��"
							+ "�������Ϸ���" + "iLeftColumn��ΧΪ:1~"
							+ iCol_StartDraw_Max + "  iTopRow��ΧΪ:1~"
							+ iRow_StartDraw_Max);
			throw new IllegalArgumentException("��ʼ����ͼλ���쳣");
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

	/** ȷ����ͼ�ƶ��߽����� */
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

	/** ȷ����ͼ�ƶ����� */
	private void confirmMoveParam() {/* �ú�����ɣ�1.����ͼһ���ƶ��������أ�2.Զ��ͼһ���ƶ��������� */
		iPixelsPerStep = domainData.iViewGridSideLength / iStepsPerTile;
		iPerspectiveStep = (int) (iPixelsPerStep * fPerspectiveVelocity);
		if (0 == iPixelsPerStep || 0 == iPerspectiveStep) {
			Log.w(strTag, "��ͼ�ƶ�����Ϊ�㡣iPixelsPerStep:" + iPixelsPerStep
					+ "  iPerspectiveStep:" + iPerspectiveStep
					+ "���飺����_setStepsPerTile(int)���ý�С����ֵ��\n"
					+ "����_setPerspectiveVelocity(int)���ýϴ����ֵ��");
			System.out.println("��ͼ�ƶ�����Ϊ�㡣iPixelsPerStep:" + iPixelsPerStep
					+ "  iPerspectiveStep:" + iPerspectiveStep
					+ "���飺����_setStepsPerTile(int)���ý�С����ֵ��\n"
					+ "����_setPerspectiveVelocity(int)���ýϴ����ֵ��");

		}
	}

	@Override
	protected void onSendRequests(
			java.util.Map<Integer, fyy.ygame_frame.base.YABaseDomainLogic<?>> domainLogics) {
	};

	/**
	 * �Ƿ񵽴��ͼ<b>���Ե</b><br>
	 * ע��<b>���Ե</b>�ĺ��壺��ͼ������һ�У�����x����ƫ��Ϊ�㣬����Ϊ�������Ե
	 * 
	 * @return ���ﷵ��true�����򷵻�false
	 */
	public final boolean isLeftEdge() {
		return bLeft;
	}

	/**
	 * �Ƿ񵽴��ͼ<b>�ұ�Ե</b><br>
	 * ע��<b>�ұ�Ե</b>�ĺ��壺��ͼ������ĩ�У�����x����ƫ��Ҳ�Ѿ�����������Ϊ�����ұ�Ե
	 * 
	 * @return ���ﷵ��true�����򷵻�false
	 */
	public final boolean isRightEdge() {
		return bRight;
	}

	/**
	 * �Ƿ񵽴��ͼ<b>�ϱ�Ե</b><br>
	 * ע��<b>�ϱ�Ե</b>�ĺ��壺��ͼ������һ�У�����y����ƫ��Ϊ�㣬����Ϊ�����ϱ�Ե
	 * 
	 * @return ���ﷵ��true�����򷵻�false
	 */
	public final boolean isTopEdge() {
		return bTop;
	}

	/**
	 * �Ƿ񵽴��ͼ<b>�±�Ե</b><br>
	 * ע��<b>�±�Ե</b>�ĺ��壺��ͼ������ĩ�У�����y����ƫ��Ҳ�Ѿ�����������Ϊ�����±�Ե
	 * 
	 * @return ���ﷵ��true�����򷵻�false
	 */
	public final boolean isBottomEdge() {
		return bBottom;
	}

	/**
	 * ��ʼ����ͼ��ʾλ��
	 * 
	 * @param iTopRow
	 *            ��ͼ��ͼ��һ�еı��
	 * @param iLeftColumn
	 *            ��ͼ��ͼ��һ�еı��
	 */
	public final void initializeMapPosition(int iTopRow, int iLeftColumn) {
		if (iLeftColumn <= 0 || iTopRow <= 0) {
			System.out.println("�쳣����ͼ��ʼ��ʾ����������Ӧ�ô�����");
			Log.e(strTag, "��ͼ��ʼ��ʾ����������Ӧ�ô�����");
			throw new IllegalArgumentException("��ͼ���ó�ʼ��ʾλ�ò����쳣");
		}
		iInitColumn = iLeftColumn;
		iInitRow = iTopRow;
	}

	/**
	 * �����ƶ�һ������Ҫ�Ĳ���
	 * 
	 * @param iStepsPerTile
	 *            �ƶ�һ������Ҫ�Ĳ���
	 */
	public final void setStepsPerTile(int iStepsPerTile) {
		this.iStepsPerTile = iStepsPerTile;
	}

	/**
	 * ��ȡ�ƶ�һ������Ҫ�Ĳ���
	 * 
	 * @return �ƶ�һ������Ҫ�Ĳ���
	 */
	public final int getStepsPerTile() {
		return iStepsPerTile;
	}

	/**
	 * ����Զ��ͼ�ƶ��ٶ�
	 * 
	 * @param iPerspectiveVelocity
	 *            Զ��ͼ�ƶ��ٶ�<b> ע���ò����������ͼԪ������ͼ�����ƶ��ٶȣ� ����������0~1֮�䡣Ȼ����ʹ��ˣ�
	 *            ͼԪ������ͼ���ƶ��ٶȣ���setStepsPerTile(int)�в�������С�� Ҳ���ܵ��¸��ٶ�Ϊ��</b>
	 */
	public final void setPerspectiveVelocity(float fPerspectiveVelocity) {
		this.fPerspectiveVelocity = fPerspectiveVelocity;
	}

	/**
	 * ���Ը������ұ��ϰ���ľ���
	 * <hr>
	 * <b>ע��
	 * <li>����ָ���ǣ������������С���ұ�Ե�����ϰ������Ե�ľ���
	 * <li>�����������ָ���ǵ�ͼ�ϵ�ʵ�����꣬������ͼ�ϵ�����
	 * <li>��������������ϰ���С��һ����룬�õ��ľ��������ʵ����</b>
	 * 
	 * @param iX
	 *            �����������
	 * @param iY
	 *            ������������
	 * @return ����������ϰ������һ��ľ��룬����һ��������������������ϰ���С��һ��ľ��룬�򷵻ؾ����ϰ������Ե��ʵ�ʾ��롣
	 */
	public final int howFarIsBarrierInMyRight(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyRight(iX, iY,
				iTestTileNumInRight, 2 & iFlagObturate, 1 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "�����㲻�ܴ��ڵ�ͼ֮����Ϸ����ͼ֮����·���" + "�����㵱ǰY����Ϊ��" + iY);
			throw new IllegalArgumentException("����������Ƿ�");
		} else
			return iRes;
	}

	/**
	 * ���Ը������±��ϰ���ľ���
	 * <hr>
	 * <b>ע��
	 * <li>����ָ���ǣ������������С���±�Ե�����ϰ����ϱ�Ե�ľ���
	 * <li>�����������ָ���ǵ�ͼ�ϵ�ʵ�����꣬������ͼ�ϵ�����
	 * <li>��������������ϰ���С��һ����룬�õ��ľ��������ʵ����</b>
	 * 
	 * @param iX
	 *            �����������
	 * @param iY
	 *            ������������
	 * @return ����������ϰ������һ��ľ��룬����һ��������������������ϰ���С��һ��ľ��룬�򷵻ؾ����ϰ����ϱ�Ե��ʵ�ʾ��롣
	 */
	public final int howFarIsBarrierInMyBottom(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyBottom(iX, iY,
				iTestTileNumInBottom, 8 & iFlagObturate, 4 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "�����㲻�ܴ��ڵ�ͼ֮�����߻��ͼ֮����ұ�" + "�����㵱ǰX����Ϊ��" + iX);
			throw new IllegalArgumentException("����������Ƿ�");
		} else
			return iRes;

	}

	/**
	 * ���Ը���������ϰ���ľ���
	 * <hr>
	 * <b>ע��
	 * <li>����ָ���ǣ������������С�����Ե�����ϰ����ұ�Ե�ľ���
	 * <li>�����������ָ���ǵ�ͼ�ϵ�ʵ�����꣬������ͼ�ϵ�����
	 * <li>��������������ϰ���С��һ����룬�õ��ľ��������ʵ����</b>
	 * 
	 * @param iX
	 *            �����������
	 * @param iY
	 *            ������������
	 * @return ����������ϰ������һ��ľ��룬����һ��������������������ϰ���С��һ��ľ��룬�򷵻ؾ����ϰ����ұ�Ե��ʵ�ʾ��롣
	 */
	public final int howFarIsBarrierInMyLeft(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyLeft(iX, iY,
				iTestTileNumInLeft, 2 & iFlagObturate, 1 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "�����㲻�ܴ��ڵ�ͼ֮����ϱ߻��ͼ֮����±�" + "�����㵱ǰY����Ϊ��" + iY);
			throw new IllegalArgumentException("����������Ƿ�");
		} else
			return iRes;
	}

	/**
	 * ���Ը������ϱ��ϰ���ľ���
	 * <hr>
	 * <b>ע��
	 * <li>����ָ���ǣ������������С���ϱ�Ե�����ϰ����±�Ե�ľ���
	 * <li>�����������ָ���ǵ�ͼ�ϵ�ʵ�����꣬������ͼ�ϵ�����
	 * <li>��������������ϰ���С��һ����룬�õ��ľ��������ʵ����</b>
	 * 
	 * @param iX
	 *            �����������
	 * @param iY
	 *            ������������
	 * @return ����������ϰ������һ��ľ��룬����һ��������������������ϰ���С��һ��ľ��룬�򷵻ؾ����ϰ����±�Ե��ʵ�ʾ��롣
	 */
	public final int howFarIsBarrierInMyTop(int iX, int iY) {
		int iRes = mapLogicNative.howFarIsBarrierInMyTop(iX, iY,
				iTestTileNumInTop, 8 & iFlagObturate, 4 & iFlagObturate);
		if (iERROR_IN_C == iRes) {
			Log.e(strTag, "�����㲻�ܴ��ڵ�ͼ֮�����߻��ͼ֮����ұ�" + "�����㵱ǰX����Ϊ��" + iX);
			throw new IllegalArgumentException("����������Ƿ�");
		} else
			return iRes;
	}

	/**
	 * ��ȡ��ͼ�ܸ߶ȣ�������Ϊ��λ��������Ļ������֪
	 * 
	 * @return ��ͼ�ܸ߶�
	 */
	public int getMapHeightByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			System.out.println("�쳣��Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			throw new IllegalAccessError("��ͼ������δ��ɣ��Ƿ����ʸ�ֵ");
		}
		return iMapHeight;
	}

	/**
	 * ��ȡ��ͼͼԪ�߳���������Ϊ��λ��������Ļ������֪
	 * 
	 * @return ��ͼͼԪ�߳�
	 */
	public int getTileSideLengthByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			System.out.println("�쳣��Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			throw new IllegalAccessError("��ͼ������δ��ɣ��Ƿ����ʸ�ֵ");
		}
		return domainData.iViewGridSideLength;
	}

	/**
	 * ��ȡ��ͼ��߶ȣ�������Ϊ��λ��������Ļ������֪
	 * 
	 * @return ��ͼ��߶�
	 */
	public int getMapWidthByPixels() {
		if (!bLogicConfirmed) {
			Log.e(strTag, "Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			System.out.println("�쳣��Ӧ�ȵ���ͼ������ɲ��ܻ�ȡ��ֵ");
			throw new IllegalAccessError("��ͼ������δ��ɣ��Ƿ����ʸ�ֵ");
		}

		return iMapWidth;
	}

	/**
	 * ��ȡ��ͼ�������X��ƫ����
	 * 
	 * @return ��ͼ�������X��ƫ����, iOffsetX
	 * @author Jayvee
	 */
	public int getiOffsetX() {
		return iCurX;
	}

	/**
	 * ��ȡ��ͼ�������Y��ƫ����
	 * 
	 * @return ��ͼ�������Y��ƫ����, iOffsetY
	 * @author Jayvee
	 */
	public int getiOffsetY() {
		return iCurY;
	}

	/**
	 * ���õ�ͼ�߽����ԣ������Ϊ���ϰ�������ͨ����������Ϊ���ϰ�����ͨ����Ĭ��Ϊ�ĸ�����ȫ��ա�
	 * 
	 * @param iObturate
	 *            ��ձ�ʶ��ȡֵΪ���ڵ���0��С�ڵ���15��Ϊ��λ����������
	 *            ˳��Ӹ�λ����λ���α�ʶ��ͼ�������ҵķ���ԣ�1��ʶ��գ�0��ʶ����
	 */
	public void setObturate(int iObturate) {
		if (iObturate > 15 || iObturate < 0) {
			Log.e(strTag, "��ձ�ʶ��ΧӦ���ڵ���0��С�ڵ���15������ǰֵΪ��" + iObturate);
			System.out.println("�쳣����ձ�ʶ��ΧӦ���ڵ���0��С�ڵ���15������ǰֵΪ��" + iObturate);
			throw new IllegalArgumentException("��ձ�ʶ��Χ���Ϸ�");
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
	 * ���ü������ϰ�ʱ��չ�ĵ�ͼ����Ŀ��Ĭ��Ϊ1��<br>
	 * ���飺�������Ҫ��ȷ����ͼ�ϰ������Ļ����������ã�����Ĭ�ϼ���
	 * 
	 * @param iTestTileNumInTop
	 *            ����Ϸ��ϰ�ʱ����չ����
	 * @param iTestTileNumInBottom
	 *            ����·��ϰ�ʱ����չ����
	 * @param iTestTileNumInLeft
	 *            ������ϰ�ʱ����չ����
	 * @param iTestTileNumInRight
	 *            ����ҷ��ϰ�ʱ����չ����
	 */
	public void setTestDistance(int iTestTileNumInTop,
			int iTestTileNumInBottom, int iTestTileNumInLeft,
			int iTestTileNumInRight) {
		if (iTestTileNumInBottom <= 0 || iTestTileNumInLeft <= 0
				|| iTestTileNumInRight <= 0 || iTestTileNumInTop <= 0) {
			Log.e(strTag, "�Ƿ��������ϰ������չ����Ӧ�ô��ڵ���1");
			throw new IllegalArgumentException("�ϰ������չ�����Ƿ�");
		}
		this.iTestTileNumInBottom = iTestTileNumInBottom;
		this.iTestTileNumInLeft = iTestTileNumInLeft;
		this.iTestTileNumInRight = iTestTileNumInRight;
		this.iTestTileNumInTop = iTestTileNumInTop;
	}

	/**
	 * ���������˺���Ϣ�Ķ�������
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
		String str = "____________________\n" + "MapLogic:\n" + "��ʼ�У�"
				+ iInitColumn + "  ��ʼ�У�" + iInitRow + "\n��ʼ�����������"
				+ iCol_StartDraw_Max + "  ��ʼ�����������" + iRow_StartDraw_Max
				+ "\n�ƶ�һ�����貽����" + iStepsPerTile + "  һ��������������" + iPixelsPerStep
				+ "\nԶ��ͼ�ƶ��ٶȣ�����ڽ���ͼ����" + fPerspectiveVelocity + "  һ��������������"
				+ iPerspectiveStep;
		return str;
	}

	/**
	 * �ڵ�ͼָ��λ�ã����ĳ<b>�����߼�</b>{@link YABaseDomainLogic}ռ���˸�λ��<br>
	 * <hr>
	 * ע��
	 * <li>�÷�����Ҫ�ڵ�ͼ��ͼ������Ļ֮����ܵ���<br>
	 * <li>Ӧ�ñ�֤�����iX��iY����Ϸ����ڵ�ͼ�߼�֮�ڣ�<br>
	 * <li>�����ǵ������߼���Ӧ�����뿪��λ�õ�ʱ�����
	 * {@link #removeMark(int, int, YABaseDomainLogic)}�Ƴ��ñ��
	 * 
	 * @param iX
	 *            �����߼��ڵ�ͼ������λ�õĺ����꣨������ͼ���꣬���߼����꣩
	 * @param iY
	 *            �����߼��ڵ�ͼ������λ�õ������꣨������ͼ���꣬���߼����꣩
	 * @param domainLogic
	 *            ��Ǹø�������߼�
	 */
	@Deprecated
	public void mark(int iX, int iY, YABaseDomainLogic<?> domainLogic) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		domainData.tl_arr2[iRow][iCol].addDomainLogic(domainLogic);
	}

	/**
	 * �ڵ�ͼָ��λ�ã��Ƴ�ĳ<b>�����߼�</b>{@link YABaseDomainLogic}<br>
	 * ע��<li>�÷�����Ҫ�ڵ�ͼ��ͼ������Ļ֮����ܵ��� <li>Ӧ�ñ�֤�����iX��iY����Ϸ����ڵ�ͼ�߼�֮�ڣ�
	 * 
	 * @param iX
	 *            �����߼��ڵ�ͼ������λ�õĺ����꣨������ͼ���꣬���߼����꣩
	 * @param iY
	 *            �����߼��ڵ�ͼ������λ�õ������꣨������ͼ���꣬���߼����꣩
	 * @param domainLogic
	 *            Ҫ�Ƴ��������߼�
	 */
	@Deprecated
	public void removeMark(int iX, int iY, YABaseDomainLogic<?> domainLogic) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		domainData.tl_arr2[iRow][iCol].removeDomainLogic(domainLogic);
	}

	/**
	 * ��ȡ��ͼ��ָ��λ���ϵ�����<b>�����߼�</b>{@link YABaseDomainLogic}<br>
	 * ע��<li>�÷�����Ҫ�ڵ�ͼ��ͼ������Ļ֮����ܵ��� <li>Ӧ�ñ�֤�����iX��iY����Ϸ����ڵ�ͼ�߼�֮�ڣ� <li>
	 * ��ȡ������һ�����ϣ����ǵ��������߼�
	 * 
	 * @param iX
	 *            ָ��λ�õĺ����꣨������ͼ���꣬���߼����꣩
	 * @param iY
	 *            ָ��λ�õ������꣨������ͼ���꣬���߼����꣩
	 */
	@Deprecated
	public List<YABaseDomainLogic<?>> getMarks(int iX, int iY) {
		int iCol = iX / domainData.iViewGridSideLength;
		int iRow = iY / domainData.iViewGridSideLength;
		return domainData.tl_arr2[iRow][iCol].getDomainLogic();
	}

}
