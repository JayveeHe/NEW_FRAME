/** */
package fyy.ygame_frame.base;

import fyy.ygame_frame.base.broadcast.YIBroadcast;

/**
*<b>游戏环境</b>
*
*<p>
*<b>概述</b>：
*游戏框架中的全局对象、变量可以通过本对象得到。此外，
*该对象还定义了一些全局的常量，如<b>广播接口</b>{@link YIBroadcast}
*的<b>消息键值</b>{@link BroadcastMsgKey}。
*
*<p>
*<b>建议</b>：
*TODO
*
*<p>
*<b>详细</b>：
*TODO
*
*<p>
*<b>注</b>：
*TODO
*
*<p>
*<b>例</b>：
*TODO
*
*@author FeiYiyun
*
*/
public final class YGameEnvironment
{
	/**
	*<b>广播消息键值</b>
	*
	*<p>
	*<b>概述</b>：
	*定义了一些<b>广播接口</b>{@link YIBroadcast}发送的消息键值
	*
	*@author FeiYiyun
	*
	*/
	public final static class BroadcastMsgKey
	{
		/**<b>消息</b>：地图视图布局完成。*/
		public static final int MSG_MAP_VIEW_LAYOUTED = 10001;
		/**<b>消息</b>：地图逻辑值（如屏幕适配后的地图宽度等）确定。*/
		public static final int MSG_MAP_LOGIC_CONFIRMED = 10002;
		/**<b>消息</b>：<b>领域视图</b>{@link YABaseDomainView}布局完成。*/
		public static final int MSG_DOMAIN_VIEW_LAYOUTED = 10003;
		/**<b>消息</b>：<b><i>游戏实体</i></b>所有部件建立完成。*/
		public static final int MSG_DOMAIN_FINISHED = 10004;
	}

	private YGameEnvironment()
	{
	}
}
