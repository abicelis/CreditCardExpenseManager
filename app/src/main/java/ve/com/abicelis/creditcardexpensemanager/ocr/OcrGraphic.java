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

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final int LINE_COLOR = Color.BLUE;
    private static final int RECT_COLOR = Color.CYAN;
    private static final int DISTANCE_LINE_COLOR = Color.YELLOW;
    private static final int DISTANCE_LINE_SELECTED_COLOR = Color.GREEN;

    private static Paint sTextPaint;
    private static Paint sLinePaint;
    private static Paint sRectPaint;
    private static Paint sDistLinePaint;
    private static Paint sDistLineSelPaint;
    private final TextParagraph mText;
    private Rect mDetectionBoundingBox;
    private boolean mIsSelected;


    OcrGraphic(GraphicOverlay overlay, TextParagraph text, Rect detectionBoundingBox, boolean isSelected) {
        super(overlay);

        mText = text;
        mDetectionBoundingBox = detectionBoundingBox;
        mIsSelected = isSelected;

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(34.0f);
        }

        if (sLinePaint == null) {
            sLinePaint = new Paint();
            sLinePaint.setColor(LINE_COLOR);
            sLinePaint.setStyle(Paint.Style.STROKE);
            sLinePaint.setStrokeWidth(2.0f);
        }
        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(RECT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(2.0f);
        }
        if (sDistLinePaint == null) {
            sDistLinePaint = new Paint();
            sDistLinePaint.setColor(DISTANCE_LINE_COLOR);
            sDistLinePaint.setStyle(Paint.Style.STROKE);
            sDistLinePaint.setStrokeWidth(3.0f);
        }
        if (sDistLineSelPaint == null) {
            sDistLineSelPaint = new Paint();
            sDistLineSelPaint.setColor(DISTANCE_LINE_SELECTED_COLOR);
            sDistLineSelPaint.setStyle(Paint.Style.STROKE);
            sDistLineSelPaint.setStrokeWidth(6.0f);
        }


        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextParagraph getTextBlock() {
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
        // TODO: Draw the text onto the canvas.
        if(mText == null) {
            return;
        }



        RectF rect = new RectF(mText.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);


        if(mIsSelected) {
            canvas.drawCircle(rect.centerX(), rect.centerY(), 15f, sDistLineSelPaint);
            canvas.drawLine(rect.centerX(), rect.centerY(), mDetectionBoundingBox.centerX(), mDetectionBoundingBox.centerY(), sDistLineSelPaint);

            // Break the text into multiple lines and draw each one according to its own bounding box.
            List<? extends Text> textComponents = mText.getComponents();

            for(Text currentText : textComponents) {

                RectF textRect = new RectF(currentText.getBoundingBox());
                textRect.left = translateX(textRect.left);
                textRect.top = translateY(textRect.top);
                textRect.right = translateX(textRect.right);
                textRect.bottom = translateY(textRect.bottom);
                canvas.drawLine(textRect.left, textRect.bottom, textRect.right, textRect.bottom, sLinePaint);
                //canvas.drawRect(rect, sRectPaint);

                float left = translateX(currentText.getBoundingBox().left);
                float bottom = translateY(currentText.getBoundingBox().bottom);
                canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
            }

        } else {
            canvas.drawCircle(rect.centerX(), rect.centerY(), 6f, sDistLinePaint);
            canvas.drawLine(rect.centerX(), rect.centerY(), mDetectionBoundingBox.centerX(), mDetectionBoundingBox.centerY(), sDistLinePaint);
        }

    }



    private TextParagraph getOcrLinesInsideDetectionBoundingBox(TextBlock item) {
        int maxLines = 2;
        int detectedLines = 0;
        TextParagraph texts = new TextParagraph();
        Text text;

        for (int i = 0; i < item.getComponents().size(); ++i) {
            detectedLines++;
            text = item.getComponents().get(i);

            if(onePointInsideDetectionBoundingBox(text.getCornerPoints())) {
                texts.addComponent(text);
            }

            if(detectedLines == maxLines) {
                break;
            }
        }
        return texts;
    }


    private boolean onePointInsideDetectionBoundingBox(Point[] points) {
        for (Point point : points) {
            if(point.x > mDetectionBoundingBox.left &&
                    point.x < mDetectionBoundingBox.right &&
                    point.y < mDetectionBoundingBox.bottom &&
                    point.y > mDetectionBoundingBox.top)
                return true;
        }
        return false;
    }
}
