package hu.pe.thinhhoang.aaosync.utils;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by hoang on 2/1/2016.
 */
public class HidingScrollListener implements ListView.OnScrollListener {
    int mLastFirstVisibleItem = 0;
    private ListView listView;
    Delegates dlg;

    public void setListView(ListView l)
    {
        listView=l;
    }

    public void addControlsOperationListener(Delegates d)
    {
        dlg=d;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getId() == listView.getId()) {
            final int currentFirstVisibleItem = listView.getFirstVisiblePosition();

            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().hide();
                dlg.hideControls();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().show();
                dlg.showControls();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public interface Delegates
    {
        public void hideControls();
        public void showControls();
    }
}
