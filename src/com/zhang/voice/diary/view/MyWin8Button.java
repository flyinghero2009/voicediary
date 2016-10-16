package com.zhang.voice.diary.view;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;

public class MyWin8Button extends ImageView{
	private Camera camera;   // ����һ��ͼ��������
	private boolean newOne;  // ����һ��ͼ���Ƿ��ǵ�һ�μ���
	private int realWidth;       // ͼƬ��ʵ�Ŀ��
	private int realHeight;       // ͼƬ��ʵ�ĸ߶�
	private float x;    // �����x���
	private float y;    // �����y���
	private boolean isMiddle;  // �Ƿ������м�����λ�õ��
	private boolean isMove;    // �Ƿ��ڵ�����Ƴ���ͼ������
	private final float SCALE_NUM = 0.95f;  // ���ű���
	private final int ROTATE_NUM = 10;      // ��б�̶�
	private Matrix oldMatrix;   // δ����κ�Ч���ͼ��
	private boolean isFinish;   // �Ƿ������Ч
	boolean xBigY = false;      // �ж�x�Ƿ����y
	float RolateX = 0;  // xƫ��λ��
	float RolateY = 0;  // yƫ��λ��

	public MyWin8Button(Context context) {
		super(context);
		camera = new Camera();   //  ����һ��ͼ�������
		newOne = true;           //  �����ǵ�һ�μ���
		oldMatrix = new Matrix();
		oldMatrix.set(getImageMatrix());  // ��ȡδ����κ�Ч���ͼ��
		isFinish = true;  // �����������Ч
		isMiddle = false; // ���ò����м�����
		isMove = false;   // ����û���ƿ�ͼ������
	}
	
	public MyWin8Button(Context context, AttributeSet attributeSet) {
		super(context,attributeSet);
		camera = new Camera();   //  ����һ��ͼ�������
		newOne = true;			 //  �����ǵ�һ�μ���
		oldMatrix = new Matrix();
		oldMatrix.set(getImageMatrix());  // ��ȡδ����κ�Ч���ͼ��
		isFinish = true;  // �����������Ч
		isMiddle = false; // ���ò����м�����
		isMove = false;   // ����û���ƿ�ͼ������
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (newOne){   // �����һ���µ���� ���г�ʼ�� �����б�ʶ
			newOne = false;
			init();    // ���г�ʼ��
		}
		
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
	}
	
	private void init(){
		// ��ȡ��ʵ��ͼ����
		realWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		// ��ȡ��ʵ��ͼ��߶�
		realHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		
		BitmapDrawable bd = (BitmapDrawable) getDrawable();  // ��ȡλͼ
		bd.setAntiAlias(true);   // ����ʹ�ÿ���ݵȹ���
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {  // �����¼�����Ӧ
		super.onTouchEvent(event);
		
		// �ж��¼�  �������Ǵ����¼�
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// ��Ӧ���µ��¼�
		case MotionEvent.ACTION_DOWN:
			x = event.getX();  // ��ȡ�����x���
			y = event.getY();  // ��ȡ�����y���
			
			RolateX = realWidth / 2 - x;
			RolateY = realHeight / 2 - y;
			xBigY = Math.abs(RolateX) > Math.abs(RolateY) ? true : false;
			
			isMove = false;    // ����δ�Ƴ�������
			
			// �ж��Ƿ����м�λ��
			isMiddle = x > realWidth / 3 && x < realWidth * 2 / 3 && y > realHeight / 3 && y < realHeight * 2 / 3;
			if (isMiddle){
				scaleHandler.sendEmptyMessage(1);
			}
			else {
				rolateHandler.sendEmptyMessage(1);
			}
			
			break;
			
		// ��Ӧ�ƶ����¼�
		case MotionEvent.ACTION_MOVE:
			float x=event.getX();float y=event.getY();
			if(x > realWidth || y > realHeight || x < 0 || y < 0){
				isMove=true;
			}else{
				isMove=false;
			}
			break;
			
		// ��Ӧ̧����¼�
		case MotionEvent.ACTION_UP:
			if (isMiddle) {
				scaleHandler.sendEmptyMessage(6);
			} else {
				rolateHandler.sendEmptyMessage(6);
			}
			break;

		default:
			break;
		}
		return true;
	}
	
