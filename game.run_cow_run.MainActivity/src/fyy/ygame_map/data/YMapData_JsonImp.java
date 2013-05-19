package fyy.ygame_map.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import fyy.ygame_frame.util.YFileUtil;
import fyy.ygame_map.extra.DigitalTile;

import android.content.res.Resources;

public class YMapData_JsonImp extends YAMapData
{

	private String strJson;

	public YMapData_JsonImp(int iID, Resources resources,
			String strAssetsFileName) {
		super(iID, resources, strAssetsFileName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 从Json字符串中取出int型的值
	 * 
	 * @param strJson
	 *                要解析的Json字符串
	 * @param strKey
	 *                要获取的值对应的键
	 * @return 从对应键中取出的值
	 * @throws JSONException
	 */
	private int parserMapInt(String strJson, String strKey)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// 此时还未读取任何json文本，直接读取就是一个JSONObject对象
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// 接下来的就是JSON对象的操作
		// 根据传入键，取出对应数据
		return json_objMap.getInt(strKey);
	}

	private int parserIndexMapInt(String strJson, String strKey)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// 此时还未读取任何json文本，直接读取就是一个JSONObject对象
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// 接下来的就是JSON对象的操作
		// 获取索引地图Json对象数组
		JSONArray json_arryLayers = json_objMap
				.getJSONArray("tilesets");

		// 从上述数组读取元素
		JSONObject json_objIndexMap = json_arryLayers.getJSONObject(0);
		// 从上述元素中，根据传入键读取相应Int值
		return json_objIndexMap.getInt(strKey);
	}

	/**
	 * 根据层序号，从Json字符串中解析对应的地图层数组
	 * 
	 * @param strJson
	 *                要解析的Json字符串
	 * @param iLayerIndex
	 *                要获取的地图层的序号
	 * @return 指定地图层的一维数组
	 * @throws JSONException
	 */
	private int[] parserMapLayer(String strJson, int iLayerIndex)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// 此时还未读取任何json文本，直接读取就是一个JSONObject对象
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// 接下来的就是JSON对象的操作

		int iCapacity = iMapColumnSum * iMapRowSum;
		// 新建地图层数组
		int[] l_arrLayer = new int[iCapacity];
		// 获取地图层数组
		// 读取地图层Json对象数组
		JSONArray json_arryLayers = json_objMap.getJSONArray("layers");

		// 从上述数组读取元素
		JSONObject json_objLayer = json_arryLayers
				.getJSONObject(iLayerIndex);
		// 从上述元素中读取data数组
		JSONArray json_arryData = json_objLayer.getJSONArray("data");
		// 将data数组复制到地图层数组
		for (int i = 0; i < iCapacity; i++)
		{
			l_arrLayer[i] = json_arryData.getInt(i);
		}

		return l_arrLayer;
	}

	/**
	 * 将三层Layer中的地图信息（底层、对象、碰撞）整合为图元二维数组
	 * 
	 * @param mp_dt_prvd
	 *                地图数据提供接口
	 * @return 整合后的图元二维数组
	 */
	private DigitalTile[][] transformLayersToTiles()
	{
		int[] iFirLayer, iScdLayer, iThrLayer;
		int iMapRowSum = 0;
		int iMapColumnSum = 0;
		try
		{
			iMapColumnSum = parserMapInt(strJson, "width");
			iMapRowSum = parserMapInt(strJson, "height");
			iFirLayer = parserMapLayer(strJson, 0);
			iScdLayer = parserMapLayer(strJson, 1);
			iThrLayer = parserMapLayer(strJson, 2);
		} catch (JSONException e)
		{
			System.out.println("解析地图层出错");
			throw new RuntimeException("解析地图层出错");
		}

		int iTemp = 0;
		DigitalTile[][] tl_arr2 = new DigitalTile[iMapRowSum][iMapColumnSum];
		for (int i = 0; i < iMapRowSum; i++)
		{
			for (int j = 0; j < iMapColumnSum; j++)
			{// 第一层为基础层，第二层为对象层，第三层为碰撞层
				int[] iBitmapNum = new int[]
				{ iFirLayer[iTemp], iScdLayer[iTemp] };
				tl_arr2[i][j] = new DigitalTile(iBitmapNum,
						iThrLayer[iTemp], i, j);
				iTemp++;
			}
		}
		return tl_arr2;
	}

	@Override
	protected int getIndexPicRowSum()
	{
		try
		{
			return parserIndexMapInt(strJson, "imageheight")
					/ parserIndexMapInt(strJson,
							"tilewidth");
		} catch (JSONException e)
		{
			System.out.println("解析索引图行数出错");
			throw new RuntimeException("解析索引图行数出错");
		}
	}

	@Override
	protected int getIndexPicColumnSum()
	{
		try
		{
			return parserIndexMapInt(strJson, "imagewidth")
					/ parserIndexMapInt(strJson,
							"tilewidth");
		} catch (JSONException e)
		{
			e.printStackTrace();
			System.out.println("解析索引图列数出错");
			throw new RuntimeException("解析索引图列数出错");
		}
	}

	@Override
	protected DigitalTile[][] getDigitalTiles()
	{
		return transformLayersToTiles();
	}

	@Override
	protected int getMapColumnSum()
	{
		try
		{
			return parserMapInt(strJson, "width");
		} catch (JSONException e)
		{
			System.out.println("地图总列数解析错误");
			throw new RuntimeException("地图总列数解析错误");
		}
	}

	@Override
	protected int getMapRowSum()
	{
		try
		{
			return parserMapInt(strJson, "height");
		} catch (JSONException e)
		{
			System.out.println("地图总行数解析错误");
			throw new RuntimeException("地图总行数解析错误");
		}
	}

	@Override
	protected void startParser(Resources res, String strAssetsFileName)
	{
		strJson = YFileUtil.readFileFromAssets(res, strAssetsFileName);
	}

	@Override
	protected void endParser()
	{
		strJson = null;
	}
}
