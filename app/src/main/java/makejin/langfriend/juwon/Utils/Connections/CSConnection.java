package makejin.langfriend.juwon.Utils.Connections;

import makejin.langfriend.juwon.Model.Category;
import makejin.langfriend.juwon.Model.Explore;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.Result;
import makejin.langfriend.juwon.Model.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public interface CSConnection {
    @GET("/user/recommand/{user_id}/{page}")
    Observable<List<User>> getRecommandUsers(@Path("user_id") String user_id,
                                             @Path("page") int page);


    @GET("/postings/{id}/{user}")
    Observable<Posting> getOnePosting(@Path("id") String id,
                                   @Path("user") String user);

    @GET("/pio/buy/{user}/{posting}")
    Observable<Result> buyItem(@Path("user") String user,
                               @Path("posting") String posting);

    //알고싶어요
    @POST("/like/{uid}/{posting_id}")
    Observable<Posting> likePosting(@Path("uid") String uid,
                                 @Path("posting_id") String posting_id);

    //comment 전송
    @POST("/comment/{uid}/{posting_id}")
    Observable<Posting> commentPosting(@Body Posting posting,
                                 @Path("uid") String uid,
                                 @Path("posting_id") String posting_id);

    //친구수락 OK 정보 전송
    @POST("/friends/accept/{me_id}/{you_id}")
    Observable<User> acceptYou(@Body User You,
                                       @Path("me_id") String me_id,
                                       @Path("you_id") String you_id);

    //친구수락 NO 정보 전송
    @POST("/friends/reject/{me_id}/{you_id}")
    Observable<User> rejectYou(@Body User You,
                               @Path("me_id") String me_id,
                               @Path("you_id") String you_id);


    //친구수락 OK 정보 전송
    @POST("/friends/accept/TAB1/{me_id}/{you_id}")
    Observable<User> acceptYou_TAB1(@Body User You,
                               @Path("me_id") String me_id,
                               @Path("you_id") String you_id);

    //친구수락 NO 정보 전송
    @POST("/friends/reject/TAB1/{me_id}/{you_id}")
    Observable<User> rejectYou_TAB1(@Body User You,
                               @Path("me_id") String me_id,
                               @Path("you_id") String you_id);


    //similar 결과 값
    @GET("posting/{posting_id}/similar")
    Observable<List<Posting>> similarResult(@Path("posting_id") String posting_id);

    //recommendation 결과 값
    @POST("/posting/recommand/{user_id}/{page}")
    Observable<List<Posting>> getRecommandPosts(@Body Category fields,
                                                @Path("user_id") String user_id,
                                                @Path("page") final int page);


    //피드 가져오기
    @GET("feeds/{uid}/{page}")
    Observable<List<Posting>> getFeedList(@Path("uid") String uid,
                                          @Path("page") int page);

    //친구 요청 가져오기 //너가 친구 요청 //friends_NonFacebook_Waiting
    @GET("requests/waiting/{uid}/{page}")
    Observable<List<User>> getRequests(@Path("uid") String uid,
                                          @Path("page") int page);

    //친구 요청 가져오기 2 //내가 친구 요청 //friends_NonFacebook_Requested
    @GET("requests/requested/{uid}/{page}")
    Observable<List<User>> getRequests2(@Path("uid") String uid,
                                       @Path("page") int page);

    //친구 요청 가져오기 3 //우린 이미 친구 //friends_NonFacebook
    @GET("requests/friends/{uid}/{page}")
    Observable<List<User>> getRequests3(@Path("uid") String uid,
                                       @Path("page") int page);

    //신고하기
    @GET("report/user/{me_id}/{you_id}")
    Observable<GlobalResponse> reportUser(@Path("me_id") String me_id,
                                          @Path("you_id") String you_id);

    //신고하기
    @GET("report/posting/{me_id}/{posting_id}")
    Observable<GlobalResponse> reportPosting(@Path("me_id") String me_id,
                                             @Path("posting_id") String posting_id);

    //카테고리 목록 가져오기
    @GET("/category")
    Observable<Category> getCategoryList();

    @POST("/posting/post")
    Observable<Posting> postingPost(@Body Posting posting);

    @POST("/sign/up")
    Observable<User> signupUser(@Body User user);

    @POST("/users/{user_id}/edit/aboutMe")
    Observable<User> updateAboutme(@Path("user_id") String user_id,
                                   @Body Map<String, Object> fields);

    @POST("/sign/in")
    Observable<User> signinUser(@Body Map<String, Object> fields);

    @GET("/users/{user_id}/keyword")
    Observable<List<String>> getAllKeyword(@Path("user_id") String id);

    @GET("/posting/{keyword}")
    Observable<List<Posting>> getSearchResult(@Path("keyword") String keyword);

    @Multipart
    @POST("post/{posting_id}/image/upload")
    Observable<Posting> fileUploadWrite(@Path("posting_id") String posting_id,
                                        @Part("post_image\"; filename=\"android_post_image_file") RequestBody file);


    @GET("user/{you_id}/{me_id}/view")
    Observable<GlobalResponse> userView(@Path("me_id") String me_id,
                                        @Path("you_id") String you_id);

    @GET("posting/{posting_id}/{uid}/view")
    Observable<GlobalResponse> postingView(@Path("uid") String uid,
                                        @Path("posting_id") String posting_id);

    @GET("/users/{uid}/mylist")
    Observable<List<Posting>> getLikedPosting(@Path("uid") String uid);

    @GET("explore")
    Observable<List<Explore>> getExploreRanking();

    @GET("/like/{posting_id}")
    Observable<List<User>> getLikedPerson(@Path("posting_id") String posting_id);

    @Multipart
    @POST("user/{user_id}/image/upload/profile/{index}")
    Observable<User> fileUploadWrite_User(@Path("user_id") String user_id,
                                          @Path("index") String index,
                                          @Part("post_image\"; filename=\"android_post_image_file") RequestBody file);



    @POST("/users/{user_id}/edit/profile/facebook")
    Observable<User> updateUserImage_Facebook(@Path("user_id") String user_id,
                                              @Body Map<String, Object> fields);

    @GET("/users/{uid}/myinfo")
    Observable<User> getUserInfo(@Path("uid") String uid);

    @POST("/sign/in/NonFacebook")
    Observable<User> signinUser_NonFacebook(@Body Map<String, Object> fields);


    @POST("/user/withdrawal")
    Observable<User> withdrawalUser(@Body Map<String, Object> fields);

    @POST("/posting/comment/{posting_id}")
    Observable<List<Posting.CommentPerson>> commentPosting(@Path("posting_id") String posting_id, @Body Map<String, Object> fields);

    @GET("/posting/comment/get/{posting_id}")
    Observable<List<Posting.CommentPerson>> getCommentPosting(@Path("posting_id") String posting_id);

    @GET("/posting/comment/get/{posting_id}/{comment_id}")
    Observable<List<Posting.CommentPerson>> getOneCommentPosting(@Path("posting_id") String posting_id,
                                                                 @Path("comment_id") String comment_id);


    @POST("/posting/comment/{posting_id}/{comment_id}")
    Observable<GlobalResponse> oneCommentPosting(@Path("posting_id") String posting_id, @Path("comment_id") String comment_id, @Body Map<String, Object> fields);


    @GET("/user/check/social_id/{social_id}")
    Observable<GlobalResponse> checkSocialID(@Path("social_id") String social_id);


    @POST("/user/request/verification")
    Observable<GlobalResponse> requestVerification(@Body Map<String, Object> fields);


    @POST("/user/{user_id}/edit/matchingOption")
    Observable<User> editMatchingOption(@Path("user_id") String user_id,
                                   @Body Map<String, Object> fields);

    @POST("/user/{user_id}/edit/profile")
    Observable<User> editProfile(@Path("user_id") String user_id,
                              @Body Map<String, Object> fields);

    @GET("/user/check/matching/time/{user_id}")
    Observable<GlobalResponse> checkMatchingTime(@Path("user_id") String user_id);


    @GET("/user/{user_id}/edit/get/matchingOption")
    Observable<User> getEditMatchingOption(@Path("user_id") String user_id);

    @POST("/user/access/all")
    Observable<User> accessUser_all(@Body Map<String, Object> fields);

    @POST("/user/update/location")
    Observable<User> updateLocation(@Body Map<String, Object> fields);

    @GET("/user/get/one/{user_id}")
    Observable<User> getOneUser(@Path("user_id") String user_id);

}


