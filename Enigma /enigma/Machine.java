package enigma;


import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Neeraj Rattehalli
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors == 0) {
            throw error("0 rotors");
        }
        if (numRotors <= pawls) {
            throw error("Too many pawls");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors.toArray();
        _rotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                String name = rotors[i].toString();
                String rotorName = ((Rotor) _allRotors[j]).name();
                if (name.equals(rotorName)) {
                    _rotors[i] = (Rotor) _allRotors[j];
                }
            }
            if (_rotors[i] instanceof Reflector) {
                if (i != 0) {
                    throw error("Reflector at the wrong position");
                }
            } else if (_rotors[i] instanceof FixedRotor)  {
                if  (i == 0 || i >= _numRotors - _numPawls) {
                    throw error("FixedRotor at wrong position");
                }
            } else if (_rotors[i] instanceof MovingRotor) {
                if (i < _numRotors - _numPawls) {
                    throw error("MovingRotor at wrong position");
                }
            }
        }
        if (!(_rotors[0] instanceof Reflector)) {
            throw error("First rotor should be reflector");
        }
        if (rotors.length != _rotors.length) {
            throw error("Incorrect rotor name");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Incorrect setting");
        }
        for (int i = 1; i < _numRotors; i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw error("Invalid starting position");
            }
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] moveable = new boolean[_numRotors];
        boolean lastMoved = false;
        for (int pos = 1; pos < _numRotors; pos++) {
            if (_rotors[pos].atNotch() && _rotors[pos - 1].rotates()) {
                if (pos == _numRotors - 1) {
                    lastMoved = true;
                }
                moveable[pos] = true;
                moveable[pos - 1] = true;
            }
        }
        if (!lastMoved) {
            moveable[_numRotors - 1] = true;
        }
        for (int i = 0; i < _numRotors; i++) {
            if (moveable[i]) {
                _rotors[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int pos = _numRotors - 1; pos >= 0; pos--) {
            c = _rotors[pos].convertForward(c);
        }
        for (int pos = 1; pos <= _numRotors - 1; pos++) {
            c = _rotors[pos].convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String message = "";
        for (char l: msg.toCharArray()) {
            if (_alphabet.contains(l)) {
                char c = _alphabet.toChar(convert(_alphabet.toInt(l)));
                message += Character.toString(c);
            }
        }
        return message;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** number of rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _numPawls;
    /** all the rotors. */
    private Object[] _allRotors;
    /** rotors. */
    private Rotor[] _rotors;
    /** plugboard permutations. */
    private Permutation _plugboard;
}
