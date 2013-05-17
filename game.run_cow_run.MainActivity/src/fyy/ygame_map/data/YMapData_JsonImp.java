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
	 * ��Json�ַ�����ȡ��int�͵�ֵ
	 * 
	 * @param strJson
	 *                Ҫ������Json�ַ���
	 * @param strKey
	 *                Ҫ��ȡ��ֵ��Ӧ�ļ�
	 * @return �Ӷ�Ӧ����ȡ����ֵ
	 * @throws JSONException
	 */
	private int parserMapInt(String strJson, String strKey)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// ��ʱ��δ��ȡ�κ�json�ı���ֱ�Ӷ�ȡ����һ��JSONObject����
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// �������ľ���JSON����Ĳ���
		// ���ݴ������ȡ����Ӧ����
		return json_objMap.getInt(strKey);
	}

	private int parserIndexMapInt(String strJson, String strKey)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// ��ʱ��δ��ȡ�κ�json�ı���ֱ�Ӷ�ȡ����һ��JSONObject����
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// �������ľ���JSON����Ĳ���
		// ��ȡ������ͼJson��������
		JSONArray json_arryLayers = json_objMap
				.getJSONArray("tilesets");

		// �����������ȡԪ��
		JSONObject json_objIndexMap = json_arryLayers.getJSONObject(0);
		// ������Ԫ���У����ݴ������ȡ��ӦIntֵ
		return json_objIndexMap.getInt(strKey);
	}

	/**
	 * ���ݲ���ţ���Json�ַ����н�����Ӧ�ĵ�ͼ������
	 * 
	 * @param strJson
	 *                Ҫ������Json�ַ���
	 * @param iLayerIndex
	 *                Ҫ��ȡ�ĵ�ͼ������
	 * @return ָ����ͼ���һά����
	 * @throws JSONException
	 */
	private int[] parserMapLayer(String strJson, int iLayerIndex)
			throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(strJson);
		// ��ʱ��δ��ȡ�κ�json�ı���ֱ�Ӷ�ȡ����һ��JSONObject����
		JSONObject json_objMap = (JSONObject) jsonParser.nextValue();
		// �������ľ���JSON����Ĳ���

		int iCapacity = iMapColumnSum * iMapRowSum;
		// �½���ͼ������
		int[] l_arrLayer = new int[iCapacity];
		// ��ȡ��ͼ������
		// ��ȡ��ͼ��Json��������
		JSONArray json_arryLayers = json_objMap.getJSONArray("layers");

		// �����������ȡԪ��
		JSONObject json_objLayer = json_arryLayers
				.getJSONObject(iLayerIndex);
		// ������Ԫ���ж�ȡdata����
		JSONArray json_arryData = json_objLayer.getJSONArray("data");
		// ��data���鸴�Ƶ���ͼ������
		for (int i = 0; i < iCapacity; i++)
		{
			l_arrLayer[i] = json_arryData.getInt(i);
		}

		return l_arrLayer;
	}

	/**
	 * ������Layer�еĵ�ͼ��Ϣ���ײ㡢������ײ������ΪͼԪ��ά����
	 * 
	 * @param mp_dt_prvd
	 *                ��ͼ�����ṩ�ӿ�
	 * @return ���Ϻ��ͼԪ��ά����
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
			System.out.println("������ͼ�����");
			throw new RuntimeException("������ͼ�����");
		}

		int iTemp = 0;
		DigitalTile[][] tl_arr2 = new DigitalTile[iMapRowSum][iMapColumnSum];
		for (int i = 0; i < iMapRowSum; i++)
		{
			for (int j = 0; j < iMapColumnSum; j++)
			{// ��һ��Ϊ�����㣬�ڶ���Ϊ����㣬������Ϊ��ײ��
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
			System.out.println("��������ͼ��������");
			throw new RuntimeException("��������ͼ��������");
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
			System.out.println("��������ͼ��������");
			throw new RuntimeException("��������ͼ��������");
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
			System.out.println("��ͼ��������������");
			throw new RuntimeException("��ͼ��������������");
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
			System.out.println("��ͼ��������������");
			throw new RuntimeException("��ͼ��������������");
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
