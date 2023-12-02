# Combine mode
Imagine a situation: you want to estimate the sputtering of steel by some multicomponent beam/plasma. 
In this way, you might have different energy/angle distributions for different masses. 
Or imagine, that you use code, which doesn't support beam aenergy/angle sitributions at all. 
In such cases you have no choice but to do several calculations with various parameters. 
But you need to combine them in "one": several sputter distributions for, e.g., 
one-component beam/plasma transform to sputter distribution for multi-component. So, **Combine mode**  
transforms corresponding dependencies for calculations in every `<dir>` into one dependency: 
all energy distributions - in one energy distributions, all maps - in one map. 
Output directory for "multi-component" dependencies will be so in which the first `<dir>` lies.
