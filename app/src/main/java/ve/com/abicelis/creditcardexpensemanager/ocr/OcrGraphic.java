/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ve.com.abicelis.creditcardexpensemanager.ocr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import ve.com.abicelis.creditcardexpensemanager.ocr.camera.GraphicOverlay;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final int SELECTED_COLOR = Color.GREEN;
    private static final int UNSELECTED_COLOR = Color.RED;

    private static final int DEBUG_SELECTED_TEXT_COLOR = Color.BLUE;
    private static final int DEBUG_UNSELECTED_TEXT_COLOR = Color.YELLOW;


    private static Paint sTextPaint;
    private static Paint sSelectedPaint;
    private static Paint sUnselectedPaint;
    private static Paint sDebugSelectedTextPaint;
    private static Paint sDebugUnselectedTextPaint;

    private Text mText = null;
    private Rect mOcrWindowBoundingBox = null;
    private boolean mIsSelected = false;

    private OcrGraphic(GraphicOverlay overlay) {
        super(overlay);

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(24.0f);
        }

        if (sSelectedPaint == null) {
            sSelectedPaint = new Paint();
            sSelectedPaint.setColor(SELECTED_COLOR);
            sSelectedPaint.setStyle(Paint.Style.STROKE);
            sSelectedPaint.setStrokeWidth(2.0f);
        }

        if (sUnselectedPaint == null) {
            sUnselectedPaint = new Paint();
            sUnselectedPaint.setColor(UNSELECTED_COLOR);
            sUnselectedPaint.setStyle(Paint.Style.STROKE);
            sUnselectedPaint.setStrokeWidth(2.0f);
        }
        if (sDebugSelectedTextPaint == null) {
            sDebugSelectedTextPaint = new Paint();
            sDebugSelectedTextPaint.setColor(DEBUG_SELECTED_TEXT_COLOR);
            sDebugSelectedTextPaint.setStyle(Paint.Style.STROKE);
            sDebugSelectedTextPaint.setStrokeWidth(2.0f);
        }
        if (sDebugUnselectedTextPaint == null) {
            sDebugUnselectedTextPaint = new Paint();
            sDebugUnselectedTextPaint.setColor(DEBUG_UNSELECTED_TEXT_COLOR);
            sDebugUnselectedTextPaint.setStyle(Paint.Style.STROKE);
            sDebugUnselectedTextPaint.setStrokeWidth(3.0f);
        }

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    OcrGraphic(GraphicOverlay overlay, Rect ocrWindowBoundingBox) {
        this(overlay);
        mOcrWindowBoundingBox = ocrWindowBoundingBox;
    }

    OcrGraphic(GraphicOverlay overlay, Text text, boolean isSelected) {
        this(overlay);

        mText = text;
        mIsSelected = isSelected;
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Text getTextBlock() {
        return mText;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        // Check if this graphic's text contains this point.
        if(mText == null) {
            return false;
        }

        RectF rect = new RectF(mText.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        if(mOcrWindowBoundingBox != null) {
            canvas.drawRect(mOcrWindowBoundingBox.left, mOcrWindowBoundingBox.top, mOcrWindowBoundingBox.right, mOcrWindowBoundingBox.bottom, sDebugSelectedTextPaint);
        }
        if(mText != null) {

            RectF rect = new RectF(mText.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            //canvas.drawRect(rect, sRectPaint);


            if (mIsSelected) {
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, sSelectedPaint);
                //canvas.drawRect(rect, sRectPaint);

                canvas.drawText(mText.getValue(), rect.left, rect.bottom, sTextPaint);
            } else {
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, sUnselectedPaint);
            }
        }

    }



}
