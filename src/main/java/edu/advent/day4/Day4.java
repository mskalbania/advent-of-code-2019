package edu.advent.day4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

/*      ---- Part One ----

        You arrive at the Venus fuel depot only to discover it's protected by a password.
        The Elves had written the password on a sticky note, but someone threw it out.

        However, they do remember a few key facts about the password:

        It is a six-digit number.
        The value is within the range given in your puzzle input.
        Two adjacent digits are the same (like 22 in 122345).
        Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123 or 135679).
        Other than the range rule, the following are true:

        111111 meets these criteria (double 11, never decreases).
        223450 does not meet these criteria (decreasing pair of digits 50).
        123789 does not meet these criteria (no double).
        How many different passwords within the range given in your puzzle input meet these criteria?

        #ANSWER = 1019


        ---- Part Two ----

        An Elf just remembered one more important detail: the two adjacent matching digits are not part
        of a larger group of matching digits.

        Given this additional criterion, but still ignoring the range rule, the following are now true:

        112233 meets these criteria because the digits never decrease and all repeated digits are exactly two digits long.
        123444 no longer meets the criteria (the repeated 44 is part of a larger group of 444).
        111122 meets the criteria (even though 1 is repeated more than twice, it still contains a double 22).
        How many different passwords within the range given in your puzzle input meet all of the criteria?

        #ANSWER = 37390
*/
public class Day4 {

    public static void main(String[] args) {
        assertThat(calculatePart1(248345, 746315)).isEqualTo(1019);

        assertThat(calculatePart2(248345, 746315)).isEqualTo(660);
    }

    private static int calculatePart1(int leftInclusive, int rightInclusive) {
        return calculate(leftInclusive, rightInclusive, Day4::isValidPasswordSolutionOne);
    }

    private static int calculatePart2(int leftInclusive, int rightInclusive) {
        return calculate(leftInclusive, rightInclusive, Day4::isValidPasswordSolutionTwo);
    }

    private static int calculate(int leftInc, int rightInc, Predicate<String> passwordCheck) {
        int possiblePasswords = 0;
        for (int password = leftInc; password <= rightInc; password++) {
            if (passwordCheck.test(valueOf(password))) {
                possiblePasswords++;
            }
        }
        return possiblePasswords;
    }

    private static boolean isValidPasswordSolutionOne(String pass) {
        int[] nums = Arrays.stream(pass.split("")).mapToInt(Integer::parseInt).toArray();
        boolean foundAdjacentTwo = false;
        int currentMax = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i - 1] == (nums[i])) {
                foundAdjacentTwo = true;
            }
            if (nums[i] < currentMax) {
                return false;
            } else {
                currentMax = nums[i];
            }
        }
        return foundAdjacentTwo;
    }

    private static boolean isValidPasswordSolutionTwo(String pass) {
        int[] password = Arrays.stream(pass.split("")).mapToInt(Integer::parseInt).toArray();
        Map<Integer, Integer> occurrences = new HashMap<>();
        int currentMax = password[0];
        for (int i = 1; i < password.length; i++) {
            if (password[i] == password[i - 1]) {
                occurrences.compute(password[i], (k, v) -> (v == null) ? 2 : v + 1);
            }
            if (password[i] < currentMax) {
                return false;
            } else {
                currentMax = password[i];
            }
        }
        return occurrences.containsValue(2);
    }
}
