package edu.advent.day6;

import edu.advent.utils.InputLoader;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/*      ---- Part One ----

        You've landed at the Universal Orbit Map facility on Mercury.
        Because navigation in space often involves transferring between orbits,
        the orbit maps here are useful for finding efficient routes between, for example, you and Santa.
        You download a map of the local orbits (your puzzle input).

        Except for the universal Center of Mass (COM), every object in space is in orbit around exactly one other object.
        An orbit looks roughly like this:

                          \
                           \
                            |
                            |
        AAA--> o            o <--BBB
                            |
                            |
                           /
                          /

        In this diagram, the object BBB is in orbit around AAA.
        The path that BBB takes around AAA (drawn with lines) is only partly shown.
        In the map data, this orbital relationship is written AAA)BBB, which means "BBB is in orbit around AAA".

        Before you use your map data to plot a course, you need to make sure it wasn't corrupted during the download.
        To verify maps, the Universal Orbit Map facility uses orbit count checksums -
        the total number of direct orbits (like the one shown above) and indirect orbits.

        Whenever A orbits B and B orbits C, then A indirectly orbits C.
        This chain can be any number of objects long: if A orbits B, B orbits C, and C orbits D, then A indirectly orbits D.

        For example, suppose you have the following map:

        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L

        Visually, the above map of orbits looks like this:

                G - H       J - K - L
               /           /
        COM - B - C - D - E - F
                       \
                        I
        In this visual representation, when two objects are connected by a line,
        the one on the right directly orbits the one on the left.

        Here, we can count the total number of orbits as follows:

        D directly orbits C and indirectly orbits B and COM, a total of 3 orbits.
        L directly orbits K and indirectly orbits J, E, D, C, B, and COM, a total of 7 orbits.
        COM orbits nothing.
        The total number of direct and indirect orbits in this example is 42.

        What is the total number of direct and indirect orbits in your map data?

        #ANSWER = 142497

        ---- Part Two ----

        Now, you just need to figure out how many orbital transfers you (YOU) need to take to get to Santa (SAN).

        You start at the object YOU are orbiting; your destination is the object SAN is orbiting.
        An orbital transfer lets you move from any object to an object orbiting or orbited by that object.

        For example, suppose you have the following map:

        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L
        K)YOU
        I)SAN
        Visually, the above map of orbits looks like this:

                                  YOU
                                 /
                G - H       J - K - L
               /           /
        COM - B - C - D - E - F
                       \
                        I - SAN
        In this example, YOU are in orbit around K, and SAN is in orbit around I. To move from K to I, a minimum of 4 orbital transfers are required:

        K to J
        J to E
        E to D
        D to I
        Afterward, the map of orbits looks like this:

                G - H       J - K - L
               /           /
        COM - B - C - D - E - F
                       \
                        I - SAN
                         \
                          YOU
        What is the minimum number of orbital transfers required to move from the object YOU are orbiting to the object SAN is orbiting?
        (Between the objects they are orbiting - not between YOU and SAN.)

        #ANSWER = 301
*/
public class Day6 {

    public static void main(String[] args) {
        List<String> input = InputLoader.getInputAsStrings("day6");

        assertThat(calculatePart1(input)).isEqualTo(142497);

        assertThat(calculatePart2(input)).isEqualTo(301);
    }

    private static long calculatePart1(List<String> input) {
        long orbitsCounter = 0;
        for (Planet planet : connectPlanets(input)) {
            Planet onOrbit = planet.getOnOrbit();
            while (onOrbit != null) {
                orbitsCounter++;
                onOrbit = onOrbit.getOnOrbit();
            }
        }
        return orbitsCounter;
    }

    private static long calculatePart2(List<String> input) {
        List<Planet> planets = connectPlanets(input);
        Planet you = findByName("YOU", planets).get();
        Planet san = findByName("SAN", planets).get();
        Map<Planet, Integer> allOrbitsAndMovesYou = calculateOrbitsAndMoves(you);
        Map<Planet, Integer> allOrbitsAndMovesSan = calculateOrbitsAndMoves(san);
        Set<Planet> commonOrbits = HashSet.ofAll(allOrbitsAndMovesYou.keySet())
                                          .intersect(HashSet.ofAll(allOrbitsAndMovesSan.keySet()));
        return commonOrbits.map(p -> allOrbitsAndMovesYou.get(p) + allOrbitsAndMovesSan.get(p))
                           .min().get();
    }

    private static Map<Planet, Integer> calculateOrbitsAndMoves(Planet starting) {
        Map<Planet, Integer> orbitsAndMoves = new HashMap<>();
        Planet onOrbit = starting.getOnOrbit().getOnOrbit();
        int moveCounter = 0;
        while (onOrbit != null) {
            moveCounter++;
            orbitsAndMoves.put(onOrbit, moveCounter);
            onOrbit = onOrbit.getOnOrbit();
        }
        return orbitsAndMoves;
    }

    private static List<Planet> connectPlanets(List<String> orbitsMap) {
        List<Planet> planets = new ArrayList<>();
        for (String orbit : orbitsMap) {
            String onOrbitName = orbit.split("\\)")[0];
            String planetName = orbit.split("\\)")[1];
            Optional<Planet> planet = findByName(planetName, planets);
            Optional<Planet> orbitPlanet = findByName(onOrbitName, planets);
            if (planet.isPresent()) {
                if (orbitPlanet.isPresent()) {
                    planet.get().setOnOrbit(orbitPlanet.get());
                } else {
                    Planet newOrbitPlanet = new Planet();
                    planets.add(newOrbitPlanet);
                    newOrbitPlanet.setName(onOrbitName);
                    planet.get().setOnOrbit(newOrbitPlanet);
                }
            } else {
                Planet newPlanet = new Planet();
                newPlanet.setName(planetName);
                planets.add(newPlanet);
                if (orbitPlanet.isPresent()) {
                    newPlanet.setOnOrbit(orbitPlanet.get());
                } else {
                    Planet newOrbitPlanet = new Planet();
                    planets.add(newOrbitPlanet);
                    newOrbitPlanet.setName(onOrbitName);
                    newPlanet.setOnOrbit(newOrbitPlanet);
                }
            }
        }
        return planets;
    }

    private static Optional<Planet> findByName(String name, List<Planet> collection) {
        return collection.stream()
                         .filter(n -> n.getName().equals(name))
                         .findFirst();
    }

    private static class Planet {
        private String name;
        private Planet onOrbit;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Planet getOnOrbit() {
            return onOrbit;
        }

        public void setOnOrbit(Planet onOrbit) {
            this.onOrbit = onOrbit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Planet planet = (Planet) o;
            return Objects.equals(name, planet.name) &&
                    Objects.equals(onOrbit, planet.onOrbit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, onOrbit);
        }
    }
}
