package com.uth.steaks.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uth.steaks.ui.menu.TabPages.FragmentMenuAlmuerzos;
import com.uth.steaks.ui.menu.TabPages.FragmentMenuBebidas;
import com.uth.steaks.ui.menu.TabPages.FragmentMenuCenas;
import com.uth.steaks.ui.menu.TabPages.FragmentMenuDesayunos;
import com.uth.steaks.ui.menu.TabPages.FragmentMenuHoy;
import com.uth.steaks.R;


public class FragmentTabsMenu extends Fragment {
    static TabLayout tabLayout;
    static ViewPager2 viewPager2;
    static TabPageAdapter tabPageAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        viewPager2 = view.findViewById(R.id.ViewMainPage);
        tabLayout = view.findViewById(R.id.TabMainLayout);

        tabPageAdapter = new TabPageAdapter(getChildFragmentManager(), getLifecycle());
        tabPageAdapter.addFragment(new FragmentMenuHoy());
        tabPageAdapter.addFragment(new FragmentMenuDesayunos());
        tabPageAdapter.addFragment(new FragmentMenuAlmuerzos());
        tabPageAdapter.addFragment(new FragmentMenuCenas());
        tabPageAdapter.addFragment(new FragmentMenuBebidas());

        viewPager2.setAdapter(tabPageAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Hoy");
                    break;
                case 1:
                    tab.setText("Desayuno");
                    break;
                case 2:
                    tab.setText("Almuerzo");
                    break;
                case 3:
                    tab.setText("Cena");
                    break;
                case 4:
                    tab.setText("Bebidas");
                    break;
            }
        }).attach();


        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}






