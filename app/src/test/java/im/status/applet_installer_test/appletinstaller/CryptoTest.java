package im.status.applet_installer_test.appletinstaller;

import org.junit.Test;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;
import im.status.applet_installer_test.appletinstaller.apducommands.InitializeUpdate;

public class CryptoTest {
    @Test
    public void deriveKey() {
        byte[] cardKey = HexUtils.hexStringToByteArray("404142434445464748494a4b4c4d4e4f");
        byte[] seq = HexUtils.hexStringToByteArray("0065");

        byte[] encKey = Crypto.deriveKey(cardKey, seq, InitializeUpdate.DERIVATION_PURPOSE_ENC);
        String expectedEncKey = "85E72AAF47874218A202BF5EF891DD21";
        assertEquals(expectedEncKey, HexUtils.byteArrayToHexString(encKey));

        byte[] macKey = Crypto.deriveKey(cardKey, seq, InitializeUpdate.DERIVATION_PURPOSE_MAC);
        String expectedMacKey = "309CF99E164F3A97F3E5017FF540A79F";
        assertEquals(expectedMacKey, HexUtils.byteArrayToHexString(macKey));

        byte[] dekKey = Crypto.deriveKey(cardKey, seq, InitializeUpdate.DERIVATION_PURPOSE_DEK);
        String expectedDekKey = "93D08F8025242C4D775D69B9F16C939B";
        assertEquals(expectedDekKey, HexUtils.byteArrayToHexString(dekKey));
    }

    @Test
    public void appendDESPadding() {
        byte[] data = HexUtils.hexStringToByteArray("AABB");
        byte[] result = Crypto.appendDESPadding(data);
        String expected = "AABB800000000000";
        assertEquals(expected, HexUtils.byteArrayToHexString(result));
    }

    @Test
    public void verifyCryptogram() {
        byte[] encKey = HexUtils.hexStringToByteArray("16B5867FF50BE7239C2BF1245B83A362");
        byte[] hostChallenge = HexUtils.hexStringToByteArray("32da078d7aac1cff");
        byte[] cardChallenge = HexUtils.hexStringToByteArray("007284f64a7d6465");
        byte[] cardCryptogram = HexUtils.hexStringToByteArray("05c4bb8a86014e22");
        assertTrue(Crypto.verifyCryptogram(encKey, hostChallenge, cardChallenge, cardCryptogram));
    }

    @Test
    public void macFull3des() {
        byte[] key = HexUtils.hexStringToByteArray("5b02e75ad63190aece0622936f11abab");
        byte[] data = HexUtils.hexStringToByteArray("8482010010810b098a8fbb88da800000");
        String expected = "5271D7174A5A166A";

        byte[] result = Crypto.macFull3des(key, data, Crypto.NullBytes8);
        assertEquals(expected, HexUtils.byteArrayToHexString(result));
    }

    @Test
    public void generatePairingKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pairing = "zoynhcfz1xJjYqxO";
        byte[] key = Crypto.generatePairingKey(pairing.toCharArray());
        String expected = "BF8D606E2FE9292B633DF5E31563AF88928EEBB71FDBCBEF6CADECAA00D7874F";
        assertEquals(expected, HexUtils.byteArrayToHexString(key));
    }
}