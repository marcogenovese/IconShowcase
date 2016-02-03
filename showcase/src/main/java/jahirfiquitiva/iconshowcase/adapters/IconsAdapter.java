package jahirfiquitiva.iconshowcase.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconsHolder> implements View.OnClickListener {

    private final Context context;
    private boolean inChangelog = false;
    private ArrayList<String> iconsList = new ArrayList<>();
    private ArrayList<Integer> iconsArray = new ArrayList<>();
    private Bitmap bitmap;
    private Preferences mPrefs;

    public IconsAdapter(Context context, ArrayList<String> iconsList, ArrayList<Integer> iconsArray) {
        this.context = context;
        this.iconsList = iconsList;
        this.iconsArray = iconsArray;
        this.inChangelog = false;
        this.mPrefs = new Preferences(context);
    }

    public IconsAdapter(Context context, ArrayList<String> iconsList, ArrayList<Integer> iconsArray,
                        boolean inChangelog) {
        this.context = context;
        this.iconsList = iconsList;
        this.iconsArray = iconsArray;
        this.inChangelog = inChangelog;
        this.mPrefs = new Preferences(context);
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
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_slide);
        if (position > lastPosition && mPrefs.getAnimationsEnabled()) {
            viewToAnimate.setHasTransientState(true);
            viewToAnimate.startAnimation(anim);
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

        if (ShowcaseActivity.iconPicker) {
            Intent intent = new Intent();
            bitmap = null;

            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            } catch (Exception e) {
                Utils.showLog("Icons Picker error: " + Log.getStackTraceString(e));
            }

            if (bitmap != null) {
                intent.putExtra("icon", bitmap);
                intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", resId);
                String bmUri = "android.resource://" + context.getPackageName() + "/" + String.valueOf(resId);
                intent.setData(Uri.parse(bmUri));
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
            } else {
                ((Activity) context).setResult(Activity.RESULT_CANCELED, intent);
            }

            ((Activity) context).finish();

        } else {
            if (!inChangelog) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .customView(R.layout.dialog_icon, false)
                        .title(Utils.makeTextReadable(name))
                        .positiveText(R.string.close)
                        .show();

                if (dialog.getCustomView() != null) {
                    ImageView dialogIcon = (ImageView) dialog.getCustomView().findViewById(R.id.dialogicon);
                    dialogIcon.setImageResource(resId);
                }
            }
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
