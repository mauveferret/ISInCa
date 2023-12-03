package ru.mauveferret.Charts;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;

import javafx.application.Platform;
import javafx.scene.chart.Chart;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import ru.mauveferret.Main;

import javax.swing.*;


public class PolarChart extends ApplicationFrame {

    String title;
    double[] betaDistr;
    double dBeta;
    double Beta;

    public PolarChart(final String title, double[] betaDistr, double dBeta, double Beta, String path) {
        super(title);
        this.title = title;
        path = path.replace(".txt", ".png");
        this.betaDistr = betaDistr;
        this.dBeta = dBeta;
        this.Beta = Beta;

        JFrame f = new JFrame("ISInCa: Polar Dependence");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon icon = new ImageIcon(Main.class.getResource("pics/CrocoLogo.png").getPath());
        f.setIconImage(icon.getImage());
        f.setLayout(new BorderLayout(0, 5));
        ChartPanel chartPanel = new ChartPanel(createChart(createDataset())) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(860, 650);
            }
        };
        JFreeChart chart = chartPanel.getChart();
        chart.getPlot().setBackgroundPaint(Color.BLACK);
        chart.getPlot().setOutlinePaint(Color.WHITE);
        chartPanel.setMouseZoomable(true,true);
        setContentPane(chartPanel);
        f.add(chartPanel, BorderLayout.CENTER);
        f.pack();
        //f.setLocationRelativeTo(null);
        f.setVisible(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);

        //FIXME produce bug: doubled legend. Don't  know how to fix
        try {
            OutputStream ff = new FileOutputStream(new File(path));
            ChartUtilities.writeChartAsPNG(ff,
                    chart,
                    chartPanel.getWidth(),
                    chartPanel.getHeight());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private XYDataset createDataset() {
        final XYSeriesCollection data = new XYSeriesCollection();
        XYSeries mainSeries = new XYSeries("calculation");
        double max = 0;
        for (int i=0; i<betaDistr.length; i++) {
            if (Math.abs(i * dBeta -90)!= 0)
            {
               // System.out.println(i*dBeta-90+" "+betaDistr[i]);
                mainSeries.add(i*dBeta-90, betaDistr[i]);
                if (betaDistr[i]>max) max = betaDistr[i];
            }

        }

       // System.out.println(max);
        XYSeries cosineSeries = new XYSeries("cosine");
        for (int i=0; i<betaDistr.length; i++) {
            cosineSeries.add(i*dBeta-90, max*Math.cos(Math.toRadians(i*dBeta-90)));
        }

        XYSeries projectiles = new XYSeries("incident beam");
        projectiles.add(-1*Beta,max);
        projectiles.add(-1*Beta,0);

        data.addSeries(mainSeries);
        data.addSeries(cosineSeries);
        data.addSeries(projectiles);
        return data;
    }


    private JFreeChart createChart(XYDataset dataset) {

        final JFreeChart chart =  ChartFactory.createPolarChart(
              "Polar Chart of: "+title, dataset, true, true, true);
        PolarPlot polPlot =(PolarPlot)  chart.getPlot();
        polPlot.setAngleGridlinePaint(Color.GREEN);
        DecimalFormat df2 = new DecimalFormat("0");

        polPlot.setAngleTickUnit(new NumberTickUnit(15, df2, 4));
        polPlot.setRadiusGridlinePaint(Color.GREEN);
        Font font = new Font("Verdana", Font.BOLD, 24);
        polPlot.setAngleLabelPaint(Color.WHITE);
        polPlot.setAngleLabelFont(font);

        ValueAxis axis = polPlot.getAxis();
        axis.setTickLabelFont(font);
        axis.setLabelFont(font);


        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) polPlot.getRenderer();

        renderer.setSeriesVisible(0, true);
        renderer.setSeriesVisible(1, true);
        renderer.setSeriesVisible(2, true);


        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1,new BasicStroke(
                4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {10.0f, 6.0f}, 0.0f ));
        renderer.setSeriesStroke(2, new BasicStroke(5.0f));


        renderer.setSeriesFilled(1,false);
        renderer.setSeriesFilled(0,true);
        renderer.setSeriesFilled(2,false);



        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1,Color.CYAN);
        renderer.setSeriesPaint(2,Color.YELLOW);

        return chart;
    }

}