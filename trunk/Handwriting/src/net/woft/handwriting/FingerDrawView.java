package net.woft.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Vector;

public class FingerDrawView extends View{

    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint borderPaint;
    private List<Point> points;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    private static final String LOGTAG = "FingerDrawView";
    protected static final Point STROKE_END = new Point(255, 0);
    protected static final Point CHAR_END = new Point(255, 255);

    public FingerDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xFF0000FF);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(6);

        borderPaint = new Paint();
        borderPaint.setColor(0xFFFF0000);
        borderPaint.setStrokeWidth(2);
        borderPaint.setStyle(Style.STROKE);

        bitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        points = new Vector<Point>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);
        canvas.drawRect(0, 0, 255, 255, borderPaint);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
        points.add(new Point((int)x, (int)y));
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        points.add(new Point((int)x, (int)y));
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        path.lineTo(mX, mY);
        // commit the path to our offscreen
        canvas.drawPath(path, paint);
        // kill this so we don't double draw
        path.reset();
        points.add(STROKE_END);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void resetView() {
        points.clear();
        bitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    protected void dump() {
        for(Point pt : points) {
            Log.v(LOGTAG, String.format("(%d, %d)", pt.x, pt.y));
        }
    }

    protected List<Point> getPoints() {
        return points;
    }
}
