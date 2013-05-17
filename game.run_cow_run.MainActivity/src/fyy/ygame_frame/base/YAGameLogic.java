package fyy.ygame_frame.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
*<b>游戏逻辑</b>
*
*<p>
*<b>概述</b>：
*游戏里的<b><i>实体</i></b>（Domain）泛指任何可视化的对象：
*如地图、精灵、怪物、技能等。任何一个<b><i>实体</i></b>由三个部件构成：
*<ol>
*<li>领域数据：{@link YABaseDomainData}
*<li>领域逻辑：{@link YABaseDomainLogic}
*<li>领域视图：{@link YABaseDomainView}
*</ol>
*本对象负责管理、维护上述部件中的<b>领域逻辑</b>。
*
*<p>
*<b>详细</b>：本对象有职责如下：
*<li>每个周期到来，要求<b>领域逻辑</b>提交<b>任务</b>{@link YITask}并运行之，得到<b>绘图信息</b>
*{@link YDrawInformation} ，并将它们并成一帧的信息， 统一提交到<b>游戏视图</b> {@link YGameView}
*，完成一帧画面的绘制；
*<li>每个周期到来，通知<b>领域逻辑</b>发送<b>请求</b>；
*<li>不定时地，接收玩家通过操作发来的<b>请求</b>和游戏计算产生的<b>请求</b> {@link YRequest}，
*转交给对应的<b>领域逻辑</b>处理；<br>
*<b>运行框图如下：</b>
*<p>
*<img src="../../../YGameFrame.jpg" height="380" width="550"/>
*</p>
*
*<p>
*<b>注</b>：
*<li>一个<b>领域逻辑</b>的实例一定要通过{@link #addDomainLogic(YABaseDomainLogic...)}
*方法添加到本对象中。
*<li>本对象实现了<b>广播接口</b>{@link YIBroadcast}<br>
*
*@author FeiYiyun
*
*/
@SuppressLint(
{ "UseSparseArrays" })
public abstract class YAGameLogic implements YIBroadcast
{
	private static final String strUpdateThreadName = "更新线程";
	private final String strTag = "YAGameLogic";
	/* //哈希插入无序，目前不使用 private HashMap<Integer,
	 * YABaseDomainLogic<?, ?>> domainLogics = new
	 * HashMap<Integer, YABaseDomainLogic<?, ?>>(); /**
	 * 本对象管理的领域逻辑的列表<br> 选择LinkedHashMap基于这些考虑：
	 * <li>可以根据键值随取随用，容易找到； <li>有顺序， 将绘图信息列表传给游戏视图方便 </br>
	 * <b>???哈希表亦可、但要求游戏视图也须用哈希表存储领域视图，不知效率如何</b> */
	/**<b>领域逻辑列表</b>：被管理的<b>领域逻辑</b>{@link YABaseDomainLogic}存储在该列表中，
	*处于<b>消息线程</b>{@link #msg_thrd}与<b>更新线程</b>{@link #tmrUpdateLogic}
	*都需要对该列表进行操作，故采用了同步哈希表。*/
	private Map<Integer, YABaseDomainLogic<?>> domainLogics = new ConcurrentHashMap<Integer, YABaseDomainLogic<?>>();
	/**<b>更新周期</b>：更新游戏逻辑的周期，单位为毫秒，默认为30毫秒。*/
	private int iUpdatePeriod = 30;
	/**<b>游戏逻辑启动标识</b>：游戏逻辑是否启动的标识，默认假。*/
	private boolean bStart;
	/**<b>游戏活动</b>：承载<b>游戏视图</b>{@link YGameView}的Activity。*/
	protected YGameActivity gm_actvty;

	/**<b>消息线程</b>：运行本对象的两条线程之一，该线程负责接收请求消息，处理请求。*/
	private YMsgThread msg_thrd;
	/**<b>更新线程</b>：运行本对象的两条线程之一，该线程负责定时更新游戏逻辑。*/
	private Timer tmrUpdateLogic;
	/**<b>更新任务</b>：运行定时更新的载体*/
	private TimerTask tmrTask;

	/**<b>游戏视图消息句柄</b>：与<b>游戏视图</b>{@link YGameView}通信
	*的消息句柄。一般地，每一帧画面的<b>绘图信息</b>{@link YDrawInformation}列表将被封装在msg.obj，
	*通过该句柄传递给<b>游戏视图</b>。*/
	private Handler hndlrGameView;

	/**<b>游戏逻辑消息句柄</b>：玩家操作产生的和<b>领域逻辑</b>
	*{@link YABaseDomainLogic}定时更新产生的<b>请求</b>通过该句柄向提交。*/
	private Handler handlerGameLogic;

	/**当本对象接收到玩家操作产生的或游戏内部产生<b>请求</b>{@link YRequest}时，
	*该函数将会被回调。子类应该重写本方法，采用不同的策略去处理这些<b>请求</b>。
	*@param rqstUIorGame 来自于玩家操作产生的或游戏内部产生的<b>请求</b>
	*/
	protected abstract void onReceiveRequest(YRequest rqstUIorGame);

	/**子类应重写本方法，根据指定的<b>领域逻辑</b>{@link YABaseDomainLogic}
	*提交相应的<b>任务</b>{@link YITask}列表。
	*@param dmn_lgcKey 指定的<b>领域逻辑</b>
	*@return <b>任务</b>列表
	*/
	protected abstract YITask[] onSubmitCurrentTasks(YABaseDomainLogic<?> dmn_lgcKey);

	/**当有<b>领域逻辑</b>{@link YABaseDomainLogic}被添加时，该函数被调用。
	*如果有必要的话，子类应该通过重写本方法为新添加的<b>领域逻辑</b>做相应的初始化工作。
	*@param dmn_lgcKey 新增的<b>领域逻辑</b>
	*/
	// 这个方法的设计是基于这样的考虑，如果新增的对象都需要一个辅助对象，
	// 那么框架可以在它们被增加进来时统一的为它们添加这个辅助对象，并统一管理，而不需要
	// 它们独自管理
	protected abstract void onAddDomainLogic(YABaseDomainLogic<?> dmn_lgcKey);

	/**当有<b>领域逻辑</b>{@link YABaseDomainLogic}被移除时，该函数被调用。如果有必要的话，
	*子类应该通过重写本方法做相应的善后工作，如内存回收等……
	*@param dmn_lgcKey 被移除的<b>领域逻辑</b>
	*/
	protected abstract void onRemoveDomainLogic(YABaseDomainLogic<?> dmn_lgcKey);

	YAGameLogic()
	{

	}

	/**设置<b>游戏视图</b>{@link YGameView}的消息句柄
	*@param hndlrGameView <b>游戏视图</b>消息句柄
	*/
	final void setGameViewHandler(Handler hndlrGameView)
	{
		this.hndlrGameView = hndlrGameView;
	}

	/**启动游戏逻辑，<li>新建并启动<b>消息线程</b>{@link YAGameLogic#msg_thrd}；
	*<li>新建并启动<b>更新线程</b>{@link #tmrUpdateLogic}<br>
	*/
	synchronized final void start()
	{
		if (bStart)
		{
			Log.e(strTag, "游戏逻辑已经启动，不能再次启动。");
			throw new IllegalStateException("游戏逻辑重复启动");
		}
		bStart = true;
		msg_thrd = new YMsgThread();
		msg_thrd.start();
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			Log.e(strTag, "游戏逻辑start()函数暂停异常");
		}

		tmrUpdateLogic = new Timer(strUpdateThreadName, true);// 置为守护线程
		tmrUpdateLogic.schedule(tmrTask = new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					onUpdateLogic();
				} catch (Exception e)
				{
					e.printStackTrace();
					Log.e(strTag, "onUpdateLogic调用异常");
				}
			}
		}, 0, iUpdatePeriod);

		Log.i(strTag, "游戏逻辑启动完成");
	}

	/**退出游戏逻辑，销毁<b>消息线程</b>{@link #msg_thrd}、<b>更新线程</b>{@link #tmrUpdateLogic}。*/
	synchronized final void end()
	{
		while (!tmrTask.cancel())
			;
		tmrUpdateLogic.purge();
		tmrUpdateLogic.cancel();
		tmrTask = null;
		tmrUpdateLogic = null;

		msg_thrd.lpr.quit();
		msg_thrd.lpr = null;
		while (msg_thrd.isAlive())
			try
			{
				msg_thrd.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				Log.e(strTag, "游戏逻辑线程加入异常");
			}
		bStart = false;
		Log.i(strTag, "游戏逻辑退出完成");
	}

	/**添加<b>领域逻辑</b>{@link YABaseDomainLogic}，不可添加相同ID的对象。
	*@param domainLogics 添加的<b>领域逻辑</b>，可多个
	*/
	public final void addDomainLogic(YABaseDomainLogic<?>... domainLogics)
	{
		for (YABaseDomainLogic<?> domainLogic : domainLogics)
		{
			if (this.domainLogics.containsKey(domainLogic.iID))
			{
				Log.e(strTag, "游戏逻辑中已经存在ID为：" + domainLogic.iID + "的对象，"
						+ "不能重复添加ID相同的对象");
				throw new IllegalArgumentException("重复添加ID相同的对象");
			}
			this.domainLogics.put(domainLogic.iID, domainLogic);
			domainLogic.handlerGameLogic = this.handlerGameLogic;
			domainLogic.broadcastLogic = this;
			onAddDomainLogic(domainLogic);
		}
	}

	/**移除<b>领域逻辑</b>{@link YABaseDomainLogic}。
	*@param domainLogics 需要移除的<b>领域逻辑</b>，可多个
	*/
	public final void removeDomainLogic(YABaseDomainLogic<?>... domainLogics)
	{
		for (YABaseDomainLogic<?> domainLogic : domainLogics)
		{
			this.domainLogics.remove(domainLogic.iID);
			onRemoveDomainLogic(domainLogic);
		}
	}

	/**设置<b>游戏逻辑</b>{@link YAGameLogic}更新周期，毫秒为单位，默认为30毫秒
	*@param iUpdatePeriod 更新周期
	*/
	public final void setUpdatePeriod(int iUpdatePeriod)
	{
		if (bStart)
		{
			Log.w(strTag, "在游戏逻辑启动后，重新设置了更新周期");
			throw new IllegalStateException("目前不支持此操作");
		} else
			this.iUpdatePeriod = iUpdatePeriod;
	}

	/**获取更新周期，以毫秒为单位，默认为30毫秒
	*@return 更新周期
	*/
	public final int getUpdatePeriod()
	{
		return iUpdatePeriod;
	}

	/**更新逻辑，要求各个<b>领域逻辑</b>{@link YABaseDomainLogic}提交本周期需要执行的<b>任务</b>
	*{@link YITask}并执行之，得到<b>绘图信息</b>{@link YDrawInformation}，封装
	*在msg.obj中，交付给<b>游戏视图</b>{@link YGameView}。
	*/
	private void onUpdateLogic()
	{
		Map<Integer, YDrawInformation> mapDrawInfos = new HashMap<Integer, YDrawInformation>();
		Iterable<YABaseDomainLogic<?>> iterable = domainLogics.values();
		for (YABaseDomainLogic<?> baseDomainLogic : iterable)
		{// 轮询逻辑领域接口列表：将键值告诉子类，子类依据键值取出对应的任务列表，本对象得到任务列表后
			// 依次执行任务接口
			YITask[] tasks = onSubmitCurrentTasks(baseDomainLogic);
			if (null != tasks)
				for (YITask task : tasks)
					task.execute(baseDomainLogic.drawInfoForm, domainLogics);
			mapDrawInfos.put(baseDomainLogic.iID,
					baseDomainLogic.drawInfoForm.print());
		}
		Message msgView = Message.obtain();
		msgView.obj = mapDrawInfos;
		hndlrGameView.sendMessage(msgView);

		for (YABaseDomainLogic<?> dmn_lgc : iterable)
			// 回调每个领域逻辑接口，让其发送请求
			dmn_lgc.onSendRequests(domainLogics);
	}

	/**设置关联的<b>游戏活动</b>{@link YGameActivity}
	*@param gm_actvty 游戏活动
	*/
	final void setGameActivity(YGameActivity gm_actvty)
	{
		this.gm_actvty = gm_actvty;
	}

	public void send(int iMsgKey, Object objDetailMsg, YIReceiver receiverSend)
	{
		Iterable<YABaseDomainLogic<?>> iterable = domainLogics.values();
		for (YABaseDomainLogic<?> baseDomainLogic : iterable)
			if (baseDomainLogic != receiverSend)
				baseDomainLogic.onReceiveBroadcastMsg(iMsgKey, null);
	}

	/**
	*<b>消息线程</b>
	*
	*<p>
	*<b>概述</b>：
	*当接收玩家操作消息及游戏内部消息时，该线程被唤醒；没有消息时，该线程被挂起。该线程
	*主要负责处理封装在消息中的<b>请求</b>{@link YRequest}。
	*
	*<p>
	*<b>详细</b>：
	*接收消息，分派<b>请求</b>到对应的<b>领域逻辑</b>{@link YABaseDomainLogic}处理
	*
	*<p>
	*<b>注</b>：
	*处理<b>请求</b>和提交<b>任务</b>不在一条线程中
	*
	*@author FeiYiyun
	*/
	private class YMsgThread extends Thread
	{

		/**<b>消息线程的消息循环</b>：方便<b>游戏逻辑</b>终结时退出该线程。*/
		private Looper lpr;
		private static final String strMsgThreadName = "消息线程";

		private YMsgThread()
		{
			super();
			setName(strMsgThreadName);
		}

		@Override
		public void run()
		{
			// 建立消息队列
			Looper.prepare();
			// 获取消息循环引用，以备退出
			lpr = Looper.myLooper();
			// 将与本线程通信的消息句柄传给YGameActivity，以接受玩家操作产生的请求
			// 注：新建Handler一定要在Looper.prepare()后
			// 游戏中的领域逻辑计算产生的消息和玩家操作产生的消息，通过该消息句柄向本线程发消息。
			handlerGameLogic = new YMsgHandler(YAGameLogic.this);
			gm_actvty.setGameLogicHandler(handlerGameLogic);
			// 进入消息循环
			Looper.loop();
		}
	}

	// ???采取强引用是否合适？合适，该handler被MsgThread持有
	private static class YMsgHandler extends Handler
	{
		private YAGameLogic gameLogic;

		private YMsgHandler(YAGameLogic gameLogic)
		{
			this.gameLogic = gameLogic;
		}

		@Override
		public void handleMessage(Message msg)
		{
			gameLogic.onReceiveRequest((YRequest) msg.obj);
		}
	}

}
