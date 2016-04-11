package hu.pe.thinhhoang.aaosync.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import hu.pe.thinhhoang.aaosync.R;

/**
 * TODO: document your custom view class.
 */
public class PageIndicatorView extends LinearLayout {
    private int mTotalPages;
    private int mCurrentPage;
    private float mCircleSize;
    private int pCircleSize;
    private float mCircleDist;
    private int pCircleDist;
    private float scale;
    private Paint activeCirclePaint;
    private Paint inactiveCirclePaint;

    public PageIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale=context.getResources().getDisplayMetrics().density;
        init(attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PageIndicatorView, defStyle, 0);

        mTotalPages = a.getInt(
                R.styleable.PageIndicatorView_totalPages, 0);
        mCurrentPage = a.getInt(
                R.styleable.PageIndicatorView_currentPage, 0);

        Log.v("AAOSync", "4 Density is "+scale);

        mCircleSize=a.getFloat(R.styleable.PageIndicatorView_circleSize, 0f);
        pCircleSize=(int) (mCircleSize*scale + 0.5f);
        mCircleDist=a.getFloat(R.styleable.PageIndicatorView_circleDist,0f);
        pCircleDist=(int) (mCircleDist*scale + 0.5f);

        a.recycle();

        // Prepare the painting materials
        activeCirclePaint = new Paint();
        activeCirclePaint.setStyle(Paint.Style.FILL);
        activeCirclePaint.setColor(Color.WHITE);
        inactiveCirclePaint=new Paint();
        inactiveCirclePaint.setStyle(Paint.Style.FILL);
        inactiveCirclePaint.setColor(Color.parseColor("#80FFFFFF"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the circles before the active one
        int i;
        for (i=1;i<=mCurrentPage-1;i++)
        {
            canvas.drawCircle((2*i+1)*pCircleSize+i*pCircleDist,pCircleSize,pCircleSize,inactiveCirclePaint);
        }
        // Draw the active circle
        canvas.drawCircle((2*i+1)*pCircleSize+i*pCircleDist,pCircleSize,pCircleSize,activeCirclePaint);
        i++;
        // Draw the circles after the active one
        for (int k=i;k<=mTotalPages;k++)
        {
            canvas.drawCircle((2*k+1)*pCircleSize+k*pCircleDist,pCircleSize,pCircleSize,inactiveCirclePaint);
        }
    }

    public void setCurrentPage(int p)
    {
        mCurrentPage=p;
        invalidate();
        requestLayout();
    }

    public void setTotalPages(int p)
    {
        mTotalPages=p;
        invalidate();
    }

}
