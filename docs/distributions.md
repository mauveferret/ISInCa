# Distributions in ISInCa

![N|Solid](https://i.ibb.co/0cQrTDm/Croco-Logo.png)

There are several types of distributions in ISInCa:

### **Graphs**

- Energy distributions N(E) 
- Angle distributions dN/dβ(β) 
- ~~Depth distributions for primary implanted and target displaced particles~~ <span style="color:#ff0000">under development</span>

### **Maps:**
- Polar Map N(β, φ) for backscattered and transmitted primary particles and sputtered particles of target
- EneAng Maps N(E, β) for backscattered and transmitted primary particles and sputtered particles of target
- Cartesian Maps N(Z, Y), N(Z, X)

### **Summary**
- Scattered coefficient 
- Sputtered coefficient 
- Implanted coefficient 
- Transmitted coefficient 

Each type has features of configuration and output, which will be discussed here. It is firstly important to define the
established names of angles, that can be seen in the  figure below.

![N|Solid](https://github.com/mauveferret/ISInCa/blob/master/src/main/resources/ru/mauveferret/pics/axes.png?raw=true)


## Energy distributions dN/dE(E)

This is the dependence of the number of particles visible to the detector at a certain solid angle on their energy. 
The main parameters are: 

 - `E0 [eV]`,that defines the maximum energy on the spectra, while the minimal is fixed to 0 eV.
 - `dE [eV]`, that defines the step on the distribution and has a default value of E0/100. The higher dE will lead to a better intensity of
the signal on the spectrum, but the capability to distinguish closely located energy peaks would reduce.
 - `β [deg]`, that is the polar registration angle relative to the surface normal.
 - `dβ [deg]`, that defines  the energy analyzer registration angle in  scattering plane.
 -  `β [deg]`, that is the polar registration angle relative to the surface normal.
 -  `dβ [deg]`, that defines  the width of the β registration angle
 - `φ [deg]`, that is the azimuth registration angle relative to the Z axis.
 - `dφ [deg]`, that defines  the width of the φ registration angle.
 - `deltaE/E`, which is an attempt to take into account, that real energy analyzers introduce distortions into the true energy distribution. 
it is discussed in **[spectra_distortions](https://github.com/mauveferret/ISInCa/tree/master/docs/spectra_distortions.md)**.

### There are several points to be mentioned here:

1. The formation of this distribution in ISInCa occurs by creating a one-dimensional array with the number of elements 
equal to `E0/dE`. Then, for each particle from the simulation output tables, the condition for matching the sort and solid angle is checked, 
and in case of matching 1 is added to the array element defined as `Math.round( E/dE)`, where E is particle's energy. 
2. After postprocessing all simulation tables the values of the array, which are integer numbers, are divided by dE.
So actually the intensities in the energy distribution given by ISInCa are not the number of particles, but N(E)/dE.

### Output



## Polar distributions dN/dβ(β)

...

## EneAng Maps N(E, β)

...

