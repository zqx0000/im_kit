package com.yimaxiaoerlang.im_kit.view.inputmore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.core.IMKitCallback;

import java.util.ArrayList;
import java.util.List;

public class InputMoreFragment extends Fragment {

    public static final int REQUEST_CODE_FILE = 1011;
    public static final int REQUEST_CODE_PHOTO = 1012;

    private View mBaseView;
    private List<InputMoreActionUnit> mInputMoreList = new ArrayList<>();
    private IMKitCallback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.chat_inputmore_fragment, container, false);
        InputMoreLayout layout = mBaseView.findViewById(R.id.input_extra_area);
        layout.init(mInputMoreList);
        return mBaseView;
    }

    public void setActions(List<InputMoreActionUnit> actions) {
        this.mInputMoreList = actions;
    }

    public void setCallback(IMKitCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE
                || requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode != -1) {
                return;
            }
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            if (mCallback != null) {
                mCallback.onSuccess(uri);
            }
        }
    }
}
