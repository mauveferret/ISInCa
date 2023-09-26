package ru.mauveferret;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ScatterColorMap {


    private static double maxParticlesCount = 5;

    double[][] array;
   static double dPhi, dTheta;

   static  String pathToLog = "";

    public ScatterColorMap(String title, double[][] array, double dPhi, double dTheta, String pathToLog) {
        this.array = array;
        this.dPhi = dPhi;
        this.dTheta = dTheta;
        this.pathToLog = pathToLog;

        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pics/CrocoLogo.png")));
        ChartPanel chartPanel = new ChartPanel(createChart(createDataset())) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 640);
            }
        };
        chartPanel.setMouseZoomable(true, false);
        f.add(chartPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        //return low value for the next plots
        maxParticlesCount=5;

        try {
            BufferedImage bi = createChart(createDataset()).createBufferedImage(640,640);
            File outputfile = new File(pathToLog.replace("txt","png"));
            ImageIO.write(bi, "png", outputfile);

        } catch (IOException ex) {
            System.out.println();
        }

    }

    private static JFreeChart createChart(XYDataset dataset) {
        NumberAxis xAxis = new NumberAxis("polar angle, degrees");
        NumberAxis yAxis = new NumberAxis("polar angle, degrees");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYBlockRenderer renderer = new XYBlockRenderer();
        SpectrumPaintScale ps = new SpectrumPaintScale(0, maxParticlesCount);
        renderer.setPaintScale(ps);
        //colored dot's sizes
        renderer.setBlockHeight(dTheta);
        renderer.setBlockWidth(dTheta);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart("particles angular Map",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        NumberAxis scaleAxis = new NumberAxis("particles number");
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setSubdivisionCount(126);
        legend.setMargin(10, 10, 10, 10);


        legend.setStripWidth(40);

        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private  XYZDataset createDataset() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        int n=0;
        for (int i = 0; i < (int) (360/dPhi)+1; i++) {  //azimuth angle
            double[][] data = new double[3][1000];
            for (int j = 0; j < (int) (90/dTheta); j++) {    //polar angle
                data[0][j] = j*dTheta * Math.cos(i*dPhi*2*3.14/360);
                data[1][j] = j*dTheta * Math.sin(i*dPhi*2*3.14/360);
                //if (i>(int) (180/dPhi)) n = i/2;
                //else n = i;
                data[2][j] = array[i][j];
                //finding optimal ColorScale
                if (array[i][j]>maxParticlesCount) maxParticlesCount = array[i][j];

            }
            dataset.addSeries("series" + i, data);
        }
            return dataset;
    }


        private static class SpectrumPaintScale implements PaintScale {

            //colors range
            private static final float H1 = 0.9f;
            private static final float H2 = 0f;
            private final double lowerBound;
            private final double upperBound;

            public SpectrumPaintScale(double lowerBound, double upperBound) {
                this.lowerBound = lowerBound;
                this.upperBound = upperBound;
            }

            @Override
            public double getLowerBound() {
                return lowerBound;
            }

            @Override
            public double getUpperBound() {
                return upperBound;
            }

            @Override
            public Paint getPaint(double value) {
                float scaledValue = (float) (value / (getUpperBound() - getLowerBound()));
                float scaledH = H1 + scaledValue * (H2 - H1);
                return Color.getHSBColor(scaledH, 1f, 1f);
            }
        }
    }