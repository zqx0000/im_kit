package com.yimaxiaoerlang.im_kit.dialog;

public class LodingUtils {
    private static final LodingUtils ourInstance = new LodingUtils();

    private CustomLoadingDialog dialog;

    public static LodingUtils getInstance() {
        return ourInstance;
    }

    private LodingUtils() {
    }

    public void startLoading() {
//        if (dialog == null) {
//            dialog = CustomLoadingDialog(ActivityUtils.top)
//        }
////        if (!(context as Activity).isFinishing) {
//        try {
//            dialog?.let {
//                if (!it.isShowing){
//                    it.show()
//                    it.setCanceledOnTouchOutside(false)
//                }
//            }
////                dialog?.show()
////                dialog?.setCanceledOnTouchOutside(false)
//        } catch (e: Exception) {
//            Log.e("DialogUtils", "--------------startLoading-----$e")
//        }
//        }
    }

    public void stopLoading() {
//        if (dialog != null) {
//            try {
//                dialog!!.dismiss()
//            } catch (e: java.lang.Exception) {
//
//            }
//
//            dialog = null
//        }
    }


}
