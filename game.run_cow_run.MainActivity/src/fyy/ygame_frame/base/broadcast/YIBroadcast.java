package fyy.ygame_frame.base.broadcast;


/**
 * <b>广播</b>
 * <p>
 * 向<b>广播接收接口</b>广播消息
 * </p>
 * <hr>
 * 注：
 * 
 * <li><b>广播接收接口</b>通过{@link #send(int, Object, YIReceiver)}向<b>广播</b>发送消息
 * <li>例：某个<b>广播接收接口</b>向<b>广播</b>发送自己的状态、行为变化，其他添加进该<b>广播</b>
 * 的<b>广播接收接口</b>将收到该信息。（主动发送信息的这个<b>广播接收接口</b>自己不会收到自己广播的消息）
 * 
 * @author fei yiyun
 * 
 */
/**
*<b>广播接口</b>
*
*<p>
*<b>概述</b>：
*路由<b>广播接收接口</b>{@link YIReceiver}的状态变化，沟通它们之间的交流。
*
*<p>
*<b>注</b>：
*本对象的消息来自于<b>广播接收接口</b>{@link YIReceiver}。
*
*@author FeiYiyun
*
*/
public interface YIBroadcast
{
//	/**<b>消息</b>：同一<b><i>实体</i></b>的所有部件——
//	*<b>数据</b>{@link YABaseDomainData}、<b>逻辑</b>{@link YABaseDomainLogic}、
//	*<b>视图</b>{@link YABaseDomainView}建立完成。*/
//	// 即不会存在创建了父类，而子类还没有被创建的情况；
//	// 目前该消息的发出在所有广播接收接口添入广播完成后发送。
//	public final static int iMSG_AllComponentForThisCreated = 743896;
//	/**<b>消息</b>：<b>领域视图</b>{@link YABaseDomainView}布局完成。*/
//	public final static int iMSG_DomainViewLayouted = 745729;
//	/**<b>消息</b>：<b>地图视图</b>布局完成。*/
//	public final static int iMSG_MapViewLayouted = 467023;
//	/**<b>消息</b>：<b>地图逻辑</b>逻辑值确认完成。*/
//	public final static int iMSG_MapLogicConfirmed = 7496490;

	/**向<b>广播接口</b>{@link YIBroadcast}发送消息，消息应该是<b>广播接收接口</b>{@link YIReceiver}
	*的状态变化。
	*@param iMsgKey 消息键值
	*@param objDetailMsg 消息的详细信息
	*@param receiverSend 发送该消息的<b>广播接收接口</b>
	*/
	void send(int iMsgKey, Object objDetailMsg, YIReceiver receiverSend);
}
