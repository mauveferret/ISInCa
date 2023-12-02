package ru.mauveferret;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import ru.mauveferret.Simulators.Simulator;
import ru.mauveferret.Simulators.SDTrimSP;
import ru.mauveferret.Simulators.Scatter;
import ru.mauveferret.Simulators.TRIM;
import ru.mauveferret.Dependencies.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class RootFxmlController {

    //FileChooser chooser = new FileChooser();
    //private boolean accessGranted=true;
    Simulator yourSimulator;

    //Buttons for calculating
    @FXML
    private CheckBox getTXT;
    @FXML
    private CheckBox getSummary;
    @FXML
    CheckBox visualize;

    //energy distributions
    @FXML
    private CheckBox NEB,NES,NET;

    //polar distributions
    @FXML
    private CheckBox NBetaB, NBetaS,NBetaT;

    //angle map
    @FXML
    private CheckBox NBetaPhiB, NBetaPhiS, NBetaPhiT;
    @FXML
    private CheckBox NEBetaB, NEBetaS, NEBetaT;
    @FXML
    private CheckBox NzyB, NzyS, NzyI,NzyT, NzyD;
    @FXML
    private CheckBox NzxB, NzxS, NzxI,NzxT, NzxD;
    //other
    @FXML
    private CheckBox depthProfile;


    ///////////////////////////

    @FXML
    private TextField time;
    @FXML
    private TextField codeName;

    @FXML
    private Label secret;
    @FXML
    private Button button;
    //Energy spectrum buttons
    @FXML
    private TextField E0;
    @FXML
    private TextField energyResolution;
    @FXML
    private TextField deltaEtoE;
    @FXML
    private TextField polarAngleNE;
    @FXML
    private TextField dPolarAngleNE;
    @FXML
    private TextField azimuthAngleNE;
    @FXML
    private TextField dAzimuthAngleNE;
    //PolarAngle spectrum buttons

    //energy?

    @FXML
    private TextField dPolarAngleNbeta;
    @FXML
    private TextField azimuthAngleNbeta;
    @FXML
    private TextField dAzimuthAngleNbeta;

    //Surface particle distribution
    @FXML
    private TextField NdBetaPhi, NBetadPhi;
    @FXML
    private TextField NEBetadBeta, NEBetadE;

    // particles coefficients fields
    @FXML
    private TextField count;
    @FXML
    private TextField scattered;
    @FXML
    private TextField sputtered;
    @FXML
    private TextField implanted;
    @FXML
    private TextField transmitted;
    @FXML
    private TextField displaced;
    @FXML
    private TextField numberOfParticlesInScatter;
    @FXML
    TextField energyScattering;

    String path="lol";
    File file;

    @FXML
    public void readme()
    {
        Platform.runLater(() -> new GUI().showHelpPage("pics/readme.png"));
    }

    @FXML
    public void openGIT(){
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/mauveferret/ISInCa/blob/master/Readme.md").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void pushed() {

        DirectoryChooser chooser = new DirectoryChooser();

        try {


            if (path.equals("lol")) {
                //looking for SC*.dat in  jar directory
                String myJarPath = RootFxmlController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                String dirPath = new File(myJarPath).getParent();

                 chooser = new DirectoryChooser();
                chooser.setTitle("Choose data files directory (like /out (SCATTER) or case/.. (SDTrimSP) or /outputs (TRIM)");
                File defaultDirectory = new File(dirPath);
                //chooser.setInitialDirectory(defaultDirectory);
                // File selectedDirectory = chooser.showDialog(primaryStage);

            } else
                chooser.setInitialDirectory(new File(path.substring(0, path.lastIndexOf("\\"))));

            file = chooser.showDialog(button.getScene().getWindow());
            path = file.getAbsolutePath();

            yourSimulator = new Scatter(path,true);
            String initialize = yourSimulator.initializeModelParameters();
            if (!initialize.equals("OK")) {
                //System.out.println(initialize);
                yourSimulator = new TRIM(path,true);
                initialize = yourSimulator.initializeModelParameters();
                if (!initialize.equals("OK")) {
                    //System.out.println(initialize);
                    yourSimulator = new SDTrimSP(path, visualize.isSelected(), getSummary.isSelected());
                    initialize = yourSimulator.initializeModelParameters();
                    if (!initialize.equals("OK")) {
                        //System.out.println(initialize);
                        new GUI().showNotification("ERROR: "+initialize);
                    } else codeName.setText("SDTrimSP");
                } else codeName.setText("TRIM");
            } else codeName.setText("SCATTER");
            if (!initialize.equals("OK"))
                new GUI().showNotification("ERROR: directory doesn't contain all files for calculation");

            numberOfParticlesInScatter.setText(yourSimulator.projectileAmount + "");
            E0.setText(yourSimulator.projectileMaxEnergy + "");
            energyResolution.setText((yourSimulator.projectileMaxEnergy/100)+"");
            NEBetadE.setText((yourSimulator.projectileMaxEnergy/100)+"");

            polarAngleNE.setText(yourSimulator.projectileIncidentPolarAngle + "");
            azimuthAngleNE.setText(yourSimulator.projectileIncidentAzimuthAngle + "");
        }
        catch (Exception e)
        {
            System.out.println("ERROR175: "+e.getMessage());
        }
}

    @FXML
    public void AngleChanged()
    {
        try {
            double t = Double.parseDouble(polarAngleNE.getText());
            if (t < 0 || t > 90) {
                polarAngleNE.setText("74");
                new GUI().showNotification("Please set β in a range of  0<=β<=90\n For transmitted particles use T flag.");
            }
        }
        catch (Exception e)
        {
            polarAngleNE.setText("74");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNE.getText());
            if (t < 0 || t > 180) {
                azimuthAngleNE.setText("0");

                new GUI().showNotification("Please set φ in a range of  0<=φ<=180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNE.setText("0");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNbeta.getText());
            if (t < 0 || t > 179 ) {
                azimuthAngleNbeta.setText("0");

                new GUI().showNotification("Please set φ in a range of  0<=φ<=180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNbeta.setText("0");
        }
    }
    @FXML
    public void DeltaChanged()
    {
        try {
            double t = Double.parseDouble(dPolarAngleNE.getText());
            if (t <= 0 ) {
                dPolarAngleNE.setText("2");
                new GUI().showNotification("Please set positive dβ.");
            }
        }
        catch (Exception e)
        {
            dPolarAngleNE.setText("2");
        }
        try {
            double t = Double.parseDouble(dAzimuthAngleNE.getText());
            if (t <= 0 ) {
                dAzimuthAngleNE.setText("2");

                new GUI().showNotification("Please set positive dφ.");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNE.setText("2");
        }
        try {
            double t = Double.parseDouble(dAzimuthAngleNbeta.getText());
            if (t <= 0 ) {
                dAzimuthAngleNbeta.setText("2");

                new GUI().showNotification("Please set positive dβ.");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNbeta.setText("2");
        }
        try {
            double t = Double.parseDouble(NEBetadBeta.getText());
            if (t <= 0 ) {
                NEBetadBeta.setText("2");

                new GUI().showNotification("Please set positive dβ.");

            }
        }
        catch (Exception e)
        {
            NEBetadBeta.setText("2");
        }
    }

    @FXML
    public void EChanged()
    {
        try {
            double E = Double.parseDouble(E0.getText());
            if (E<0) throw new Exception();
            energyResolution.setText((int) (E/100)+"");
        }
        catch (Exception e)
        {
            E0.setText("25000");
        }

        try {
            double E = Double.parseDouble(deltaEtoE.getText());
            if (E<0) throw new Exception();
        }
        catch (Exception e)
        {
            deltaEtoE.setText("0");
            new GUI().showNotification("Please set correct deltaEtoE");
        }
    }

    @FXML
    public void ResChanged()
    {
        try {
            double E = Double.parseDouble(energyResolution.getText());
            if (E<0) throw new Exception();
        }
        catch (Exception e)
        {
            energyResolution.setText("250");
        }
    }

    @FXML
    public void secretLaunch()
    {
        if (secret.getText().equals("А не попить ли чаю?")) secret.setText("");
        else
        secret.setText("А не попить ли чаю?");
    }

    @FXML
    public  void showHelp()
    {
        Platform.runLater(() -> new GUI().showHelpPage("pics/axes.png"));
    }

    @FXML
    public  void runCalculation()
    {

        if (path.equals("lol"))
        {
            Platform.runLater(() -> new GUI().showNotificationAboutFile());
        }

            new Thread(() -> {
                double E;
                if (!E0.getText().equals("WAIT"))
                     E=Double.parseDouble(E0.getText());
                else E=yourSimulator.projectileMaxEnergy;

                //int energyReturnValue = E;
                double dE = Double.parseDouble(energyResolution.getText());
                double betaNE = Double.parseDouble(polarAngleNE.getText());
                double dBetaNE = Double.parseDouble(dPolarAngleNE.getText());
                double energyAnalyserBroadening = Double.parseDouble(deltaEtoE.getText().replace(",","."));
                double phiNE=Double.parseDouble(azimuthAngleNE.getText());
                double dphiNE=Double.parseDouble(dAzimuthAngleNE.getText());

                ///////////

                double dBetaNBeta=Double.parseDouble(dPolarAngleNbeta.getText());
                double phiNBeta=Double.parseDouble(azimuthAngleNbeta.getText());
                double dPhiNBeta=Double.parseDouble(dAzimuthAngleNbeta.getText());
                double NBetadPhi1 = Double.parseDouble(NBetadPhi.getText());
                double NdBetaPhi1 = Double.parseDouble(NdBetaPhi.getText());
                double NEBetadBeta1 = Double.parseDouble(NEBetadBeta.getText());
                double NEBetadE1 = Double.parseDouble(NEBetadE.getText());

                E0.setText("WAIT");

                //basicDistributions

                ArrayList<Dependence> distributions = new ArrayList<>();

                String sort = (NEB.isSelected()) ? "B" : "";
                sort += (NES.isSelected()) ? "S" : "";
                sort += (NET.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new Energy(dE,phiNE, dphiNE, betaNE, dBetaNE,sort, yourSimulator, energyAnalyserBroadening));

                sort = (NBetaB.isSelected()) ? "B" : "";
                sort += (NBetaS.isSelected()) ? "S" : "";
                sort += (NBetaT.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new Polar(phiNBeta, dPhiNBeta, dBetaNBeta,sort, yourSimulator));

                sort = (NBetaPhiB.isSelected()) ? "B" : "";
                sort += (NBetaPhiS.isSelected()) ? "S" : "";
                sort += (NBetaPhiT.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new AngleMap(dPhiNBeta, dBetaNBeta,sort, yourSimulator));

                sort = (NEBetaB.isSelected()) ? "B" : "";
                sort += (NEBetaS.isSelected()) ? "S" : "";
                sort += (NEBetaT.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new AngEnMap(NEBetadBeta1,NEBetadE1, sort, yourSimulator));

                //FIXME full funtional is not presented, not made in Scatter, Trim

                sort = (NzyB.isSelected()) ? "B" : "";
                sort += (NzyS.isSelected()) ? "S" : "";
                sort += (NzyI.isSelected()) ? "I" : "";
                sort += (NzyT.isSelected()) ? "T" : "";
                sort += (NzyD.isSelected()) ? "D" : "";

                if (!sort.equals("")) distributions.add(new CartesianMap(1, "ZY", sort, yourSimulator));

                if (getTXT.isSelected()) distributions.add(new getTXT(yourSimulator, ""));

                yourSimulator.postProcessCalculatedFiles(distributions);
                yourSimulator.printAndVisualizeData(distributions);

                //Calculation is ended

                E0.setText(E+"");
                count.setText(
                        new BigDecimal(yourSimulator.particleCount).setScale(5, RoundingMode.UP).doubleValue()+"");
                time.setText(yourSimulator.calcTime +"");

                scattered.setText(new BigDecimal( yourSimulator.scattered.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                sputtered.setText(new BigDecimal(yourSimulator.sputtered.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                implanted.setText(new BigDecimal(yourSimulator.implanted.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                displaced.setText(new BigDecimal(yourSimulator.displaced.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                transmitted.setText(new BigDecimal(yourSimulator.transmitted.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                energyScattering.setText(new BigDecimal(yourSimulator.energyRecoil.get("all")).setScale(3, RoundingMode.UP).doubleValue()
                        +"");

                //FIXME to remove: every launch we create new calculator?!
                for (String element: yourSimulator.elementsList) {
                    yourSimulator.scattered.put(element,0.0);
                    yourSimulator.sputtered.put(element, 0.0);
                    yourSimulator.implanted.put(element, 0.0);
                    yourSimulator.transmitted.put(element, 0.0);
                    yourSimulator.displaced.put(element, 0.0);
                    yourSimulator.energyRecoil.put(element,0.0);
                }

                yourSimulator.particleCount = 0;

            }).start();
    }
}
