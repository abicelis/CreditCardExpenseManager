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

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

import ve.com.abicelis.creditcardexpensemanager.ocr.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock>{

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Rect mDetectionBoundingBox;             //The actual boundingbox of the detection box
    private Rect mDetectionContainerBoundingBox;    //The container of the detection box

    private static int MAX_LINES_PER_PARAGRAPH = 2;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
        mDetectionBoundingBox = new Rect();
    }

    public void setDetectionBoundingBox(Rect detectionBoundingBox) {
        mDetectionBoundingBox = detectionBoundingBox;
    }

    public void setContainerDetectionBoundingBox(Rect detectionContainerBoundingBox) {
        mDetectionContainerBoundingBox = detectionContainerBoundingBox;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        List<TextParagraph> paragraphs = new ArrayList<>();

        //Chop up the TextBlocks which contain more Components (Lines?) than MAX_LINES_PER_PARAGRAPH
        // into TextParagraphs
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if(item != null && item.getValue() != null && itemInsideContainerDetectionBoundingBox(item.getBoundingBox())) {
                int j = 0;
                int componentCount = item.getComponents().size();
                while(j < componentCount) {
                    int availableLines = componentCount - j;
                    int linesToAdd = (availableLines > MAX_LINES_PER_PARAGRAPH ? MAX_LINES_PER_PARAGRAPH : availableLines);

                    TextParagraph paragraph = new TextParagraph();
                    for (int k = j; k < j+linesToAdd; k++) {
                        paragraph.addComponent(item.getComponents().get(k));
                    }

                    paragraphs.add(paragraph);
                    j += linesToAdd;
                }
            }
        }


        //Get the paragraph closest to the center of the detectionBoundingBox
        double selectedItemDistance = Double.MAX_VALUE;
        TextParagraph selectedItem = null;
        for (TextParagraph paragraph : paragraphs) {
            double distance = getDistanceToCenterOfDetectionBoundingBox(paragraph);
            if(distance < selectedItemDistance) {
                selectedItem = paragraph;
                selectedItemDistance = distance;
            }
        }

        for (TextParagraph paragraph : paragraphs) {
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, paragraph, mDetectionBoundingBox, (paragraph.equals(selectedItem)));
            mGraphicOverlay.add(graphic);

        }

//        //Get the paragraph closest to the center of the detectionBoundingBox
//        double selectedItemDistance = Double.MAX_VALUE;
//        TextBlock selectedItem = null;
//        for (int i = 0; i < items.size(); ++i) {
//            TextBlock item = items.valueAt(i);
//            if(item != null && item.getValue() != null && getDistanceToCenterOfDetectionBoundingBox(item) < selectedItemDistance) {
//                selectedItem = item;
//                selectedItemDistance = getDistanceToCenterOfDetectionBoundingBox(item);
//            }
//        }
//
//        for (int i = 0; i < items.size(); ++i) {
//            TextBlock item = items.valueAt(i);
//            if (item != null && item.getValue() != null && itemInsideContainerDetectionBoundingBox(item.getBoundingBox())) {
//                Log.d("Processor", "Text detected! " + item.getValue());
//                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item, mDetectionBoundingBox, (item.equals(selectedItem)));
//                mGraphicOverlay.add(graphic);
//            }
//        }
    }

    private double getDistanceToCenterOfDetectionBoundingBox(TextParagraph item) {
        Rect itemBounds = item.getBoundingBox();
        return Math.sqrt(Math.pow(itemBounds.exactCenterX() - mDetectionBoundingBox.exactCenterX(), 2) + Math.pow(itemBounds.exactCenterY() - mDetectionBoundingBox.exactCenterY(), 2));
    }

//    private double getDistanceToCenterOfDetectionBoundingBox(TextBlock item) {
//        Rect itemBounds = item.getBoundingBox();
//        return Math.sqrt(Math.pow(itemBounds.exactCenterX() - mDetectionBoundingBox.exactCenterX(), 2) + Math.pow(itemBounds.exactCenterY() - mDetectionBoundingBox.exactCenterY(), 2));
//    }

    private boolean itemInsideContainerDetectionBoundingBox(Rect itemBounds) {
        if(itemBounds.bottom < mDetectionContainerBoundingBox.bottom)
            return true;
        return false;
    }
}
