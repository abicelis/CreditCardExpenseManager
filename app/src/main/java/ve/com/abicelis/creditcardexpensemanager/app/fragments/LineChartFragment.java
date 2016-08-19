package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    //UI
    private LineChartView chart;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean isCubic = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private boolean hasAxes = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;

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

       // generateValues();
        //generateData();
        refreshChartData();



        //chart.setViewportCalculationEnabled(false);
        //resetViewport();



        return rootView;
    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
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



        //Convert the data to PointValues
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<>();


        List<DailyExpense> accumulatedDailyExpenses = creditPeriod.getDailyExpenses();


        for (int i = 0; i < creditPeriod.getTotalDaysInPeriod(); ++i) {
            values.add(new PointValue(i, accumulatedDailyExpenses.get(i).getAmount().floatValue()));
            axisValues.add(new AxisValue(i).setLabel(accumulatedDailyExpenses.get(i).getFormattedDate()));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);


        List<Line> lines = new ArrayList<>();
        lines.add(line);

        data = new LineChartData(lines);


        if (hasAxes) {
            Axis axisX = new Axis(axisValues).setHasTiltedLabels(true);
            Axis axisY = new Axis().setHasLines(true)
                    .setHasTiltedLabels(true);
                    //.setFormatter(new SimpleAxisValueFormatter().setAppendedText(creditCard.getCurrency().getCode().toCharArray()));
            axisX.setName("Days");
            axisY.setName("Money (" + creditCard.getCurrency().getCode() + ")");

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);
        chart.setMaxZoom(0);
        chart.startDataAnimation(300);

    }


}
