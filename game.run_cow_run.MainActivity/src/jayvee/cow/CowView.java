package jayvee.cow;

import java.util.LinkedList;
import java.util.Queue;

import jayvee.drawbox.Boxs;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import fyy.ygame_frame.base.YABaseDomainView;
import fyy.ygame_frame.base.YDrawInformation;
import fyy.ygame_frame.base.YGameEnvironment;
import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.util.YImageUtil;
import game.run_cow_run.R;

public class CowView extends YABaseDomainView<CowData> {
	Queue<Boxs> dq = new LinkedList<Boxs>();

	public CowView(CowData domainData) {
		super(domainData);
		// TODO Auto-generated constructor stub
		for (int queue_count = 0; queue_count < 2; queue_count++) {
			Boxs b = new Boxs();
			dq.add(b);
		}

	}

	public static final int iRunImg = 0;
	public static final int iJumpImg = 1;

	public static final int iMSG_Trans_Canvas = 99284;

	protected static int iPerLength;

	private int logicX, logicY;

	private int[] iRunImgIds = new int[] { R.drawable.run_1, R.drawable.run_2,
			R.drawable.run_3, R.drawable.run_4, R.drawable.run_5,
			R.drawable.run_6, R.drawable.run_7, R.drawable.run_8,
			R.drawable.run_9, R.drawable.run_10, R.drawable.run_11,
			R.drawable.run_12 };
	private int[] iJumpImgIds = new int[] { R.drawable.jump_1,
			R.drawable.jump_2, R.drawable.jump_3, R.drawable.jump_4 };

	private Bitmap[] btmpRun;
	private Bitmap[] btmpJump;

	private int xoffset = 5;

	private int[] btmpBoxIds = new int[] { R.drawable.box_1, R.drawable.box_2,
			R.drawable.box_3, R.drawable.box_4, };
	private Bitmap[] btmpBoxs;

	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg) {
		// TODO Auto-generated method stub
		switch (iMsgKey) {

		case YGameEnvironment.BroadcastMsgKey.MSG_MAP_VIEW_LAYOUTED:
			broadcastDomain.send(
					YGameEnvironment.BroadcastMsgKey.MSG_MAP_VIEW_LAYOUTED,
					objectDetailMsg, this);
			break;
		case 339:
			logicX = ((int[]) objectDetailMsg)[0];
			logicY = ((int[]) objectDetailMsg)[1];
		default:
			break;
		}
	}

	@Override
	protected void onLoadBitmaps(Resources resources, int iWidth, int iHeight,
			int[] iMapLayoutParams) {
		// TODO Auto-generated method stub
		int iSideLength;
		// iSideLength = 3 * iTileSideLength;
		iSideLength = 3 * 30;
		// iSideLength = 100;
		iPerLength = iSideLength;
		btmpRun = YImageUtil.getBitmapArray(resources, iRunImgIds);
		btmpRun = YImageUtil.stretchImageArray(btmpRun, iSideLength);
		btmpJump = YImageUtil.getBitmapArray(resources, iJumpImgIds);
		btmpJump = YImageUtil.stretchImageArray(btmpJump, iSideLength);
		broadcastDomain.send(
				YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_VIEW_LAYOUTED,
				null, this);
		btmpBoxs = YImageUtil.getBitmapArray(resources, btmpBoxIds);
		btmpBoxs = YImageUtil.stretchImageArray(btmpBoxs, 64);
	}

	@Override
	protected void onDraw(Canvas canvas, YDrawInformation drawInformation) {
		// TODO Auto-generated method stub
		Bitmap[] btmp = null;
		switch (drawInformation.iPicKind) {
		case iRunImg:
			btmp = btmpRun;
			break;
		case iJumpImg:
			btmp = btmpJump;
			break;
		default:
			break;
		}
		canvas.drawBitmap(btmp[drawInformation.iPicIndex], drawInformation.iX,
				drawInformation.iY, null);
		Paint paint = new Paint();
		// paint.set
		paint.setColor(Color.RED);
		canvas.drawText("判断原点", logicX, logicY, paint);

		// broadcastDomain.send(CowView.iMSG_Trans_Canvas, canvas, this);
		Boxs tempbox = dq.peek();
		for (int i = 0; i < dq.size(); i++) {
			tempbox = dq.peek();

			dq.peek().addX(xoffset);// 增加偏移
			if (dq.peek().isCreated != 0) {
				canvas.drawBitmap(btmpBoxs[dq.peek().iKind], dq.peek().iX,
						dq.peek().iY, null);
			}
			tempbox = dq.poll();
			dq.add(tempbox);// 将队尾元素取出并重新塞入队头；
		}
		if (tempbox.iX > 600)// 超出了屏幕，此处600须修改（适配后）
		{
			dq.poll();
			dq.add(new Boxs());
		} else if (tempbox.iX == 100) {
			dq.add(new Boxs());
		}

	}

	@Override
	protected void onRecycleBitmaps() {
		// TODO Auto-generated method stub
		YImageUtil.recycleBitmapArray(btmpRun, btmpJump, btmpBoxs);
	}

}
