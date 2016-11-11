package com.example.joey.spyfall.Spyfall;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.joey.spyfall.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Joey on 08-Nov-16.
 */

public class Spyfall {

    public static final String LOCATION_FILE_NAME = "locations.txt";

    public static final int MINIMUM_NUMBER_OF_PLAYERS = 3;
    public static final int MAXIMUM_NUMBER_OF_PLAYERS = 9;
    private static final int LOCATION_CODE_LENGTH = 3;
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Gets the user's information from the given code.
     * @param playerNumber The player's number, 1 <= playerNumber <= totalNumberOfPlayers.
     * @return The player's information as a String.
     */
    public static PlayerInformation getPlayerInformation(String code, int playerNumber, Context context) {
        code = filter(code);
        String userCode = code.substring(LOCATION_CODE_LENGTH, code.length());

        // Getting the location is pretty simple
        Location location = getLocationFromCode(code, context);

        // Getting the role is more... complicated
        String role;
        ArrayList<String> uniqueRoles = location.getSingleRoles();
        List<ArrayList<String>> buckets = new ArrayList<>();
        // Populate the buckets with uniqueRoles.size() empty lists.
        for (String s : uniqueRoles)
            buckets.add(new ArrayList<String>());

        // For each character in userCode that corresponds to a player number, add that userCode to it's respective
        // spot in the bucket
        for (int i = 0; i < userCode.length(); i++) {
            // This loop relies on the property of userCode.length() == totalNumberOfPlayers
            int userNumIndex = convertCharacterToInt(userCode.charAt(i)) % uniqueRoles.size();
            buckets.get(userNumIndex).add("" + userCode.charAt(i));
        }

        // If the user's code isn't the first one in the bucket, then it gets a random role
        int userNumIndex = convertCharacterToInt(userCode.charAt(playerNumber - 1)) % uniqueRoles.size();
        if (buckets.get(userNumIndex) != null && buckets.get(userNumIndex).get(0).equals("" + userCode.charAt(playerNumber - 1))
                && characterExactlyOnceInList("" + userCode.charAt(playerNumber - 1), buckets.get(userNumIndex))) {
            // if the bucket has a list, and the first character in that list is also this user's character, they get a unique role
            role = uniqueRoles.get(userNumIndex);
        } else {
            Random rand = new Random();
            role = location.getMultiRoles().get(rand.nextInt(location.getMultiRoles().size()));
        }

        // Determine who the spy is
        int sum = 0;
        for (int i = 0; i < userCode.length(); i++)
            sum += convertCharacterToInt(userCode.charAt(i));

        if ((sum % getTotalNumberOfPlayersFromCode(code)) + 1 == playerNumber)
            return new PlayerInformation(true);
        else
            return new PlayerInformation(location.getLocation(), role);
    }

    /**
     * Generates a new, random code.
     * @return A new, randomly generated code of length @link(LOCATION_CODE_LENGTH) + @link(totalNumberOfPlayers).
     */
    public static String generateCode(int totalNumberOfPlayers) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();

        // Generate the location-specific random characters.
        for (int i = 0; i < LOCATION_CODE_LENGTH; i++)
            sb.append(convertIntToCharacter(rand.nextInt(CODE_CHARACTERS.length())));
        // Generate the rest of the random characters.
        for (int i = 0; i < totalNumberOfPlayers; i++)
            sb.append(convertIntToCharacter(rand.nextInt(CODE_CHARACTERS.length())));

