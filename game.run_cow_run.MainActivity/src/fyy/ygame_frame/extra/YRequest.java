package fyy.ygame_frame.extra;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YAGameLogic;

/**
 * <b>请求</b><br>
 * <p>
 * 由UI（玩家操作）或者游戏内部自行计算得到，通过游戏逻辑句柄 {@link YAGameLogic#handlerGameLogic} 提交，
 * 并指定某个或（多个）领域逻辑对象 {@link #dmn_lgc_arrTaskExecutors}开始或结束 {@link #bStart}
 * 某任务（通过制定任务的主键 {@link #iTaskKey} ）；<br>
 * 请求的任务可能不被执行，也可能立即执行，也可能同步执行，这跟优先级{@link #iPriority}有关；<br>
 * 
 * <b>额外地，请求要封装在msg.obj中！</b>
 * 
 * @author fei yiyun
 * 
 */
/**
*<b>请求</b>
*
*<p>
*<b>概述</b>：
*来自于<b>玩家操作</b>和<b>游戏内部计算</b>，通过<b>游戏逻辑</b>
*{@link YAGameLogic}的消息句柄发往<b>游戏逻辑</b>，最后被相应的
*<b>领域逻辑</b>{@link YABaseDomainLogic}收到。
*
*<p>
*<b>注</b>：
*<li>一个<b>请求</b>针对于<b>开始</b>或<b>结束</b>某个操作，发送<b>请求</b>时应该注意这点。
*<li><b>请求</b>并不一定被处理，这要视游戏的具体情景而定；
*<li>一个<b>请求</b>并不一定对应于一项<b>任务</b>{@link YITask}。
*
*@author FeiYiyun
*
*/
public final class YRequest
{
	/**<b>请求的优先级</b>*/
	public final int iPriority;
	/** 请求的任务的主键 */
	/**<b>请求的键值</b>*/
	public final int iKey;
	/**<b>请求的接收者</b>：为<b>领域逻辑</b>{@link YABaseDomainLogic}*/
	public final YABaseDomainLogic<?>[] domainLogicReceivers;

	/**<b>请求的附加信息</b>*/
	public Object objExtra;
	/**<b>请求的附加信息</b>*/
	public int iArg1;
	/**<b>请求的附加信息</b>*/
	public int iArg2;
	/**<b>请求标识</b>：请求某项操作是开始还是结束，默认为真。*/
	public boolean bStart = true;

	/**
	*@param iPriority <b>请求</b>{@link YRequest}的优先级
	*@param iKey <b>请求</b>的键值
	*@param domainLogicReceivers <b>请求</b>接收者，可多个
	*/
	public YRequest(int iPriority, int iKey,
			YABaseDomainLogic<?>... domainLogicReceivers)
	{
		this.iPriority = iPriority;
		this.iKey = iKey;
		this.domainLogicReceivers = domainLogicReceivers;
	}

}
