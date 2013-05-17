package fyy.ygame_simple_map;

import java.util.Map;

import android.util.Log;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.base.YGameView;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;

/**
*<b>远景图逻辑</b>
*
*<p>
*<b>概述</b>：
*控制多个<b>远景图层</b>{@link YPerspective}的行为，如移动、暂停等。
*所谓<b>远景图</b>指的是游戏场景中远处的背景，
*如天空、云彩、山等。使用<b>远景图</b>可以渲染更好的画面效果。
*
*<p>
*<b>注</b>：
*设置<b>远景图层</b>移动速度应该遵循“自近至远速度由快到慢”的原则。
*
*@author FeiYiyun
*
*/
public class YPerspectivesLogic extends YABaseDomainLogic<YPerspectivesData>
{
	private static final String strTag = "YPerspectivesLogic";

	private YPerspective[] perspectives;

	private MoveTask[] moveTasks;
	private YState state;

	/**<b>远景图起始绘制点横坐标</b>：所谓“绘制点”指的是从<b>远景图</b>
	*{@link YPerspective}的哪点开始绘制*/
	private int[] iXs;

	public YPerspectivesLogic(YPerspectivesData domainData)
	{
		super(domainData);
		perspectives = domainData.perspectives;
		state = YState.NO_SCROLL;
		moveTasks = new MoveTask[]
		{ new MoveTask() };
	}

	@Override
	protected void initializeDrawInfoForm(YDrawInfoForm drawInfoForm)
	{
		super.initializeDrawInfoForm(drawInfoForm);
		iXs = new int[perspectives.length];
		for (int i = 0; i < perspectives.length; i++)
			iXs[i] = perspectives[i].iInitX;
		drawInfoForm.objExtra = iXs;
	}

	/**设置<b>远景图层</b>“比例步长”数组，即<b>远景图层</b>移动一次所移动的“比例长度”
	*（该比例相对于<b>游戏视图</b>{@link YGameView}宽度）。<br>
	*<b>注</b>：<li>数组元素应与<b>远景图数据</b>{@link YPerspectivesData}中的
	*<b>远景图层</b>{@link YPerspective}一一对应。
	*<li>比例步长范围应为大于0，小于1。
	*@param fStepLengths 比例步长数组
	*/
	public void setStepLengths(float[] fStepLengths)
	{
		if (fStepLengths.length != perspectives.length)
		{
			Log.e(strTag, "比例步长应该一一对应" + "于相应的远景图层，" + "而此处比例步长数组长度为："
					+ fStepLengths.length + "，远景图层数目为：" + perspectives.length);
			throw new IllegalArgumentException("比例步长数组长度与远景图层数目不对应");
		}

		for (int i = 0; i < perspectives.length; i++)
		{
			if (fStepLengths[i] <= 0 || fStepLengths[i] >= 1)
			{
				Log.e(strTag, "比例步长应该大于0，小于1");
				throw new IllegalArgumentException("比例步长超出范围");
			}

			perspectives[i].setStepLenght(fStepLengths[i]);
		}
	}

	/**获取<b>远景图层</b>“比例步长”数组。“比例步长”指<b>远景图层</b>移动一次所移动的
	*“比例长度”（该比例相对于<b>游戏视图</b>{@link YGameView}宽度）。
	*@return 比例步长数组
	*/
	public float[] getStepLengths()
	{
		float[] fStepLengths = new float[perspectives.length];
		for (int i = 0; i < perspectives.length; i++)
			fStepLengths[i] = perspectives[i].fStepLength;
		return fStepLengths;
	}

	@Override
	protected void onSendRequests(Map<Integer, YABaseDomainLogic<?>> domainLogics)
	{
		// TODO
	}

	@Override
	protected YITask[] onSubmitCurrentTasks()
	{
		switch (state)
		{
		case LEFT_SCROLL:
		{
			moveTasks[0].bLeft = true;
			return moveTasks;
		}

		case RIGHT_SCROLL:
		{
			moveTasks[0].bLeft = false;
			return moveTasks;
		}

		case NO_SCROLL:
		default:
			return null;
		}
	}

	@Override
	protected int onDealRequest(YRequest request)
	{
		switch (request.iKey)
		{
		case YPerspectivesConstant.RequestKey.LEFT_SCROLL:
			if (request.bStart)
				state = YState.LEFT_SCROLL;
			else
				state = YState.NO_SCROLL;
			break;

		case YPerspectivesConstant.RequestKey.RIGHT_SCROLL:
			if (request.bStart)
				state = YState.RIGHT_SCROLL;
			else
				state = YState.NO_SCROLL;

		default:
			break;
		}
		return 0;
	}

	private static enum YState
	{
		NO_SCROLL, LEFT_SCROLL, RIGHT_SCROLL;
	}

	private class MoveTask implements YITask
	{
		private boolean bLeft = true;
		private int iCountSum = 1;
		private int iCount;

	//	@Override
		public void execute(YDrawInfoForm drawInfoForm,
				Map<Integer, YABaseDomainLogic<?>> domainLogics)
		{
			if (iCountSum != (iCount = iCount % iCountSum + 1))
				return;

			int[] iTempXs = new int[perspectives.length];
			for (int i = 0; i < perspectives.length; i++)
				if (bLeft)
				{
					iXs[i] += perspectives[i].iStepLength;
					if (iXs[i] >= domainData.iViewWidth)
						iXs[i] -= domainData.iViewWidth ;
					iTempXs[i] = iXs[i];
				} else
				{
					iXs[i] -= perspectives[i].iStepLength;
					if (iXs[i] < 0)
						iXs[i] += domainData.iViewWidth;
					iTempXs[i] = iXs[i];
				}
			if(iTempXs[0] == 25)
			{
				System.out.println("fsdfsd" + ((int[]) (drawInfoForm.objExtra))[0]);
			}
			drawInfoForm.objExtra = iTempXs;
		}
	}

}
