package com.example.blog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PostList extends ArrayAdapter<Post> {
    private Activity context;
    List<Post> posts;

    public PostList(Activity context, List<Post> posts) {
        super(context, R.layout.layout_post_list, posts);
        this.context = context;
        this.posts = posts;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_post_list, null, true);

        TextView textViewTitle = (TextView) listViewItem.findViewById(R.id.textViewTitle);
        TextView textViewTime = (TextView) listViewItem.findViewById(R.id.textViewTime);
        TextView textViewDescription = (TextView) listViewItem.findViewById(R.id.textViewDescription);
        TextView textViewCategory = (TextView) listViewItem.findViewById(R.id.textViewCategory);

        Post post = posts.get(position);
        textViewTitle.setText(post.getTitle());
        textViewTime.setText(post.getTimeStamp());
        textViewDescription.setText(post.getDescription());
        textViewCategory.setText(post.getCategory());

        return listViewItem;
    }
}