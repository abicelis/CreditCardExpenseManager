package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotGetDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.DailyExpense;

/**
 * Created by Alex on 17/8/2016.
 */
public class LineChartFragment extends Fragment {

    //UI
    private LineChartView chart;

    //DATA
    private ExpenseManagerDAO dao;
    private LineChartData data;
    CreditPeriod creditPeriod;
    CreditCard creditCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        chart = (LineChartView) rootView.findViewById(R.id.chart);

        refreshChartData();

        return rootView;
    }



    public void refreshChartData() {

        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity());


        //Refresh list from DB
        try {
            //TODO: get correct periods, cards, this is a for-now hack
            creditPeriod = dao.getCreditPeriod(0);
            creditCard = dao.getCreditCardList().get(0);
        }catch(CouldNotGetDataException e) {
            Toast.makeText(getContext(), "ERROR getting data for chart", Toast.LENGTH_SHORT).show();
        }


        //Convert the data to PointValues, add those to lines
        List<Line> lines = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> accumulatedValues = new ArrayList<>();
        List<PointValue> dailyValues = new ArrayList<>();
        List<PointValue> maxValue = new ArrayList<>();

        //Set max values
        maxValue.add(new PointValue(0, creditPeriod.getCreditLimit().floatValue()));
        maxValue.add(new PointValue(creditPeriod.getTotalDaysInPeriod(), creditPeriod.getCreditLimit().floatValue()));


        //Set daily and accumulated values.
        List<DailyExpense> dailyExpenses = creditPeriod.getDailyExpenses();
        List<DailyExpense> accumulatedDailyExpenses = creditPeriod.getAccumulatedDailyExpenses();
        for (int i = 0; i < creditPeriod.getTotalDaysInPeriod(); ++i) {
            accumulatedValues.add(new PointValue(i, accumulatedDailyExpenses.get(i).getAmount().floatValue()));
            dailyValues.add(new PointValue(i, dailyExpenses.get(i).getAmount().floatValue()));
            axisValues.add(new AxisValue(i).setLabel(accumulatedDailyExpenses.get(i).getFormattedDate()));
        }

        //Add accumulatedValues line
        Line line = new Line(accumulatedValues);
        line.setColor(ChartUtils.COLOR_BLUE);
        line.setCubic(true);
        line.setFilled(true);
        line.setHasPoints(false);
        lines.add(line);

        //Add maxValue line
        line = new Line(maxValue);
        line.setColor(ChartUtils.COLOR_RED);
        line.setHasPoints(false);
        lines.add(line);


        //Add dailyValues line
        line = new Line(dailyValues);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setPointRadius(5);
        line.setHasLines(false);
        lines.add(line);


        //Add lines to chart data
        data = new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        //Setup axis
        Axis axisX = new Axis(axisValues)
                .setHasTiltedLabels(true)
                .setName("Days");

        Axis axisY = new Axis().setHasLines(true)
                .setHasTiltedLabels(true)
                .setName("Money (" + creditCard.getCurrency().getCode() + ")");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);


        //Setup chart
        chart.setLineChartData(data);
        chart.setMaxZoom(0);
        //chart.startDataAnimation(1000);
        //chart.setViewportCalculationEnabled(false);
        //resetViewport(creditPeriod.getCreditLimit().intValue(), creditPeriod.getTotalDaysInPeriod());
    }


    private void resetViewport(int top, int right) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = top;
        v.left = 0;
        v.right = right - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

}
