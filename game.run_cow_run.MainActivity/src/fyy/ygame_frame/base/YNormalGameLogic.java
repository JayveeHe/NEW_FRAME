package fyy.ygame_frame.base;

import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;

/**
*<b>普通游戏逻辑</b>
*
*<p>
*<b>概述</b>：
*继承自<b>游戏逻辑</b>{@link YGameView}，是一般游戏中常用逻辑。
*
*<p>
*<b>注</b>：
*应将该类名（不包括包名）设置为相应<b>游戏视图</b>的标签。
*
*@author FeiYiyun
*
*/
public final class YNormalGameLogic extends YAGameLogic
{

	@Override
	protected void onReceiveRequest(YRequest rqstUIorGame)
	{
		if (null != rqstUIorGame.domainLogicReceivers)
			// 目的对象非空，交付逻辑处理请求
			for (YABaseDomainLogic<?> dmn_lgc : rqstUIorGame.domainLogicReceivers)
				dmn_lgc.onDealRequest(rqstUIorGame);
	}

	@Override
	protected YITask[] onSubmitCurrentTasks(YABaseDomainLogic<?> dmn_lgcKey)
	{
		return dmn_lgcKey.onSubmitCurrentTasks();
	}

	@Override
	protected void onAddDomainLogic(YABaseDomainLogic<?> dmn_lgcKey)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRemoveDomainLogic(YABaseDomainLogic<?> dmn_lgcKey)
	{
		// TODO Auto-generated method stub

	}
}
