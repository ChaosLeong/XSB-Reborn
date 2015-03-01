package com.sise.help.rank;

import android.app.Activity;
import android.app.Fragment;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class RanksFragment extends Fragment {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("排行榜");
    }
}
