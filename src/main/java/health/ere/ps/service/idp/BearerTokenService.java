package health.ere.ps.service.idp;

import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import health.ere.ps.config.AppConfig;
import health.ere.ps.model.idp.client.IdpTokenResult;
import health.ere.ps.service.connector.cards.ConnectorCardsService;
import health.ere.ps.service.connector.certificate.CardCertificateReaderService;
import health.ere.ps.service.idp.client.IdpClient;
import health.ere.ps.service.idp.client.IdpHttpClientService;
import io.quarkus.runtime.Startup;

@ApplicationScoped
@Startup
public class BearerTokenService {
    private static final Logger log = Logger.getLogger(BearerTokenService.class.getName());

    @Inject
    AppConfig appConfig;
    @Inject
    IdpClient idpClient;
    @Inject
    CardCertificateReaderService cardCertificateReaderService;
    @Inject
    ConnectorCardsService connectorCardsService;
    @Inject
    Event<Exception> exceptionEvent;


    @PostConstruct
    public void init() {
        String discoveryDocumentUrl = appConfig.getIdpBaseURL() + IdpHttpClientService.DISCOVERY_DOCUMENT_URI;
        try {
            idpClient.init(appConfig.getIdpClientId(), appConfig.getIdpAuthRequestRedirectURL(), discoveryDocumentUrl, true);
            idpClient.initializeClient();
        } catch (Exception e) {
            log.log(Level.WARNING, "Idp init did not work", e);
        }
    }

    public String requestBearerToken() {
        try {
            String cardHandle = connectorCardsService.getConnectorCardHandle(
                    ConnectorCardsService.CardHandleType.SMC_B);

            X509Certificate x509Certificate =
                    cardCertificateReaderService.retrieveSmcbCardCertificate(cardHandle);
            IdpTokenResult idpTokenResult = idpClient.login(x509Certificate);

            return idpTokenResult.getAccessToken().getRawString();
        } catch (Exception e) {
            log.log(Level.WARNING, "Idp login did not work, couldn't request bearer token", e);
            exceptionEvent.fireAsync(e);
            throw new RuntimeException(e);
        }
    }
}
