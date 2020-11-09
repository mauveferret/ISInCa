package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;
import ru.mauveferret.ScatterColorMap;
import java.awt.*;
import java.io.FileOutputStream;

public class AngleMap extends Dependence {

    private final double dPhi;
    private final double dTheta;

    public AngleMap(double dPhi, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.dPhi = dPhi;
        this.dTheta = dTheta;

        depType = "map";
        mapArrayXsize = (int) Math.ceil(360/dPhi)+1;
        mapArrayYsize = (int) Math.ceil(90/dTheta)+1;
        endOfPath="_dphi "+dPhi+"_dTheta"+dTheta+".txt";
        String addheaderComment = " dPhi "+dPhi+" degrees dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.lineLength)+"\n";
    }

    public  void check (PolarAngles angles, String someSort, String element)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
        {
            mapArray.get(element)[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
            mapArray.get("all")[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
            //FIXME ITS  A TRAP!!!
            mapArray.get(element)[(int) (Math.round((360-angles.getAzimuth())/ dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
            mapArray.get("all")[(int) (Math.round((360-angles.getAzimuth())/ dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
        }
    }

    public double[]getSpectrum() {
        return null;
    }

    @Override
    public boolean logDependencies() {

        //FIXME ITS A TRAP!!!
        for (String element: elements) {

            for (int i = 0; i < (int) Math.ceil(90 / dTheta) + 1; i++) {
                mapArray.get(element)[0][i] = mapArray.get(element)[(int) Math.round((360 - dPhi) / dPhi)][i];
            }

            // from probability distr. to angle

            for (int i = 0; i <= (int) Math.round(360 / dPhi); i++) {
                for (int j = 1; j <= (int) Math.round(90 / dTheta); j++) {
                    mapArray.get(element)[i][j] = mapArray.get(element)[i][j] / (dPhi * Math.sin(Math.toRadians(Math.abs(j * dTheta))));
                }
            }

            try {
                FileOutputStream surfaceWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka = "Phi";
                surfaceWriter.write(headerComments.get(element).getBytes());
                for (int i = 0; i <= (int) Math.round(90 / dTheta); i++) {
                    stroka = stroka + columnSeparatorInLog + (int) (i * dTheta);
                }
                stroka = stroka + "\n";
                surfaceWriter.write(stroka.getBytes());

                for (int i = 0; i <= (int) Math.round(360 / dPhi); i++) {
                    stroka = (int) (i * dPhi) + columnSeparatorInLog;
                    for (int j = 0; j <= (int) Math.round(90 / dTheta); j++) {
                        if (i < (int) Math.round(360 / dTheta)) stroka = stroka + mapArray.get(element)[i][j] + columnSeparatorInLog;
                        else stroka = stroka + mapArray.get(element)[i - 1][j] + columnSeparatorInLog;
                    }
                    stroka = stroka + "\n";
                    surfaceWriter.write(stroka.getBytes());
                }
                surfaceWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return  true;
    }

    @Override
    public boolean visualize() {
        if (!sort.equals("") && doVisualisation) EventQueue.invokeLater(() ->
                new ScatterColorMap("ISInCa",  mapArray.get("all"), dPhi, dTheta));
        return  true;
    }


}

