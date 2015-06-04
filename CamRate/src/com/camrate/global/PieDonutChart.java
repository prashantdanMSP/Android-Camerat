package com.camrate.global;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.camrate.R;

/**
 * Custom view that shows a pie chart and, optionally, a label.
 */
public class PieDonutChart extends ViewGroup {

	private List<Item> mData = new ArrayList<Item>();
	private int mTotal = 0;

	private RectF mPieBounds = new RectF();

	private Paint mPiePaint;
	private Paint mCirclePaint;

	private PieView mPieView;
	private CircleView mCircleView;

	private float mRatioCircleInside = 0.5f;
	private int mColorCircleInside;
	private float mHighlightStrength = 1.15f;
	private int mPieRotation;

	/**
	 * Class constructor taking only a context. Use this constructor to create
	 * {@link PieDonutChart} objects from your own code.
	 * 
	 * @param context
	 */
	public PieDonutChart(Context context) {
		super(context);
		init();
	}

	/**
	 * Class constructor taking a context and an attribute set. This constructor
	 * is used by the layout engine to construct a {@link PieDonutChart} from a
	 * set of XML attributes.
	 * 
	 * @param context
	 * @param attrs
	 *            An attribute set which can contain attributes from
	 *            {@link com.PieDonutChart.chartdemo.charting.R.styleable.PieChart}
	 *            as well as attributes inherited from {@link android.view.View}
	 *            .
	 */
	public PieDonutChart(Context context, AttributeSet attrs) {
		super(context, attrs);

		// attrs contains the raw values for the XML attributes
		// that were specified in the layout, which don't include
		// attributes set by styles or themes, and which may have
		// unresolved references. Call obtainStyledAttributes()
		// to get the final values for each attribute.
		//
		// This call uses R.styleable.PieChart, which is an array of
		// the custom attributes that were declared in attrs.xml.
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PieDonutChart, 0, 0);

		try {
			// Retrieve the values from the TypedArray and store into
			// fields of this class.
			//
			// The R.styleable.PieChart_* constants represent the index for
			// each custom attribute in the R.styleable.PieChart array.
			mHighlightStrength = a.getFloat(
					R.styleable.PieDonutChart_highlightStrength, 1.0f);
			mRatioCircleInside = a.getFloat(
					R.styleable.PieDonutChart_ratioCircleInside, 0.0f);
			mColorCircleInside = a.getColor(
					R.styleable.PieDonutChart_colorCircleInside, 0xffffffff);
			mPieRotation = a.getInt(R.styleable.PieDonutChart_pieRotation, 0);
		} finally {
			// release the TypedArray so that it can be reused.
			a.recycle();
		}

