package iconshowcase.lib.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import iconshowcase.lib.R;
import iconshowcase.lib.models.RequestItem;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsHolder> {

    public interface ClickListener {
        void onClick(int index);
    }

    public ArrayList<RequestItem> appsList;
    Context context;
    private final ClickListener mCallback;

    AppIconFetchingQueue mAppIconFetchingQueue;

    public RequestsAdapter(Context context, final ArrayList<RequestItem> appsList) {
        this.context = context;
        this.appsList = appsList;
        this.mCallback = new ClickListener() {
            @Override
            public void onClick(int position) {
                RequestItem requestsItem = appsList.get(position);
                requestsItem.setSelected(!requestsItem.isSelected());
                appsList.set(position, requestsItem);
                notifyItemChanged(position);
            }
        };
    }

    @Override
    public RequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app_to_request, parent, false);
        return new RequestsHolder(v);
    }

    @Override
    public void onBindViewHolder(RequestsHolder holder, int position) {
        RequestItem requestsItem = appsList.get(position);
        holder.txtName.setText(requestsItem.getAppName());
        holder.imgIcon.setImageDrawable(requestsItem.getIcon());
        holder.chkSelected.setChecked(requestsItem.isSelected());
        holder.view.setTag(position);
    }

    @Override
    public int getItemCount() {
        return appsList == null ? 0 : appsList.size();
    }

    public class RequestsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final LinearLayout view;
        final ImageView imgIcon;
        final TextView txtName;
        final CheckBox chkSelected;

        public RequestsHolder(View v) {
            super(v);
            view = (LinearLayout) v.findViewById(R.id.requestCard);
            imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
            txtName = (TextView) v.findViewById(R.id.txtName);
            chkSelected = (CheckBox) v.findViewById(R.id.chkSelected);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                if (mCallback != null)
                    mCallback.onClick(index);
            }
        }

    }

    public void startIconFetching(RecyclerView view) {
        mAppIconFetchingQueue = new AppIconFetchingQueue(view);
    }

    public void stopAppIconFetching() {
        if (mAppIconFetchingQueue != null) {
            mAppIconFetchingQueue.stop();
        }
    }

    public class AppIconFetchingQueue {
        int mIconsRemaining;
        RecyclerView mRecyclerView;

        AppIconFetchingQueue(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mIconsRemaining = appsList != null ? appsList.size() : 0;
        }

        public void stop() {
            // Avoids calling stop on thread, which will cause crash.
            mIconsRemaining = 0;
        }

    }

}