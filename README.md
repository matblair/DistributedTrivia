# distributedtrivia

### Introduction
This project an Android application that demonstrates the implementation of the Fast Paxos algorithm.
To demonstrate the algorithm this project implements a trivia game. The game is played using multiple
Android devices. One device will act as the host to the others.  Once all devices have connected to
the host, the host can start the game, selecting to either turn on Paxos or not.  Turning Paxos on
will allow the game to run as intended where each contestant will be judged on who took the shortest
time to hit the buzzer once the question was displayed on their device.  With Paxos turned off, Paxos
will not be used to determine this, and an additional buzzer button will be added to the contestant's
screen which will deliberately delay sending their buzzer message to the host.  That way, if the
contestant is the quickest in hitting their buzzer relative to their device, they will not necessarily
be allowed to answer the question

### Build and run the project
  Install Android Studio and import the project. Build and run!
