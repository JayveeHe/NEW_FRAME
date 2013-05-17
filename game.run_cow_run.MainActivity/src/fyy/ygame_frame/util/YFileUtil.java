package fyy.ygame_frame.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.res.Resources;
import android.util.Log;

/**
 * <b>文件工具</b><br>
 * 
 * @author fei yiyun
 * 
 */
/**
*<b>文件工具</b>
*
*<p>
*<b>概述</b>：
*处理文件操作的方便工具
*
*@author FeiYiyun
*
*/
public final class YFileUtil
{
	private static final String strTag = "YFileUtil";

	private YFileUtil()
	{
	}

	/**从assets文件夹中的文件读取文本。
	*@param res 资源
	*@param strFileName 文件名
	*@return 读取到的字符串
	*/
	public static String readFileFromAssets(Resources res, String strFileName)
	{
		InputStream inpt_strm = null;
		int iLenght = 0;
		byte[] byteBuffer = null;

		try
		{
			// 读取文件
			inpt_strm = res.getAssets().open(strFileName);
			// 获取文件长度
			iLenght = inpt_strm.available();
			// 根据文件长度开辟相应的内存
			byteBuffer = new byte[iLenght];
			// 将文件内容读入内存
			inpt_strm.read(byteBuffer);
			// 关闭文件
			inpt_strm.close();
			// 将内存中的值编码为UTF-8字符串
			return EncodingUtils.getString(byteBuffer, "UTF-8");
		} catch (IOException e)
		{
			Log.e(strTag, "读取文件：" + strFileName + "失败");
			e.printStackTrace();
			throw new RuntimeException("读取文件失败");
		} finally
		{
			if (null != inpt_strm)
				try
				{
					inpt_strm.close();
				} catch (IOException e)
				{
					Log.e(strTag, "关闭文件" + strFileName + "失败");
					e.printStackTrace();
				}
		}
	}
}
