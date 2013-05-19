package fyy.ygame_frame.extra;

import java.util.Map;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;

/**
 * <b>任务接口</b>
 * 
 * @author fei yiyun
 * 
 */
/**
*<b>任务</b>
*
*<p>
*<b>概述</b>：
*<b>领域逻辑</b>{@link YABaseDomainLogic}每回合可能被执行的任务（需要被<b>领域逻辑</b>
*提交才能被执行到）。
*
*<p>
*<b>建议</b>：
*<li>实现该接口的类应该不必公开；
*<li>与<b>领域逻辑</b>放在同一个包下。
*
*<p>
*<b>详细</b>：
*参见{@link #execute(YDrawInfoForm, Map)}；
*
*@author FeiYiyun
*
*/
public interface YITask
{
	/**执行任务，这里需要做的工作为：改变<b>领域逻辑</b>
	*{@link YABaseDomainLogic}相应的逻辑值，计算绘图用到的信息，并填写进
	*<b>绘图信息表单</b>{@link YDrawInfoForm}。
	*@param drawInfoForm <b>绘图信息表单</b>
	*@param domainLogics <b>领域逻辑</b>列表
	*/
	void execute(YDrawInfoForm drawInfoForm,
	// domainLogics必须有，比如人在地图上执行走动任务，人必须知道地图信息
			Map<Integer, YABaseDomainLogic<?>> domainLogics);
}
