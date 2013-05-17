package fyy.ygame_frame.base;

import fyy.ygame_frame.extra.YRequest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
*<b>游戏活动</b>
*
*<p>
*<b>概述</b>：
*主要职责为：将玩家操作产生的<b>请求</b>{@link YRequest}封装在msg.obj中，通过
*<b>游戏逻辑消息句柄</b>{@link #handlerGameLogic}将消息发送到<b>游戏逻辑</b>{@link YAGameLogic}。
*
*<p>
*<b>注</b>：
*该活动默认设置为全屏、无标题栏、横屏。
*
*@author FeiYiyun
*
*/
public class YGameActivity extends Activity
{
	/**<b>游戏逻辑消息句柄</b>：<b>游戏逻辑</b>{@link YAGameLogic}的消息句柄。应将玩家
	*操作产生的<b>请求</b>{@link YRequest}封装在msg.obj中，通过该句柄向<b>游戏逻辑</b>发送。*/
	protected Handler handlerGameLogic;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 设置横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// 设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**设置<b>游戏逻辑</b>{@link YAGameLogic}的消息句柄
	*@param handlerGameLogic 游戏逻辑消息句柄
	*/
	void setGameLogicHandler(Handler handlerGameLogic)
	{
		this.handlerGameLogic = handlerGameLogic;
	}
}
