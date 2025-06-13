package ru.mauveferret.Dependencies;

import javafx.application.Platform;
import ru.mauveferret.Charts.JFree_EnergyChart;
import ru.mauveferret.Simulators.Simulator;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class Energy extends Dependence {

    public final double E0;
    public final double beta;
    public final double dBeta;
    public final double phi;
    public final double dPhi;
    public final double Estep;

    public final double deltaEtoE;
    private double energyChannelWidth;
    private boolean IsdEconstChoosen;

    public Energy(double dE, double phi, double dPhi, double beta, double dBeta, String sort, Simulator calculator, double deltaEtoE, boolean IsdEconstChoosen) {
        super(calculator, sort);
        this.E0 = calculator.projectileMaxEnergy;
        this.beta = beta;
        // Idea is that input delta is absolute values, but we use at as difference |a-b|<delta
        this.dBeta = dBeta/2;
        this.phi = phi;
        this.dPhi = dPhi/2;
        this.Estep = dE;
        this.deltaEtoE = deltaEtoE;
        this.IsdEconstChoosen = IsdEconstChoosen;

        depType = "distribution";
        distributionSize = (int) Math.ceil(E0/dE)+1;
        endOfPath="_beta "+beta+"_phi "+phi+"_Estep"+dE+".txt";
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = simulator.createHeader();
        String addheaderComment = " E step "+ Estep +" eV "+ ((deltaEtoE!=0.0)?((IsdEconstChoosen)?" , dE = "+deltaEtoE+ " eV":" , dE/E = "+deltaEtoE):"")+" beta "+beta+" deg dBeta "+dBeta+" deg phi "+
                phi+" deg dPhi "+dPhi+" deg";
        headerComment += simulator.createLine(addheaderComment)+"*".repeat(simulator.LINE_LENGTH)+"\n";
        headerComment= "Energy Intensity"+"\n"+"eV  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }

    public void check(PolarAngles angles, String someSort, double E, String element){

        if (sort.contains(someSort)) {

            if (angles.doesAzimuthAngleMatch(phi, dPhi) && angles.doesPolarAngleMatch(beta, dBeta)) {

                if (deltaEtoE==0) {
                    distributionArray.get(element)[(int) Math.round(E / Estep)]++;
                    distributionArray.get("all")[(int) Math.round(E / Estep)]++;
                }
                else {
                    //Consider that a particle with specific energy may contribute to different "bins" of the energy analyser due to dE/E = const
                    energyChannelWidth = (IsdEconstChoosen)?deltaEtoE/2:E * deltaEtoE/2;
                   /*

                   //old variant. Seems to be incorrect, as we give a distortion not to the channel, but to each particle
                    for (int E_bins=(int) (Math.round((E-broadening)/dE));E_bins<=(int) (Math.round((E+broadening)/dE)) && E_bins<distributionSize;E_bins++){
                        distributionArray.get(element)[E_bins]++;
                        distributionArray.get("all")[E_bins]++;
                        if (E_bins>(int) (Math.round((E-broadening)/dE))) System.out.println(dE+" "+deltaEtoE+" "+broadening+" "+E+" "+E_bins+" "+(int) (Math.round((E-broadening)/dE)));
                    }
                    */
                    for (int E_bins = (int) (Math.round(E/ Estep)-Math.round(energyChannelWidth/ Estep)); E_bins<=(int) (Math.round(E/ Estep)+Math.round(energyChannelWidth/ Estep)) && E_bins<distributionSize; E_bins++){
                        if ((Math.abs(E_bins* Estep -E)< energyChannelWidth)&&(E_bins>=0)){
                            distributionArray.get(element)[E_bins]++;
                            distributionArray.get("all")[E_bins]++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {

            if (!isNumeric(element)) {
                double[] newArray = new double[distributionArray.get(element).length];
                for (int j = 0; j < newArray.length; j++) {
                    newArray[j] = distributionArray.get(element)[j];
                    // fix by @Aksiradmir
                    if (j==0 || j==newArray.length-1) newArray[j] *=2;
                }


                try {
                    FileOutputStream energyWriter = new FileOutputStream(pathsToLog.get(element));
                    String stroka;
                    energyWriter.write(headerComments.get(element).getBytes());
                    for (int i = 0; i <= (int) Math.round(E0 / Estep); i++) {
                        stroka = String.format("%.2f",i * Estep) + columnSeparatorInLog
                                + new BigDecimal(newArray[i]).
                                setScale(3, RoundingMode.UP) + "\n";
                        stroka = stroka.replaceAll(",",".");
                        energyWriter.write(stroka.getBytes());
                    }
                    energyWriter.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        }
        return  true;
    }

    @Override
    public boolean visualize() {

        //make normalization on dE for the chart
        double[] normSpectrum = new double[distributionArray.get("all").length];
        System.arraycopy(distributionArray.get("all"), 0, normSpectrum, 0, distributionArray.get("all").length);
        //for (int i=0; i<normSpectrum.length; i++) normSpectrum[i] = normSpectrum[i]/ Estep;

        Platform.runLater(() -> {

            if (!sort.equals("") && doVisualisation) {

                //For different Chart types

                /*new GUI().showGraph(normSpectrum, E0, dE,
                        simulator.projectileElements+" --> "+ simulator.targetElements+" phi = "+phi+" beta = "+beta);
                 */


                /*
                new GRAL_XYChart(normSpectrum, E0,  dE, simulator.projectileElements+" with E0="+(int) (E0/1000)+" keV strikes "+
                        simulator.targetElements+" target under φ = "+phi+"±"+dPhi+" deg β = "+beta+"±"+dBeta+"deg dE/E= "+deltaEtoE).showInFrame();
                 */

                new JFree_EnergyChart( normSpectrum,  E0, Estep,  simulator.projectileElements+" with E0 = "+E0/1000+" keV that hit "+
                        simulator.targetElements+" target under β = "+simulator.projectileIncidentPolarAngle+" deg. \n" +
                        "The spectrum is calculated for φ = "+phi+"±"+dPhi+" deg, β = "+beta+"±"+dBeta+" deg"+
                        ((deltaEtoE!=0.0)?((IsdEconstChoosen)?" , dE = "+deltaEtoE+ " eV":" , dE/E = "+deltaEtoE):"")+" for " + sort+" particles", pathsToLog.get("all"));

            }
        });
        return  true;
    }
}



