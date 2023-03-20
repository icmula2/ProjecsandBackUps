package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Neeraj Rattehalli
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkOneCycleTransform() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        checkPerm("one cycle", UPPER_STRING, "BCDEFGHIJKLMNOPQRSTUVWXYZA");
    }

    @Test
    public void checkMultipleCycleTransform() {
        String cycles = "(ABCDEFG) (HIJKLM) (NOPQRSTUVWXYZ)";
        perm = new Permutation(cycles, UPPER);
        String toAlpha = "BCDEFGAIJKLMHOPQRSTUVWXYZN";
        checkPerm("multiple cycles", UPPER_STRING, toAlpha);
    }

    @Test
    public void checkCycleWithFixedTransform() {
        String cycles = "(ABCDEFG) (JKLM) (NOPQRSTUVWXYZ)";
        perm = new Permutation(cycles, UPPER);
        String toAlpha = "BCDEFGAHIKLMJOPQRSTUVWXYZN";
        checkPerm("multiple cycles", UPPER_STRING, toAlpha);
    }

    @Test(expected = EnigmaException.class)
    public void checkInvalidPermute() {
        perm = new Permutation("", UPPER);
        perm.permute('{');
    }

    @Test(expected = EnigmaException.class)
    public void checkInvalidInvert() {
        perm = new Permutation("", UPPER);
        perm.invert('{');
    }

    @Test(expected = EnigmaException.class)
    public void checkMissingParenthesis() {
        perm = new Permutation("(ABD) CFE)", UPPER);
    }

    @Test
    public void checkNoSpaces() {
        perm = new Permutation("(ABD)(CFE)", UPPER);
        assertEquals(perm.cycleLength(), 2);
    }

    @Test(expected = EnigmaException.class)
    public void checkInvalidChar() {
        perm = new Permutation("(ABD) ({FE)", UPPER);
    }

    @Test(expected = EnigmaException.class)
    public void checkDup() {
        perm = new Permutation("(ABD) (DE)", UPPER);
    }
}
