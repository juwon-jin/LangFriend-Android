package makejin.langfriend.juwon.Utils.Loadings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class LoadingUtil {
    public static void startLoading(View view) {
        if (view==null)
            return ;
        view.setAlpha(0.4f);
        view.setVisibility(View.VISIBLE);
    }
    public static void stopLoading(final View view) {
        if (view==null)
            return ;
        view.animate()
                .alpha(0.0f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
}
