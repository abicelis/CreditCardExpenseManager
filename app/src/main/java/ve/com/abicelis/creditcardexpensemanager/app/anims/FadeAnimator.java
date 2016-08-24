package ve.com.abicelis.creditcardexpensemanager.app.anims;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by Alex on 12/8/2016.
 */
public class FadeAnimator {

    public static void startAnimation(View view) {

        AlphaAnimation anim = new AlphaAnimation(1f, 0.2f);
        anim.setDuration(1000);
        anim.setRepeatCount(AlphaAnimation.INFINITE);
        anim.setRepeatMode(AlphaAnimation.REVERSE);
        view.startAnimation(anim);

    }

    public static void stopAnimation(View view) {
        view.clearAnimation();
    }
}
