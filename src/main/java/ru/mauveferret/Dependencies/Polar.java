package ru.mauveferret.Dependencies;

import javafx.application.Platform;
import ru.mauveferret.Simulators.Simulator;
import ru.mauveferret.Charts.PolarChart;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Polar extends Dependence {

    public final double phi;
    public final double dPhi;
    public final double dBeta;

    public Polar(double phi, double dPhi, double dBeta, String sort, Simulator calculator) {
        super(calculator, sort);
        //even if user entered phi>180, we will look after the plane with phi<180, which is generally the same
        this.phi = (phi > 180) ? phi-180 : phi;
        // Idea is that input delta is absolute values, but we use at as difference |a-b|<delta
        this.dPhi = dPhi/2;
        this.dBeta = dBeta;

        depType = "distribution";
        distributionSize = (int) Math.ceil(180/dBeta)+1;
        endOfPath="_phi "+phi+"_dphi "+dPhi+"_dBeta"+dBeta+".txt";
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = simulator.createHeader();
        String addheaderComment = " phi "+phi+"  degrees dPhi "+dPhi+" degrees dBeta "+dBeta+" degrees ";
        headerComment += simulator.createLine(addheaderComment)+"*".repeat(simulator.LINE_LENGTH)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }

    public void check(PolarAngles angles, String someSort, String element){
        //if (Math.abs(57.2958*Math.acos(cosa)-phi)<dPhi || (57.2958*Math.acos(cosa) > 180- dPhi))


        //FIXME a lot of NaN in Scatter!
        if (sort.contains(someSort) && (!Double.isNaN(angles.getPolar()))) {
            if (angles.doesAzimuthAngleMatch(phi,dPhi)) {
                //System.out.println((int) Math.round((90+angles.getPolar()) / dBeta));
                distributionArray.get(element)[(int) Math.round((90+angles.getPolar()) / dBeta)]++;
                distributionArray.get("all")[(int) Math.round((90+angles.getPolar()) / dBeta)]++;
            }
            if (angles.doesAzimuthAngleMatch(phi+180,dPhi)) {
                distributionArray.get(element)[(int) Math.round((90-angles.getPolar()) / dBeta)]++;
                distributionArray.get("all")[(int) Math.round((90-angles.getPolar()) / dBeta)]++;
            }

        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {
            try {
                FileOutputStream polarWriter = new FileOutputStream(pathsToLog.get(element));
                //System.out.println(pathsToLog.get(element));
                String stroka;
               polarWriter.write(headerComments.get(element).getBytes());

               double[] newArray = normDistrToDBeta(distributionArray.get(element));

                for (int i = 0; i <= (int) Math.round(180 / dBeta); i++) {
                    stroka = ((i * dBeta - 90)) + columnSeparatorInLog
                            + new BigDecimal(newArray[i]).setScale(3, RoundingMode.UP) + "\n";
                    stroka = stroka.replaceAll(",",".");
                    if (i * dBeta != 90) polarWriter.write(stroka.getBytes());
                }
                polarWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }


        return  true;
    }

    //only for "all" elements of the target (for those which were chosen in GUI: B, S ....)
    @Override
    public boolean visualize() {

        if (!sort.equals("") && doVisualisation)
        Platform.runLater(() -> {
            String title = simulator.projectileElements+" with E0 = "+ simulator.projectileMaxEnergy/1000+" keV that hit "+
                    simulator.targetElements+" target under β = "+simulator.projectileIncidentPolarAngle+" deg.\n"+"The spectrum is calculated for " +
                    "φ = "+phi+"±"+dPhi+" deg, delta β = "+dBeta+" deg"+
                    " for " + sort+" particles";
           new PolarChart(title,normDistrToDBeta(distributionArray.get("all")), dBeta, simulator.projectileIncidentPolarAngle, pathsToLog.get("all"));
        });
        return  true;
    }

    private double[] normDistrToDBeta(double[] someDistr1){

        //We make a copy of someDistr1 because it's not an array, but  just a link to distributionArray
        //If we use someDistr1 further, we would change the arraylist("all"), which is unfavourable
        double[] someDistr = new double[someDistr1.length];
        System.arraycopy(someDistr1, 0, someDistr, 0, someDistr1.length);

        for (int i = 0; i <= (int) Math.round(180 / dBeta); i++) {

            if (Math.abs(i * dBeta -90)!= 0) {
                someDistr[i] = someDistr[i] / (Math.toRadians(dPhi) *
                        Math.sin(Math.toRadians(Math.abs(i * dBeta - 90))));
            } //we can't divide by zero
            else {
                someDistr[i] = someDistr[i] / (Math.toRadians(dPhi) *
                        Math.sin(Math.toRadians(dBeta)));
            }
        }
        return someDistr;
    }
}