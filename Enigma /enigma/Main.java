package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Neeraj Rattehalli
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        boolean configFound = false;
        while (_input.hasNextLine()) {
            String nextLine = _input.nextLine().trim();
            if (nextLine.startsWith("*")) {
                setUp(machine, nextLine);
                configFound = true;
            } else if (configFound) {
                printMessageLine(machine.convert(nextLine));
            } else {
                throw error("config not found yet");
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.nextLine().trim();
            _alphabet = new Alphabet(alphabet);
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rName = _config.next();
            String rNotches = _config.next();
            String rCycles = "";
            while (_config.hasNext(".*[\\(|\\)]+.*")) {
                rCycles += _config.next();
            }
            Permutation rPerm = new Permutation(rCycles, _alphabet);
            if ((rNotches.substring(0, 1)).equals("M")) {
                return new MovingRotor(rName, rPerm, rNotches.substring(1));
            } else if ((rNotches.substring(0, 1)).equals("N")) {
                return new FixedRotor(rName, rPerm);
            } else {
                return new Reflector(rName, rPerm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] splitSettings = settings.trim().split(" ");
        String[] rotors = new String[M.numRotors()];
        System.arraycopy(splitSettings, 1, rotors, 0, rotors.length);
        String cycle = "";
        if (splitSettings.length - rotors.length - 2 >= 0) {
            int size = splitSettings.length - rotors.length - 2;
            String[] cycles = new String[size];
            int srcPos = M.numRotors() + 2;
            System.arraycopy(splitSettings, srcPos, cycles, 0, cycles.length);
            cycle = String.join(" ", cycles);
        } else {
            throw error("invalid setting");
        }
        String startingPos = splitSettings[M.numRotors() + 1];
        for (int i = 0; i < rotors.length; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw new EnigmaException("Duplicate rotors");
                }
            }
        }
        M.insertRotors(rotors);
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("First rotor not reflecting");
        }
        M.setRotors(startingPos);
        M.setPlugboard(new Permutation(cycle, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (i > 0 && i % 5 == 0) {
                message.append(" ");
            }
            message.append(msg.charAt(i));
        }
        _output.print(message + "\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
