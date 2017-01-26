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

import android.graphics.Rect;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Element;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.ocr.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock>{

    private static final String TAG = "OcrDetectorProcessor";

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private OcrCreateExpenseActivity mCallerActivity;
    private Rect mOcrWindowBoundingBox;                     //The actual ocr window bounding box
    private Rect mOcrWindowContainerBoundingBox;            //The container of the detector box
    private String mDetectedText;


    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, OcrCreateExpenseActivity callerActivity) {
        mGraphicOverlay = ocrGraphicOverlay;
        mCallerActivity = callerActivity;
        mOcrWindowBoundingBox = new Rect();
        mOcrWindowContainerBoundingBox = new Rect();
        mDetectedText = "";
    }

    public void setOcrWindowBoundingBox(Rect boundingBox) {
        mOcrWindowBoundingBox = boundingBox;
    }

    public void setOcrWindowContainerBoundingBox(Rect boundingBox) {
        mOcrWindowContainerBoundingBox = boundingBox;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        List<Text> lines = new ArrayList<>();


        //Get rid of the textblocks, extract the components (Lines)
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                List<? extends Text> l = item.getComponents();
                for (int j = 0; j < l.size(); ++j) {
                    Text text = l.get(j);
                    if(itemInsideContainerWindowBoundingBox(text)) {
                        lines.add(text);
                    }

                }
            }
        }

        //Evaluate every line, determine which texts are inside the window bounding box
        OcrGraphic graphic;
        StringBuilder sb = new StringBuilder();     //Clear the string

        for (Text text : lines) {
            if(!itemBelowOrAboveWindowBoundingBox(text)) {
                if(itemInsideWindowBoundingBox(text)) {
                    graphic = new OcrGraphic(mGraphicOverlay, text, true);
                    sb.append(text.getValue()).append("\r\n");
                    mGraphicOverlay.add(graphic);

                } else {           //If Text is a Line, try to get elements (words) which may be inside the window bounding box
                    if(text instanceof Line) {
                        TextLine textLine = new TextLine();
                        for (int i = 0; i < text.getComponents().size(); ++i) {
                            Element element = (Element) text.getComponents().get(i);
                            if(itemInsideWindowBoundingBox(element)) {
                                textLine.addComponent(element);
                            } else {
                                graphic = new OcrGraphic(mGraphicOverlay, element, false);
                                mGraphicOverlay.add(graphic);
                            }
                        }
                        graphic = new OcrGraphic(mGraphicOverlay, textLine, true);
                        sb.append(textLine.getValue()).append(" ");

                        mGraphicOverlay.add(graphic);
                    }
                }
            }
            else {
                graphic = new OcrGraphic(mGraphicOverlay, text, false);
                mGraphicOverlay.add(graphic);
            }

        }

        mDetectedText = sb.toString();
        //Log.d(TAG, "DETECTED TEXT " + mDetectedText);
        mCallerActivity.setNewDetectedText(mDetectedText);

        //Draw the bounding box
        graphic = new OcrGraphic(mGraphicOverlay, mOcrWindowBoundingBox);
        mGraphicOverlay.add(graphic);

    }

    private boolean itemInsideContainerWindowBoundingBox(Text item) {
        if(item.getBoundingBox().bottom < mOcrWindowContainerBoundingBox.bottom)
            return true;
        return false;
    }

    private boolean itemBelowOrAboveWindowBoundingBox(Text item) {
        if(item.getBoundingBox().bottom > mOcrWindowBoundingBox.bottom ||
                item.getBoundingBox().top < mOcrWindowBoundingBox.top)
            return true;
        return false;
    }

    private boolean itemInsideWindowBoundingBox(Text item) {
        return mOcrWindowBoundingBox.contains(item.getBoundingBox().left, item.getBoundingBox().top, item.getBoundingBox().right, item.getBoundingBox().bottom);
    }

}
