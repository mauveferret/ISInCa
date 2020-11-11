package ru.mauveferret.Calcuators;

import ru.mauveferret.Dependencies.Dependence;

import java.util.ArrayList;

public class CalculationCombiner extends ParticleInMatterCalculator {

    /* used to summary data from several single calculations with common targets
    in order to create model for multi -energy -angle -mass incident beam
     */



    private final ArrayList<ParticleInMatterCalculator> calculators;

    public CalculationCombiner(String directoryPath, ArrayList<ParticleInMatterCalculator> calculators) {
        super(directoryPath, false);
        this.calculators = calculators;
        elementsList = new ArrayList<>();
        LINE_LENGTH = 150;
    }

    public boolean combine(){
        try{
            combineCalculatorsVariables();
            initializeCalcVariables();
            generateDependencies();
            combineDependencies();
            combineCalcConstants();
            finishCalcVariables();
            printAndVisualizeData(dependencies);
            return  true;
        }
        catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    private void combineCalculatorsVariables (){
        projectileElements = "";

        projectileAmount = 0;
        targetElements = "";
        calcTime = 0;
        modelingID = "COMBO"+((int) (Math.random()*10000));
        for (ParticleInMatterCalculator calculator: calculators){
            if (!calculatorType.contains(calculator.calculatorType)) calculatorType+=calculator.calculatorType+";";
            projectileElements+=calculator.projectileElements+";";
            sProjectileMaxEnergy+=calculator.projectileMaxEnergy+";";
            sProjectileIncidentPolarAngle+=calculator.projectileIncidentPolarAngle+";";
            sProjectileIncidentAzimuthAngle+=calculator.projectileIncidentAzimuthAngle+";";
            projectileAmount+=calculator.projectileAmount;
            if (!targetElements.contains(calculator.targetElements)) targetElements+=calculator.targetElements+";";
            for (String element: calculator.elementsList) {
                if (!elementsList.contains(element)) elementsList.add(element);
            }
            calcTime+=calculator.calcTime;
            System.out.println("        [COMBINER] "+modelingID+" found new calc: "+calculator.modelingID);
        }
        for (ParticleInMatterCalculator calculator: calculators){
            //FIXME maybe you calculate incorrect energy recoil for summ!!!!!
            projectileMaxEnergy=calculator.projectileMaxEnergy*calculator.projectileAmount/projectileAmount;
        }
        //to remove "," from the end
        calculatorType = calculatorType.substring(0, calculatorType.length()-1);
        projectileElements = projectileElements.substring(0, projectileElements.length()-1);
        sProjectileMaxEnergy =  sProjectileMaxEnergy.substring(0, sProjectileMaxEnergy.length()-1);
        sProjectileIncidentPolarAngle = sProjectileIncidentPolarAngle.
                substring(0, sProjectileIncidentPolarAngle.length()-1);
        sProjectileIncidentAzimuthAngle = sProjectileIncidentAzimuthAngle.
                substring(0,sProjectileIncidentAzimuthAngle.length()-1);
        if (targetElements.contains(";")) targetElements = targetElements.substring(0, targetElements.length()-1);
    }

    private void generateDependencies (){
        //random calculator, because they all should have the same deps parameters
        dependencies = calculators.get(0).dependencies;
        for (Dependence dep: dependencies){
            dep.calculator = this;
            //find the largest arrays
            for (ParticleInMatterCalculator calculator: calculators){
                for (Dependence dependence: calculator.dependencies){
                    if (dep.depName.equals(dependence.depName)){
                        switch (dep.depType){
                            case "distribution":
                                if (dependence.distributionSize>dep.distributionSize){
                                    dep.distributionSize = dependence.distributionSize;
                                }
                                break;
                            case "map":
                                if (dependence.mapArrayXsize>dep.mapArrayXsize){
                                    dep.mapArrayXsize =dependence.mapArrayXsize;
                                }
                                if (dependence.mapArrayYsize>dep.mapArrayYsize){
                                    dep.mapArrayYsize =dependence.mapArrayYsize;
                                }
                                break;
                        }
                    }
                }
            }
            dep.initializeArrays(elementsList);
        }
    }

    private void combineDependencies(){
        for (Dependence dependence: dependencies){
            for (ParticleInMatterCalculator calculator: calculators){
                for (Dependence dep: calculator.dependencies){
                    if (dep.depName.equals(dependence.depName)){
                        switch (dependence.depType){
                            case "distribution":
                                for (int i=0; i<dep.distributionSize; i++){
                                    for (String element: calculator.elementsList){
                                        dependence.distributionArray.get(element)[i]=
                                                dependence.distributionArray.get(element)[i]+
                                        dep.distributionArray.get(element)[i];
                                    }
                                }
                                break;
                            case "map":
                                for (int i=0; i<dep.mapArrayXsize; i++)
                                    for (int j=0; j<dep.mapArrayYsize; j++){
                                    for (String element: calculator.elementsList){
                                        dependence.mapArray.get(element)[i][j]=
                                                dependence.mapArray.get(element)[i][j]+
                                                        dep.mapArray.get(element)[i][j];
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    private void combineCalcConstants(){
        for (ParticleInMatterCalculator calculator: calculators){
            for (String element: elementsList){
                try {
                    particleCount+=calculator.particleCount;
                    scattered.put(element, scattered.get(element) + calculator.scattered.get(element) * calculator.projectileAmount);
                    sputtered.put(element, sputtered.get(element) + calculator.sputtered.get(element) * calculator.projectileAmount);
                    implanted.put(element, implanted.get(element) + calculator.implanted.get(element) * calculator.projectileAmount);
                    transmitted.put(element, transmitted.get(element) + calculator.transmitted.get(element) * calculator.projectileAmount);
                    displaced.put(element, displaced.get(element) + calculator.displaced.get(element) * calculator.projectileAmount);
                    energyRecoil.put(element, energyRecoil.get(element)+calculator.energyRecoil.get(element)*
                            calculator.projectileMaxEnergy*calculator.projectileAmount);
                }
                catch (Exception ignored){} //FIXME some scattered of one calc dont have scattered of another

            }
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
