package ru.mauveferret.Simulators;

import ru.mauveferret.Dependencies.Dependence;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CombSum extends Simulator {

    private final ArrayList<CalculationCombiner> combiners;
    SortedMap<Double, CalculationCombiner> combinersMap;
    private ArrayList<String> elements;

    //type: angle, energy
    public CombSum(ArrayList<CalculationCombiner> combiners, ArrayList<String> elements) {
        super(combiners.get(0).directoryPath, false);
        this.combiners = combiners;
        elements.add("all");
        modelingID = "COMBSUM"+((int) (Math.random()*10000))+"_"+combiners.get(0).dirSubname+"_";
        combinersMap = new TreeMap();
        this.elements = elements;

        for (CalculationCombiner combiner: combiners) combinersMap.put(Double.parseDouble(combiner.getDirSubname()), combiner);

        if (combiners.get(0).dirSubname.equals("angle")) combineAngle();
    }

    public void combineAngle(){
      for (String element: elements){
          try {
              FileOutputStream writer = new FileOutputStream(directoryPath+ File.separator+modelingID+element+".txt");
              SortedSet<String> combWithElements = new TreeSet<>();
              for (CalculationCombiner combiner: combinersMap.values()) {
                  if (combiner.elementsList.contains(element)) combWithElements.add(combiner.modelingID);
              }
              String listOfCombiners = cl("");
              for (String str: combWithElements) listOfCombiners+=cl(str+"");
              writer.write((listOfCombiners+"\n").getBytes());
              String table = cl(combiners.get(0).dirSubname);
              for (Double db: combinersMap.keySet())  table+=cl(db+"");
              writer.write((table+"\n").getBytes());
              table = "";
              double dTheta1 = combiners.get(0).dTheta1;
              double dPhi1 = combiners.get(0).dPhi1;

              for (int i = 0; i <= (int) Math.round(180 / dTheta1); i++){
                  table +="\n"+cl((i * dTheta1 - 90)+"");
                  for (CalculationCombiner combiner: combinersMap.values()) {
                      if (combiner.elementsList.contains(element)) {
                          if (i*dTheta1!= 90) {
                              double zaebalsa = combiner.dependencies.get(1).distributionArray.get(element)[i]
                                      / (Math.toRadians(dPhi1) * Math.sin(Math.toRadians(Math.abs(i * dTheta1 - 90))));

                              table += cl(new BigDecimal(zaebalsa).setScale(4, RoundingMode.UP) + "");
                          }
                      }
                  }
              }
              writer.write(table.getBytes());
              writer.close();

          }
          catch (Exception e){e.printStackTrace();}
      }

    }

    @Override
    public String initializeModelParameters() {
        return null;
    }

    @Override
    public void postProcessCalculatedFiles(ArrayList<Dependence> distributions) {

    }
}
