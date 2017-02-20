package ve.com.abicelis.creditcardexpensemanager.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by abice on 17/2/2017.
 */

public class HorizontalBar extends View {

    //Consts
    private static final int DEFAULT_TEXT_SIZE_PX = 26;
    private static final int DEFAULT_BACKGROUND_COLOR = R.color.horizontal_bar_default_background;
    private static final int DEFAULT_TEXT_COLOR = R.color.horizontal_bar_default_text;
    private static final int DEFAULT_TEXT_COLOR_IN = R.color.horizontal_bar_default_text_in;
    private static final int DEFAULT_TEXT_COLOR_OUT = R.color.horizontal_bar_default_text_out;
    private static final int DEFAULT_BAR_COLOR = R.color.horizontal_bar_default_bar;
    private static final int DEFAULT_BAR_HEIGHT = 40;           //Bar height in px
    private static final int DEFAULT_CANVAS_HEIGHT = 80;        //Canvas height in px when no height is given
    private static final int BAR_PADDING_X = 90;                //Bar padding on sides
    private static final int TEXT_PADDING_TOP = 90;             //Text from top of canvas
    private static final int BAR_TEXT_MARGIN = 30;              //Extra horizontal margin for mTextBar
    private static final int BAR_PADDING_TOP = 10;              //Bar from top of canvas
    private static final int BACKGROUND_EXTRA = 14;              //Background paint stroke = mBarHeightPx + BACKGROUND_EXTRA

    //Vars
    private int mCanvasHeight;
    private int mCanvasWidth;
    private int mBarLeftX;
    private int mBarProgressX;
    private int mBarRightX;
    private int mBarCenterY;

    //Attrs
    private String mTextLo;
    private String mTextHi;
    private String mTextBar;
    private float mTextSizePx;
    private int mTextColor;
    private int mBarTextColorIn;
    private int mBarTextColorOut;
    private int mBarHeightPx;
    private int mBarColor;
    private int mBarGradientColorStart;
    private int mBarGradientColorEnd;
    private int mProgressPercentage;
    private int mBackgroundColor;

    //Paints
    private Paint mTextPaint;
    private Paint mBarTextPaint;
    private Paint mBackgroundPaint;
    private Paint mBarPaint;

