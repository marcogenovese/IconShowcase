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

package jahirfiquitiva.iconshowcase.activities.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import jahirfiquitiva.iconshowcase.fragments.BaseFragment;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;

/**
 * Created by Allan Wang on 2016-08-19.
 * <p/>
 * Handles all fab related things
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected FloatingActionButton mFab;

    public FloatingActionButton getFab () {
        if (mFab == null)
            throw new RuntimeException("Fab not set in BaseActivity; use setupFab method");
        return mFab;
    }

    protected abstract
    @IdRes
    int getFragmentId ();

    private BaseFragment getCurrentBaseFragment () {
        Fragment current = getSupportFragmentManager().findFragmentById(getFragmentId());
        if (!(current instanceof BaseFragment))
            throw new RuntimeException("Fragment does not extend BaseFragment");
        return (BaseFragment) current;
    }

    protected void setupFab (@IdRes int id) {
        mFab = (FloatingActionButton) findViewById(id);
        mFab.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick (View view) {
                getCurrentBaseFragment().onFabClick(view);
            }
        });
    }

    public static void hideFab (Context context) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).getFab().hide();
        } else {
            Log.e("hideFab", "context not instance of BaseActivity");
        }
    }
}
