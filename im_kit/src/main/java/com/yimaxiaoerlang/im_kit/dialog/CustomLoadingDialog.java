package com.yimaxiaoerlang.im_kit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yimaxiaoerlang.im_kit.R;
import com.wang.avi.AVLoadingIndicatorView;

//class CustomLoadingDialog(context:Context) : BaseDialog<CustomLoadingDialog>(context) {
//        override fun setUiBeforShow() {
//        }
//
//        override fun onCreateView(): View {
//        widthScale(0.80f)
//        //        widthScale(0);
////        showAnim(Swing())
//        val inflate =LayoutInflater.from(context).inflate(R.layout.dialog_loading,null)
////            View.inflate(WeakReference<Context>(context).get(), R.layout.dialog_loading, null)
//        //        inflate.setBackgroundDrawable(
//        //                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
//
//        inflate.findViewById<AVLoadingIndicatorView>(R.id.avi).show()
//        return inflate
//        }
//        }
public class CustomLoadingDialog extends BaseDialog<CustomLoadingDialog> {
    private Context context;

    public CustomLoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateView() {
        widthScale(0.80f);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        ((AVLoadingIndicatorView) (view.findViewById(R.id.avi))).show();
        return view;
    }

    @Override
    public void setUiBeforShow() {

    }
}
