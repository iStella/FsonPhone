package com.example.Fson.ToolsClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.Fson.R;

/**
 * Created by jiping on 8/10/17.
 */
@SuppressLint("DrawAllocation")
public class BorderTextView extends AppCompatTextView{
    public BorderTextView(Context context) {
        super(context);
    }
    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int sroke_width = 1;
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        //  将边框设为黑色
        paint.setColor(getResources().getColor(R.color.lightgray));
        //  画TextView的4个边
        //canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);       //上
        //canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);      //左
        //canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);        //右
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);     //下
        super.onDraw(canvas);
    }
}
