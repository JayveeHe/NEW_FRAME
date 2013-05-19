package fyy.ygame_frame.base;

import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;
import android.content.res.Resources;

/**
 * <b></b><br>
 * 
 * <p>
 * <b>概述</b>：组成<b><i>游戏实体</i></b>的三个部件之一，具体的<b>数据</b>需要继承该类得以实现。
 * 本对象实现了<b>广播接收接口 </b> {@link YIReceiver}
 * ，需要将它添加进对应的<b><i>游戏实体</i></b>的<b>广播</b> {@link YIBroadcast}中。
 * 
 * <p>
 * <b>建议</b>：一个<b><i>游戏实体</i></b>建一个包，本对象为包内对象，不必向外公开。
 * 
 * <p>
 * <b>详细</b>：本对象主要完成以下三个职责，<br>
 * <li>持久化数据：从持久化设备中保存、读取数据；
 * <li>封装数据：封装方便的“数据体”提供给<b>领域逻辑</b>{@link YABaseDomainLogic}
 * 与<b>领域视图</b> {@link YABaseDomainView} 使用；
 * <li>传递数据：<b>视图</b>和<b>逻辑</b>可以将要传递给对方的数据挂载在该对象上传递过去；
 * 
 * <p>
 * <b>注</b>：本对象实现了<b>广播接收接口</b>{@link YIReceiver}
 * ，它可以收听到同一<b><i>游戏实体</i></b> 其它部件的状态变化。
 * 
 * @author FeiYiyun
 * 
 */
/**
*<b>领域数据</b>
*
*<p>
*<b>概述</b>：组成<b><i>游戏实体</i></b>的三个部件之一，具体的<b>数据</b>需要继承该类得以实现。
*本对象实现了<b>广播接收接口 </b> {@link YIReceiver}，
*需要将它添加进对应的<b><i>游戏实体</i></b>的<b>广播</b> {@link YIBroadcast}中。
*
*<p>
*<b>建议</b>：一个<b><i>游戏实体</i></b>建一个包，本对象为公开对象。
*
*<p>
*<b>详细</b>：本对象主要有以下三个职责，<br>
*<li>持久化数据：从持久化设备中保存、读取数据；
*<li>封装数据：封装方便的“数据体”提供给<b>领域逻辑</b>{@link YABaseDomainLogic}
 * 与<b>领域视图</b> {@link YABaseDomainView} 使用；
 * <li>传递数据：<b>视图</b>和<b>逻辑</b>可以将要传递给对方的数据挂载在该对象上传递过去。
*<p>
*<b>注</b>：
*本对象实现了<b>广播接收接口</b>{@link YIReceiver}
*，它可以收听到同一<b><i>游戏实体</i></b> 其它部件的状态变化。
*<p>
*
*@author FeiYiyun
*
*/
public abstract class YABaseDomainData implements YIReceiver
{

	/**<b>领域广播</b>：本对象实现了<b>广播接收接口</b>{@link YIReceiver}，
	 *这是它收听的<b>广播</b>{@link YIBroadcast}。通过该广播，本对象可以与属于同一个领域实体
	 *的其他两部件——<b>领域逻辑</b>{@link YABaseDomainLogic}、 <b>领域视图</b>
	 *{@link YABaseDomainView} 实时通信，发布自己的状态、行为变化。*/
	protected YIBroadcast broadcastDomain;

	/**<b><i>游戏实体</i>编号</b>：<li>为对象标识，不是类标识，同一个类有多个实例，这些实例的编号
	 *不应相同；
	 *<li>同一<b><i>游戏实体</i></b>不同部件的都持有相同的编号；*/
	public final int iID;

	/**@param iID <b><i>游戏实体</i></b>的<b>编号</b>{@link #iID}*/
	protected YABaseDomainData(int iID)
	{
		this.iID = iID;
	}

	/**
	*@param iID <b><i>游戏实体</i></b>的<b>编号</b>{@link #iID}
	*@param resources 资源
	*@param strAssetsFileName assets文件夹下的文件名
	*/
	protected YABaseDomainData(int iID, Resources resources,
			String strAssetsFileName)
	{
		this(iID);
	}

	public final void setBroadcast(YIBroadcast broadcast)
	{
		this.broadcastDomain = broadcast;
	}

}
