package edu.advent.day3;

import edu.advent.utils.InputLoader;
import io.vavr.collection.HashSet;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*      ---- Part One ----
        The gravity assist was successful, and you're well on your way to the Venus refuelling station.
        During the rush back on Earth, the fuel management system wasn't completely installed, so that's next on the priority list.

        Opening the front panel reveals a jumble of wires.
        Specifically, two wires are connected to a central port and extend outward on a grid.
        You trace the path each wire takes as it leaves the central port, one wire per line of text (your puzzle input).

        The wires twist and turn, but the two wires occasionally cross paths.
        To fix the circuit, you need to find the intersection point closest to the central port.
        Because the wires are on a grid, use the Manhattan distance for this measurement.
        While the wires do technically cross right at the central port where they both start,
        this point does not count, nor does a wire count as crossing with itself.

        For example, if the first wire's path is R8,U5,L5,D3, then starting from the central port (o),
        it goes right 8, up 5, left 5, and finally down 3:

        ...........
        ...........
        ...........
        ....+----+.
        ....|....|.
        ....|....|.
        ....|....|.
        .........|.
        .o-------+.
        ...........
        Then, if the second wire's path is U7,R6,D4,L4, it goes up 7, right 6, down 4, and left 4:

        ...........
        .+-----+...
        .|.....|...
        .|..+--X-+.
        .|..|..|.|.
        .|.-X--+.|.
        .|..|....|.
        .|.......|.
        .o-------+.
        ...........
        These wires cross at two locations (marked X), but the lower-left one is closer to the central port: its distance is 3 + 3 = 6.

        Here are a few more examples:

        R75,D30,R83,U83,L12,D49,R71,U7,L72
        U62,R66,U55,R34,D71,R55,D58,R83 = distance 159
        R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
        U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = distance 135
        What is the Manhattan distance from the central port to the closest intersection?

        #ANSWER = 1264


        ---- Part Two ----

        It turns out that this circuit is very timing-sensitive; you actually need to minimize the signal delay.

        To do this, calculate the number of steps each wire takes to reach each intersection;
        choose the intersection where the sum of both wires' steps is lowest.
        If a wire visits a position on the grid multiple times, use the steps value from the first time it visits that
        position when calculating the total value of a specific intersection.

        The number of steps a wire takes is the total number of grid squares the wire has entered to get to that location,
        including the intersection being considered. Again consider the example from above:

        ...........
        .+-----+...
        .|.....|...
        .|..+--X-+.
        .|..|..|.|.
        .|.-X--+.|.
        .|..|....|.
        .|.......|.
        .o-------+.
        ...........
        In the above example, the intersection closest to the central port is reached after 8+5+5+2 = 20
        steps by the first wire and 7+6+4+3 = 20 steps by the second wire for a total of 20+20 = 40 steps.

        However, the top-right intersection is better: the first wire takes only 8+5+2 = 15 and the
        second wire takes only 7+6+2 = 15, a total of 15+15 = 30 steps.

        Here are the best steps for the extra examples from above:

        R75,D30,R83,U83,L12,D49,R71,U7,L72
        U62,R66,U55,R34,D71,R55,D58,R83 = 610 steps
        R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
        U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = 410 steps
        What is the fewest combined steps the wires must take to reach an intersection?

        #ANSWER = 37390
*/
public class Day3 {

    public static void main(String[] args) {
        List<String> input = InputLoader.getInputAsStrings("day3");

        assertThat(calculatePart1(input)).isEqualTo(1264);
        assertThat(calculatePart2(input)).isEqualTo(37390);
    }

    private static int calculatePart1(List<String> input) {
        String[] firstWirePath = input.get(0).split(",");
        String[] secondWirePath = input.get(1).split(",");

        Map<Point, Integer> allOccupiedPointsWire1 = calculateAllPointsOnPath(firstWirePath);
        Map<Point, Integer> allOccupiedPointsWire2 = calculateAllPointsOnPath(secondWirePath);

        Set<Point> intersections = HashSet.ofAll(allOccupiedPointsWire1.keySet())
                                          .intersect(HashSet.ofAll(allOccupiedPointsWire2.keySet()));

        return intersections.map(p -> (int) p.getX() + (int) p.getY())
                            .min().get();
    }

    private static int calculatePart2(List<String> input) {
        String[] firstWirePath = input.get(0).split(",");
        String[] secondWirePath = input.get(1).split(",");

        Map<Point, Integer> allPointsOnPath1 = calculateAllPointsOnPath(firstWirePath);
        Map<Point, Integer> allPointsOnPath2 = calculateAllPointsOnPath(secondWirePath);

        Set<Point> intersections = HashSet.ofAll(allPointsOnPath1.keySet())
                                          .intersect(HashSet.ofAll(allPointsOnPath2.keySet()));

        return intersections.map(point -> allPointsOnPath1.get(point).get() + allPointsOnPath2.get(point).get())
                            .min().get();
    }

    //This returns map to keep track which point took which amount of steps to move to
    private static Map<Point, Integer> calculateAllPointsOnPath(String[] instructions) {
        Map<Point, Integer> result = LinkedHashMap.empty();
        int currentAmount = 0;
        Point currentCords = new Point(0, 0);
        for (String instruction : instructions) {
            Map<Point, Integer> occupiedPoints = pointsOnMove(currentAmount, currentCords, instruction);
            currentAmount = occupiedPoints.last()._2;
            currentCords = occupiedPoints.last()._1;
            result = result.merge(occupiedPoints);
        }
        return result;
    }

    private static Map<Point, Integer> pointsOnMove(int currentAmount, Point currentCords, String instruction) {
        Map<Point, Integer> occupiedPoints = LinkedHashMap.empty();
        char direction = instruction.charAt(0);
        int amount = Integer.parseInt(instruction.substring(1));
        int currentSteps = currentAmount;
        int oldX = (int) currentCords.getX();
        int oldY = (int) currentCords.getY();
        switch (direction) {
            case 'U':
                for (int i = oldY + 1; i <= oldY + amount; ++i) {
                    occupiedPoints = occupiedPoints.put(new Point(oldX, i), ++currentSteps);
                }
                break;
            case 'D':
                for (int i = oldY - 1; i >= oldY - amount; i--) {
                    occupiedPoints = occupiedPoints.put(new Point(oldX, i), ++currentSteps);
                }
                break;
            case 'R':
                for (int i = oldX + 1; i <= oldX + amount; ++i) {
                    occupiedPoints = occupiedPoints.put(new Point(i, oldY), ++currentSteps);
                }
                break;
            case 'L':
                for (int i = oldX - 1; i >= oldX - amount; i--) {
                    occupiedPoints = occupiedPoints.put(new Point(i, oldY), ++currentSteps);
                }
                break;
            default:
                throw new IllegalStateException("Invalid direction?");
        }
        return occupiedPoints;
    }
}
