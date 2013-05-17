package fyy.ygame_map.logic;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;

final class ScrollRightTask implements YITask
{
	private YMapLogic mapLogic;

	public ScrollRightTask(YMapLogic mapLogic)
	{
		this.mapLogic = mapLogic;
	}


	public void execute(YDrawInfoForm drawInfoForm,
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		if (0 > (mapLogic.iCurX += mapLogic.iPixelsPerStep))
		{// 不在起始位置，可以右移动
			mapLogic.bLeft = false;
			if ((mapLogic.iCurPerspectiveX -= mapLogic.iPerspectiveStep) < 0)
				mapLogic.iCurPerspectiveX = mapLogic.mapData.iMapViewWidth;
		} else
		{// 处在最起始位置，强制偏移归零，置边缘标识
			mapLogic.bLeft = true;
			mapLogic.iCurX = 0;
		}

		drawInfoForm.iPicIndex = mapLogic.iCurPerspectiveX;
		drawInfoForm.iX = mapLogic.iCurX;
		drawInfoForm.iY = mapLogic.iCurY;
	}

}
