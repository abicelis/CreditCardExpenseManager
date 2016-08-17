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

import ve.com.abicelis.creditcardexpensemanager.ocr.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock>{

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Rect mOcrWindowBoundingBox;             //The actual ocr window bounding box
    private Rect mOcrWindowContainerBoundingBox;            //The container of the detector box


    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
        mOcrWindowBoundingBox = new Rect();
        mOcrWindowContainerBoundingBox = new Rect();
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
        StringBuilder detectedText = new StringBuilder();
        OcrGraphic graphic;

        for (Text text : lines) {
            if(!itemBelowOrAboveWindowBoundingBox(text)) {
                if(itemInsideWindowBoundingBox(text)) {
                    graphic = new OcrGraphic(mGraphicOverlay, text, true);
                    detectedText.append(text.getValue() + "\r\n");
                    mGraphicOverlay.add(graphic);

                } else {                                                                 //If Text is a Line, try to get elements (words) which may be inside the window bounding box
                    if(text instanceof Line) {
                        TextLine textLine = new TextLine();
                        for (int i = 0; i < text.getComponents().size(); ++i) {
                            Element element = (Element) text.getComponents().get(i);
                            if(itemInsideWindowBoundingBox(element)) {
                                textLine.addComponent(element);
                            }
                        }
                        graphic = new OcrGraphic(mGraphicOverlay, textLine, true);
                        detectedText.append(textLine.getValue() + "\r\n");
                        mGraphicOverlay.add(graphic);
                    }
                }
            }
            else {
                graphic = new OcrGraphic(mGraphicOverlay, text, false);
                mGraphicOverlay.add(graphic);
            }

        }

    //Draw the bounding box
    //graphic = new OcrGraphic(mGraphicOverlay, mOcrWindowBoundingBox);
    //mGraphicOverlay.add(graphic);

















//        List<TextLine> paragraphs = new ArrayList<>();

//        TextBlock closestTextBlock = getClosestTextBlockOverlappingDetectorBoundingBox(items);
//
//
//        //Chop up the TextBlocks which contain more Components (Lines?) than MAX_LINES_PER_PARAGRAPH
//        // into TextParagraphs
//        for (int i = 0; i < items.size(); ++i) {
//            TextBlock item = items.valueAt(i);
//            if(item != null && item.getValue() != null && itemInsideContainerDetectorBoundingBox(item.getBoundingBox())) {
//                int j = 0;
//                int componentCount = item.getComponents().size();
//                while(j < componentCount) {
//                    int availableLines = componentCount - j;
//                    int linesToAdd = (availableLines > MAX_LINES_PER_PARAGRAPH ? MAX_LINES_PER_PARAGRAPH : availableLines);
//
//                    TextLine paragraph = new TextLine();
//                    for (int k = j; k < j+linesToAdd; k++) {
//                        paragraph.addComponent(item.getComponents().get(k));
//                    }
//
//                    paragraphs.add(paragraph);
//                    j += linesToAdd;
//                }
//            }
//        }
//
//        //Get the paragraph closest to the center of the detectorBoundingBox
//        double selectedItemDistance = Double.MAX_VALUE;
//        TextLine selectedItem = null;
//        for (TextLine paragraph : paragraphs) {
//            double distance = getDistanceToCenterOfDetectorBoundingBox(paragraph);
//            if(distance < selectedItemDistance) {
//                selectedItem = paragraph;
//                selectedItemDistance = distance;
//            }
//        }
//
//        for (TextLine paragraph : paragraphs) {
//            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, paragraph, mDetectorBoundingBox, (paragraph.equals(selectedItem)));
//            mGraphicOverlay.add(graphic);
//        }














//        //Get the paragraph closest to the center of the detectorBoundingBox
//        double selectedItemDistance = Double.MAX_VALUE;
//        TextBlock selectedItem = null;
//        for (int i = 0; i < items.size(); ++i) {
//            TextBlock item = items.valueAt(i);
//            if(item != null && item.getValue() != null && getDistanceToCenterOfDetectorBoundingBox(item) < selectedItemDistance) {
//                selectedItem = item;
//                selectedItemDistance = getDistanceToCenterOfDetectorBoundingBox(item);
//            }
//        }
//
//        for (int i = 0; i < items.size(); ++i) {
//            TextBlock item = items.valueAt(i);
//            if (item != null && item.getValue() != null && itemInsideContainerDetectorBoundingBox(item.getBoundingBox())) {
//                Log.d("Processor", "Text detected! " + item.getValue());
//                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item, mDetectorBoundingBox, (item.equals(selectedItem)));
//                mGraphicOverlay.add(graphic);
//            }
//        }
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

    private boolean lineHasElementsInsideWindowBoundingBox(Line line) {

        for(int i = 0; i < line.getComponents().size(); ++i) {
            Element element = (Element) line.getComponents().get(i);
        }

        return false;
    }

   /* private double getDistanceToCenterOfDetectorBoundingBox(TextLine item) {
        Rect itemBounds = item.getBoundingBox();
        return Math.sqrt(Math.pow(itemBounds.exactCenterX() - mDetectorBoundingBox.exactCenterX(), 2) + Math.pow(itemBounds.exactCenterY() - mDetectorBoundingBox.exactCenterY(), 2));
    }

    private double getDistanceToCenterOfDetectorBoundingBox(TextBlock item) {
        Rect itemBounds = item.getBoundingBox();
        return Math.sqrt(Math.pow(itemBounds.exactCenterX() - mDetectorBoundingBox.exactCenterX(), 2) + Math.pow(itemBounds.exactCenterY() - mDetectorBoundingBox.exactCenterY(), 2));
    }


    private boolean itemIntersectsDetectorBoundingBox(Rect itemBounds) {
        return itemBounds.intersects(itemBounds.left, itemBounds.top, itemBounds.right, itemBounds.bottom);
    }

    private TextBlock getClosestTextBlockOverlappingDetectorBoundingBox(SparseArray<TextBlock> items) {

        //Get the paragraph closest to the center of the detectorBoundingBox
        double selectedItemDistance = Double.MAX_VALUE;
        TextBlock selectedItem = null;
        for(int i=0; i < items.size(); ++i) {
            TextBlock item = items.get(i);
            if(itemIntersectsDetectorBoundingBox(item.getBoundingBox())) {
                double distance = getDistanceToCenterOfDetectorBoundingBox(item);
                if(distance < selectedItemDistance) {
                    selectedItem = item;
                    selectedItemDistance = distance;
                }
            }
        }

        return selectedItem;
    }*/
}
