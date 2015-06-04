package com.camrate.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.widget.TextView;

public class ArcTextView extends TextView {
	private Path circle;
	private Paint cPaint;
	private Paint tPaint;
	String finalstr;

	public ArcTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		circle = new Path();
		circle.addCircle(350, 420, 150, Direction.CCW);

		cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cPaint.setStyle(Paint.Style.STROKE);
		cPaint.setColor(Color.LTGRAY);
		cPaint.setStrokeWidth(3);

		tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		tPaint.setColor(Color.WHITE);
		tPaint.setTextSize(30);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		finalstr = (String) text;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawTextOnPath("Hello", circle, 485, 20, tPaint);
		invalidate();
	}

}
