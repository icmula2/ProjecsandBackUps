package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Neeraj Rattehalli
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        String s = Character.toString(alphabet().toChar(this.setting()));
        return _notches.contains(s);
    }

    @Override
    void advance() {
        this.set(wrap(this.setting() + 1, size()));
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    String notches() {
        return _notches;
    }

    /** notch string. */
    private String _notches;
}
