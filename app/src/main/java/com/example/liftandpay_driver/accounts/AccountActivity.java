package com.example.liftandpay_driver.accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.liftandpay_driver.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    private final String[] titles = new String[]{"ALL", "RECEIVED", "WITHDRAWAL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        viewPager2.setAdapter(new ViewPagerFragmentStateAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titles[position])
        ).attach();
    }
    public class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {
        public ViewPagerFragmentStateAdapter(FragmentActivity fragmentActivity){
            super(fragmentActivity);
        }
        @Override
        public Fragment createFragment(int position){
            switch(position){
                case 0:
                    return new All();
                case 1:
                    return new Received();
                case 2:
                    return new Withdrawal();
            }
            return new All();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}