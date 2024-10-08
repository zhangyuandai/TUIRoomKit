package com.tencent.cloud.tuikit.roomkit.view.page.widget.ScheduleConference.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.cloud.tuikit.engine.common.TUICommonDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.cloud.tuikit.roomkit.R;
import com.tencent.cloud.tuikit.roomkit.common.utils.FetchRoomId;
import com.tencent.cloud.tuikit.roomkit.common.utils.RoomToast;
import com.tencent.cloud.tuikit.roomkit.model.ConferenceEventCenter;
import com.tencent.cloud.tuikit.roomkit.model.controller.ScheduleController;
import com.tencent.cloud.tuikit.roomkit.model.data.ConferenceListState;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ScheduleConferenceView extends FrameLayout {
    private Context                  mContext;
    private FrameLayout              mLayoutSetConferenceDetail;
    private FrameLayout              mLayoutSetConferenceEncrypt;
    private FrameLayout              mLayoutSetConferenceDevice;
    private LinearLayout             mLayoutStartScheduleConference;
    private LinearLayout             mLayoutClose;
    private SetConferenceDetailView  mConferenceDetailView;
    private SetConferenceEncryptView mConferenceEncryptView;
    private SetConferenceDeviceView  mConferenceDeviceView;
    private String                   mConferenceId = "";

    private final ScheduleConferenceStateHolder mStateHolder = new ScheduleConferenceStateHolder();

    public ScheduleConferenceView(Context context) {
        this(context, null);
    }

    public ScheduleConferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mConferenceDetailView = new SetConferenceDetailView(mContext);
        mConferenceEncryptView = new SetConferenceEncryptView(mContext);
        mConferenceDeviceView = new SetConferenceDeviceView(mContext);
        initView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ScheduleController.sharedInstance().loginEngine(null);
    }

    private void initView() {
        addView(LayoutInflater.from(mContext)
                .inflate(R.layout.tuiroomkit_view_schedule_conference,
                        this, false));
        mLayoutClose = findViewById(R.id.ll_return);
        mLayoutClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).finish();
            }
        });
        mLayoutSetConferenceDetail = findViewById(R.id.fl_set_scheduled_conference_info);
        mLayoutSetConferenceEncrypt = findViewById(R.id.fl_set_conference_password);
        mLayoutSetConferenceDevice = findViewById(R.id.fl_set_conference_device);
        mLayoutStartScheduleConference = findViewById(R.id.tuiroomkit_ll_conference_edit_confirm);
        mLayoutStartScheduleConference.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutStartScheduleConference.setClickable(false);
                scheduleConference(new TUIRoomDefine.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        Map<String, Object> params = new HashMap<>();
                        params.put("roomId", mConferenceId);
                        params.put("roomLink", "");
                        ConferenceEventCenter.getInstance().
                                notifyUIEvent(ConferenceEventCenter.RoomKitUIEvent.SCHEDULED_CONFERENCE_SUCCESS, params);
                        ((Activity) mContext).finish();
                    }

                    @Override
                    public void onError(TUICommonDefine.Error error, String message) {
                        mLayoutStartScheduleConference.setClickable(true);
                    }
                });
            }
        });
        mConferenceDetailView.setStateHolder(mStateHolder);
        mConferenceEncryptView.setStateHolder(mStateHolder);
        mConferenceDeviceView.setStateHolder(mStateHolder);
        mLayoutSetConferenceDetail.addView(mConferenceDetailView);
        mLayoutSetConferenceEncrypt.addView(mConferenceEncryptView);
        mLayoutSetConferenceDevice.addView(mConferenceDeviceView);
    }

    private void scheduleConference(TUIRoomDefine.ActionCallback callback) {
        FetchRoomId.fetch(new FetchRoomId.GetRoomIdCallback() {
            @Override
             public void onGetRoomId(String roomId) {
                mConferenceId = roomId;
                ConferenceListState.ConferenceInfo conferenceInfo = new ConferenceListState.ConferenceInfo(roomId);

                SetConferenceDetailUiState conferenceDetailUiState = mStateHolder.mConferenceDetailData.get();
                conferenceInfo.basicRoomInfo.name = conferenceDetailUiState.conferenceName;
                conferenceInfo.basicRoomInfo.isSeatEnabled = conferenceDetailUiState.isSeatEnabled;
                conferenceInfo.scheduleStartTime = conferenceDetailUiState.scheduleStartTime;
                conferenceInfo.scheduleEndTime = conferenceDetailUiState.scheduleStartTime + conferenceDetailUiState.scheduleDuration;
                conferenceInfo.scheduleAttendees = mStateHolder.mAttendeeData.getList();

                SetConferenceDeviceUiState conferenceDeviceUiState = mStateHolder.mConferenceDeviceData.get();
                conferenceInfo.basicRoomInfo.isMicrophoneDisableForAllUser = conferenceDeviceUiState.isMicrophoneDisableForAllUser;
                conferenceInfo.basicRoomInfo.isCameraDisableForAllUser = conferenceDeviceUiState.isCameraDisableForAllUser;

                SetConferenceEncryptViewUiState conferenceEncryptViewUiState = mStateHolder.mConferenceEncryptData.get();
                conferenceInfo.basicRoomInfo.password = conferenceEncryptViewUiState.password;
                if (conferenceInfo.scheduleStartTime < System.currentTimeMillis()) {
                    RoomToast.toastLongMessageCenter(mContext.getString(R.string.tuiroomkit_start_time_earlier_than_current_time));
                    callback.onError(TUICommonDefine.Error.FAILED, null);
                    return;
                }
                String conferenceName = conferenceInfo.basicRoomInfo.name;
                if (TextUtils.isEmpty(conferenceName)) {
                    RoomToast.toastLongMessageCenter(mContext.getString(R.string.tuiroomkit_conference_name_empty));
                    callback.onError(TUICommonDefine.Error.FAILED, null);
                    return;
                }
                if (conferenceName.getBytes(StandardCharsets.UTF_8).length > 100) {
                    RoomToast.toastLongMessageCenter(mContext.getString(R.string.tuiroomkit_conference_name_exceeds_max_length));
                    callback.onError(TUICommonDefine.Error.FAILED, null);
                    return;
                }
                ScheduleController.sharedInstance().scheduleConference(conferenceInfo, callback);
            }
        });
    }
}
