package bokhakwang.softwareengineering;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bokhakwang.softwareengineering.data.source.Repository;
import bokhakwang.softwareengineering.editpost.EditPostActivity;
import bokhakwang.softwareengineering.firestore.FireStorage;
import bokhakwang.softwareengineering.firestore.Firestore;
import bokhakwang.softwareengineering.model.Post;

public class HomeFragment extends Fragment {
    public static final int REQUEST_CODE_ADD = 2000;
    public static final int REQUEST_CODE_UPDATE = 3000;
    private RecyclerView mRecyclerView;
    private HomeRecyclerAdapter mHomeRecyclerAdapter;
    private MainActivity mContext;

    private FloatingActionButton mFab;
    List<Post> mPostList;

    Repository mRepository;

    private TextView mPostCount;

    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    List<Post> mSearchPostList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mSearchPostList = new ArrayList<>();

            if (newText.equals("")) {
                mHomeRecyclerAdapter.setPostList(mPostList);
                mPostCount.setText("Posts : " + mPostList.size());
            } else {
                for (Post post : mPostList) {
                    String time = post.getTime();
                    String location = post.getDetail_location();
                    String author = post.getAuthor();
                    String contents = post.getContents();

                    String str = time + " " + location + " " + author + " " + contents;

                    if (str.contains(newText)) {
                        mSearchPostList.add(post);
                    }
                }

                mHomeRecyclerAdapter.setPostList(mSearchPostList);
                mPostCount.setText("Posts : " + mSearchPostList.size());
            }
            return true;
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
        mRepository = Repository.getRepo(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MYTAG", "HomeFragment에서 onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MYTAG", "HomeFragment에서 onResume");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Repository.getRepo(getContext()).fetchPostList(result -> {
            if(result) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mPostList = Repository.getRepo(getContext()).getPostList();
                mHomeRecyclerAdapter.setPostList(mPostList);
                mPostCount.setText("Posts : " + mPostList.size());
            } else {
                Log.d("MYTAG", "HomeFragment에서... fetch 실패");
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("MYTAG", "HomeFragment에서 onCreateView");
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mToolbar = v.findViewById(R.id.home_toolbar);
        mToolbar.inflateMenu(R.menu.search);

        mSearchView = (SearchView) mToolbar.getMenu().findItem(R.id.menu_search).getActionView();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);

        mPostCount = v.findViewById(R.id.post_count);
        mFab = v.findViewById(R.id.fab);
        mFab.setOnClickListener(__ -> {
            Intent intent = new Intent(getContext(), EditPostActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        mPostList = new ArrayList<>();
        mHomeRecyclerAdapter = new HomeRecyclerAdapter(mPostList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mHomeRecyclerAdapter);

        mProgressBar = v.findViewById(R.id.home_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Repository.getRepo(getContext()).fetchPostList(result -> {
                    if(result) {
                        mPostList = Repository.getRepo(getContext()).getPostList();
                        mHomeRecyclerAdapter.setPostList(mPostList);
                        mPostCount.setText("Posts : " + mPostList.size());
                    } else {
                        Log.d("MYTAG", "HomeFragment onRefresh에서... fetch 실패");
                    }

                    mSearchView.setQuery("",false);
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD && resultCode == getActivity().RESULT_OK) {
            mPostList.add(0, (Post) data.getSerializableExtra("addPost"));
            mHomeRecyclerAdapter.notifyDataSetChanged();
            mPostCount.setText("Posts : " + mPostList.size());
            Toast.makeText(getContext(), R.string.toast_post_saved, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_UPDATE && resultCode == getActivity().RESULT_OK) {
            Post post = (Post) data.getSerializableExtra("updatePost");
            replacePost(post);
            mHomeRecyclerAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), R.string.toast_post_updated, Toast.LENGTH_SHORT).show();
        }
    }

    //Post Update시 DB를 거치지 않고 Local에서 PostList를 바로 update
    public void replacePost(Post post) {
        for (int i = 0; i < mPostList.size(); i++) {
            if (post.getId().equals(mPostList.get(i).getId())) {
                mPostList.add(i, post);
                mPostList.remove(i + 1);
                break;
            }
        }
    }

    class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeViewHolder> {
        List<Post> myPostList;

        private HomeRecyclerAdapter(List<Post> list) {
            myPostList = list;
        }

        @NonNull
        @Override
        public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            return new HomeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeViewHolder holder, int i) {
            Post post = myPostList.get(i);
            Glide.with(getContext()).load(post.getImages().get(0)).into(holder.image);
            holder.time.setText(post.getTime());
            holder.location.setText(post.getDetail_location());
            holder.author.setText(post.getAuthor());
            holder.contents.setText(post.getContents());

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),PictureActivity.class);
                    intent.putExtra("picture", (Serializable) myPostList.get(i).getImages().get(0));
                    startActivity(intent);
                }
            });

            holder.more_vert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] items = {"Edit", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select menu");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("MYTAG", "which : " + which);
                            if (which == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                EditText password = new EditText(getContext());
                                builder.setTitle("포스트 변경");
                                builder.setMessage("비밀번호 입력 후 확인");
                                builder.setView(password);
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (password.getText().toString().equals(post.getPassword())) {
                                            Intent intent = new Intent(getContext(), EditPostActivity.class);
                                            intent.putExtra("post", post);
                                            intent.setAction("update");
                                            //startActivity(intent);
                                            startActivityForResult(intent, REQUEST_CODE_UPDATE);

                                        } else {
                                            Toast.makeText(getContext(), R.string.toast_wrong_password, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton("취소", null).create().show();
                            } else if (which == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                EditText password = new EditText(getContext());
                                builder.setTitle("포스트 삭제");
                                builder.setMessage("비밀번호 입력 후 확인");
                                builder.setView(password);
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (password.getText().toString().equals(post.getPassword())) {
                                            Firestore.getInstance().deletePost(post, result -> {
                                                if (result) {
                                                    myPostList.remove(i);
                                                    mPostCount.setText("Posts : " + myPostList.size());
                                                    mHomeRecyclerAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(), R.string.toast_post_deleted, Toast.LENGTH_SHORT).show();
                                                }
                                                FireStorage.getInstance().deleteImage(post.getImages().get(0));
                                            });
                                        } else {
                                            Toast.makeText(getContext(), R.string.toast_wrong_password, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton("취소", null).create().show();
                            }
                        }
                    });

                    builder.create().show();

                }
            });
        }


        @Override
        public int getItemCount() {
            return myPostList.size();
        }

        public void setPostList(List<Post> postList) {
            myPostList = postList;
            notifyDataSetChanged();
        }
    }


    static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView time;
        TextView location;
        TextView author;
        TextView contents;
        ImageView more_vert;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_item_image);
            time = itemView.findViewById(R.id.list_item_time);
            location = itemView.findViewById(R.id.list_item_location);
            author = itemView.findViewById(R.id.list_item_author);
            contents = itemView.findViewById(R.id.list_item_contents);
            more_vert = itemView.findViewById(R.id.list_btn_more);
        }
    }
}
