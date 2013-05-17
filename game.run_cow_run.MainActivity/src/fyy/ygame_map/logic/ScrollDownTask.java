package fyy.ygame_map.logic;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;

final class ScrollDownTask implements YITask
{
	private YMapLogic mapLogic;

	public ScrollDownTask(YMapLogic mapLogic)
	{
		this.mapLogic = mapLogic;
	}

	public void execute(YDrawInfoForm drawInfoForm,
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		if (0 > (mapLogic.iCurY += mapLogic.iPixelsPerStep))
		{// 不在初始位置，可以下移
			mapLogic.bTop = false;

			// if ((mapLogic.iCurPerspectiveX +=
			// mapLogic.iPerspectiveStep) >
			// mapLogic.mapData.iMapViewWidth)
			// mapLogic.iCurPerspectiveX = 0;
		} else
		{// 处在初始位置，强制偏移归零，置方向标识
			mapLogic.bTop = true;
			mapLogic.iCurY = 0;
		}

		drawInfoForm.iPicIndex = mapLogic.iCurPerspectiveX;
		drawInfoForm.iX = mapLogic.iCurX;
		drawInfoForm.iY = mapLogic.iCurY;
	}

}
