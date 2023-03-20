package enigma;

import java.util.ArrayList;
import java.util.Arrays;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Neeraj Rattehalli
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replace(")(", ") (");
        _cycles = new ArrayList(Arrays.asList(cycles.split(" ")));
        for (int i = 0; i < _cycles.size(); i++) {
            String cycle = _cycles.get(i);
            if (cycle.length() > 0) {
                if (cycle.charAt(0) != '(') {
                    throw error("parenthesis missing");
                }
                if (cycle.charAt(cycle.length() - 1) != ')') {
                    throw error("parenthesis missing");
                }
                _cycles.set(i, cycle.substring(1, cycle.length() - 1));
            }
        }

        for (String cycle: _cycles) {
            for (int i = 0; i < cycle.length(); i++) {
                if (!_alphabet.contains(cycle.charAt(i))) {
                    throw error("invalid character in cycle");
                }
            }
        }

        checkDup("");
    }

    private void checkDup(String str) {
        String cycles = String.join("", _cycles);
        cycles += str;
        for (int i = 0; i < cycles.length(); i++) {
            for (int j = i + 1; j < cycles.length(); j++) {
                if (cycles.charAt(i) == cycles.charAt(j)) {
                    throw error("duplicate character in cycle");
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (!_alphabet.contains(cycle.charAt(i))) {
                throw error("invalid character in cycle");
            }
        }
        checkDup(cycle);
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size S of this permutation. */
    final int wrap(int p, int s) {
        int r = p % s;
        if (r < 0) {
            r += s;
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of cycling P S times. */
    int shift(int p, int s) {
        char c = _alphabet.toChar(wrap(p, size()));
        for (String cycle : _cycles) {
            for (int pos = 0; pos < cycle.length(); pos++) {
                if (cycle.charAt(pos) == c) {
                    int idx = wrap(pos + s, cycle.length());
                    return _alphabet.toInt(cycle.charAt(idx));
                }
            }
        }
        return wrap(p, size());
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return shift(p, 1);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return shift(c, -1);  }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw error("char not in alphabet");
        }
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw error("char not in alphabet");
        }
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return _cycles length. **/
    int cycleLength() {
        return _cycles.size();
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int count = 0;
        for (String cycle: _cycles) {
            count += cycle.length();
        }
        return count == _alphabet.size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Arraylist of different cycles. */
    private ArrayList<String> _cycles;
}
