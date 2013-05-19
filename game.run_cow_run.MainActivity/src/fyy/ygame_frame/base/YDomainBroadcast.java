package fyy.ygame_frame.base;

import android.util.Log;
import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;

/**
*<b>领域广播</b>
*
*<p>
*<b>概述</b>：
*实现了<b>广播接口</b>{@link YIBroadcast}，用于<b>同一个</b><b><i>游戏实体</i></b>的三个部件
*——<b>数据</b>{@link YABaseDomainData}、<b>逻辑</b>{@link YABaseDomainLogic}、<b>视图</b>
*{@link YABaseDomainView}之间的通信。
*
*<p>
*<b>注</b>：
*加入该广播的必须是<b>同一个</b>实体的三个部件。
*
*@author FeiYiyun
*
*/
public class YDomainBroadcast implements YIBroadcast
{
	private static final String strTag = "YDomainBroadcast";
	private final YIReceiver[] receivers;

	/**参数必须为同一个<b><i>游戏实体</i></b>的三个部件，不可多余或少于。
	*@param receivers 同一<b><i>游戏实体</i></b>的三个部件
	*/
	public YDomainBroadcast(YIReceiver... receivers)
	{
		if (3 != receivers.length)
		{
			Log.e(strTag, "领域广播中应该只添加对应于同一个游戏实体的三个部件，当前添加了" + receivers.length
					+ "个部件。请检查" + "是否多添加或少添加了部件。");
			throw new IllegalArgumentException("领域广播添加实体部件异常");
		}
		this.receivers = receivers;
		for (YIReceiver receiver : receivers)
		{
			receiver.setBroadcast(this);
			receiver.onReceiveBroadcastMsg(
					YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_FINISHED, null);
		}
	}

	public void send(int iMsgKey, Object objectDetailMsg, YIReceiver rcvrSend)
	{
		for (YIReceiver receiver : receivers)
			if (rcvrSend != receiver)
				receiver.onReceiveBroadcastMsg(iMsgKey, objectDetailMsg);
	}

}
