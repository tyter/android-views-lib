package com.sp.android.views.sample.pickerview;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.sp.android.views.PickerView;
import com.sp.android.views.sample.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

class MonthSelectDialog extends Dialog {

    private static final int START_YEAR = 2016;
    private static final int START_MONTH = 7;

    interface Callback {
        void onYearMonthSelected(YearMonth yearMonth);
    }

    static class YearMonth {
        private int mYear;
        private int mMonth;

        YearMonth() {
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
        }

        YearMonth(YearMonth yearMonth) {
            this.mYear = yearMonth.getYear();
            this.mMonth = yearMonth.getMonth();
        }

        YearMonth(boolean start) {
            mYear = START_YEAR;
            mMonth = START_MONTH;
        }

        int getYear() {
            return mYear;
        }

        int getMonth() {
            return mMonth;
        }

        void increaseMonth() {
            if (mMonth + 1 > Calendar.DECEMBER) {
                mYear++;
                mMonth = Calendar.JANUARY;
            } else {
                mMonth++;
            }
        }

        void decreaseMonth() {
            if (mMonth - 1 < Calendar.JANUARY) {
                mYear--;
                mMonth = Calendar.DECEMBER;
            } else {
                mMonth--;
            }
        }

        boolean isStartMonth() {
            return mYear == START_YEAR && mMonth == START_MONTH;
        }

        boolean isEndMonth() {
            Calendar calendar = Calendar.getInstance();
            return mYear == calendar.get(Calendar.YEAR) && mMonth == calendar.get(Calendar.MONTH);
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%d年%d月", mYear, mMonth + 1);
        }
    }

    private List<YearMonth> mYearMonths;
    private PickerView mPickerView;
    private Callback mCallback;

    MonthSelectDialog(Context context) {
        this(context, R.style.date_dialog);
    }

    private MonthSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
        mYearMonths = new ArrayList<>();
        mCallback = null;
        onCreateDialog();
    }

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void onCreateDialog() {
        setCancelable(true);

        setContentView(R.layout.layout_select_month_dialog);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.date_dialog_animate;
        dialogWindow.setAttributes(lp);

        mPickerView = (PickerView) findViewById(R.id.picker_list);
        initMonths();
        initViews();
    }

    private void initMonths() {
        int selected = 0;
        List<String> months = new ArrayList<>();
        YearMonth nowMonth = new YearMonth();
        YearMonth yearMonth = new YearMonth(true);
        while (true) {
            if (yearMonth.toString().equals(nowMonth.toString())) {
                mYearMonths.add(new YearMonth(yearMonth));
                months.add(yearMonth.toString());
                break;
            } else {
                mYearMonths.add(new YearMonth(yearMonth));
                months.add(yearMonth.toString());
                yearMonth.increaseMonth();
            }
        }
        selected = months.size() - 1;

        mPickerView.setList(months);
        mPickerView.setSelected(selected);
    }

    private void initViews() {
        findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.txt_current_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearMonth yearMonth = new YearMonth();
                if (mCallback != null) {
                    mCallback.onYearMonthSelected(yearMonth);
                }
                dismiss();
            }
        });

        findViewById(R.id.txt_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = mPickerView.getSelected();
                YearMonth yearMonth = mYearMonths.get(selected);
                if (mCallback != null) {
                    mCallback.onYearMonthSelected(yearMonth);
                }
                dismiss();
            }
        });
    }
}
