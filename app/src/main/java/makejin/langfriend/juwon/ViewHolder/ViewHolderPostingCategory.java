package makejin.langfriend.juwon.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import makejin.langfriend.R;
import com.zhy.view.flowlayout.TagFlowLayout;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderPostingCategory extends ViewHolderParent {
    public TagFlowLayout location, language, activity;
    public LinearLayout layout_category;
    public ImageView expand_btn;
    public LinearLayout getLayout_category;

    public ViewHolderPostingCategory(View v) {
        super(v);
        location = (TagFlowLayout) v.findViewById(R.id.location_flowlayout);
        language = (TagFlowLayout) v.findViewById(R.id.language_flowlayout);
        activity = (TagFlowLayout) v.findViewById(R.id.activity_flowlayout);
        layout_category = (LinearLayout) v.findViewById(R.id.layout_category);
        expand_btn =  (ImageView) v.findViewById(R.id.expand_btn);
    }
}