	private Handler scaleHandler = new Handler(){
		private Matrix matrix = new Matrix();  // �����µ�matrix
		private int count = 0;    // �����޶�
		private float s;  // ���ű���ֵ
		
		public void handleMessage(Message msg) {
			
			if (isFinish){  // ���û���   ��ȡ��ǰ�ľ���
				matrix.set(getImageMatrix()); 
			}
			
			switch (msg.what) {  // �ж�ֵ
			case 1:
				if (!isFinish) {  // û��ɷ���
					return;
				} else {
					isFinish = false;  // �趨δ���
					count = 0;         
					s = (float) Math.sqrt(Math.sqrt(SCALE_NUM));
					matrix.set(oldMatrix);
					BeginScale(matrix, s);
					scaleHandler.sendEmptyMessage(2);
				}
				break;
			case 2:
				BeginScale(matrix, s);
				if (count < 4) {
					scaleHandler.sendEmptyMessage(2);
				} else {
					isFinish = true;
				}
				count++;
				break;
			case 6:
				if (!isFinish) {
					scaleHandler.sendEmptyMessage(6);
				} else {
					isFinish = false;
					count = 0;
					s = (float) Math.sqrt(Math.sqrt(1.0f / SCALE_NUM));
					BeginScale(matrix, s);
					scaleHandler.sendEmptyMessage(2);
				}
				break;
			}
			
		};
	};
	
	//  ��ʼ��������
	@SuppressWarnings("unused")
	@SuppressLint("HandlerLeak")
	private synchronized void BeginScale(Matrix matrix, float scale) {
		int scaleX = (int) (realWidth * 0.5f);
		int scaleY = (int) (realHeight * 0.5f);
		matrix.postScale(scale, scale, scaleX, scaleY);
		setImageMatrix(matrix);
	}
	
	
	private Handler rolateHandler = new Handler() {
		private Matrix matrix = new Matrix();
		private float count = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix.set(getImageMatrix());
			switch (msg.what) {
			case 1:
				count = 0;
				BeginRolate(matrix, (xBigY ? count : 0), (xBigY ? 0 : count));
				rolateHandler.sendEmptyMessage(2);
				break;
			case 2:
				BeginRolate(matrix, (xBigY ? count : 0), (xBigY ? 0 : count));
				if (count < ROTATE_NUM) {
					rolateHandler.sendEmptyMessage(2);
				} else {
					isFinish = true;
				}
				count++;
				count++;
				break;
			case 3:
				BeginRolate(matrix, (xBigY ? count : 0), (xBigY ? 0 : count));
				if (count > 0) {
					rolateHandler.sendEmptyMessage(3);
				} else {
					isFinish = true;
				}
				count--;
				count--;
				break;
			case 6:
				count = ROTATE_NUM;
				BeginRolate(matrix, (xBigY ? count : 0), (xBigY ? 0 : count));
				rolateHandler.sendEmptyMessage(3);
				break;
			}
		}
	};

	private synchronized void BeginRolate(Matrix matrix, float rolateX,
			float rolateY) {
		// Bitmap bm = getImageBitmap();
		int scaleX = (int) (realWidth * 0.5f);
		int scaleY = (int) (realHeight * 0.5f);
		camera.save();
		camera.rotateX(RolateY > 0 ? rolateY : -rolateY);
		camera.rotateY(RolateX < 0 ? rolateX : -rolateX);
		camera.getMatrix(matrix);
		camera.restore();
		

		if (RolateX > 0 && rolateX != 0) {
			matrix.preTranslate(-realWidth, -scaleY);
			matrix.postTranslate(realWidth, scaleY);
		} else if (RolateY > 0 && rolateY != 0) {
			matrix.preTranslate(-scaleX, -realHeight);
			matrix.postTranslate(scaleX, realHeight);
		} else if (RolateX < 0 && rolateX != 0) {
			matrix.preTranslate(-0, -scaleY);
			matrix.postTranslate(0, scaleY);
		} else if (RolateY < 0 && rolateY != 0) {
			matrix.preTranslate(-scaleX, -0);
			matrix.postTranslate(scaleX, 0);
		}

		
		setImageMatrix(matrix);
	}

}
