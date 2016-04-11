package hu.pe.thinhhoang.aaosync.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import hu.pe.thinhhoang.aaosync.R;

/**
 * TODO: document your custom view class.
 */
public class ClassIndicatorView extends View {
    private int mSoTiet; // Number of squares to draw
    private int mResourceForeground; // Color of the squares
    private int mResourceBackground;
    private float scale;
    private float mSquareWidth; // Width of a square
    private float mSquareHeight; // Height of a square
    private int pSquareWidth;
    private int pSquareHeight;
    private float mGap; // Distance between 2 squares
    private int pGap;
    private Paint activeSquarePaint;
    private Paint inactiveSquarePaint;

    public ClassIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public ClassIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale=context.getResources().getDisplayMetrics().density;
        init(attrs, 0);
    }

    public ClassIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ClassIndicatorView, defStyle, 0);

        mSoTiet = a.getInt(
                R.styleable.ClassIndicatorView_numberOfClasses, 0);

        mResourceForeground = a.getColor(
                R.styleable.ClassIndicatorView_foregroundColor, ContextCompat.getColor(this.getContext(), R.color.green));

        mSquareWidth = a.getFloat(R.styleable.ClassIndicatorView_squareSizeWidth, 10f);
        mSquareHeight = a.getFloat(R.styleable.ClassIndicatorView_squareSizeHeight, 10f);
        mGap=a.getFloat(R.styleable.ClassIndicatorView_gap, 5f);

        pSquareWidth = (int) (mSquareWidth * scale +0.5f);
        pSquareHeight = (int) (mSquareHeight * scale + 0.5f);
        pGap=(int) (mGap*scale+0.5f);

        mResourceBackground = Color.LTGRAY;

        a.recycle();

        // Set up a default Paint objects
        activeSquarePaint = new Paint();
        activeSquarePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        activeSquarePaint.setStyle(Paint.Style.FILL);
        activeSquarePaint.setColor(mResourceForeground);
        inactiveSquarePaint = new Paint();
        inactiveSquarePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        inactiveSquarePaint.setStyle(Paint.Style.FILL);
        inactiveSquarePaint.setColor(mResourceBackground);
        // Update TextPaint and text measurements from attributes
        // invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int i;
        for(i=1;i<=mSoTiet;i++)
        {
            canvas.drawRect((i-1)*(pSquareWidth+pGap),0,(i-1)*(pSquareWidth+pGap)+pSquareWidth,pSquareHeight,activeSquarePaint);
        }
        for(int k=i; k<=4; k++)
        {
            canvas.drawRect((k-1)*(pSquareWidth+pGap),0,(k-1)*(pSquareWidth+pGap)+pSquareWidth,pSquareHeight,inactiveSquarePaint);
        }
    }

    public void setmSoTiet(int soTiet)
    {
        mSoTiet = soTiet;
        invalidate();
        requestLayout();
    }
}
