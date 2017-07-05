package makejin.langfriend.juwon.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public class Posting implements Serializable {
    public String _id;
    public String update_date;
    public String create_date;

    public String name;
    public int report_cnt;
    public int view_cnt;
    public int like_cnt;
    public Author author = new Author();
    public String image_url;

    public List<String> location = new ArrayList<>();
    public List<String> language = new ArrayList<>();
    public List<String> activity = new ArrayList<>();
    public String posting;
    public List<LikePerson> like_person = new ArrayList<>();
    public int comment_cnt;
    public List<CommentPerson> comment_person = new ArrayList<>();

    public class LikePerson implements Serializable {
        public String user_id;
        public String like_date_;

        public String getUser_id() {
            return user_id;
        }

        public String getLike_date() {
            return like_date_;
        }
    }

    public List<String> like_person_id(){
        List<String> result = new ArrayList<>();
        for (LikePerson user_id:like_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public LikePerson newLike(String user_id,String like_date){
        LikePerson lp = new LikePerson();
        lp.user_id = user_id;
        lp.like_date_ = like_date;
        return lp;
    }

    public class Author implements Serializable {
        public String author_id;
        public String author_nickname;
        public String author_pic_small;
        public User.LocationPoint author_location_point;
    }

    public class CommentPerson implements Serializable {
        public String user_id;
        public String user_name;
        public String comment;
        public String comment_date;
        public String user_pic_small;
        public List<ReCommentPerson> re_comment_person;
        public String comment_id;

        public String getUser_id() {
            return user_id;
        }

        public String getComment() {
            return comment;
        }
        public String getCommentDate() {
            return comment_date;
        }
    }

    public List<String> comment_person_id(){
        List<String> result = new ArrayList<>();
        for (CommentPerson user_id:comment_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public CommentPerson newComment(String user_id, String comment){
        CommentPerson cp = new CommentPerson();
        cp.comment = comment;
        cp.user_id = user_id;
        return cp;
    }


    public class ReCommentPerson implements Serializable {
        public String user_id;
        public String user_name;
        public String comment;
        public String comment_date;
        public String user_pic_small;

        public String getUser_id() {
            return user_id;
        }

        public String getComment() {
            return comment;
        }
        public String getCommentDate() {
            return comment_date;
        }
    }

    public List<String> re_comment_person_id(){
        List<String> result = new ArrayList<>();
        for (CommentPerson user_id:comment_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public CommentPerson newReComment(String user_id, String comment){
        CommentPerson cp = new CommentPerson();
        cp.comment = comment;
        cp.user_id = user_id;
        return cp;
    }



}
