# Distributions in ISInCa

![N|Solid](https://i.ibb.co/0cQrTDm/Croco-Logo.png)

There are several types of distributions in ISInCa:

### **Graphs**

- Energy distributions dN/dE(E) 
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
- Transmitted coefficient (separately for incident TB and target RS atoms)

Each type has features of configuration and output, which will be discussed here. It is firstly important to define the
established names of angles, that can be seen in the  figure below.

![N|Solid](https://github.com/mauveferret/ISInCa/blob/master/src/main/resources/ru/mauveferret/pics/axes.png?raw=true)

![N|Solid](https://github.com/mauveferret/ISInCa/blob/master/src/main/resources/ru/mauveferret/pics/part_types.png?raw=true)


## Energy distributions dN/dE(E)

This is the dependence of the number of particles visible to the detector at a certain solid angle on their energy. 
The main parameters are: 
 - `sort`, that specify the types of particles to take into account. Can be: B, S, R, T or any it's sum like: "BSTR". 
 - `E0 [eV]`,that defines the maximum energy on the spectra, while the minimal is fixed to 0 eV.
 - `Estep [eV]` (`delta` in Console mode), that defines the step on the distribution and has a default value of E0/100. The higher Estep will lead to a better intensity of
the signal on the spectrum, but the capability to distinguish closely located energy peaks would reduce.
 - `β [deg]` (`Beta` in Console mode), that is the polar registration angle relative to the surface normal.
 -  `dβ [deg]` (`dBeta` in Console mode), that defines  the width of the β registration angle
 - `φ [deg]` (`phi` in Console mode), that is the azimuth registration angle relative to the Z axis.
 - `dφ [deg]` (`deltaPhi` in Console mode), that defines  the width of the φ registration angle.
 - `dE/E` (`deltaetoe` and `dE` in Console mode), which is an attempt to take into account, that real energy analyzers introduce distortions into the true energy distribution. 
it is discussed in **[energy spectra distortions](https://github.com/mauveferret/ISInCa/tree/master/docs/energy_spectra_distortions.md)**.

### There are several points to be mentioned here:

1. The formation of this distribution in ISInCa occurs by creating a one-dimensional array with the number of elements 
equal to `E0/Estep`. Then, for each particle from the simulation output tables, the condition for matching the sort and solid angle is checked, 
and in case of matching 1 is added to the array element defined as `Math.round(E/dE)`, where E is particle's energy. 
2. After postprocessing all simulation tables the values of the array, which are integer numbers, which are actually
the number of particles.

### Output

"Energy of particles [eV]  Intensity  [number of particles]

## Polar distributions dN/dβ(β)

The naming logic is similarly the same as for Energy distributions here. The main difference is that `step` or `delta` is now defines step for polar chart in degrees:

- `sort`, that specify the types of particles to take into account. Can be: B, S, R, T or any it's sum like: "BSTR".
- `β step [degrees]` (`delta` in Console mode), that defines the step on the Polar distribution
- `φ [deg]` (`phi` in Console mode), that is the azimuth registration angle relative to the Z axis.
- `dφ [deg]` (`deltaPhi` in Console mode), that defines  the width of the φ registration angle.

### Output

Polar angle of reflection [degrees]  Intensity [particles/(rad^2)] == [particles/(dPhi dBeta sin(Beta))]

**Please note that due to requests of some users `dPhi` and `dBeta` are converted to radians in this output !**

## EneAng Maps N(E, β)

- `sort`, that specify the types of particles to take into account. Can be: B, S, R, T or any it's sum like: "BSTR".
- `E step [eV]` (`deltaE` in Console mode), that defines the step on the Energy axis
- `β step [degrees]` (`deltaA` in Console mode), that defines the step on the Polar angle axis

Note that at the moment it does not take into account azimuthal angle! Create the Issue if you need this feature!

### Output

First row is Polar angle axis
First column is Energy axis
Columns: Polar angle [degrees]
Rows: energy [eV]
Intensity: [1/(degrees*eV)] == [particles/(IncidentAmount dBeta deltaE sin(Beta))]
where `IncidentAmount` - total amount of incident particles. 

**Please note that due to requests of some users the intensity normalization is quite strange here!**

# Angle Maps N(φ, β)

- `sort`, that specify the types of particles to take into account. Can be: B, S, R, T or any it's sum like: "BSTR".
- `φ step [eV]` (`deltaPhi` in Console mode), that defines the step on the Energy axis
- `β step [degrees]` (`deltaPolar` in Console mode), that defines the step on the Polar angle axis

### Output

First row is Polar angle axis
First column is Azimuthal angle axis
Columns: Polar angle [degrees]
Rows: Azimuthal angle [degrees]
Intensity: [1/(rads^2)] == [particles/(dBeta dPhi sin(Beta))]
where `IncidentAmount` - total amount of incident particles.

**Please note that due to requests of some users `dPhi` and `dBeta` are converted to radians in this output !**



