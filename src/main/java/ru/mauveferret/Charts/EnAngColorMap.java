package ru.mauveferret.Charts;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class EnAngColorMap {


    private static double maxParticlesCount = 5;

    double[][] array;
   static double E0, dE, dBeta;
   static  String title = "";
   static  String pathToLog = "";
    public EnAngColorMap(String title, double[][] array, double E0, double dE, double dBeta, String pathToLog) {
        this.array = array;
        this.dE = dE;
        this.E0 = E0;
        this.dBeta = dBeta;
        this.title = title;
        this.pathToLog = pathToLog;

        try {
            BufferedImage bi = createChart(createDataset()).createBufferedImage(640,640);
            File outputfile = new File(pathToLog.replace("txt","png"));
            ImageIO.write(bi, "png", outputfile);

        } catch (IOException  ex) {
            System.out.println();
        }
        try{
            JFrame f = new JFrame("ISInCa: energy/angle colored map");
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
        }
        catch (Exception e){
            System.out.println("[WARNING ] Are you in GUI mode?! No X11 DISPLAY variable was set. Check EnAngColorMap.");
        }
    }

    private static JFreeChart createChart(XYDataset dataset) {
        NumberAxis xAxis = new NumberAxis("polar angle, degrees");
        NumberAxis yAxis = new NumberAxis("energy, keV");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYBlockRenderer renderer = new XYBlockRenderer();
        SpectrumPaintScale ps = new SpectrumPaintScale(0, maxParticlesCount);
        renderer.setPaintScale(ps);
        //colored dot's sizes
        renderer.setBlockHeight(dE/1000);
        renderer.setBlockWidth(dBeta);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title,
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        NumberAxis scaleAxis = new NumberAxis("particle's count");
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
        for (int i = 0; i < (int) (E0/dE)+1; i++) {  //energies
            double[][] data = new double[3][10000];
            for (int j = 0; j < (int) (90/dBeta); j++) {    //polar angle
                data[1][j] = i*dE/1000;
                data[0][j] = j*dBeta;
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
            private static final float H1 = 0.8f;
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