package fyy.runcowrun;

import fyy.ygame_frame.base.YDomainBroadcast;
import fyy.ygame_frame.base.YGameActivity;
import fyy.ygame_frame.base.YGameView;
import fyy.ygame_frame.base.YNormalGameLogic;
import fyy.ygame_frame.extra.YRequest;
import fyy.ygame_simple_map.YPerspective;
import fyy.ygame_simple_map.YPerspectivesConstant;
import fyy.ygame_simple_map.YPerspectivesData;
import fyy.ygame_simple_map.YPerspectivesLogic;
import fyy.ygame_simple_map.YPerspectivesView;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends YGameActivity
{

//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
		//YPerspective perspective1 = new YPerspective(R.drawable.tiankong, 0.001f, 0.3f, 0, 0);
//		YPerspective perspective2 = new YPerspective(R.drawable.yuanshumo, 0.002f, 0.4f, 0, 0.2f);
//		YPerspective perspective3 = new YPerspective(R.drawable.jinshamo, 0.003f, 0.6f, 0, 0.4f);
//		YPerspective perspective4 = new YPerspective(R.drawable.xianrenzhang, 0.004f, 0.3f, 0, 0.7f);
//		YPerspectivesData mapData = new YPerspectivesData(123, perspective1, perspective2 , perspective3,perspective4);
//		
////		YPerspective perspective = new YPerspective(R.drawable.p3, 0.5f, 1, 0, 0);
		//YPerspectivesData mapData = new YPerspectivesData(123, perspective2);
//		
		//final YPerspectivesLogic mapLogic = new YPerspectivesLogic(mapData);
		//ma
//		YPerspectivesView mapView = new YPerspectivesView(mapData);
//		new YDomainBroadcast(mapData, mapLogic, mapView);
//
//		YGameView gameView = (YGameView) findViewById(R.id.GameView);
//		YNormalGameLogic gameLogic = gameView.getGameLogic();
//
//		gameLogic.addDomainLogic(mapLogic);
//		gameView.addDomainView(mapView);
//
//		Button button = (Button) findViewById(R.id.Btn);
//		button.setOnClickListener(new OnClickListener()
//		{
//			private YRequest request = new YRequest(123,
//					YPerspectivesConstant.RequestKey.LEFT_SCROLL, mapLogic);
//
//			// private boolean b
//			@Override
//			public void onClick(View v)
//			{
//				Message msg = Message.obtain();
//				msg.obj = request;
//				handlerGameLogic.sendMessage(msg);
//			}
//		});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the
//		// action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
