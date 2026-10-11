package com.github.mmin18.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import shahzod.projects.ozbektiliningizohliugati.R;

@SuppressWarnings("deprecation")
public class RealtimeBlurView extends View {

    private float mDownsampleFactor;
    private float mBlurRadius;
    private int mOverlayColor;

    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;
    private boolean mIsRendering;
    private final Rect mRectSrc = new Rect();
    private final Rect mRectDst = new Rect();
    private View mDecorView;
    private boolean mDifferentRoot;
    private final Paint mPaint;

    public RealtimeBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RealtimeBlurView);
        try {
            mBlurRadius = a.getDimension(R.styleable.RealtimeBlurView_realtimeBlurRadius,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()));
            mDownsampleFactor = a.getFloat(R.styleable.RealtimeBlurView_realtimeDownsampleFactor, 4.0f);
            mOverlayColor = a.getColor(R.styleable.RealtimeBlurView_realtimeOverlayColor, 0);
        } finally {
            a.recycle();
        }

        mPaint = new Paint();
    }

    public void setBlurRadius(float radius) {
        if (mBlurRadius != radius) {
            mBlurRadius = radius;
            invalidate();
        }
    }

    public void setDownsampleFactor(float factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }
        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            releaseBitmap();
            invalidate();
        }
    }

    public void setOverlayColor(int color) {
        if (mOverlayColor != color) {
            mOverlayColor = color;
            invalidate();
        }
    }

    private void releaseBitmap() {
        if (mBlurInput != null) {
            try { mBlurInput.destroy(); } catch (Exception ignored) {}
            mBlurInput = null;
        }
        if (mBlurOutput != null) {
            try { mBlurOutput.destroy(); } catch (Exception ignored) {}
            mBlurOutput = null;
        }
        if (mBitmapToBlur != null) {
            mBitmapToBlur.recycle();
            mBitmapToBlur = null;
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap.recycle();
            mBlurredBitmap = null;
        }
    }

    private void releaseScript() {
        if (mBlurScript != null) {
            try { mBlurScript.destroy(); } catch (Exception ignored) {}
            mBlurScript = null;
        }
        if (mRenderScript != null) {
            try { mRenderScript.destroy(); } catch (Exception ignored) {}
            mRenderScript = null;
        }
    }

    protected void release() {
        releaseBitmap();
        releaseScript();
    }

    protected boolean prepare() {
        if (mBlurRadius == 0) {
            release();
            return false;
        }

        float downsampleFactor = mDownsampleFactor;
        float radius = mBlurRadius / downsampleFactor;
        if (radius > 25) {
            downsampleFactor = downsampleFactor * radius / 25;
            radius = 25;
        }

        final int width = getWidth();
        final int height = getHeight();

        if (width <= 0 || height <= 0) {
            return false;
        }

        final int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
        final int scaledHeight = Math.max(1, (int) (height / downsampleFactor));

        if (mBlurringCanvas == null || mBlurredBitmap == null
                || mBlurredBitmap.getWidth() != scaledWidth
                || mBlurredBitmap.getHeight() != scaledHeight) {
            releaseBitmap();

            try {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                mBlurringCanvas = new Canvas(mBitmapToBlur);
                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                releaseBitmap();
                return false;
            }
        }

        if (mRenderScript == null) {
            try {
                mRenderScript = RenderScript.create(getContext());
                mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            } catch (Throwable e) {
                releaseScript();
            }
        }

        if (mBlurScript != null) {
            try {
                mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                        Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
                mBlurScript.setRadius(Math.max(1, Math.min(25, radius)));
            } catch (Throwable e) {
                releaseScript();
            }
        }

        return true;
    }

    protected void blur(Bitmap src, Bitmap dest) {
        if (mBlurScript != null && mBlurInput != null && mBlurOutput != null) {
            try {
                mBlurInput.copyFrom(src);
                mBlurScript.setInput(mBlurInput);
                mBlurScript.forEach(mBlurOutput);
                mBlurOutput.copyTo(dest);
                return;
            } catch (Throwable e) {
            }
        }
        fastBlur(src, dest, (int) Math.max(1, mBlurRadius / mDownsampleFactor));
    }

    private void fastBlur(Bitmap src, Bitmap dest, int radius) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pix = new int[w * h];
        src.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] r1;

        for (y = 0; y < h; y++) {
            rsum = gsum = bsum = 0;
            int rsumout = 0, gsumout = 0, bsumout = 0;
            int rsumin = 0, gsumin = 0, bsumin = 0;

            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                r1 = stack[i + radius];
                r1[0] = (p & 0xff0000) >> 16;
                r1[1] = (p & 0x00ff00) >> 8;
                r1[2] = (p & 0x0000ff);
                int rbs = radius + 1 - Math.abs(i);
                rsum += r1[0] * rbs;
                gsum += r1[1] * rbs;
                bsum += r1[2] * rbs;
                if (i > 0) {
                    rsumin += r1[0];
                    gsumin += r1[1];
                    bsumin += r1[2];
                } else {
                    rsumout += r1[0];
                    gsumout += r1[1];
                    bsumout += r1[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= rsumout;
                gsum -= gsumout;
                bsum -= bsumout;

                stackstart = stackpointer - radius + div;
                r1 = stack[stackstart % div];

                rsumout -= r1[0];
                gsumout -= r1[1];
                bsumout -= r1[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                r1[0] = (p & 0xff0000) >> 16;
                r1[1] = (p & 0x00ff00) >> 8;
                r1[2] = (p & 0x0000ff);

                rsumin += r1[0];
                gsumin += r1[1];
                bsumin += r1[2];

                rsum += rsumin;
                gsum += gsumin;
                bsum += bsumin;

                stackpointer = (stackpointer + 1) % div;
                r1 = stack[stackpointer % div];

                rsumout += r1[0];
                gsumout += r1[1];
                bsumout += r1[2];

                rsumin -= r1[0];
                gsumin -= r1[1];
                bsumin -= r1[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rsum = gsum = bsum = 0;
            int rsumout = 0, gsumout = 0, bsumout = 0;
            int rsumin = 0, gsumin = 0, bsumin = 0;

            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                r1 = stack[i + radius];
                r1[0] = r[yi];
                r1[1] = g[yi];
                r1[2] = b[yi];
                int rbs = radius + 1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += r[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rsumin += r1[0];
                    gsumin += r1[1];
                    bsumin += r1[2];
                } else {
                    rsumout += r1[0];
                    gsumout += r1[1];
                    bsumout += r1[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= rsumout;
                gsum -= gsumout;
                bsum -= bsumout;

                stackstart = stackpointer - radius + div;
                r1 = stack[stackstart % div];

                rsumout -= r1[0];
                gsumout -= r1[1];
                bsumout -= r1[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + radius + 1, hm) * w;
                }
                p = x + vmin[y];

                r1[0] = r[p];
                r1[1] = g[p];
                r1[2] = b[p];

                rsumin += r1[0];
                gsumin += r1[1];
                bsumin += r1[2];

                rsum += rsumin;
                gsum += gsumin;
                bsum += bsumin;

                stackpointer = (stackpointer + 1) % div;
                r1 = stack[stackpointer % div];

                rsumout += r1[0];
                gsumout += r1[1];
                bsumout += r1[2];

                rsumin -= r1[0];
                gsumin -= r1[1];
                bsumin -= r1[2];

                yi += w;
            }
        }

        dest.setPixels(pix, 0, w, 0, 0, w, h);
    }

    private final ViewTreeObserver.OnPreDrawListener mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            int[] locations = new int[2];
            Bitmap oldBmp = mBlurredBitmap;
            View decor = mDecorView;
            if (decor != null && isShown() && prepare()) {
                boolean hasBlurred = mBlurredBitmap != oldBmp;
                decor.getLocationOnScreen(locations);
                int x = -locations[0];
                int y = -locations[1];

                getLocationOnScreen(locations);
                x += locations[0];
                y += locations[1];

                mBitmapToBlur.eraseColor(mOverlayColor & 0xffffff);

                int rc = mBlurringCanvas.save();
                mIsRendering = true;
                RENDERING_COUNT++;
                try {
                    mBlurringCanvas.scale(1.0f / mDownsampleFactor, 1.0f / mDownsampleFactor);
                    mBlurringCanvas.translate(-x, -y);
                    if (decor.getBackground() != null) {
                        decor.getBackground().draw(mBlurringCanvas);
                    }
                    decor.draw(mBlurringCanvas);
                } catch (Exception e) {
                } finally {
                    mIsRendering = false;
                    RENDERING_COUNT--;
                    mBlurringCanvas.restoreToCount(rc);
                }

                blur(mBitmapToBlur, mBlurredBitmap);

                if (hasBlurred || mDifferentRoot) {
                    invalidate();
                }
            }
            return true;
        }
    };

    protected View getDecorView() {
        Context ctx = getContext();
        for (int i = 0; i < 4 && ctx instanceof ContextWrapper && !(ctx instanceof Activity); i++) {
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        if (ctx instanceof Activity) {
            return ((Activity) ctx).getWindow().getDecorView();
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDecorView = getDecorView();
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
            mDifferentRoot = mDecorView.getRootView() != getRootView();
            if (mDifferentRoot) {
                mDecorView.postInvalidate();
            }
        } else {
            mDifferentRoot = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().removeOnPreDrawListener(mPreDrawListener);
        }
        release();
        super.onDetachedFromWindow();
    }

    private static int RENDERING_COUNT;

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mIsRendering) {
            return;
        }
        if (RENDERING_COUNT > 0) {
            return;
        }
        super.draw(canvas);
        drawBlurredBitmap(canvas, mBlurredBitmap, mOverlayColor);
    }

    protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap, int overlayColor) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.getWidth();
            mRectSrc.bottom = blurredBitmap.getHeight();
            mRectDst.right = getWidth();
            mRectDst.bottom = getHeight();
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null);
        }
        mPaint.setColor(overlayColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }
}
