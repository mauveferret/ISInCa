package ru.mauveferret;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import ru.mauveferret.Simulators.ParticleInMatterCalculator;
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
    ParticleInMatterCalculator yourCalcuator;

    //Buttons for calculating
    @FXML
    private CheckBox getTXT;
    @FXML
    private CheckBox getSummary;
    @FXML CheckBox visualize;

    //energy distributions
    @FXML
    private CheckBox NEB;
    @FXML
    private CheckBox NES;
    @FXML
    private CheckBox NEI;
    @FXML
    private CheckBox NET;

    //polar distributions
    @FXML
    private CheckBox NThetaB;
    @FXML
    private CheckBox NThetaS;
    @FXML
    private CheckBox NThetaT;
    @FXML
    private CheckBox NThetaI;

    //angle map
    @FXML
    private CheckBox NThetaPhiB;
    @FXML
    private CheckBox NThetaPhiS;
    @FXML
    private CheckBox NThetaPhiI;
    @FXML
    private CheckBox NThetaPhiT;
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
    private TextField dPolarAngleNtheta;
    @FXML
    private TextField azimuthAngleNtheta;
    @FXML
    private TextField dAzimuthAngleNtheta;

    //Surface particle distribution
    @FXML
    private TextField NdThetaPhi;
    @FXML
    private TextField NThetadPhi;

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
                chooser.setTitle("Choose data files directory (like /out (SCATTER) or /case/.. (SDTrimSP) or /outputs (TRIM)");
                File defaultDirectory = new File(dirPath);
                //chooser.setInitialDirectory(defaultDirectory);
                // File selectedDirectory = chooser.showDialog(primaryStage);

            } else
                chooser.setInitialDirectory(new File(path.substring(0, path.lastIndexOf("\\"))));

            file = chooser.showDialog(button.getScene().getWindow());
            path = file.getAbsolutePath();

            yourCalcuator = new Scatter(path,true);
            String initialize = yourCalcuator.initializeModelParameters();
            if (!initialize.equals("OK")) {
                //System.out.println(initialize);
                yourCalcuator = new TRIM(path,true);
                initialize = yourCalcuator.initializeModelParameters();
                if (!initialize.equals("OK")) {
                    //System.out.println(initialize);
                    yourCalcuator = new SDTrimSP(path, visualize.isSelected(), getSummary.isSelected());
                    initialize = yourCalcuator.initializeModelParameters();
                    if (!initialize.equals("OK")) {
                        //System.out.println(initialize);
                        new GUI().showNotification("ERROR: "+initialize);
                    } else codeName.setText("SDTrimSP");
                } else codeName.setText("TRIM");
            } else codeName.setText("SCATTER");
            if (!initialize.equals("OK"))
                new GUI().showNotification("ERROR: directory doesn't contain all files for calculation");

            numberOfParticlesInScatter.setText(yourCalcuator.projectileAmount + "");
            E0.setText(yourCalcuator.projectileMaxEnergy + "");
            energyResolution.setText((yourCalcuator.projectileMaxEnergy/100)+"");
            polarAngleNE.setText(yourCalcuator.projectileIncidentPolarAngle + "");
            azimuthAngleNE.setText(yourCalcuator.projectileIncidentAzimuthAngle + "");
        }
        catch (Exception e)
        {
            System.out.println("175: "+e.getMessage());
        }
}

    @FXML
    public void AngleChanged()
    {
        try {
            double t = Double.parseDouble(polarAngleNE.getText());
            if (t < 0 || t > 90) {
                polarAngleNE.setText("71");

                new GUI().showNotification("Установите угол  в пределах 0<=t<=90");
            }
        }
        catch (Exception e)
        {
            polarAngleNE.setText("71");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNE.getText());
            if (t < 0 || t > 180) {
                azimuthAngleNE.setText("0");

                new GUI().showNotification("Установите угол  в пределах 0<=t<=180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNE.setText("0");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNtheta.getText());
            if (t < 0 || t > 179 ) {
                azimuthAngleNtheta.setText("0");

                new GUI().showNotification("Установите угол  в пределах 0<=t<180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNtheta.setText("0");
        }
    }
    @FXML
    public void DeltaChanged()
    {
        try {
            double t = Double.parseDouble(dPolarAngleNE.getText());
            if (t <= 0 ) {
                dPolarAngleNE.setText("2");

                new GUI().showNotification("Установите разброс по углу больше нуля!");

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

                new GUI().showNotification("Установите разброс по углу больше нуля!");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNE.setText("2");
        }
        try {
            double t = Double.parseDouble(dAzimuthAngleNtheta.getText());
            if (t <= 0 ) {
                dAzimuthAngleNtheta.setText("2");

                new GUI().showNotification("Установите разброс по углу больше нуля!");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNtheta.setText("5");
        }
    }

    @FXML
    public void EChanged()
    {
        try {
            double E = Double.parseDouble(E0.getText());
            if (E<0) throw new Exception();
            energyResolution.setText((int) (E/100)+"");
            if (E==100000)
            {
                new GUI().Futurama();
                new GUI().showNotification("ничто не вечно под Луной ... кроме SCATTER");
            }
        }
        catch (Exception e)
        {
            E0.setText("25000");
        }

    }

    @FXML
    public void YouDoWhatYouGonnaDo()
    {
        Platform.runLater(() -> new GUI().Futurama());
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
                else E=yourCalcuator.projectileMaxEnergy;

                //int energyReturnValue = E;
                double dE = Double.parseDouble(energyResolution.getText());
                double thetaNE = Double.parseDouble(polarAngleNE.getText());
                double dThetaNE = Double.parseDouble(dPolarAngleNE.getText());
                double phiNE=Double.parseDouble(azimuthAngleNE.getText());
                double dphiNE=Double.parseDouble(dAzimuthAngleNE.getText());

                ///////////

                double dThetaNTheta=Double.parseDouble(dPolarAngleNtheta.getText());
                double phiNTheta=Double.parseDouble(azimuthAngleNtheta.getText());
                double dPhiNTheta=Double.parseDouble(dAzimuthAngleNtheta.getText());
                double NThetadPhi1 = Double.parseDouble(NThetadPhi.getText());
                double NdThetaPhi1 = Double.parseDouble(NdThetaPhi.getText());

                E0.setText("WAIT");

                //basicDistributions

                ArrayList<Dependence> distributions = new ArrayList<>();

                String sort = (NEB.isSelected()) ? "B" : "";
                sort += (NES.isSelected()) ? "S" : "";
                //sort += (NEI.isSelected()) ? "I" : "";
                sort += (NET.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new Energy(dE,phiNE, dphiNE, thetaNE, dThetaNE,sort, yourCalcuator));

                sort = (NThetaB.isSelected()) ? "B" : "";
                sort += (NThetaS.isSelected()) ? "S" : "";
                sort += (NThetaI.isSelected()) ? "I" : "";
                sort += (NThetaT.isSelected()) ? "T" : "";

                if (!sort.equals(""))
                    distributions.add(new Polar(phiNTheta, dPhiNTheta, dThetaNTheta,sort, yourCalcuator));

                sort = (NThetaPhiB.isSelected()) ? "B" : "";
                sort += (NThetaPhiS.isSelected()) ? "S" : "";
                sort += (NThetaPhiI.isSelected()) ? "I" : "";
                sort += (NThetaPhiT.isSelected()) ? "T" : "";


                if (!sort.equals(""))
                    distributions.add(new AngleMap(dPhiNTheta, dThetaNTheta,sort, yourCalcuator));

                if (!sort.equals(""))
                    distributions.add(new AngEnMap(dThetaNTheta,dE, sort, yourCalcuator));

                //others

                //FIXME full funtional is not presented, not made in Scatter, Trim

                sort = (NzyB.isSelected()) ? "B" : "";
                sort += (NzyS.isSelected()) ? "S" : "";
                sort += (NzyI.isSelected()) ? "I" : "";
                sort += (NzyT.isSelected()) ? "T" : "";
                sort += (NzyD.isSelected()) ? "D" : "";

                if (!sort.equals("")) distributions.add(new CartesianMap(1, "ZY", sort, yourCalcuator));

                if (getTXT.isSelected()) distributions.add(new getTXT(yourCalcuator, ""));

                yourCalcuator.postProcessCalculatedFiles(distributions);
                yourCalcuator.printAndVisualizeData(distributions);

                //Calculation is ended

                E0.setText(E+"");
                count.setText(
                        new BigDecimal(yourCalcuator.particleCount).setScale(5, RoundingMode.UP).doubleValue()+"");
                time.setText(yourCalcuator.calcTime +"");

                scattered.setText(new BigDecimal( yourCalcuator.scattered.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                sputtered.setText(new BigDecimal(yourCalcuator.sputtered.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                implanted.setText(new BigDecimal(yourCalcuator.implanted.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                displaced.setText(new BigDecimal(yourCalcuator.displaced.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                transmitted.setText(new BigDecimal(yourCalcuator.transmitted.get("all")).setScale(4, RoundingMode.UP).doubleValue()
                        +"");
                energyScattering.setText(new BigDecimal(yourCalcuator.energyRecoil.get("all")).setScale(3, RoundingMode.UP).doubleValue()
                        +"");

                //FIXME to remove: every launch we create new calculator?!
                for (String element: yourCalcuator.elementsList) {
                    yourCalcuator.scattered.put(element,0.0);
                    yourCalcuator.sputtered.put(element, 0.0);
                    yourCalcuator.implanted.put(element, 0.0);
                    yourCalcuator.transmitted.put(element, 0.0);
                    yourCalcuator.displaced.put(element, 0.0);
                    yourCalcuator.energyRecoil.put(element,0.0);
                }

                yourCalcuator.particleCount = 0;

            }).start();
    }
}
