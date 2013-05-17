package fyy.ygame_simple_map;

import android.util.Log;
import fyy.ygame_frame.base.YABaseDomainData;

/**
*<b>远景图数据</b>
*
*@author FeiYiyun
*
*/
public class YPerspectivesData extends YABaseDomainData
{
	private static final String strTag = "YPerspectivesData";
	/**<b>远景图层数组</b>*/
	final YPerspective[] perspectives;
	/**<b>游戏视图宽度</b>*/
	int iViewWidth;

	/**
	*@param iID <b><i>远景图实体</i></b>标识
	*@param perspectives <b>远景图层</b>{@link YPerspective}，可多个（至少要有一个），
	*加入的顺序即为绘图顺序，应该注意合理该顺序，以免图像被覆盖
	*/
	public YPerspectivesData(int iID, YPerspective... perspectives)
	{
		super(iID);
		if (null == perspectives)
		{
			Log.e(strTag, "至少要有一个远景图层对象");
			throw new NullPointerException("远景图层为空");
		}
		this.perspectives = perspectives;
	}

//	@Override
	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg)
	{
		// TODO Auto-generated method stub

	}

}
