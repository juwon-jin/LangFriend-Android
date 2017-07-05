package makejin.langfriend.juwon.Utils.SharedManager;

import makejin.langfriend.juwon.Model.Category;
import makejin.langfriend.juwon.Model.User;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class SharedManager {
    private volatile static SharedManager single;
    private User me;
    private User you;

    private Category category;
    private Boolean push;

    public static SharedManager getInstance() {
        if (single == null) {
            synchronized(SharedManager.class) {
                if (single == null) {
                    single = new SharedManager();
                }
            }
        }
        return single;
    }

    private SharedManager() {

    }

    public boolean setLogout(){
        me = null;
        you = null;
        category = null;
        push = null;

        return true;
    }

    public boolean setYou(User response) {
        try {
            this.you = response;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public User getYou() {
        return this.you;
    }


    public boolean setMe(User response) {
        try {
            this.me = response;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public User getMe() {
        return this.me;
    }


    public Category getCategory() {
        return this.category;
    }

    public boolean setCategory(Category category) {
        try {
            this.category = category;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean getPush() {
        return this.push;
    }

    public boolean setPush(Boolean push) {
        try {
            this.push = push;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
