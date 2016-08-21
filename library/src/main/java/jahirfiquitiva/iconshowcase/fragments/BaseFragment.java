/*
 * Copyright (c) 2016 Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.activities.base.BaseActivity;

/**
 * Created by Allan Wang on 2016-08-19.
 * <p/>
 * Handles all fab related things
 */
public abstract class BaseFragment extends Fragment {

    public abstract void onFabClick (View v);

    abstract
    @DrawableRes
    int getFabIcon ();

    abstract boolean hasFab ();

    protected void showFab () {
        ((BaseActivity) getActivity()).getFab().show();
    }

    protected void hideFab () {
        ((BaseActivity) getActivity()).getFab().hide();
    }

    protected void setFabIcon (@DrawableRes int icon) {
        ((ShowcaseActivity) getActivity()).getFab().setImageResource(icon);
    }

    @CallSuper
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (hasFab()) {
            showFab();
            setFabIcon(getFabIcon());
        } else {
            hideFab();
        }
        return null;
    }

    protected View loadingView (LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.loading_section, container, false);
    }

}
