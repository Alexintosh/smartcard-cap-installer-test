package im.status.applet_installer_test.appletinstaller.apducommands;

import java.security.SecureRandom;

import im.status.applet_installer_test.appletinstaller.APDUCommand;
import im.status.applet_installer_test.appletinstaller.APDUException;
import im.status.applet_installer_test.appletinstaller.APDUResponse;
import im.status.applet_installer_test.appletinstaller.Crypto;
import im.status.applet_installer_test.appletinstaller.HexUtils;

public class InitializeUpdate {
    public static final int CLA = 0x80;
    public static final int INS = 0x50;
    public static final int P1 = 0;
    public static final int P2 = 0;

    public static byte[] DERIVATION_PURPOSE_ENC = new byte[]{(byte) 0x01, (byte) 0x82};
    public static byte[] DERIVATION_PURPOSE_MAC = new byte[]{(byte) 0x01, (byte) 0x01};
    public static byte[] DERIVATION_PURPOSE_DEK = new byte[]{(byte) 0x01, (byte) 0x81};

    private byte[] hostChallenge;

    public InitializeUpdate(byte[] challenge) {
        this.hostChallenge = challenge;
    }

    public APDUCommand getCommand() {
        return new APDUCommand(CLA, INS, P1, P2, this.hostChallenge, true);
    }

    public static byte[] generateChallenge() {
        SecureRandom random = new SecureRandom();
        byte challenge[] = new byte[8];
        random.nextBytes(challenge);

        return challenge;
    }

    public boolean validateResponse(byte[] encKeyData, APDUResponse resp) throws APDUException {
        if (resp.getSw() == APDUResponse.SW_SECURITY_CONDITION_NOT_SATISFIED) {
            throw new APDUException(resp.getSw(), "security confition not satisfied");
        }

        if (resp.getSw() == APDUResponse.SW_AUTHENTICATION_METHOD_BLOCKED) {
            throw new APDUException(resp.getSw(), "authentication method blocked");
        }

        byte[] data = resp.getData();

        if (data.length != 28) {
            throw new APDUException(resp.getSw(), String.format("bad data length, expected 28, got %d", data.length));
        }

        byte[] cardChallenge = new byte[8];
        System.arraycopy(data, 12, cardChallenge, 0, 8);

        byte[] cardCryptogram = new byte[8];
        System.arraycopy(data, 20, cardCryptogram, 0, 8);

        return Crypto.verifyCryptogram(encKeyData, this.hostChallenge, cardChallenge, cardCryptogram);

        //System.out.printf("key data: %s, %n", HexUtils.byteArrayToHexString(keyData));

    }
}
