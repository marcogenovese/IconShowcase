package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pitchedapps.butler.iconrequest.App;
import com.pitchedapps.butler.iconrequest.IconRequest;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.config.Config;
import jahirfiquitiva.iconshowcase.holders.RequestHolder;
import jahirfiquitiva.iconshowcase.utilities.Preferences;

public class RequestsAdapter extends RecyclerView.Adapter<RequestHolder> {

    @Nullable
    public ArrayList<App> getApps() {
        if (IconRequest.get() != null)
            return IconRequest.get().getApps();
        return null;
    }

    @Nullable
    public ArrayList<App> getSelectedApps() {
        if (IconRequest.get() != null)
            return IconRequest.get().getSelectedApps();
        return null;
    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int position) {
        Preferences mPrefs = new Preferences(parent.getContext());
        View view = LayoutInflater.from(parent.getContext())
                .inflate((Config.get().devOptions() ?
                        mPrefs.getDevListsCards() : Config.get().bool(R.bool.request_cards))
                        ? R.layout.card_app_to_request :
                        R.layout.item_app_to_request, parent, false);
        return new RequestHolder(view, new RequestHolder.OnAppClickListener() {
            @Override
            public void onClick(Context context, AppCompatCheckBox checkBox, App item) {
                onItemClick(context, checkBox, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getApps() != null ? getApps().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RequestHolder holder, int position) {
        //noinspection ConstantConditions
        holder.setItem(getApps().get(holder.getAdapterPosition()));
    }

    public void selectOrDeselectAll(boolean select) {
        final IconRequest ir = IconRequest.get();
        if (ir != null) {
            if (select) {
                ir.selectAllApps();
            } else {
                ir.unselectAllApps();
            }
            notifyDataSetChanged();
        }
    }

    private void onItemClick(Context context, AppCompatCheckBox checkBox, App app) {
        final IconRequest ir = IconRequest.get();
        if (ir != null && ir.getApps() != null) {
            ir.toggleAppSelected(app);
            checkBox.setChecked(ir.isAppSelected(app));
        }
    }

}