    public HorizontalBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalBar, 0, 0);
        try {
            mTextLo = a.getString(R.styleable.HorizontalBar_textLo);
            mTextHi = a.getString(R.styleable.HorizontalBar_textHi);
            mTextBar = a.getString(R.styleable.HorizontalBar_textBar);
            mTextSizePx = a.getInt(R.styleable.HorizontalBar_textSizePx, DEFAULT_TEXT_SIZE_PX);
            mTextColor = a.getColor(R.styleable.HorizontalBar_textColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR));
            mBarTextColorIn = a.getColor(R.styleable.HorizontalBar_barTextColorIn, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR_IN));
            mBarTextColorOut = a.getColor(R.styleable.HorizontalBar_barTextColorOut, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR_OUT));
            mBarHeightPx = a.getInt(R.styleable.HorizontalBar_barHeightPx, DEFAULT_BAR_HEIGHT);
            mBarColor = a.getColor(R.styleable.HorizontalBar_barColor, ContextCompat.getColor(context, DEFAULT_BAR_COLOR));
            mBarGradientColorStart = a.getColor(R.styleable.HorizontalBar_barGradientColorStart, -1);
            mBarGradientColorEnd = a.getColor(R.styleable.HorizontalBar_barGradientColorEnd, -1);
            mProgressPercentage = a.getInt(R.styleable.HorizontalBar_progressPercentage, 0);
            mBackgroundColor = a.getColor(R.styleable.HorizontalBar_backgroundColor, ContextCompat.getColor(context, DEFAULT_BACKGROUND_COLOR));

        } finally {
            a.recycle();
        }

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSizePx);

        mBarTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarTextPaint.setTextSize(mTextSizePx);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(mBarHeightPx + BACKGROUND_EXTRA);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStrokeWidth(mBarHeightPx);
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);

        if(mBarGradientColorStart == -1 || mBarGradientColorEnd == -1) {    //If gradient was not setup
            mBarPaint.setColor(mBarColor);
        }

    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        int xpad = getPaddingLeft() + getPaddingRight();
        int ypad = getPaddingTop() + getPaddingBottom();

        //Calculate usable canvas size
        mCanvasHeight = (h != 0 ? (h - ypad) : DEFAULT_CANVAS_HEIGHT);
        mCanvasWidth = w - xpad;

        mBarLeftX = getPaddingLeft() + BAR_PADDING_X;
        mBarRightX = getPaddingLeft() + mCanvasWidth - BAR_PADDING_X;
        mBarProgressX = mBarLeftX + (int)((mBarRightX - mBarLeftX)*((float)mProgressPercentage/100));
        if(mBarProgressX > mBarRightX)
            mBarProgressX = mBarRightX;
        mBarCenterY = getPaddingTop() + BAR_PADDING_TOP + (int)(mBarHeightPx*0.5);

        if(mBarGradientColorStart != -1 && mBarGradientColorEnd != -1) {    //If gradient was setup
            Shader shader = new LinearGradient(0, 0, mBarRightX, 0, mBarGradientColorStart, mBarGradientColorEnd, Shader.TileMode.MIRROR);
            mBarPaint.setShader(shader);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw background
        canvas.drawLine(mBarLeftX, mBarCenterY, mBarRightX, mBarCenterY, mBackgroundPaint);

        //Draw bar
        canvas.drawLine(mBarLeftX, mBarCenterY, mBarProgressX, mBarCenterY, mBarPaint);

        if(mTextLo != null)
            canvas.drawText(mTextLo, mBarLeftX, getPaddingTop() + TEXT_PADDING_TOP, mTextPaint);
        if(mTextHi != null)
            canvas.drawText(mTextHi, mBarRightX, getPaddingTop() + TEXT_PADDING_TOP, mTextPaint);
        if(mTextBar != null) {
            if(mBarTextPaint.measureText(mTextBar, 0, mTextBar.length()) > (mBarProgressX - mBarLeftX)) {    //Text is too big to fit inside bar
                mBarTextPaint.setTextAlign(Paint.Align.LEFT);
                mBarTextPaint.setColor(mBarTextColorOut);
                canvas.drawText(mTextBar, mBarProgressX+BAR_TEXT_MARGIN, mBarCenterY+((float)DEFAULT_BAR_HEIGHT/4), mBarTextPaint);
            }
            else {
                mBarTextPaint.setTextAlign(Paint.Align.RIGHT);
                mBarTextPaint.setColor(mBarTextColorIn);
                canvas.drawText(mTextBar, mBarProgressX, mBarCenterY+((float)DEFAULT_BAR_HEIGHT/4), mBarTextPaint);

            }
        }

    }

    public String getTextLo() {
        return mTextLo;
    }

    public String getTextHi() {
        return mTextHi;
    }

    public String getTextBar() {
        return mTextBar;
    }

    public int getProgressPercentage() {
        return mProgressPercentage;
    }

    public int getLineColor() {
        return mBackgroundColor;
    }

    public int getBarColor() {
        return mBarColor;
    }

    public int getBarGradientColorStart() {
        return mBarGradientColorStart;
    }

    public int getBarGradientColorEnd() {
        return mBarGradientColorEnd;
    }

    public void setTextLo(String mTextLo) {
        this.mTextLo = mTextLo;
        invalidate();
        requestLayout();
    }

    public void setTextHi(String mTextHi) {
        this.mTextHi = mTextHi;
        invalidate();
        requestLayout();
    }

    public void setTextBar(String mTextBar) {
        this.mTextBar = mTextBar;
        invalidate();
        requestLayout();
    }

    public void setProgressPercentage(int mProgressPercentage) {
        this.mProgressPercentage = mProgressPercentage;
        invalidate();
        requestLayout();
    }

    public void setLineColor(int mLineColor) {
        this.mBackgroundColor = mLineColor;
        invalidate();
        requestLayout();
    }

    public void setBarColor(int mBarColor) {
        this.mBarColor = mBarColor;
        invalidate();
        requestLayout();
    }

    public void setBarGradientColorStart(int mBarGradientColorStart) {
        this.mBarGradientColorStart = mBarGradientColorStart;
        invalidate();
        requestLayout();
    }

    public void setBarGradientColorEnd(int mBarGradientColorEnd) {
        this.mBarGradientColorEnd = mBarGradientColorEnd;
        invalidate();
        requestLayout();
    }
}
