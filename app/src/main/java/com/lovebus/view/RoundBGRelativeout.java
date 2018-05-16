package com.lovebus.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import bus.android.com.lovebus.R;

public class RoundBGRelativeout extends RelativeLayout {
    /**
     * 默认圆角半径
     */
    private static final int DEFAULT_CORNER_RADIUS = 6;
    /**
     * 背景图片
     */
    private Bitmap mBg;
    /**
     * 圆角半径
     */
    private int mCornerRadius = DEFAULT_CORNER_RADIUS;

    public RoundBGRelativeout(Context context) {
        this(context, null);
    }

    public RoundBGRelativeout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundBGRelativeout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);//设置调用onDraw方法
        init(context, attrs, defStyle);
    }

    /**
     * 得到自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundBGRelativeout,defStyle,0);
        int indexCount = ta.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.RoundBGRelativeout_custom_background://得到自定义背景图片
                    int resourceId = ta.getResourceId(index,0);
                    mBg= BitmapFactory.decodeResource(getResources(), resourceId);
                    break;
                case R.styleable.RoundBGRelativeout_corner_radius://得到自定义圆角半径
                    mCornerRadius= (int) ta.getDimension(index,DEFAULT_CORNER_RADIUS);
                    break;
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (mBg!=null) {
            int width = getMeasuredWidth();//得到测量的高度
            int height = getMeasuredHeight();//得到测量的宽度
            mBg=Bitmap.createScaledBitmap(mBg, width,height ,false);//创建一个缩放到指定大小的bitmap
            canvas.drawBitmap(createRoundImage(mBg,width,height), 0,0,null);//绘制圆角背景
        }
        super.onDraw(canvas);//让RelativeLayout绘制自己
    }
    /**
     * 创建圆角图片
     * @param bitmap 源图片
     * @param width 高度
     * @param height 宽度
     * @return 圆角图片
     */
    private Bitmap createRoundImage(Bitmap bitmap,int width,int height) {
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿画笔
        Bitmap target = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);//创建一个bigmap
        Canvas canvas=new Canvas(target);//创建一个画布
        RectF rectF=new RectF(0, 0,width,height);//矩形
        //绘制圆角矩形
        canvas.drawRoundRect(rectF,mCornerRadius,mCornerRadius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//画笔模式
        canvas.drawBitmap(bitmap,0,0, paint);//将画笔
        return target;
    }
    /**
     * 设置背景图片
     * @param r 资源ID
     */
    public void setBGResource(int r){
        this.mBg=BitmapFactory.decodeResource(getResources(),r);
        invalidate();
    }

    /**
     * 设置背景图片
     * @param b bitmap
     */
    public void setBGBitmap(Bitmap b){
        this.mBg=b;
        invalidate();
    }
}