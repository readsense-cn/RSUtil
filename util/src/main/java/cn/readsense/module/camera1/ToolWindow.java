package cn.readsense.module.camera1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.widget.RadioGroup;

import cn.readsense.module.R;
import cn.readsense.module.util.DLog;
import cn.readsense.module.util.DialogUtil;
import cn.readsense.module.util.SoundManager;


class ToolWindow implements RadioGroup.OnCheckedChangeListener {
    private WindowEventListener windowEventListener;
    private Context context;
    private CameraParams cameraParams;

    public ToolWindow(WindowEventListener windowEventListener, Context context, CameraParams cameraParams) {
        this.windowEventListener = windowEventListener;
        this.context = context;
        this.cameraParams = cameraParams;
    }

    void showWindow() {

        AlertDialog.Builder builder = DialogUtil.dialogBuilder(context, "修改摄像头参数", R.layout.camera_setting);

        builder.setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        windowEventListener.eventEnd();
                    }
                });

        final AlertDialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);

        int index = cameraParams.facing == 0 ? R.id.facing_0 : R.id.facing_1;
        checkRadioGroup((RadioGroup) dialog.findViewById(R.id.config_select_facing), index);

        switch (cameraParams.oritationDisplay) {
            case 0:
                index = R.id.angle_0;
                break;
            case 90:
                index = R.id.angle_90;
                break;
            case 180:
                index = R.id.angle_180;
                break;
            case 270:
                index = R.id.angle_270;
                break;
            default:
                index = 0;
        }
        checkRadioGroup((RadioGroup) dialog.findViewById(R.id.rg_angle), index);
        index = cameraParams.filp ? R.id.flip_1 : R.id.flip_0;
        checkRadioGroup((RadioGroup) dialog.findViewById(R.id.flip_), index);
        index = cameraParams.scaleWidth ? R.id.scale_1 : R.id.scale_0;
        checkRadioGroup((RadioGroup) dialog.findViewById(R.id.scale_), index);

    }

    void checkRadioGroup(RadioGroup radioGroup, int indexId) {
        if (indexId != 0)
            radioGroup.check(indexId);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.facing_0) {
            cameraParams.facing = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else if (checkedId == R.id.facing_1) {
            cameraParams.facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else if (checkedId == R.id.angle_0) {
            cameraParams.oritationDisplay = 0;
        } else if (checkedId == R.id.angle_90) {
            cameraParams.oritationDisplay = 90;
        } else if (checkedId == R.id.angle_180) {
            cameraParams.oritationDisplay = 180;
        } else if (checkedId == R.id.angle_270) {
            cameraParams.oritationDisplay = 270;
        } else if (checkedId == R.id.flip_1) {
            cameraParams.filp = true;
        } else if (checkedId == R.id.flip_0) {
            cameraParams.filp = false;
        } else if (checkedId == R.id.scale_1) {
            cameraParams.scaleWidth = true;
        } else if (checkedId == R.id.scale_0) {
            cameraParams.scaleWidth = false;
        }
    }

    interface WindowEventListener {
        void eventEnd();
    }
}
