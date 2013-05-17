package fyy.ygame_frame.base;

/**
*<b>绘图信息</b>
*
*<p>
*<b>概述</b>：
*由于<b>领域逻辑</b>计算产生，包含了<b>领域视图</b>{@link YABaseDomainView}必要的绘图信息。
*
*@author FeiYiyun
*
*/
public final class YDrawInformation
{
	/**<b>所绘制图片的类型</b>：如走、跑、攻击等。*/
	public final int iPicKind;
	/**<b>所绘制图片的序号</b>：即第几张图片。*/
	public final int iPicIndex;
	/**<b>所绘制图片的横坐标</b>：像素为单位，应该注意约定该坐标的基准点为图片的上的哪个点。*/
	public final int iX;
	/**<b>所绘制图片的纵坐标</b>：像素为单位，应该注意约定该坐标的基准点为图片的上的哪个点。*/
	public final int iY;
	/**<b>所绘制图片的角度</b>：图片的旋转角度。*/
	public final float fAngle;

	/**<b>备用绘图信息</b>*/
	public final Object objExtra;
	/**<b>备用绘图信息</b>*/
	public final int iArg1;
	/**<b>备用绘图信息</b>*/
	public final int iArg2;

	private YDrawInformation(YDrawInfoForm drawInfoForm)
	{
		this.fAngle = drawInfoForm.fAngle;
		this.iArg1 = drawInfoForm.iArg1;
		this.iArg2 = drawInfoForm.iArg2;
		this.iPicIndex = drawInfoForm.iPicIndex;
		this.iPicKind = drawInfoForm.iPicKind;
		this.iX = drawInfoForm.iX;
		this.iY = drawInfoForm.iY;
		this.objExtra = drawInfoForm.objExtra;
	}

	/**
	*<b>绘图信息表单</b>
	*
	*<p>
	*<b>概述</b>：
	*<b>领域逻辑</b>{@link YABaseDomainLogic}计算完绘图需要的信息后，在此表单中填写这些信息，
	*这些信息将会被路由到<b>领域视图</b>{@link YABaseDomainView}去绘制。
	*
	*<p>
	*<b>注</b>：
	*表单中的绘图信息<b>不会</b>被自动清除。
	*
	*@author FeiYiyun
	*
	*/
	public final static class YDrawInfoForm
	{
		/**<b>所绘制图片的类型</b>：如走、跑、攻击等。*/
		public int iPicKind;
		/**<b>所绘制图片的序号</b>：即第几张图片。*/
		public int iPicIndex;
		/**<b>所绘制图片的横坐标</b>：像素为单位，应该注意该坐标的基准点为图片的上的哪个点。*/
		public int iX;
		/**<b>所绘制图片的纵坐标</b>：像素为单位，应该注意该坐标的基准点为图片的上的哪个点。*/
		public int iY;
		/**<b>所绘制图片的角度</b>：图片的旋转角度。*/
		public float fAngle;

		/**<b>备用绘图信息</b>*/
		public Object objExtra;
		/**<b>备用绘图信息</b>*/
		public int iArg1;
		/**<b>备用绘图信息</b>*/
		public int iArg2;

		YDrawInfoForm()
		{
		}

		YDrawInformation print()
		{
			return new YDrawInformation(this);
		}
	}
}
