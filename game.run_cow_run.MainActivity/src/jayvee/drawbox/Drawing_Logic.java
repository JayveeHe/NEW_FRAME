package jayvee.drawbox;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.os.Message;

import fyy.ygame_frame.base.YABaseDomainLogic;
import fyy.ygame_frame.base.YDrawInformation.YDrawInfoForm;
import fyy.ygame_frame.extra.YITask;
import fyy.ygame_frame.extra.YRequest;

public class Drawing_Logic extends YABaseDomainLogic<Drawing_Data> {

	public Queue<Boxs> dq = new LinkedList<Boxs>();
	private int xoffset = 5;// ÿ������λ�Ƶľ���
	private float fRate = 0.4f;// ��������ʱ�����Ӳ��ᱻ��ʾ�ĸ���

	public Drawing_Logic(Drawing_Data domainData) {
		super(domainData);
		// TODO Auto-generated constructor stub
		// for (int queue_count = 0; queue_count < 2; queue_count++) {
		Boxs b = new Boxs(fRate);
		dq.add(b);
		// }
	}

	@Override
	protected void onSendRequests(
			Map<Integer, YABaseDomainLogic<?>> domainLogics) {
		// TODO Auto-generated method stub
		// Message msg = Message.obtain();
		// msg.obj = dq;
		// domainLogics.get(arg0)
		// handlerGameLogic.sendMessage(msg);
		

	}
	

	YITask drawtask = new YITask() {
		int newcount = 0;

		public void execute(YDrawInfoForm drawInfoForm,
				Map<Integer, YABaseDomainLogic<?>> domainLogics) {
			// TODO Auto-generated method stub
			Boxs tempbox = dq.peek();
			int queue_count = dq.size();
			for (int i = 0; i < queue_count; i++) {

				
				if (dq.peek().iX > 700)// ��������Ļ���˴�600���޸ģ������
				{
					dq.poll();
					queue_count -=2;
					// dq.add(new Boxs(fRate));
					continue;
				} else if (dq.peek().iX == 100) {
//					dq.add(new Boxs(fRate));
					newcount++;//��Ҫ�½����ӵļ�����һ��ѭ����Ӧ�ÿ��Լ������Ҫ�½���������
				}
				dq.peek().addX(xoffset);// ����ƫ��
				tempbox = dq.poll();
				dq.add(tempbox);// ����βԪ��ȡ�������������ͷ��
			}
			while (newcount>0)
			{
				dq.add(new Boxs(fRate));
				newcount--;
			}//ÿ���Ժ��½�����ȷ���������ӣ������newcount=0
			tempbox = dq.peek();
			// һ�ֽ�����tempboxָ������ʼ��һ������
			drawInfoForm.iArg1 = tempbox.iX;// �����Ž�tempbox��λ�û�����
			drawInfoForm.objExtra = dq;

		}
	};

	@Override
	protected YITask[] onSubmitCurrentTasks() {
		// TODO Auto-generated method stub
		return new YITask[] { drawtask };
	}

	@Override
	protected int onDealRequest(YRequest request) {
		// TODO Auto-generated method stub
		return 0;
	}

	// public Boxs getQhead(){
	// return
	// }

}
