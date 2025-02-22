package health.ere.ps.service.connector.certificate;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.LogManager;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.CryptoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import health.ere.ps.exception.connector.ConnectorCardCertificateReadException;
import health.ere.ps.exception.connector.ConnectorCardsException;
import health.ere.ps.profile.TitusTestProfile;
import health.ere.ps.service.connector.cards.ConnectorCardsService;
import health.ere.ps.service.idp.crypto.CryptoLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(TitusTestProfile.class)
class CardCertificateReaderServiceTest {

    @Inject
    ConnectorCardsService connectorCardsService;
    @Inject
    CardCertificateReaderService cardCertificateReaderService;

    @BeforeEach
    void init() {
        try {
            // https://community.oracle.com/thread/1307033?start=0&tstart=0
            LogManager.getLogManager().readConfiguration(
                CardCertificateReaderServiceTest.class
                            .getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    @Test
    void test_Successful_ReadCardCertificate_API_Call() throws ConnectorCardCertificateReadException, ConnectorCardsException {
        
        String smcbHandle = connectorCardsService.getConnectorCardHandle(
                ConnectorCardsService.CardHandleType.SMC_B);
        
        Assertions.assertTrue(ArrayUtils.isNotEmpty(
                cardCertificateReaderService.readCardCertificate(smcbHandle)),
                "Smart card certificate was retrieved");
    }

    @Test
    void test_Successful_X509Certificate_Creation_From_ReadCardCertificate_API_Call()
            throws ConnectorCardCertificateReadException, IOException, CertificateException,
            CryptoException, ConnectorCardsException {

                String smcbHandle = connectorCardsService.getConnectorCardHandle(
                ConnectorCardsService.CardHandleType.SMC_B);
        
                
        byte[] base64_Decoded_Asn1_DER_Format_CertBytes =
                cardCertificateReaderService.readCardCertificate(smcbHandle);
        Assertions.assertTrue(ArrayUtils.isNotEmpty(base64_Decoded_Asn1_DER_Format_CertBytes),
                "Smart card certificate was retrieved");

        X509Certificate x509Certificate = CryptoLoader.getCertificateFromAsn1DERCertBytes(
                base64_Decoded_Asn1_DER_Format_CertBytes);

        x509Certificate.checkValidity();
    }
}