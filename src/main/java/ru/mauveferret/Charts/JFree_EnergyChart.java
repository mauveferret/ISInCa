package ru.mauveferret.Charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import ru.mauveferret.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class JFree_EnergyChart extends ApplicationFrame{
    String name;

    public JFree_EnergyChart(double spectra[], double E0, double dE, String name, String path) {
        super(name);

        this.name = name;
        path = path.replace(".txt", ".png");


        JFrame f = new JFrame("ISInCa: Energy spectrum");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setResizable(true);
        ImageIcon icon = new ImageIcon(Main.class.getResource("pics/CrocoLogo.png"));
        f.setIconImage(icon.getImage());
        f.setLayout(new BorderLayout(0, 5));

        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                "Energy spectrum for: "+name ,
                "Energy, keV" ,
                "Intensity, particles/dE" ,

                createDataset( spectra,  E0,  dE) ,
                PlotOrientation.VERTICAL ,
                false , true , false);

        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 890 , 650 ) );
        chartPanel.setMouseZoomable(true,true);

        chartPanel.setDisplayToolTips(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setAutoscrolls(true);
        chartPanel.setBackground(Color.BLACK);

        final XYPlot plot = xylineChart.getXYPlot( );
        XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA_AND_SHAPES);

        renderer.setSeriesVisible(0, true);
        renderer.setOutline(true);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.BLUE);

        renderer.setSeriesPaint(0, new Color(0, 180, 0, 80));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesStroke( 0 , new BasicStroke( 2.0f ) );
        renderer.setBaseOutlinePaint(Color.WHITE);
        renderer.setBaseFillPaint(Color.GREEN);
        renderer.setBaseOutlineStroke(new BasicStroke(2f));

        renderer.setBaseFillPaint(Color.PINK);
        chartPanel.getPopupMenu().setLocale(new Locale.Builder()
                .setLanguage("en")
                .build());

        plot.setRenderer(renderer);

        //Set scale for Y axis to max in a range  of (400 eV, Emax), as 0-400 eV is useless high sputter tail
        ValueAxis axis =  plot.getRangeAxis();
        double maxEnergy = 0;
        for (int i=(int) (400/dE); i<spectra.length; i++) maxEnergy= Math.max(maxEnergy, spectra[i]);
        axis.setRange(0, maxEnergy*1.08);
        plot.setBackgroundPaint(Color.BLACK);
        setContentPane( chartPanel );

        f.add(chartPanel, BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);

        try {
            OutputStream ff = new FileOutputStream(new File(path));
            ChartUtilities.writeChartAsPNG(ff,
                    xylineChart,
                    chartPanel.getWidth(),
                    chartPanel.getHeight());

        } catch (Exception ex) {
            System.out.println("[ERROR 126]"+ex.getMessage());
        }
    }

    private XYDataset createDataset(double spectra[], double E0, double dE ) {

        final XYSeries data = new XYSeries( "Energy Spectrum" );
        for(int i=0; i<=(int) Math.round(E0 / dE); i++){
            data.add(i*dE/1000,spectra[i]);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( data );
        return dataset;
    }
}