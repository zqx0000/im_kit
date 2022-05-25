package com.yimaxiaoerlang.im_kit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.core.YMIMKit;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.UserUtils;

public class MainMeFragment  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_me, null);
        view.findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YMIMKit.getInstance().logout();
                startActivity(new Intent(getActivity(),YMLoginActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView(requireView());
    }

    private void initView(View view) {
        ImageView headImage = view.findViewById(R.id.iv_icon);
        TextView name = view.findViewById(R.id.tv_name);
        TextView uid = view.findViewById(R.id.tv_uid);
        ImageUtils.loadImage(headImage, UserUtils.getInstance().getUser().getUserAvatar());
        name.setText(UserUtils.getInstance().getUser().getUsername());
        uid.setText("用户ID: " + UserUtils.getInstance().getUser().getUid());
    }
}
