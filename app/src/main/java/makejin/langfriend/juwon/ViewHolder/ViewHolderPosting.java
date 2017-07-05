package makejin.langfriend.juwon.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import makejin.langfriend.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderPosting extends ViewHolderParent {
    public LinearLayout cellPostingHeader, cellPostingBodyImage, cellPostingBodyDesc, cellPostingTail, eat_btn, comment_btn, layout_like_people, indicator;
    public TextView postingName, authorName, category_tag, people_like, posting, user_info, TV_comment;
    public ImageView heart,star,posting_img, report_btn;
    public CircleImageView author_image;

    public ViewHolderPosting(View v) {
        super(v);
        cellPostingHeader = (LinearLayout) v.findViewById(R.id.cell_posting_header);
        cellPostingBodyImage = (LinearLayout) v.findViewById(R.id.cell_posting_body_image);
        cellPostingBodyDesc = (LinearLayout) v.findViewById(R.id.cell_posting_body_desc);
        cellPostingTail = (LinearLayout) v.findViewById(R.id.cell_posting_tail);
        eat_btn = (LinearLayout) v.findViewById(R.id.eat_btn);
        comment_btn = (LinearLayout) v.findViewById(R.id.comment_btn);
        indicator = (LinearLayout) v.findViewById(R.id.indicator);

        layout_like_people = (LinearLayout) v.findViewById(R.id.layout_like_people);
        postingName = (TextView) v.findViewById(R.id.posting_name);
        authorName = (TextView) v.findViewById(R.id.author_name);
        category_tag = (TextView) v.findViewById(R.id.txt_category);
        people_like = (TextView) v.findViewById(R.id.txt_people_like);
        posting = (TextView) v.findViewById(R.id.txt_posting);
        user_info = (TextView) v.findViewById(R.id.user_info);

        heart = (ImageView) v.findViewById(R.id.heart_img);
        star = (ImageView) v.findViewById(R.id.star_img);
        posting_img = (ImageView) v.findViewById(R.id.posting_image);
        report_btn = (ImageView) v.findViewById(R.id.report_btn);

        author_image = (CircleImageView) v.findViewById(R.id.author_image);


        TV_comment = (TextView) v.findViewById(R.id.TV_comment);
    }
}