package fyy.ygame_frame.base;

import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;

/**
*<b>领域视图</b>
*
*<p>
*<b>概述</b>：组成<b><i>游戏实体</i></b>的三个部件之一，具体的<b>视图</b>需要继承该类得
*以实现。被<b>游戏视图</b>{@link YGameView} 管理，
*为了能被纳入管理，需要通过 {@link YGameView#addDomainView(YABaseDomainView...)}添加本对象。
*此外，本对象实现了<b>广播接收接口</b>{@link YIReceiver}，需要将它添加进对应的<b><i>游戏实体
*</i></b>的<b>广播</b>{@link YIBroadcast}中。
*
*<p>
*<b>建议</b>：一个<b><i>游戏实体</i></b>建一个包，本对象为公开对象。
*
*<p>
*<b>详细</b>：本对象主要有以下三个职责，<br>
*<li>适配屏幕：参见{@link #onLoadBitmaps(Resources, int, int, int)}；
*<li>绘图：参见{@link #onDraw(Canvas, YDrawInformation)}；
*<li>回收位图：参见{@link #onRecycleBitmaps()};
*
*<p>
*<b>注</b>：该对象由于实现了<b>广播接收接口</b>{@link YIReceiver}，它可以收听到其它<b>视图</b>
*以及同一<b><i>游戏实体</i></b>其它部件的状态变化。
*
*@author FeiYiyun
*
*@param <D> <b>领域数据</b>{@link YABaseDomainData}或其子类
*/
public abstract class YABaseDomainView<D extends YABaseDomainData> implements YIReceiver
{
	private static final String strTag = "YABaseDomainView";

	/**<b>领域广播</b>：本对象收听的<b><i>游戏实体</i></b><b>广播</b>{@link YIBroadcast}，
	 * 用于发送自己的状态变化给其他部件。*/
	protected YIBroadcast broadcastDomain;

	/**<b>视图广播</b>：本对象收听的<b><i>视图</i></b><b>广播</b>{@link YIBroadcast}，
	 * 用于向其他<b>领域视图</b>{@link YABaseDomainView}发送自己的状态变化。*/
	protected YIBroadcast broadcastView;

	/**<b>数据</b>：与该视图关联的<b>领域数据</b>{@link YABaseDomainData}*/
	final protected D domainData;

	// 视图标号， 与逻辑一致
	final int iID;

	/**
	 * @param domainData
	 *                关联的<b>领域数据</b>{@link YABaseDomainData}
	 */
	public YABaseDomainView(D domainData)
	{
		if (null == domainData)
		{
			Log.e(strTag, "与本对象相关的数据——domainData不能为空");
			throw new NullPointerException("与本对象相关的数据——domainData为空");
		}
		this.domainData = domainData;
		this.iID = this.domainData.iID;
	}

	/**加载位图资源，屏幕适配可在此完成。应该留意<b>iMapLayoutParams</b>。
	*@param resources 资源
	*@param iWidth <b>游戏视图</b>{@link YGameView}的宽度
	*@param iHeight <b>游戏视图</b>的高度
	*@param iMapLayoutParams 由“地图”布局完成后，进一步确定的布局参数，用于其他
	*<b>领域视图</b>{@link YABaseDomainView}作屏幕适配时的参考。它的具体
	*数值跟游戏中采用何种“地图”有关。<br>
	*所谓“地图”亦为<b>领域视图</b>的子类，它通常应该是第一个添加到<b>游戏视图</b>
	*{@link YGameView}中的<b>领域视图</b>。如果它布局完成后并且有必要的话，它应该负责填充
	*该参数。否则该参数为空。<br>
	*下面是一个作为“地图”的<b>领域视图</b>如何填充该参数示例代码：<br>
	*/
	protected abstract void onLoadBitmaps(Resources resources, int iWidth, int iHeight,
			int[] iMapLayoutParams);

	/**绘图，在画布上根据<b>绘图信息</b>{@link YDrawInformation}绘制画面
	*@param canvas 画布
	*@param drawInformation <b>绘图信息</b>{@link YDrawInformation}
	*/
	protected abstract void onDraw(Canvas canvas, YDrawInformation drawInformation);

	/**回收各个位图对象
	*/
	protected abstract void onRecycleBitmaps();

	public final void setBroadcast(YIBroadcast broadcast)
	{
		this.broadcastDomain = broadcast;
	}
}
