package jayvee.cow;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;
import fyy.ygame_map.logic.YMapLogic;
import fyy.ygame_map.view.YAMapView;

public class CowLogic extends YABaseDomainLogic<CowData> {

	//
	private YMapLogic mapLogic;
	private YAMapView mapView;
	private Canvas canvas;
	// 动作宏定义
	public static final int iRun = 0;
	public static final int iJump = 1;

	// 逻辑变量
	private int iCurX = 100;// 给人物的位置设置初始值
	private int iCurY = 140;
	private int iCurViewX = 0;
	private int iCurViewY = 140;
	private int iLastX = iCurX;
	private int iLastY = iCurY;
	private int damege = 100;
	private int xoffset = 0;
	private int yoffset = 0;
	private int is_being_hurt = 0;

	// 逻辑判断值——切记一定要有完整的true/false互相转换的条件！！！
	private boolean isSprite_Left_Direction = false;// 人物是否朝向左，初始值为右
	public boolean iJumpAble; // 传给外界的可否跳跃信号
	private boolean isJumping = false;// 跳跃动作过程正在进行
	private boolean isOnGround = false;
	private boolean isAttacking = false;
	private boolean isHurting = false;

	// 参数变量
	private int iPerTileLength = 100; // 适配出来的每格地图块的宽度（像素）
	private int iBodyLength = 100;// 精灵图块的适配大小
	private final double iWidthRate = 0.11f; // 人物图片的逻辑原点相对于绘图原点的X轴偏移比例
	private final double iHeightRate = 1.30f; // 人物图片的逻辑原点相对于绘图原点的Y轴偏移比例
	private float iG = 3f; // 虚拟的重力加速度常量
	private float upV = 30f;// 跳跃初期给人物施加的向上的速度
	private int xV = 0;
	public int iState;

