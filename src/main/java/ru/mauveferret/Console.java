package ru.mauveferret;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mauveferret.Simulators.*;
import ru.mauveferret.Dependencies.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

public class Console {


    public static boolean isConsole = false;
    String fullpath;
    File configFile;
    //params
    private double betaNE, phiNE, phiNBeta, deltaNE, energyAnalyserBroadening, deltaPhiNE, deltaBetaNE, deltaPhiNBeta, deltaBetaNBeta, deltaPolarMap, deltaPhi_AngMap, deltaPolar_AngMap, deltaE_eneangMap, deltaA_eneangMap, deltaCartesianMap, MapSize;
    private String  sortNE, sortNBeta, sortPolarMap, sortEneangMap, sortCartesianMap, cartesianMapType;
    private String dirSubname="empty";
    //Preferences
    private boolean getTXT, getSummary, visualize, combine;
    //local
    private final int LINE_LENGTH = 80;

    ArrayList<Thread> calculationThreads;
    ArrayList<Thread> printandvisualize;
    ArrayList<CalculationCombiner> combiners;

    //for cpmbSum
    ArrayList<String> combElements;
    boolean combSum = false;



    public Console(String args[]) {
        try {

            String headerComment = "\n"+"*".repeat(LINE_LENGTH)+"\n"+createLine1("  ");
            headerComment+=createLine1(" ISInCa - Ion Surface Interaction Calculator "+
                    Main.getVersion()+" ")+createLine1("  ");
            headerComment+="*".repeat(LINE_LENGTH)+"\n";
            headerComment+=createLine1(" Created by mauveferret@gmail.com at the MEPhI University ");
            headerComment+="*".repeat(LINE_LENGTH)+"\n";
            headerComment+= createLine1(" Check updates at https://github.com/mauveferret/ISInCa ");
            headerComment+="*".repeat(LINE_LENGTH)+"\n\n";
            System.out.println(headerComment);

            //Basic values for all parameters

            getTXT = false;
            getSummary = true;
            visualize = false;
            combine = false;
            // N_E
            sortNE = "B";
            deltaNE = 100;
            energyAnalyserBroadening = 0;
            betaNE = 70;
            deltaBetaNE = 3;
            phiNE = 0;
            deltaPhiNE = 3;
            //N_Beta
            sortNBeta = "S";
            phiNBeta = 0;
            deltaPhiNBeta = 3;
            deltaBetaNBeta = 3;
            //polar_Map
            sortPolarMap = "S";
            deltaPolarMap = 3;
            deltaPolar_AngMap = 3;
            deltaPhi_AngMap = 3;
            //cartesian_Map
            sortCartesianMap = "S";
            deltaCartesianMap = 10;
            MapSize = 1000;
            cartesianMapType = "ZY";


            //  look for *.xml file and load DOM XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //in order not to see comments on getChildNodes (YES, comments are also Nodes!)
            dbFactory.setIgnoringComments(true);
            Document doc = dbFactory.newDocumentBuilder().parse(getConfigFile(args));
            doc.getDocumentElement().normalize(); //to start from the beginning

            //remove empty nodes, occurred due to spaces
            XPathFactory xpathFactory = XPathFactory.newInstance();
            // XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)
                    xpathExp.evaluate(doc, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }

            NodeList root =  doc.getFirstChild().getChildNodes();
            // load values for prefs like getTXT, getSummary
            getPref(root.item(0).getChildNodes());
            //List of all calcs
            NodeList calcs = root.item(1).getChildNodes();

            System.out.println("[ISInCa] Started postprocessing files...");

            calculationThreads = new ArrayList<>();
            printandvisualize = new ArrayList<>();
            combiners = new ArrayList<>();

            isConsole = true;

            //create threads
            for (int i = 0; i < calcs.getLength(); i++) {
                try {
                    loadCalc(calcs.item(i), calcs.item(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //run threads
            for (Thread thread : calculationThreads) {
                thread.start();
                System.out.println("[ISInCa] Thread " + thread.getName() + " is STARTED");
            }

            int i = 1;
            double start = System.currentTimeMillis();

            //wait for end of calculations
            for (Thread thread : calculationThreads) {
               try {
                   thread.join();
               }
               catch (Exception e){e.printStackTrace();}
                System.out.println("___________________");
                System.out.println("PROGRESS: " + i * 100 / calculationThreads.size() + "%");
                System.out.println("-------------------");
                double newTime = (((double) System.currentTimeMillis()) - start) / ((double) 60000);
                System.out.println("time for " + thread.getName() + " : " +
                        new BigDecimal(newTime).setScale(4, RoundingMode.UP) + " min");
                System.out.println("----------------------------------");
                i++;
            }


            //print ANd vizualise data

            for (Thread thread : printandvisualize) {
                try {
                    thread.start();
                }
                catch (Exception ignored){}
            }
            for (Thread thread : printandvisualize) {
                try {
                    thread.join();
                    System.out.println("[ISInCa] Thread " + thread.getName() + " is logged");
                }
                catch (Exception ignored){ignored.printStackTrace();}
            }



            //do combine section
            if (combine) for (CalculationCombiner combiner: combiners) {
                combiner.setDirSubname(dirSubname);
                if (combiner.combine()) System.out.println("        [COMBINER] "+combiner.modelingID+" succeed!");
            }

            if (combSum){
                new CombSum(combiners, combElements);
            }

            System.out.println();
            System.out.println("[ISInCa] Finished! Good bye, my lord :)");

        }
        catch (Exception e){
            e.printStackTrace();
            //System.out.println("[ISInCa] file not found exception: "+e.getLocalizedMessage());
        }

    }


    private File getConfigFile(String args[]){


        //it returns *jar path, but we need it directory (parent)
         fullpath = ""+Console.class.getProtectionDomain().getCodeSource().getLocation().getPath();
         File file = new File(fullpath);
         fullpath = file.getParentFile().getAbsolutePath();
        // ISInCa -c /case/H_W
        if (args.length>1)
        {
            if (args[0].contains("c"))
            {
                String configPath = args[1];
                configFile = new File(configPath);
                if (configFile.exists()) return configFile;
                else {
                    if (!configPath.startsWith(File.separator))  {
                        configPath= File.separator+configPath;
                    }
                    configPath = fullpath+configPath;
                    System.out.println("[ISInCa] config path: "+configPath);
                     configFile = new File(configPath);
                     if (configFile.exists()) return configFile;
                     else {
                         System.out.println("[ISInCa] ERROR: wrong path");
                         System.exit(-1);
                     }
                }

                //check if the config file exists, otherwise exiting
                if (!configFile.exists()) {
                    System.out.println("[ISInCa] ERROR: File " + configPath + " doesn't exist! Turning off...");
                    System.exit(-1);
                }
            }
            else {
                System.out.println("[ISInCa] ERROR: wrong parameter: "+args[0]);
                System.exit(-1);
            }
        }
        else {
            System.out.println("[ISInCa] ERROR: no paths were found");
            System.exit(-1);
        }
        System.out.println("[ISInCa] ZHOPPA");
        return null;
    }

    private void getPref(NodeList prefs){
        for (int i=0; i<prefs.getLength(); i++){
            switch (prefs.item(i).getNodeName().toLowerCase()){
                case "gettxt":  getTXT = prefs.item(i).getTextContent().equals("true");
                break;
                case "getsummary": getSummary = prefs.item(i).getTextContent().equals("true");
                break;
                case "visualize": visualize = prefs.item(i).getTextContent().equals("true");
                break;
                case "combine": combine =  prefs.item(i).getTextContent().equals("true");
                break;
                case "dirsubname": dirSubname = prefs.item(i).getTextContent();
                break;
                case "combsum":
                    combSum = true;
                    combElements = new ArrayList<>();
                   try {
                       combElements.addAll(Arrays.asList(prefs.item(i).getTextContent().split(",")));
                   }
                   catch (Exception e){e.printStackTrace();}
                break;
            }
        }
    }

    private void loadCalc(Node calc, Node zeroCalc) {

        NodeList dirs = ((Element) calc).getElementsByTagName("dir");
        String combinerDir = "";
        boolean isCombinerModeEnabled = dirs.getLength()>1;
        ArrayList<Simulator> calculatorsForCombiner = new ArrayList<>();

        for (int someDir=0; someDir<dirs.getLength(); someDir++){
            try {

                String calcType = "not found";
                String dir = dirs.item(someDir).getTextContent();
                if (!dir.startsWith(File.separator)) dir = File.separator + dir;
                File file = new File(dir);
                if (!file.exists() || !file.isDirectory())  dir = fullpath + dir;
                file = new File(dir);
                if (!file.exists() || !file.isDirectory()) {
                    System.out.println("[ISInCa] wrong path: " + dir);
                    throw new Exception();
                }
                combinerDir = file.getParent();

                Simulator yourCalculator = new Scatter(dir, visualize);
                yourCalculator.setDirSubname(dirSubname);
                String initialize = yourCalculator.initializeModelParameters();
                if (!initialize.equals("OK")) {
                    yourCalculator = new TRIM(dir, visualize);
                    yourCalculator.setDirSubname(dirSubname);
                    initialize = yourCalculator.initializeModelParameters();
                    if (!initialize.equals("OK")) {
                        yourCalculator = new SDTrimSP(dir, visualize, getSummary);
                        yourCalculator.setDirSubname(dirSubname);
                        initialize = yourCalculator.initializeModelParameters();
                        if (!initialize.equals("OK")) {
                            {
                                System.out.println("ERROR: for id" + calc.getAttributes().item(0).getTextContent() + " get wrong path: " + dir);
                                throw new Exception();
                            }
                        } else calcType = "SDTrimSP";
                    } else calcType = "TRIM";
                } else calcType = "SCATTER";

                System.out.println("**********************************************************");
                System.out.println(dir);
                System.out.println("Thread: " + calc.getChildNodes().item(0).getTextContent() + " calc type " + calcType);
                System.out.println("***********************************************************");

                ArrayList<Dependence> distributions = new ArrayList<>();

                NodeList distrs = (calc.getChildNodes().getLength() > 1) ? calc.getChildNodes() : zeroCalc.getChildNodes();

                for (int i = 0; i < distrs.getLength(); i++) {

                    NodeList distrPars = distrs.item(i).getChildNodes();

                    switch (distrs.item(i).getNodeName()) {

                        case "N_E": {
                            deltaNE = yourCalculator.projectileMaxEnergy / 100;
                            betaNE = yourCalculator.projectileIncidentPolarAngle;
                            boolean isDeConstChosen = false;

                            for (int j = 0; j < distrPars.getLength(); j++) {
                                switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                    case "sort":
                                        sortNE = distrPars.item(j).getTextContent();
                                        break;
                                    case "beta":
                                        betaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "phi":
                                        phiNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "delta":
                                        deltaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltaetoe":
                                        energyAnalyserBroadening = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "de":
                                    {
                                        energyAnalyserBroadening  = Double.parseDouble(distrPars.item(j).getTextContent());
                                        isDeConstChosen = true;
                                    }
                                    break;
                                    case "deltabeta":
                                        deltaBetaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltaphi":
                                        deltaPhiNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                }
                            }
                            distributions.add(new Energy(deltaNE, phiNE, deltaPhiNE, betaNE, deltaBetaNE, sortNE, yourCalculator, energyAnalyserBroadening, isDeConstChosen));
                        }
                        break;
                        case "N_Beta": {
                            for (int j = 0; j < distrPars.getLength(); j++) {
                                switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                    case "sort":
                                        sortNBeta = distrPars.item(j).getTextContent();
                                        break;
                                    case "delta":
                                        deltaBetaNBeta = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "phi":
                                        phiNBeta = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltaphi":
                                        deltaPhiNBeta = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                }
                            }
                            distributions.add(new Polar(phiNBeta, deltaPhiNBeta, deltaBetaNBeta, sortNBeta, yourCalculator));
                        }
                        break;
                        case "polar_Map": {
                            boolean deltaIsGiven = false;
                            for (int j = 0; j < distrPars.getLength(); j++) {
                                switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                    case "sort":
                                        sortPolarMap = distrPars.item(j).getTextContent();
                                        break;
                                    case "delta":
                                        deltaIsGiven = true;
                                        deltaPolarMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltaphi":
                                        deltaPhi_AngMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltapolar":
                                        deltaPolar_AngMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                }
                            }
                            if (deltaIsGiven) {
                                distributions.add(new AngleMap(deltaPolarMap, deltaPolarMap, sortPolarMap, yourCalculator));
                            }
                            else {
                                distributions.add(new AngleMap(deltaPhi_AngMap, deltaPolar_AngMap, sortPolarMap, yourCalculator));
                            }
                        }
                        break;
                        case "eneang_Map": {
                            deltaE_eneangMap = yourCalculator.projectileMaxEnergy / 200;
                            for (int j = 0; j < distrPars.getLength(); j++) {
                                switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                    case "sort":
                                        sortEneangMap = distrPars.item(j).getTextContent();
                                        break;
                                    case "deltae":
                                        deltaE_eneangMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "deltaa":
                                        deltaA_eneangMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                }
                            }
                            distributions.add(new AngEnMap(deltaA_eneangMap, deltaE_eneangMap, sortEneangMap, yourCalculator));
                        }
                        break;
                        case "cartesianmap": {
                            for (int j = 0; j < distrPars.getLength(); j++) {
                                switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                    case "sort":
                                        sortCartesianMap = distrPars.item(j).getTextContent();
                                        break;
                                    case "mapsize":
                                        MapSize = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "delta":
                                        deltaCartesianMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                        break;
                                    case "cartesianmaptype":
                                        cartesianMapType = distrPars.item(j).getTextContent();
                                }
                            }
                            distributions.add(new CartesianMap(deltaCartesianMap, cartesianMapType, sortCartesianMap, yourCalculator));
                        }
                        break;
                    }

                    if (getTXT) distributions.add(new getTXT(yourCalculator, ""));
                }

                final Simulator calculator = yourCalculator;
                Thread newCalculation = new Thread(() -> {
                    calculator.postProcessCalculatedFiles(distributions);
                });

                Thread printAndVizualiseThread = new Thread(() -> {
                    calculator.printAndVisualizeData(distributions);
                });

                newCalculation.setName(dirs.item(someDir).getTextContent());
                printAndVizualiseThread.setName(dirs.item(someDir).getTextContent());
                calculationThreads.add(newCalculation);
                printandvisualize.add(printAndVizualiseThread);
                calculatorsForCombiner.add(calculator);
            }
            catch (Exception ignored){ignored.printStackTrace();}
        }

        if (isCombinerModeEnabled && combine) combiners.add(new CalculationCombiner(combinerDir, calculatorsForCombiner));

        //return newCalculation;
    }

    private String createLine1(String line){
        int spaces = (LINE_LENGTH -line.length())/2;
        return "*"+" ".repeat(spaces-1)+line+" ".repeat(spaces-1)+((((LINE_LENGTH -line.length())%2)==0) ? "" : " ")+"*"+"\n";
    }

}
