package fyy.ygame_map.extra;

import java.util.LinkedList;
import java.util.List;

import fyy.ygame_frame.base.YABaseDomainLogic;

/**
 * ����ͼԪ
 * 
 * @author fei yiyun
 */
public final class DigitalTile {
	/** ͼԪ�ϵ�ͼƬ��ţ����Ϊ�����ʾ��ͼƬ */
	public final int[] iBitmapNum;
	/** ͼԪ���� */
	public final int iLandScape;
	/** ͼԪ�ڵ�ͼ������������ */
	public final int iRow;
	/** ͼԪ�ڵ�ͼ������������ */
	public final int iColumn;
	/** ռ���ڸ�ͼԪ�ϵ������߼�������������Ϸ���ж�̬�ı�ģ� */
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
			System.out.println("�Ƴ���ͼ���ʱ��������");
		}
		
		if (0 == domainLogics.size())
			domainLogics = null;
	}
}
