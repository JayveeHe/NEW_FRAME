package fyy.ygame_map.logic;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;

final class ScrollLeftTask implements YITask
{
	private YMapLogic mapLogic;

	public ScrollLeftTask(YMapLogic mapLogic)
	{
		this.mapLogic = mapLogic;
	}



	public void execute(YDrawInfoForm drawInfoForm,
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		if (mapLogic.iMaxXCanMove < (mapLogic.iCurX -= mapLogic.iPixelsPerStep))
		{// 不在最末位置，可以左移动
			mapLogic.bRight = false;

			if ((mapLogic.iCurPerspectiveX += mapLogic.iPerspectiveStep) > mapLogic.mapData.iMapViewWidth)
				mapLogic.iCurPerspectiveX = 0;
		} else
		{// 处在最末位置，强制偏移最大，置边缘标识
			mapLogic.bRight = true;
			mapLogic.iCurX = mapLogic.iMaxXCanMove;
		}

		drawInfoForm.iPicIndex = mapLogic.iCurPerspectiveX;
		drawInfoForm.iX = mapLogic.iCurX;
		drawInfoForm.iY = mapLogic.iCurY;
	}
}
