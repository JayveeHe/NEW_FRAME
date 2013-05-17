package fyy.ygame_map.logic;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;

final class NoScrollTask implements YITask
{
	protected YMapLogic mapLogic;

	NoScrollTask(YMapLogic mapLogic)
	{
		this.mapLogic = mapLogic;
	}

	public void execute(YDrawInfoForm drawInfoForm,
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		// TODO Auto-generated method stub
		drawInfoForm.iPicIndex = mapLogic.iCurPerspectiveX;
		drawInfoForm.iX = mapLogic.iCurX;
		drawInfoForm.iY = mapLogic.iCurY;
		
	}
}
