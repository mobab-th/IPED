package iped.engine.task.regex.validator.crypto;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import iped.engine.task.regex.BasicAbstractRegexValidatorService;

public class RippleAddressValidatorService extends BasicAbstractRegexValidatorService {
    private static final Base58CheckedValidator base58validator = new Base58CheckedValidator(
            "rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz".toCharArray());

    @Override
    public void init(File confDir) {
        // Nothing to do.
    }

    @Override
    public List<String> getRegexNames() {
        return Arrays.asList("CRYPTOCOIN_RIPPLE");
    }

    @Override
    protected boolean validate(String hit) {
        return validateRippleAddress(hit);
    }

    public boolean validateRippleAddress(String addr) {
        try {
            base58validator.decodeChecked(addr);
            return true;
        } catch (Exception x) {
        }
        return false;
    }

}
