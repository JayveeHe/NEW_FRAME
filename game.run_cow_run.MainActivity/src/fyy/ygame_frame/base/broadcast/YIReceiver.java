package fyy.ygame_frame.base.broadcast;

/**
*<b>广播接收接口</b>
*
*<p>
*<b>概述</b>：
*收听<b>广播接口</b>{@link YIBroadcast}发来的消息。
*
*<p>
*<b>注</b>：
*收听到的消息来自于处于同一个<b>广播接口</b>{@link YIBroadcast}的其他<b>广播接收接口</b>，
*其内容应为其他<b>接收接口</b>的状态变化。
*
*@author FeiYiyun
*
*/
public interface YIReceiver
{
	/**当接收到<b>广播接口</b>{@link YIBroadcast}发来的消息时，该函数被回调。
	*@param iMsgKey 消息键值
	*@param objectDetailMsg 消息详细内容
	*/
	void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg);

	/**设置收听的<b>广播接口</b>{@link YIBroadcast}
	*@param broadcast 收听的<b>广播接口</b>
	*/
	void setBroadcast(YIBroadcast broadcast);
}
