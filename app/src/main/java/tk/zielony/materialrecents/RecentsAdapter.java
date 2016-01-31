package tk.zielony.materialrecents;

import android.view.View;

/**
 * Created by Marcin on 2015-04-13.
 */
public interface RecentsAdapter {
    View getView(int position);
    int getCount();
}