	public CowLogic(CowData domainData, YMapLogic mapLogic) {
		super(domainData);
		// TODO Auto-generated constructor stub
		this.mapLogic = mapLogic;
	}

	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg) {
		// TODO Auto-generated method stub
		switch (iMsgKey) {
		case YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED:
			iBodyLength = CowView.iPerLength;
			iG = 3f / 214f * iBodyLength;
			upV = 30f / 214f * iBodyLength;
			Log.i("广播信息", "收到了适配信息");
			break;
		case CowView.iMSG_Trans_Canvas:
			canvas = (Canvas) objectDetailMsg;
		default:
			break;
		}
	}

	@Override
	protected void onSendRequests(
			java.util.Map<Integer, fyy.ygame_frame.base.YABaseDomainLogic<?>> domainLogics) {
	};


	YITask drawtask = new YITask() {

		int count = 0;
		int period = 11;
		int jumpcount = 0;

		public void execute(YDrawInfoForm drawInfoForm,
				Map<Integer, YABaseDomainLogic<?>> domainLogics) {
			if (isOnGround && !isJumping) {
				count = count % period;
				// drawInfoBuilder.iX = iCurViewX;
				// drawInfoBuilder.iY = iCurViewY;
				drawInfoForm.iPicIndex = count;
				drawInfoForm.iPicKind = CowView.iRunImg;
				count++;

			} else if (isOnGround && isJumping) {
				drawInfoForm.iPicIndex = 0;
				drawInfoForm.iPicKind = CowView.iJumpImg;
			} else
			// if (!isOnGround && isJumping) {
			// drawInfoBuilder.iPicIndex = 1;
			// drawInfoBuilder.iPicKind = CowView.iJumpImg;
			// } else
			{
				jumpcount = jumpcount % 1;
				drawInfoForm.iPicIndex = jumpcount + 2;
				drawInfoForm.iPicKind = CowLogic.iJump;
			}
		}
	};

	YITask YTask = new YITask() {
		int count = 0;
		int fall_count = 0;
		private int iPeriod = 1;
		private final static int PERIOD = 2;

		public void execute(YDrawInfoForm drawInfoForm,
				Map<Integer, YABaseDomainLogic<?>> domainLogics) {
			// TODO Auto-generated method stub
			// if (PERIOD != (iPeriod = iPeriod % PERIOD + 1))
			// return;
			// iCurX +=mapLogic.getiOffsetX();
			// iCurViewX = iCurX - mapLogic.getiOffsetX();
			int iCurColumn = (int) Math
					.floor((iCurX + iBodyLength * iWidthRate - mapLogic
							.getiOffsetX()));
			int iCurRow = (int) Math.floor((iCurY + iBodyLength * iHeightRate));
			// Paint paint = new Paint();
			// paint.setColor(Color.RED);
			// canvas.drawPoint(iCurColumn, iCurRow, paint);
			int[] para = new int[] { iCurColumn, iCurRow };
			broadcastDomain.send(339, para, CowLogic.this);
			// ////////////、、、、、、、、、、、、、、、、、、、、、、、、//
			// int ihowFarIsBarrierInMyBottom = mapLogic
			// .howFarIsBarrierInMyBottom(iCurColumn, iCurRow);
			// System.out.println("右边障碍多远：===="
			// + mapLogic.howFarIsBarrierInMyRight(iCurColumn, iCurRow));
			// System.out.println("距离地面多远：===="
			// + mapLogic.howFarIsBarrierInMyBottom(iCurColumn, iCurRow));
			// if (isJumping) {
			// iCurY -= upV - iG * count;
			// count++;
			// isOnGround = false;
			// if ((upV - iG * count) <= 0)// 上升阶段结束
			// {
			// count = 0;
			// isJumping = false;
			// }
			// } else if (ihowFarIsBarrierInMyBottom > 0)// 下落状态且悬空
			// {
			// isOnGround = false;
			// if (ihowFarIsBarrierInMyBottom < 1.3 * mapLogic
			// .getTileSideLengthByPixels()
			// && (iG * fall_count) > (mapLogic
			// .getTileSideLengthByPixels() / 2)) {
			// iCurY += ihowFarIsBarrierInMyBottom;// 慢慢接近地面
			// // System.out.println("daolezheyibu"+"======"+iG*fall_count);
			// } else {
			// iCurY += iG * fall_count;
			// fall_count++;
			// }
			// } else {
			// if (ihowFarIsBarrierInMyBottom < 0)
			// iCurY += ihowFarIsBarrierInMyBottom;// 防止小范围嵌入
			// isOnGround = true;
			// fall_count = 0;
			// }

			// if (isJumping)
			// {
			// iCurY -=5;
			// fall_count++;
			// if (fall_count>40)
			// isJumping = false;
			// }else if (mapLogic.howFarIsBarrierInMyBottom(iCurColumn,
			// iCurRow)<10)
			// {
			// isOnGround = true;
			//
			// }else iCurY+=5;
			// iCurViewY = iCurY + mapLogic.getiOffsetY();
			// drawInfoBuilder.iY = iCurViewY;

			if (isJumping) {
				iCurY -= upV - iG * count;
				count++;
				isOnGround = false;
				if ((upV - iG * count) <= 0)// 上升阶段结束
				{
					count = 0;
					isJumping = false;

				}
			} else // 进行下落的处理
			{
				int howfar = mapLogic.howFarIsBarrierInMyBottom(iCurColumn,
						iCurRow);
				if (howfar > 0) {
					if (howfar < 0.5 * mapLogic.getTileSideLengthByPixels()) {
						iCurY += howfar;
					} else {
						iCurY += iG * fall_count;
						fall_count++;
					}
				} else {
					isOnGround = true;
					fall_count = 0;
					iCurY -= howfar;// 防止小范围的嵌入障碍
				}
			}
			iCurViewY = iCurY;
			drawInfoForm.iY = iCurViewY;
			drawInfoForm.iX = iCurViewX;
		}
	};

	@Override
	protected YITask[] onSubmitCurrentTasks() {
		// TODO Auto-generated method stub
		System.out.println(isOnGround);
		return new YITask[] { drawtask, YTask };
	}

	@Override
	protected int onDealRequest(YRequest request) {
		// TODO Auto-generated method stub
		switch (request.iKey) {
		case CowLogic.iRun:

			break;
		case CowLogic.iJump:

			if (isOnGround) {
				isJumping = true;
			}

			break;
		default:
			break;
		}
		return 0;
	}

}
