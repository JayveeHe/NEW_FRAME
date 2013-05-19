package game.run_cow_run;

import jayvee.cow.CowData;
import jayvee.cow.CowLogic;
import jayvee.cow.CowView;
import jayvee.drawbox.Drawing_Data;
import jayvee.drawbox.Drawing_Logic;
import jayvee.drawbox.Drawing_Queue;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import fyy.ygame_frame.base.YAGameLogic;
import fyy.ygame_frame.base.YDomainBroadcast;
import fyy.ygame_frame.base.YGameActivity;
import fyy.ygame_frame.base.YGameView;
import fyy.ygame_frame.extra.YRequest;
import fyy.ygame_map.data.YAMapData;
import fyy.ygame_map.data.YMapData_JsonImp;
import fyy.ygame_map.logic.YMapLogic;
import fyy.ygame_map.view.YAMapView;

public class MainActivity extends YGameActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        YAMapData mapData = new YMapData_JsonImp(123 ,getResources(), "ddfaw.json");
        final YMapLogic mapLogic = new YMapLogic(mapData);
        YAMapView mapView = YAMapView.createSimpleHorizonMap(mapData, R.drawable.item);
        mapView.setPerspectivePic(R.drawable.bkg);
        new YDomainBroadcast(mapData , mapView , mapLogic);
        
//        mapLogic.setStepByStepScroll(false);
        
        YGameView gameView = (YGameView) findViewById(R.id.GameView);
        YAGameLogic gameLogic = gameView.getGameLogic();
        gameLogic.setUpdatePeriod(49);
        Drawing_Data dd = new Drawing_Data(444);
        Drawing_Logic dl = new Drawing_Logic(dd);
        Drawing_Queue dq = new Drawing_Queue(dd);
        CowData cd = new CowData(222);
        final CowLogic cl = new CowLogic( cd , mapLogic, dl.dq);
        CowView cv = new CowView(cd);
        new YDomainBroadcast(cd,cl,cv);
        
       
        
        gameLogic.addDomainLogic(mapLogic,cl,dl);
        gameView.addDomainView(mapView,cv,dq);
        
        //°´¼üµÄÉèÖÃ///
        Button btn_test =(Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Message msg = Message.obtain();
//		     	msg.obj = new YRequest(111, YMapConstant.TaskKey.iScrollLeft, mapLogic);
//				hndlrGameLogic.sendMessage(msg);
				
			}
		});
        
        Button btn_jump = (Button) findViewById(R.id.btn_jump);
       btn_jump.setOnTouchListener(new OnTouchListener() {
		
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction() == MotionEvent.ACTION_DOWN)
			{
				Message msg = Message.obtain();
		     	msg.obj = new YRequest(111, CowLogic.iJump, cl);
				handlerGameLogic.sendMessage(msg);
			}
			return false;
		}
	});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
