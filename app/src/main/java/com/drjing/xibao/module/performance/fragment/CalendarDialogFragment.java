package com.drjing.xibao.module.performance.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.MaterialCalendarView;
import com.drjing.xibao.common.view.calendarview.OnDateSelectedListener;
import com.kristain.common.utils.DateTimeUtils;

/**
 * Created by kristain on 16/1/21.
 */
public final class CalendarDialogFragment extends DialogFragment implements OnDateSelectedListener {

    public static CalendarDialogFragment df;


    public static CalendarDialogFragment newInstance() {
        if(df==null){
            df = new CalendarDialogFragment();
        }
        return df;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.calendardialog_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        SelectListener listener = (SelectListener) getActivity();
        listener.onSelectComplete(DateTimeUtils.formatDateTime(date.getDate(), DateTimeUtils.DF_YYYY_MM_DD));
        dismiss();
    }

    public interface SelectListener
    {
        void onSelectComplete(String date);
    }

}
