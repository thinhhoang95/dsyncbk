package hu.pe.thinhhoang.aaosync;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hu.pe.thinhhoang.aaosync.utils.PageIndicatorView;

public class WelcomeActivity extends AppCompatActivity {

    private static final int NUM_OF_PAGES=4;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private PageIndicatorView mPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mPageIndicator = (PageIndicatorView) findViewById(R.id.pageIndicator);

        // Setup the ViewPager, put contents in it
        mViewPager = (ViewPager) findViewById(R.id.pager);
        // TODO: Setup the mPagerAdapter
        mPagerAdapter = new WelcomeScreenPagesAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setCurrentPage(position+1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class WelcomeScreenPagesAdapter extends FragmentStatePagerAdapter {

        public WelcomeScreenPagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return WelcomeFragment.newInstance("Xin cảm ơn", "Bạn đã lựa chọn AAOSync cho hành trình sinh viên của mình. Vuốt sang trái để tiếp tục.", R.drawable.welcome_gift);
                case 1:
                    return WelcomeFragment.newInstance("Làm chủ", "Tự cập nhật điểm, thời khóa biểu và các thông báo quan trọng.", R.drawable.welcome_bell);
                case 2:
                    return WelcomeFragment.newInstance("Theo dõi chúng tôi", "Tìm kiếm AAOSync trên Facebook để cập nhật phiên bản mới nhất.", R.drawable.welcome_tree);
                case 3:
                    return FirstConfigFragment.newInstance();
                default:
                    return WelcomeFragment.newInstance("Lỗi", "Trang này là lựa chọn không có thật.", R.drawable.welcome_gift); // Never happens since we have only 3 pages, as declared in NUM_OF_PAGES.
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }
    }
}
