
package org.ja;

import java.util.ArrayList;

/**
 *
 * @author jared
 */
public class YamlOperatorTester {
    public static void main(String[] args) {
        String sampleYaml = "la:\nkeys:\n    weee: yay!\n    Woo!: yipee!\nother:\n";
        ArrayList<Key> keys;
        keys = YamlOperator.readKeys(sampleYaml);
        sampleYaml = YamlOperator.removeKeys(sampleYaml);
        sampleYaml += YamlOperator.makeKeys(keys);
        System.out.println(sampleYaml);
    }
}
