package fyy.ygame_map.extra;

import java.util.LinkedList;
import java.util.List;

import fyy.ygame_frame.base.YABaseDomainLogic;

/**
 * 数字图元
 * 
 * @author fei yiyun
 */
public final class DigitalTile {
	/** 图元上的图片编号，编号为零则表示无图片 */
	public final int[] iBitmapNum;
	/** 图元地形 */
	public final int iLandScape;
	/** 图元在地图上所处的行数 */
	public final int iRow;
	/** 图元在地图上所处的列数 */
	public final int iColumn;
	/** 占据在该图元上的领域逻辑（这是随着游戏进行动态改变的） */
	@Deprecated
	private List<YABaseDomainLogic<?>> domainLogics;

	public DigitalTile(int[] iBitmapNum, int iLandScape, int iRow, int iColumn) {
		this.iBitmapNum = iBitmapNum;
		this.iLandScape = iLandScape;
		this.iRow = iRow;
		this.iColumn = iColumn;
	}

	@Deprecated
	public void addDomainLogic(YABaseDomainLogic<?> domainLogic) {
		if (null == domainLogics)
			domainLogics = new LinkedList<YABaseDomainLogic<?>>();

		if (!domainLogics.contains(domainLogic))
			domainLogics.add(domainLogic);
	}

	@Deprecated
	public List<YABaseDomainLogic<?>> getDomainLogic() {
		return domainLogics;
	}

	@Deprecated
	public void removeDomainLogic(YABaseDomainLogic<?> domainLogic) {
		try{
			domainLogics.remove(domainLogic);			
		} catch (NullPointerException e){
			System.out.println("移除地图标记时发生错误！");
		}
		
		if (0 == domainLogics.size())
			domainLogics = null;
	}
}
