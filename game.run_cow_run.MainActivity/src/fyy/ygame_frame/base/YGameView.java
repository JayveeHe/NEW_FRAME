package fyy.ygame_frame.base;

import java.util.ArrayList;
import java.util.Map;

import fyy.ygame_frame.base.broadcast.YIBroadcast;
import fyy.ygame_frame.base.broadcast.YIReceiver;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
*本对象负责管理、维护上述部件中的<b>领域视图</b>。
*
*<p>
*<b>详细</b>：本对象有职责如下：
<li>接收<b>游戏逻辑</b>{@link YAGameLogic}发来的<b>绘图信息</b>{@link YDrawInformation}列表，
*分派给相应的<b>领域视图</b>绘制，以完成一帧画面的绘制；
*<li>处理android系统的回调，控制引擎中各对象相应的流程，如果<b>游戏逻辑</b>的开启、终结
* 、暂停，各个<b>领域视图</b>回收、加载位图等。<br>
*<b>运行框图如下：</b>
*<p>
*<img src="../../../YGameFrame.jpg" height="380" width="550"/>
*</p>
*
*<p>
*<b>注</b>：
*<li>一个<b>领域视图</b>的实例一定要通过{@link #addDomainView(YABaseDomainView...)}方法添加到本对象中。
*<li>本对象实现了<b>广播接口</b>{@link YIBroadcast}<br>
*
*@author FeiYiyun
*
*/
public class YGameView extends SurfaceView implements SurfaceHolder.Callback, YIBroadcast
{
	/** 日志标签 */
	private static final String strTag = "YAGameView";
	/** 游戏视图管理的领域视图列表 */
	/**<b>领域视图列表</b>：本对象管理的<b>领域视图</b>{@link YABaseDomainView}列表*/
	// 必须用有顺序的结构，否则图像可能被遮盖
	private ArrayList<YABaseDomainView<?>> domainViews = new ArrayList<YABaseDomainView<?>>();
	/**<b>游戏视图持有者</b>*/
	private SurfaceHolder surfaceHolder;
	/** 该模板中含的隐藏线程，该线程接收<b>游戏视图</b>{@link YAGameLogic}发来的绘图消息，并绘图 */
	/**<b>绘图线程</b>：接收<b>游戏逻辑</b>{@link YAGameLogic}发来的绘图消息，
	 *按序分派给各个<b>领域视图</b>{@link YABaseDomainView}进行绘制*/
	private YDrawThread drawThread;
	/**<b>测试信息画笔</b>：在<b>游戏视图</b>{@link YGameView}上输出测试信息*/
	protected Paint paintTest;
	/**<b>关联逻辑</b>：与之相关联的<b>游戏逻辑</b>{@link YAGameLogic}*/
	private YAGameLogic gameLogic;

	/**<b>地图布局参数</b>：由“地图”布局完成后，进一步确定的布局参数，用于其他
	*<b>领域视图</b>{@link YABaseDomainView}作屏幕适配时的参考。<br>
	*<b>详细</b>：所谓“地图”亦为<b>领域视图</b>的子类，它通常应该是第一个添加到<b>游戏视图</b>
	*{@link YGameView}中的<b>领域视图</b>。如果它布局完成后并且有必要的话，它应该负责填充
	*该参数。否则该参数为空。<br>
	*<b>例</b>：*/
	private int[] iMapLayoutParams;

	/**
	*@param context 上下文
	*@param attrs 属性
	*/
	public YGameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
		Log.i(strTag, "游戏视图建立完成");
	}

	/**初始化
	*@param context 上下文
	*@throws ExceptionInInitializerError 初始化错误
	*/
	private void init(Context context) throws ExceptionInInitializerError
	{// 创建游戏逻辑、获取持有者、添加回调、新建绘图线程、初始化画笔
		final String strGameViewTag = (String) getTag();
		if (null == strGameViewTag)
		{
			Log.e(strTag, "游戏视图的tag不能为空，应该设置为关联游戏逻辑的类名");
			throw new ExceptionInInitializerError("游戏视图初始化异常");
		}

		final String strLogicName = "fyy.ygame_frame.base." + strGameViewTag;
		try
		{
			Class<?> clssLogic = Class.forName(strLogicName);
			gameLogic = (YAGameLogic) clssLogic.newInstance();
		} catch (Throwable e)
		{
			Log.e(strTag, "没有找到该GameView相关的游戏逻辑：可能是"
					+ "GameView的tag中相关游戏逻辑名拼写错误（只写类名即可，不要添加包名）");
			throw new ExceptionInInitializerError("游戏视图初始化异常");
		}

		if (context instanceof YGameActivity)
			gameLogic.setGameActivity((YGameActivity) context);
		else
		{
			Log.e(strTag, "游戏主Activity需要继承于YGameActivity");
			throw new ExceptionInInitializerError("游戏主Activity类型错误");
		}

		// 获取SurfaceHolder
		surfaceHolder = getHolder();
		// 添加回调接口
		surfaceHolder.addCallback(this);
		// 设置游戏视图总是获取焦点
		setFocusable(true);

		// 新建绘图线程
		drawThread = new YDrawThread();

		// 测试画笔
		paintTest = new Paint();
		paintTest.setTextSize(30);
		paintTest.setColor(0xff000000);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	public void surfaceCreated(SurfaceHolder holder)
	{// 通知各个领域视图加载位图、开启绘图线程、开启游戏逻辑
		// 1.
		int iWidth = getWidth();
		int iHeight = getHeight();
		Resources res = getResources();
		for (YABaseDomainView<?> domainView : domainViews)
			domainView.onLoadBitmaps(res, iWidth, iHeight, iMapLayoutParams);
		// 2.
		drawThread.start();
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			Log.e(strTag, "surfaceCreated所属线程暂停异常");
			e.printStackTrace();
		}
		// 3.
		gameLogic.start();
	}

	/**
	 * 当surface被销毁， <li>退出游戏逻辑 <li>跳出游戏视图线程的消息循环、结束视图线程 <li>通知各个领域视图接口回收位图
	 */
	public void surfaceDestroyed(SurfaceHolder holder)
	{// 退出游戏逻辑、退出绘图线程、通知领域视图回收位图
		// 1.
		gameLogic.end();
		// 2.
		drawThread.lpr.quit();
		while (drawThread.isAlive())
		{
			try
			{
				drawThread.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}

		// 3.
		for (YABaseDomainView<?> domainView : domainViews)
			domainView.onRecycleBitmaps();
	}

	/**分派<b>绘图信息</b>{@link YDrawInformation}给各个<b>领域视图</b>{@link YABaseDomainView}，
	*刷新一帧视图。
	*@param mapDrawInfos <b>绘图信息</b>列表
	*@param iFps 帧速率（用于测试用，目前没有使用）
	*/
	@SuppressLint("WrongCall")
	private final void refresh(Map<Integer, YDrawInformation> mapDrawInfos, int iFps)
	{
		Canvas canvas = null;
		try
		{
			// 1.锁定画布
			canvas = surfaceHolder.lockCanvas();
			if (null != canvas)
			{// 2.锁定成功，绘图
				canvas.drawColor(0xffffffff);
				for (YABaseDomainView<?> domainView : domainViews)
					// ???bug
					// 如果领域视图和领域逻辑数据不同时该处有毛病。
					domainView.onDraw(canvas, mapDrawInfos.get(domainView.iID));
				canvas.drawText("" + iFps, 20, 30, paintTest);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.e(strTag, "GameView.refresh(...)调用异常");
		} finally
		{
			// 3.无论如何都要提交画布（非空时）
			if (null != canvas)
				surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**添加<b>领域视图</b>{@link YABaseDomainView}。<br><b>注</b>：按照绘图顺序添加，否则绘图
	*时被遮盖！
	*@param  domainViews 添加的<b>领域视图</b>，可多个
	*/
	public final void addDomainView(YABaseDomainView<?>... domainViews)
	{
		for (YABaseDomainView<?> domainView : domainViews)
		{
			this.domainViews.add(domainView);
			domainView.broadcastView = this;
		}
	}

	/**移除<b>领域视图</b>{@link YABaseDomainView}。
	*@param domainViews 需要移除的<b>领域视图</b>，可多个
	*/
	public final void removeDomainView(YABaseDomainView<?>... domainViews)
	{
		for (YABaseDomainView<?> domainView : domainViews)
			this.domainViews.remove(domainView);
	}

	/**获得与<b>游戏视图</b>{@link YGameView}关联的<b>具体游戏逻辑</b>
	*@return <b>具体游戏逻辑</b>
	*/
	@SuppressWarnings("unchecked")
	public final <GL extends YAGameLogic> GL getGameLogic()
	{
		return (GL) gameLogic;
	}

	public void send(int iMsgKey, Object objDetailMsg, YIReceiver receiverSend)
	{
		if (YGameEnvironment.BroadcastMsgKey.MSG_MAP_VIEW_LAYOUTED == iMsgKey)
			iMapLayoutParams = (int[]) objDetailMsg;
		for (YABaseDomainView<?> baseDomainView : domainViews)
			if (baseDomainView != receiverSend)
				baseDomainView.onReceiveBroadcastMsg(iMsgKey, objDetailMsg);
	}

	private static class YDrawHandler extends Handler
	{
		private YGameView gameView;

		private YDrawHandler(YGameView gameView)
		{
			this.gameView = gameView;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			gameView.refresh((Map<Integer, YDrawInformation>) (msg.obj), 0);
		}
	}

	/**
	*<b>消息线程</b>
	*
	*<p>
	*<b>概述</b>：
	*刷新视图。
	*
	*<p>
	*<b>详细</b>：
	*接收<b>游戏逻辑</b>{@link YAGameLogic}发来的绘图消息，从msg.obj中取出<b>绘图信息</b>
	*列表，按序向各个<b>领域视图</b>{@link YABaseDomainView}分发<b>绘图信息</b>，完成绘制。
	*
	*@author FeiYiyun
	*
	*/
	private final class YDrawThread extends Thread
	{
		/**<b>绘图线程的消息句柄</b>：方便<b>游戏视图</b>销毁时退出该线程。*/
		private Looper lpr;
		private static final String strDrawThreadName = "绘图线程";

		private YDrawThread()
		{
			super();
			setName(strDrawThreadName);
		}

		@Override
		public void run()
		{
			// 建立消息队列
			Looper.prepare();
			// 获取消息循环引用，以备退出
			lpr = Looper.myLooper();
			// 注：新建Handler一定要在Looper.prepare()后
			gameLogic.setGameViewHandler(new YDrawHandler(YGameView.this));
			// 进入消息循环
			Looper.loop();
		}
	}
}
