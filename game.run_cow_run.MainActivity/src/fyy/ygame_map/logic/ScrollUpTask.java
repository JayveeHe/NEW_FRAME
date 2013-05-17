package fyy.ygame_map.logic;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;

final class ScrollUpTask implements YITask
{
	private YMapLogic mapLogic;

	public ScrollUpTask(YMapLogic mapLogic)
	{
		this.mapLogic = mapLogic;
	}

	public void execute(YDrawInfoForm drawInfoForm,
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		if (mapLogic.iMaxYCanMove < (mapLogic.iCurY -= mapLogic.iPixelsPerStep))
		{// 不在最末行，可以上移
			mapLogic.bBottom = false;

			// if ((mapLogic.iCurPerspectiveX +=
			// mapLogic.iPerspectiveStep) >
			// mapLogic.mapData.iMapViewWidth)
			// mapLogic.iCurPerspectiveX = 0;
		} else
		{// 处在最末行，强制偏移最大化，置方向标识
			mapLogic.bBottom = true;
			mapLogic.iCurY = mapLogic.iMaxYCanMove;
		}

		drawInfoForm.iPicIndex = mapLogic.iCurPerspectiveX;
		drawInfoForm.iX = mapLogic.iCurX;
		drawInfoForm.iY = mapLogic.iCurY;
	}
}
