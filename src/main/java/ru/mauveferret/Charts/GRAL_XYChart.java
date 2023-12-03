package ru.mauveferret.Charts;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.DiscreteLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.areas.LineAreaRenderer2D;

public class GRAL_XYChart extends GRAL_Panel {

    String name;

    @SuppressWarnings("unchecked")
    public GRAL_XYChart(double spectra[], double E0, double dE, String name) {

        this.name = name;

        // Generate data
        DataTable data = new DataTable(Double.class, Double.class);
        for(int i=0; i<=(int) Math.round(E0 / dE); i++){
            data.add(i*dE/1000,spectra[i]);
        }

        // Create data series
        DataSeries seriesLin = new DataSeries("Energy spectrum",data, 0, 1);

        // Create new xy-plot
        XYPlot plot = new XYPlot(seriesLin);

        // Format plot
        plot.setInsets(new Insets2D.Double(10, 0, 0, 0));
        plot.setBackground(Color.WHITE);
        plot.getTitle().setText(getDescription());
        plot.setFont(new Font ("TimesRoman", Font.PLAIN, 24));

        // Format plot area
        plot.getPlotArea().setBackground(new RadialGradientPaint(
                new Point2D.Double(0.5, 0.5),
                0.75f,
                new float[] { 0.6f, 0.8f, 1.0f },
                new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 32), new Color(0, 0, 0, 128) }
        ));


        formatFilledArea(plot, seriesLin, new Color(0, 255, 0, 128));


        // Format axes
        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);

        Label linearAxisLabelX = new Label("Energy, keV");
        linearAxisLabelX.setFont(new Font ("TimesRoman", Font.PLAIN, 18));

        axisRendererX.setTicksAutoSpaced(true);
        axisRendererX.setTickLabelsOutside(false);
        axisRendererX.setShapeStroke(new BasicStroke(3f));
        axisRendererX.setLabel(linearAxisLabelX);
        axisRendererX.setTickFont(new Font ("TimesRoman", Font.PLAIN, 16));

        Label linearAxisLabelY = new Label("Intensity, particles/dE");
        linearAxisLabelY.setRotation(90);
        linearAxisLabelY.setFont(new Font ("TimesRoman", Font.PLAIN, 18));
        axisRendererY.setTicksAutoSpaced(true);
        axisRendererY.setTickLabelsOutside(false);
        axisRendererY.setShapeStroke(new BasicStroke(3f));
        axisRendererY.setLabel(linearAxisLabelY);
        axisRendererX.setTickFont(new Font ("TimesRoman", Font.PLAIN, 16));
        axisRendererX.setIntersection(-Double.MAX_VALUE);
        axisRendererY.setIntersection(-Double.MAX_VALUE);

        // Format rendering of data points
        PointRenderer sizeablePointRenderer = new SizeablePointRenderer();
        sizeablePointRenderer.setColor(GraphicsUtils.deriveDarker(new Color(0, 0, 0, 128)));
        sizeablePointRenderer.setShape(new Ellipse2D.Double(-4, -4, 8, 8));
        plot.setPointRenderers(seriesLin, sizeablePointRenderer);
        // Format data lines
        DiscreteLineRenderer2D discreteRenderer = new DiscreteLineRenderer2D();
        discreteRenderer.setColor(new Color(0, 255, 0, 255));
        discreteRenderer.setStroke(new BasicStroke(
               5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10.0f, new float[] {3f, 6f}, 0.0f));
        plot.setLineRenderers(seriesLin, discreteRenderer);




        InteractivePanel panel = new InteractivePanel(plot);
        panel.setPopupMenuEnabled(true);
        panel.setAutoscrolls(true);
        // Add plot to Swing component
        add(panel, BorderLayout.CENTER);

    }

    @Override
    public String getTitle() {
        return "ISInCa Energy spectrum";
    }

    @Override
    public String getDescription() {
        return name;
    }


    private static void formatFilledArea(XYPlot plot, DataSource data, Color color) {
        PointRenderer point = new DefaultPointRenderer2D();
        point.setColor(color);
        plot.setPointRenderers(data, point);
        LineRenderer line = new DefaultLineRenderer2D();
        line.setColor(color);
        line.setGap(3.0);
        line.setGapRounded(true);
        plot.setLineRenderers(data, line);
        AreaRenderer area = new DefaultAreaRenderer2D();
        area.setColor(GraphicsUtils.deriveWithAlpha(color, 64));
        plot.setAreaRenderers(data, area);
    }
}