		init();
	}

	/**
	 * Add a new data item to this view. Adding an item adds a slice to the pie
	 * whose size is proportional to the item's value. As new items are added,
	 * the size of each existing slice is recalculated so that the proportions
	 * remain correct.
	 * 
	 * @param value
	 *            The value of this item.
	 * @param color
	 *            The ARGB color of the pie slice associated with this item.
	 * @return The index of the newly added item.
	 */
	public int addItem(int value, int color) {
		Item it = new Item();
		it.mColor = color;
		it.mValue = value;

		// Calculate the highlight color. Saturate at 0xff to make sure that
		// high values
		// don't result in aliasing.
		it.mHighlight = Color
				.argb(0xff, Math.min(
						(int) (mHighlightStrength * (float) Color.red(color)),
						0xff),
						Math.min((int) (mHighlightStrength * (float) Color
								.green(color)), 0xff), Math.min(
								(int) (mHighlightStrength * (float) Color
										.blue(color)), 0xff));
		mTotal += value;

		mData.add(it);

		onDataChanged();

		return mData.size() - 1;
	}

	public void deleteItems() {
		mTotal = 0;
		mData.clear();
	}

	public void setRatioCircleInside(float ratioCircleInside) {
		this.mRatioCircleInside = ratioCircleInside;
		onDataChanged();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Do nothing. Do not call the superclass method--that would start a
		// layout pass
		// on this view's children. PieChart lays out its children in
		// onSizeChanged().
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Try for a width based on our minimum
		int minw = getPaddingLeft() + getPaddingRight();

		int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

		// Whatever the width ends up being, ask for a height that would let the
		// pie
		// get as big as it can
		int minh = w + getPaddingBottom() + getPaddingTop();
		int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);

		setMeasuredDimension(w, h);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		//
		// Set dimensions for pie chart
		//
		// Account for padding
		float xpad = (float) (getPaddingLeft() + getPaddingRight());
		float ypad = (float) (getPaddingTop() + getPaddingBottom());

		// Account for the view
		float ww = (float) w - xpad;
		float hh = (float) h - ypad;

		// Figure out how big we can make the pie (it's a square).
		float diameter = Math.min(ww, hh);
		mPieBounds = new RectF(0.0f, 0.0f, diameter, diameter);
		mPieBounds.offsetTo(getPaddingLeft(), getPaddingTop());

		// Lay out the child view that actually draws the pie.
		mPieView.layout((int) mPieBounds.left, (int) mPieBounds.top,
				(int) mPieBounds.right, (int) mPieBounds.bottom);
		mPieView.setPivot(mPieBounds.width() / 2, mPieBounds.height() / 2);

		mCircleView.layout((int) mPieBounds.left, (int) mPieBounds.top,
				(int) mPieBounds.right, (int) mPieBounds.bottom);

		onDataChanged();
	}

	/**
	 * Do all of the recalculations needed when the data array changes.
	 */
	private void onDataChanged() {
		try {
			// When the data changes, we have to recalculate
			// all of the angles.
			int currentAngle = 0;
			for (Item it : mData) {
				it.mStartAngle = currentAngle;

				// BL : mini hack for having the last item to close the pie
				if (it.equals(mData.get(mData.size() - 1))) {
					it.mEndAngle = 360;
				} else {
					it.mEndAngle = currentAngle + it.mValue * 360 / mTotal;
				}
				currentAngle = it.mEndAngle;

				// Recalculate the gradient shaders. There are
				// three values in this gradient, even though only
				// two are necessary, in order to work around
				// a bug in certain versions of the graphics engine
				// that expects at least three values if the
				// positions array is non-null.
				//
				it.mShader = new SweepGradient(mPieBounds.width() / 2.0f,
						mPieBounds.height() / 2.0f, new int[] { it.mHighlight,
								it.mHighlight, it.mColor, it.mColor, },
						new float[] { 0, (float) (360 - it.mEndAngle) / 360.0f,
								(float) (360 - it.mStartAngle) / 360.0f, 1.0f });
			}
			if (Build.VERSION.SDK_INT < 11) {
				invalidate();
			}
			mPieView.decelerate();
		} catch (ArithmeticException e) {
		}
	}

	/**
	 * Initialize the control. This code is in a separate method so that it can
	 * be called from both constructors.
	 */
	private void init() {
		// Force the background to software rendering because otherwise the Blur
		// filter won't work.
		setLayerToSW(this);

		// Set up the paint for the circle
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(mColorCircleInside);

		// Set up the paint for the pie slices
		mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPiePaint.setStyle(Paint.Style.FILL);

		// Add a child view to draw the pie. Putting this in a child view
		// makes it possible to draw it on a separate hardware layer that
		// rotates
		// independently
		mPieView = new PieView(getContext());
		mPieView.setBackgroundColor(Color.TRANSPARENT);
		addView(mPieView);
		mPieView.rotateTo(mPieRotation);

		// The circle doesn't need hardware acceleration, but in order to show
		// up
		// in front of the pie it also needs to be on a separate view.
		mCircleView = new CircleView(getContext());
		mCircleView.setBackgroundColor(Color.TRANSPARENT);
		addView(mCircleView);

		// In edit mode it's nice to have some demo data, so add that here.
		/*
		 * if (this.isInEditMode()) { Resources res = getResources(); addItem(3,
		 * res.getColor(R.color.holo_blue_light)); addItem(4,
		 * res.getColor(R.color.holo_green_light)); addItem(2,
		 * res.getColor(R.color.holo_red_light)); }
		 */
	}

	@SuppressLint("NewApi")
	private void setLayerToSW(View v) {
		if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/**
	 * Internal child class that draws the pie chart onto a separate hardware
	 * layer when necessary.
	 */
	private class PieView extends View {
		// Used for SDK < 11
		private float mRotation = 0;
		private Matrix mTransform = new Matrix();
		private PointF mPivot = new PointF();
		private RectF mBounds;

		/**
		 * Construct a PieView
		 * 
		 * @param context
		 */
		public PieView(Context context) {
			super(context);
		}

		/**
		 * Disable hardware acceleration (releases memory)
		 */
		public void decelerate() {
			setLayerToSW(this);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (Build.VERSION.SDK_INT < 11) {
				mTransform.set(canvas.getMatrix());
				mTransform.preRotate(mRotation, mPivot.x, mPivot.y);
				canvas.setMatrix(mTransform);
			}

			for (Item it : mData) {
				mPiePaint.setShader(it.mShader);
				canvas.drawArc(mBounds, 360 - it.mEndAngle, it.mEndAngle
						- it.mStartAngle, true, mPiePaint);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			mBounds = new RectF(0, 0, w, h);
		}

		@SuppressLint("NewApi")
		public void rotateTo(float pieRotation) {
			mRotation = pieRotation;
			if (Build.VERSION.SDK_INT >= 11) {
				setRotation(pieRotation);
			} else {
				invalidate();
			}
		}

		@SuppressLint("NewApi")
		public void setPivot(float x, float y) {
			mPivot.x = x;
			mPivot.y = y;
			if (Build.VERSION.SDK_INT >= 11) {
				setPivotX(x);
				setPivotY(y);
			} else {
				invalidate();
			}
		}
	}

	/**
	 * Maintains the state for a data item.
	 */
	private class Item {
		public int mValue;
		public int mColor;

		// computed values
		public int mStartAngle;
		public int mEndAngle;

		public int mHighlight;
		public Shader mShader;
	}

	/**
	 * View that draws the white circle on top of the pie chart
	 */
	private class CircleView extends View {
		private RectF mBounds;

		/**
		 * Construct a PointerView object
		 * 
		 * @param context
		 */
		public CircleView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawCircle(mBounds.centerX(), mBounds.centerY(),
					mBounds.width() * 0.5f * mRatioCircleInside, mCirclePaint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			mBounds = new RectF(0, 0, w, h);
		}
	}
}