        return sb.toString().toLowerCase();
    }

    /**
     * Retrieves the list of locations from the given text file.
     * The locations should be in the format of:
     * location_name
     * *single_person_role
     * **multi_person_role
     * \n
     *
     * @return A list of locations retrieved from the file name's respective file.
     * @throws FileNotFoundException Thrown if the file cannot be found.
     */
    public static ArrayList<Location> getLocationsFromFile(String filename, Context context) throws IOException {
        Scanner scanner = new Scanner(new InputStreamReader(context.getAssets().openFd(filename).createInputStream()));

        ArrayList<Location> locations = new ArrayList<>();

        Location temp = new Location();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty() || line.equals("EOF")) {
                locations.add(temp);
                temp = new Location();
            } else if (line.startsWith("**")) {
                temp.getMultiRoles().add(line.substring(2));
            } else if (line.startsWith("*")) {
                temp.getSingleRoles().add(line.substring(1));
            } else {
                temp.setLocation(line);
            }
        }

        scanner.close();
        return locations;
    }

    /**
     * Returns the total number of players for the given code.
     * This relies on the properties that, for each player, one character is added to the code.
     * @param code The code to extract the total number of players from.
     * @return The total number of players for a given code.
     */
    public static int getTotalNumberOfPlayersFromCode(String code) {
        return code.length() - LOCATION_CODE_LENGTH;
    }

    /**
     * Returns the player number of the first player.
     * @param code The code to extract the first player from.
     * @return The player who should go first.
     */
    public static int getFirstPlayerFromCode(String code) {
        code = filter(code);
        int totalNumberOfPlayers = getTotalNumberOfPlayersFromCode(code);
        int sum = 0;
        for (char c : code.toCharArray())
            sum += convertCharacterToInt(c);
        return sum % totalNumberOfPlayers + 1;
    }

    /**
     * Determines if the code and playerNumber are valid.
     * @param code The code entered by the user.
     * @param playerNumber The player number entered by the user.
     * @return True if the code / playerNumber are valid, false otherwise.
     */
    public static boolean validInformation(String code, int playerNumber) {
        return (code.length() >= LOCATION_CODE_LENGTH + MINIMUM_NUMBER_OF_PLAYERS
                && code.length() <= LOCATION_CODE_LENGTH + MAXIMUM_NUMBER_OF_PLAYERS
                && playerNumber > 0
                && playerNumber <= MAXIMUM_NUMBER_OF_PLAYERS
                && playerNumber <= code.length() - LOCATION_CODE_LENGTH);
    }

    /**
     * Pulls the location from the code.
     * @return A location based off of the given code.
     */
    private static Location getLocationFromCode(String code, Context context) {
        // Get the correct version of the code.
        code = filter(code);

        // Get the locations
        ArrayList<Location> locations = new ArrayList<>();
        try {
            locations = getLocationsFromFile(LOCATION_FILE_NAME, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Determine what location to get
        int sum = 0;
        for (int i = 0; i < LOCATION_CODE_LENGTH; i++)
            sum += convertCharacterToInt(code.charAt(i));

        return locations.get(sum % locations.size());
    }

    /**
     * The bread and butter behind obscuring how information is extracted from the randomly generated code.
     * @param code The original code (all caps, A-Z0-9).
     * @return The original code, but modified to be obscure.
     */
    private static String filter(String code) {
        char[] temp = code.toCharArray();
        Arrays.sort(temp);
        return (new String(temp)).toUpperCase();
    }

    private static char convertIntToCharacter(int i) throws IllegalArgumentException {
        if (i < 0 || i >= CODE_CHARACTERS.length()) throw new IllegalArgumentException("The input must be in [0, " + CODE_CHARACTERS.length() + ")");
        return CODE_CHARACTERS.charAt(i);
    }

    private static int convertCharacterToInt(char c) throws IllegalArgumentException {
        int i = CODE_CHARACTERS.indexOf(c);
        if (i < 0) throw new IllegalArgumentException(String.format("The input must be contained within \"%s\"", CODE_CHARACTERS));
        return i;
    }

    private static boolean characterExactlyOnceInList(String input, List<String> list) {
        boolean once = false;
        for (String s : list) {
            if (s.equals(input)) {
                if (once) return false;
                else once = true;
            }
        }
        return once;
    }

}
