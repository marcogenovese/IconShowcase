package jahirfiquitiva.apps.iconshowcase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconsHolder> implements View.OnClickListener {

    private final Context context;
    private ArrayList<String> iconsList = new ArrayList<>();
    private ArrayList<Integer> iconsArray = new ArrayList<>();

    public IconsAdapter(Context context, ArrayList<String> iconsList, ArrayList<Integer> iconsArray) {
        this.context = context;
        this.iconsList = iconsList;
        this.iconsArray = iconsArray;
    }

    public void setIcons(ArrayList<String> iconsList, ArrayList<Integer> iconsArray) {
        this.iconsList.addAll(iconsList);
        this.iconsArray.addAll(iconsArray);
        this.notifyItemRangeInserted(0, iconsList.size() - 1);
    }

    public void clearIconsList() {
        this.iconsList.clear();
        this.iconsArray.clear();
    }

    @Override
    public IconsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new IconsHolder(inflater.inflate(R.layout.item_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(IconsHolder holder, int position) {
        if (iconsArray.size() > 0) {
            holder.icon.setImageResource(iconsArray.get(position));
        }
        holder.view.setTag(position);
        holder.view.setOnClickListener(this);
        setAnimation(holder.icon, position);
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            viewToAnimate.setHasTransientState(true);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return iconsList == null ? 0 : iconsList.size();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        int resId = iconsArray.get(position);
        String name = iconsList.get(position).toLowerCase(Locale.getDefault());

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_icon, false)
                .title(Util.makeTextReadable(name))
                .positiveText(R.string.close)
                .show();

        if (dialog.getCustomView() != null) {
            ImageView dialogIcon = (ImageView) dialog.getCustomView().findViewById(R.id.dialogicon);
            dialogIcon.setImageResource(resId);
        }
    }

    class IconsHolder extends RecyclerView.ViewHolder {

        final View view;
        final ImageView icon;

        IconsHolder(View v) {
            super(v);
            view = v;
            icon = (ImageView) v.findViewById(R.id.icon_img);
        }
    }

}
