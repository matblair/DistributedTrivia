# Distributed Trivia - A Paxos Approach
### Introduction
This project an Android application that demonstrates the implementation of the Leaderless Paxos algorithm. To demonstrate the algorithm this project implements a trivia game. The game is played using multiple Android devices. One device will act as the host to initate the game, others will join. 

Paxos is not used to syncrhonise game start, as a result you will notice that occasionally this fails. The remainder of the components use paxos to syncrhonise and will generally be quite reliable given the wifi network is not overly saturated.

### Build and run the project
Install Android Studio and import the project. Build and run! Requires multiple android devices to demonstrate. If you wish to see this when marking, please email Mat at mat@matblair.com and he will happily bring a few Nexus 5's around. 
