package ve.com.abicelis.creditcardexpensemanager.ocr;


import android.graphics.Rect;

import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14/8/2016.
 */
public class TextParagraph {

    List<Text> mTexts;
    Rect mBundingBox = null;

    public TextParagraph() {
        mTexts = new ArrayList<>();
    }

    public void addComponent(Text text) {
        mTexts.add(text);
    }

    public List<Text> getComponents() {
        return mTexts;
    }


    public Rect getBoundingBox() {

        if(mBundingBox == null) {
            int top = Integer.MAX_VALUE, bottom = 0, right = 0, left = Integer.MAX_VALUE;

            for (Text text : getComponents()) {
                top = (text.getBoundingBox().top < top ? text.getBoundingBox().top : top);
                bottom = (text.getBoundingBox().bottom > bottom ? text.getBoundingBox().bottom : bottom);
                left = (text.getBoundingBox().left < left ? text.getBoundingBox().left : left);
                right = (text.getBoundingBox().right > right ? text.getBoundingBox().right : right);
            }
            mBundingBox = new Rect(left, top, right, bottom);
        }

        return mBundingBox;
    }

    public int centerX() {
        return mBundingBox.right - mBundingBox.left;
    }

    public int centerY() {
        return mBundingBox.bottom - mBundingBox.top;
    }

    public float exactCenterX() {
        return centerX();
    }

    public float exactCenterY() {
        return centerY();
    }






    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Text text: mTexts) {
            sb.append(text.toString());
        }
        return sb.toString();
    }
}
