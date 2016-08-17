package ve.com.abicelis.creditcardexpensemanager.ocr;


import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.gms.vision.text.Element;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14/8/2016.
 */
public class TextLine implements Text {

    List<Element> mElements;
    Rect mBoundingBox = null;

    public TextLine() {
        mElements = new ArrayList<>();
    }

    public void addComponent(Element element) {
        mElements.add(element);
    }

    public List<Element> getComponents() {
        return mElements;
    }



    public Rect getBoundingBox() {

        if(mBoundingBox == null) {
            int top = Integer.MAX_VALUE, bottom = 0, right = 0, left = Integer.MAX_VALUE;

            for (Text text : getComponents()) {
                top = (text.getBoundingBox().top < top ? text.getBoundingBox().top : top);
                bottom = (text.getBoundingBox().bottom > bottom ? text.getBoundingBox().bottom : bottom);
                left = (text.getBoundingBox().left < left ? text.getBoundingBox().left : left);
                right = (text.getBoundingBox().right > right ? text.getBoundingBox().right : right);
            }
            mBoundingBox = new Rect(left, top, right, bottom);
        }

        return mBoundingBox;
    }


    public int centerX() {
        return mBoundingBox.right - mBoundingBox.left;
    }

    public int centerY() {
        return mBoundingBox.bottom - mBoundingBox.top;
    }

    public float exactCenterX() {
        return centerX();
    }

    public float exactCenterY() {
        return centerY();
    }




    @Override
    public Point[] getCornerPoints() {
        //TODO: implement, maybe?
        return new Point[0];
    }

    @Override
    public String getValue() {
        return this.toString();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Element element: mElements) {
            sb.append(element.getValue()).append(" ");
        }
        return sb.toString();
    }
}
