package fyy.ygame_simple_map;

import android.content.res.Resources;
import android.graphics.Canvas;
import fyy.ygame_frame.base.YABaseDomainView;
import fyy.ygame_frame.base.YDrawInformation;

/**
*<b>‘∂æ∞Õº ”Õº</b>
*
*@author FeiYiyun
*
*/
public class YPerspectivesView extends YABaseDomainView<YPerspectivesData>
{
	private YPerspective[] perspectives;
	
	public YPerspectivesView(YPerspectivesData domainData)
	{
		super(domainData);
		perspectives = domainData.perspectives;
	}

	//@Override
	public void onReceiveBroadcastMsg(int iMsgKey, Object objectDetailMsg)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoadBitmaps(Resources resources, int iWidth, int iHeight,
			int[] iMapLayoutParams)
	{
		domainData.iViewWidth = iWidth;
		for (YPerspective perspective : perspectives)
			perspective.generateBitmap(resources, iWidth, iHeight);
	}

	@Override
	protected void onDraw(Canvas canvas, YDrawInformation drawInformation)
	{
		int i = 0;
		int[] iXs = (int[]) drawInformation.objExtra;
		for (YPerspective perspective : perspectives)
			perspective.draw(canvas, iXs[i++]);
	}

	@Override
	protected void onRecycleBitmaps()
	{
		for (YPerspective perspective : perspectives)
			perspective.recycle();
	}

}
