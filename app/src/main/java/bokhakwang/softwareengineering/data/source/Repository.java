package bokhakwang.softwareengineering.data.source;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bokhakwang.softwareengineering.firestore.FirebaseListener;
import bokhakwang.softwareengineering.firestore.Firestore;
import bokhakwang.softwareengineering.model.Post;

public class Repository {
    public List<Post> mPostList;
    public static Repository repo;

    public Repository() {

    }
    public static Repository getRepo(Context context) {
        if(repo == null) {
            repo = new Repository();
        }

        return repo;
    }

    public void fetchPostList(FirebaseListener<Boolean> callback) {
        Firestore.getInstance().fetchAllPosts(result -> {
            if (result == null) {
                callback.onComplete(false);
            } else {
                mPostList = result;
                Collections.sort(mPostList, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        if(o1.getTime().compareTo(o2.getTime()) < 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                callback.onComplete(true);
            }
        });
    }

    public List<Post> getPostList() {
        return mPostList;
    }
}
