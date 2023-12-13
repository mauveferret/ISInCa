package ru.mauveferret.Dependencies;

public class PolarAngles {

    //from 0 to 90 from normal to sufrace (-x Axis)
    private double polar;
    //from 0 to 360 from Z axis (in which the beam strikes)
    private double azimuth;

    public PolarAngles(double polarCos, double azimuthCos, double x,double y, double z) {

        //for SDTrimSP input
        azimuth = 57.2958*Math.acos(azimuthCos);
        polar = 57.2958*Math.acos(polarCos);

        //It is supposed in TRIM and SDTrimSP, that phi=0 is the XY plane
        if (z<0) azimuth =360-azimuth;
    }

    public PolarAngles(double cosx, double cosy, double cosz) {

        //for Scatter Input
        cartesianToAngles(cosx,cosy,cosz);
    }

    public double getPolar() {
        return polar;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public boolean doesAzimuthAngleMatch(double phi, double dPhi){
        if ((phi>dPhi && phi<360-dPhi) || (dPhi>45))
            return (Math.abs(phi - azimuth)<dPhi);
        else return (azimuth < dPhi || azimuth>360-dPhi);
    }

    public boolean doesPolarAngleMatch(double beta, double dBeta){
        return (Math.abs(beta-polar)<dBeta);
    }

    private void cartesianToAngles(double cosx,double cosy,double cosz){

        //Works for Scatter coordinate system (z=z normal surface, x = x, y=-y)

        if (cosx>0 & cosy>0) azimuth = 360 - Math.atan(cosy / cosx)*57.2958;
        if (cosx>0 & cosy<0) azimuth = Math.atan(Math.abs(cosy / cosx))*57.2958;
        if (cosx<0 & cosy<0) azimuth =180 - Math.atan(cosy / cosx)*57.2958;
        if (cosx<0 & cosy>0) azimuth = 180 + Math.atan(Math.abs(cosy / cosx))*57.2958;

       polar = Math.atan(Math.sqrt(cosx * cosx + cosy * cosy)/Math.abs(cosz))*57.2958;
    }
}
