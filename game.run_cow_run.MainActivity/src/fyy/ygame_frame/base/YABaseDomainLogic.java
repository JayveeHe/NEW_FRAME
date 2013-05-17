package fyy.ygame_frame.base;

import java.util.Map;

import android.os.Handler;
import android.util.Log;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;

/**
*<b>领域逻辑</b>
*
*<p>
*<b>概述</b>：组成<b><i>游戏实体</i></b>的三个部件之一，具体的<b>逻辑</b>需要继承该类得以实现。
*被<b>游戏逻辑</b>{@link YAGameLogic}管理，为了能被纳入管理，需要通过
*{@link YAGameLogic#addDomainLogic(YABaseDomainLogic...)}
*添加本对象。此外，本对象实现了<b>广播接收接口</b>{@link YIReceiver}，需要将它添加进
*对应的<b><i>游戏实体</i></b>的<b>广播</b>{@link YIBroadcast}中。 <br>
*同时，该对象也是<b><i>游戏实体</i></b>之间交流的入口。
*
*<p>
*<b>建议</b>：一个<b><i>游戏实体</i></b>建一个包，本对象为公开对象。
*
*<p>
*<b>详细</b>：本对象主要有以下三个职责，<br>
*<li>处理请求：参见{@link #onDealRequest(YRequest)}；
*<li>提交任务：参见{@link #onSubmitCurrentTasks()}；
*<li>发送请求：参见{@link #onSendRequests(Map)};
*
*<p>
*<b>注</b>：
*<li>{@link #onDealRequest(YRequest)}和
*{@link #onSubmitCurrentTasks()}、{@link #onSendRequests(Map)}
*是被框架异步调用的。
*<li>
*该对象由于实现了<b>广播接收接口</b>{@link YIReceiver}，
*它可以收听到其它<b>逻辑</b>以及同一<b><i>游戏实体</i></b>其它部件的状态变化。
*
*@author FeiYiyun
*
*@param <D> <b>领域数据</b>{@link YABaseDomainData}或其子类
*/
public abstract class YABaseDomainLogic<D extends YABaseDomainData> implements YIReceiver
{
	private static final String strTag = "YABaseDomainLogic";

	/**<b>数据</b>：与该逻辑关联的<b>领域数据</b>{@link YABaseDomainData}。*/
	protected final D domainData;

	/**
	 * <b><i>游戏实体</i>编号</b>：<li>为对象标识，不是类标识，同一个类有多个实例，
	 * 这些实例的编号不应相同； <li>
	 * 同一<b><i>游戏实体</i></b>不同部件的标号是一致的。
	 */
	public final int iID;

	/**<b>领域广播</b>：本对象收听的<b><i>游戏实体</i></b><b>广播</b>{@link YIBroadcast}
	 *，用于发送自己的状态变化给其他部件。*/
	protected YIBroadcast broadcastDomain;

	/**<b>逻辑广播</b>：本对象收听的<b><i>逻辑</i></b><b>广播</b>{@link YIBroadcast}
	 * ，用于向其他<b>领域逻辑</b> {@link YABaseDomainLogic}发送自己的状态变化*/
	protected YIBroadcast broadcastLogic;

	/**<b>消息句柄</b>：向<b>游戏逻辑</b>{@link YAGameLogic}发送消息的句柄*/
	protected Handler handlerGameLogic;

	// 防止视图对逻辑的数据产生干扰，所以交付的绘图信息是一个副本。
	final YDrawInfoForm drawInfoForm;

	/**
	*@param domainData  与之关联的<b>领域数据</b>{@link #domainData}，不可为空。
	*/
	public YABaseDomainLogic(D domainData)
	{
		if (null == domainData)
		{
			Log.e(strTag, "与本对象相关的数据——domainData不能为空");
			throw new NullPointerException("与本对象相关的数据——domainData为空");
		}
		this.domainData = domainData;
		this.iID = this.domainData.iID;
		this.drawInfoForm = new YDrawInfoForm();
	}

	protected void initializeDrawInfoForm(YDrawInfoForm drawInfoForm)
	{

	}
	
	//@Override
	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg)
	{
		switch (iMsgKey)
		{
		case YGameEnvironment.BroadcastMsgKey.MSG_DOMAIN_FINISHED:
			initializeDrawInfoForm(drawInfoForm);
			break;

		default:
			break;
		}
	}

	/**向框架或其它<b>领域逻辑</b>{@link YABaseDomainLogic}发送<b>请求</b>{@link YRequest} ，
	 *<b>请求</b>应封装在msg.what中，以消息的方式通过{@link #handlerGameLogic}发送出去
	*@param domainLogics 逻辑列表
	*/
	protected abstract void onSendRequests(Map<Integer, YABaseDomainLogic<?>> domainLogics);

	/**向框架提交本周期要处理的<b>任务</b>列表{@link YITask}
	*@return <b>任务</b>列表
	*/
	protected abstract YITask[] onSubmitCurrentTasks();

	/**处理其他<b>领域逻辑</b>{@link YABaseDomainLogic}发来的<b>请求</b>
	 *{@link YRequest} 和玩家发来的<b>请求</b>，处理完成后建议做相应的记录，
	 *以备向框架交付<b>任务</b> {@link YITask}的时候作为依据
	*@param request 收到的<b>请求</b>
	*@return <b>请求</b>处理后的结果码
	*/
	protected abstract int onDealRequest(YRequest request);

	public final void setBroadcast(YIBroadcast broadcast)
	{
		this.broadcastDomain = broadcast;
	}

}
