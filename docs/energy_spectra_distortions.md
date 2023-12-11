## On the spectra broadening due to the hardware function of the electrostatic analysers

As every scientific worker knows, depending on the settings of the measuring equipment, 
you can obtain a significantly different signal from the same object under study.
The same goes for modeling. Here we can imagine outputs of the particle-in-matter simulation 
codes as an object under study, and the ISInCa - as the measurement instrument, which allows
to obtain some values, charts and maps, which characterises is some way the outputs of the simulation.
And like any such instrument, ISInCa has a set of parameters that affect the received data.

In this section discussion is provided on the operation of the module responsible for 
generating energy spectra. We can imagine this virtual energyanalyzer as an array of channels,
each of those is associated with some range of measurable energies. Let's think for the 
simplicity that the channels are equally distributed, in other words, the distance between 
the centers of the energy channels is constant. Let's refer to it as to Estep [eV].
So, if the maximum energy of the ions of interest is E0, the number of channels would be E0/Estep.
Meanwhile, the energy width of the channels do not have to be equal.  Let's refer to it as to dE [eV].
We can distinguish several modes of such analyzer.

## ISInCa virtual energy analyzer with dE=Estep

![dE=Estep](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dE=Estep.png?raw=true)


As can be seen frome the figure above, in this mode our virtual energy analyzer
covers the entire measuring range and the sum of the signal of channels will give the total number
of particles, flying into the analyzer.

This is a basic mode of ISInCa. All you need to specify in GUI
is the `Estep`, while `dE` and/or `dE/E` should be zero (default value).

## ISInCa virtual energy analyzer with dE=const

![dE=const](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dE=const.png?raw=true)

This situation, when the Estep and dE are not equal is actually a more general case of the previous mode.
If `dE<Estep`,  not all particles, flying into the analyzer, would be registered. This submode can be named
as the most preferrable from the point of view of obtaining the true particle energy distribution in the experiment.

If `dE>Estep`,the sum of particles in channels would be even more, than the total number of particles, fying
into the virtual analyzer. 

In this case you need to enable flag dE=const in GUI and enter the value of the `dE` in the nearly located textfield.

## ISInCa virtual energy analyzer with dE/E=const

![dE/E=const](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dEtoE=const.png?raw=true)

This mode represents the situation, when the width of the channels of the energy analyzer increases linearly with the
energy. This case is the most relevant to the experiment, where electrostatic energy analyzers are utilised. Such mode
causes undesirable distortions in the spectrum. Inter alia it may influence the ratio of peak intensities located at 
sufficiently different  energies and reduce the accuracy of LEIS diagnostics. 

To enable this regime you need to specify the value of the `dE/E`. The typical values for good electrostatic analysers
are in the range from 0.001 to 0.01. Lower values will correspond to spectra of similar form in compare to the mode
`dE=const & dE<Estep`, the higher will lead to the broadening of the peaks on the spectra.


## What mode should I choose?

The answer depends on your purposes for providing the simulation. In case of exploring the "nature", embedded in the 
simulation, the  modes  `dE=Estep` or `dE=const & dE<Estep` are acceptable. 
If you want to explore your facility with it real energy analyser, you can try to use modes `dE=const & dE>Estep` or
`dE/E=const`