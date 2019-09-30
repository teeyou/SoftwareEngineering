package bokhakwang.softwareengineering;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bokhakwang.softwareengineering.data.source.Repository;
import bokhakwang.softwareengineering.editpost.EditPostActivity;
import bokhakwang.softwareengineering.firestore.FireStorage;
import bokhakwang.softwareengineering.firestore.Firestore;
import bokhakwang.softwareengineering.model.Post;

public class HomeFragment extends Fragment {
    public static final int REQUEST_CODE_ADD = 1000;
    public static final int REQUEST_CODE_UPDATE = 1100;
    private RecyclerView mRecyclerView;
    private HomeRecyclerAdapter mHomeRecyclerAdapter;
    private MainActivity mContext;

    private FloatingActionButton mFab;
    List<Post> mPostList;

    Repository mRepository;

    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private SearchView mSearchView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mToolbar = v.findViewById(R.id.home_toolbar);
        mToolbar.inflateMenu(R.menu.search);

        mSearchView = (SearchView) mToolbar.getMenu().findItem(R.id.menu_search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MYTAG", "text changed!");
                return true;
            }
        });

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

        mRepository.fetchPostList(res -> {
            if(res) {
                mPostList = mRepository.getPostList();
                mHomeRecyclerAdapter.setPostList(mPostList);
                mProgressBar.setVisibility(View.GONE);
            } else {
                //Loading postList fail
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD && resultCode == getActivity().RESULT_OK) {
            mPostList.add(0 , (Post) data.getSerializableExtra("addPost"));
            mHomeRecyclerAdapter.notifyDataSetChanged();
        }
    }

    class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeViewHolder> {
        List<Post> mPostList;

        private HomeRecyclerAdapter(List<Post> list) {
            mPostList = list;
        }

        @NonNull
        @Override
        public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            return new HomeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeViewHolder holder, int i) {
            Post post = mPostList.get(i);
            Glide.with(getContext()).load(post.getImages().get(0)).into(holder.image);
            holder.time.setText(post.getTime());
            holder.location.setText(post.getDetail_location());
            holder.author.setText(post.getAuthor());
            holder.contents.setText(post.getContents());
            holder.more_vert.setOnClickListener(__ -> {

            });
            holder.itemView.setOnClickListener(__ -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText password = new EditText(getContext());
                builder.setTitle("포스트 설정");
                builder.setMessage("비밀번호 입력 후 눌러주세요");
                builder.setView(password);
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(password.getText().toString().equals(post.getPassword())) {
                            Intent intent = new Intent(getContext(), EditPostActivity.class);
                            intent.putExtra("post", post);
                            intent.setAction("update");
                            startActivityForResult(intent, REQUEST_CODE_UPDATE);

                        } else {
                            Toast.makeText(getContext(), R.string.toast_wrong_password , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(password.getText().toString().equals(post.getPassword())) {
                            Firestore.getInstance().deletePost(post, result -> {
                                if(result) {
                                    mPostList.remove(i);
                                    mHomeRecyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), R.string.toast_post_deleted, Toast.LENGTH_SHORT).show();

                                }

                                FireStorage.getInstance().deleteImage(post.getImages().get(0));

                            });
                        } else {
                            Toast.makeText(getContext(),  R.string.toast_wrong_password , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().show();
            });
        }

        @Override
        public int getItemCount() {
            return mPostList.size();
        }

        private void setPostList(List<Post> postList) {
            mPostList = postList;
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
