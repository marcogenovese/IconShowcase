package jahirfiquitiva.apps.iconshowcase.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.fragments.ApplyFragment;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;

/**
 * @author Aidan Follestad (afollestad)
 */
public class LaunchersAdapter extends RecyclerView.Adapter<LaunchersAdapter.LauncherHolder> implements View.OnClickListener {

    public interface ClickListener {
        void onClick(int index);
    }

    private final Context context;
    private final List<ApplyFragment.Launcher> launchers;
    private final ClickListener mCallback;
    private View view;

    public LaunchersAdapter(Context context, List<ApplyFragment.Launcher> launchers, ClickListener callback) {
        this.context = context;
        this.launchers = launchers;
        this.mCallback = callback;
    }

    @Override
    public LauncherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LauncherHolder(inflater.inflate(R.layout.item_launcher, parent, false));
    }

    @Override
    public void onBindViewHolder(LauncherHolder holder, int position) {
        // Turns Launcher name "Something Pro" to "l_something_pro"
        int iconResource = context.getResources().getIdentifier(
                "ic_" + launchers.get(position).name.toLowerCase().replace(" ", "_"),
                "drawable",
                context.getPackageName()
        );

        final int light = ContextCompat.getColor(context, android.R.color.white);
        final int grey = ContextCompat.getColor(context, R.color.grey);

        holder.icon.setImageResource(iconResource);
        holder.launcherName.setText(launchers.get(position).name.toUpperCase(Locale.getDefault()));

        if (launchers.get(position).isInstalled(context)) {
            holder.icon.setColorFilter(null);
            holder.launcherName.setBackgroundColor(launchers.get(position).launcherColor);
            holder.launcherName.setTextColor(light);
        } else {
            holder.icon.setColorFilter(ThemeUtils.darkTheme ? light : grey);
            holder.launcherName.setBackgroundColor(ThemeUtils.darkTheme ? light : grey);
            holder.launcherName.setTextColor(ThemeUtils.darkTheme ? grey : light);
        }

        holder.view.setTag(position);
        holder.view.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return launchers == null ? 0 : launchers.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            int index = (Integer) v.getTag();
            if (mCallback != null)
                mCallback.onClick(index);
        }
    }

    class LauncherHolder extends RecyclerView.ViewHolder {

        final View view;
        ImageView icon;
        final TextView launcherName;

        LauncherHolder(View v) {
            super(v);
            view = v;
            icon = (ImageView) view.findViewById(R.id.launchericon);
            launcherName = (TextView) view.findViewById(R.id.launcherName);
        }
    }

}