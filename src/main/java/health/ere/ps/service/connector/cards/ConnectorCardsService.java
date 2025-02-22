package health.ere.ps.service.connector.cards;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.Cards;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.ere.ps.exception.connector.ConnectorCardsException;
import health.ere.ps.service.connector.provider.ConnectorServicesProvider;
import org.apache.commons.collections4.CollectionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@ApplicationScoped
public class ConnectorCardsService {
    private static final Logger log = Logger.getLogger(ConnectorCardsService.class.getName());

    @Inject
    ConnectorServicesProvider connectorServicesProvider;


    private GetCardsResponse getConnectorCards() throws ConnectorCardsException {
        GetCards parameter = new GetCards();
        parameter.setContext(connectorServicesProvider.getContextType());

        try {
            return connectorServicesProvider.getEventServicePortType().getCards(parameter);
        } catch (FaultMessage e) {
            throw new ConnectorCardsException("Error getting connector card handles.", e);
        }
    }

    private Optional<List<CardInfoType>> getConnectorCardsInfo() throws ConnectorCardsException {
        GetCardsResponse response = getConnectorCards();
        List<CardInfoType> cardHandleTypeList = null;

        if (response != null) {
            Cards cards = response.getCards();
            cardHandleTypeList = cards.getCard();

            if (CollectionUtils.isEmpty(cardHandleTypeList)) {
                throw new ConnectorCardsException("Error. Did not receive and card handle data.");
            }
        }

        return Optional.ofNullable(cardHandleTypeList);
    }

    public String getConnectorCardHandle(CardHandleType cardHandleType)
            throws ConnectorCardsException {
        Optional<List<CardInfoType>> cardsInfoList = getConnectorCardsInfo();
        String cardHandle = null;

        if (cardsInfoList.isPresent()) {
            Optional<CardInfoType> cardHndl =
                    cardsInfoList.get().stream().filter(ch ->
                            ch.getCardType().value().equalsIgnoreCase(
                                    cardHandleType.getCardHandleType())).findFirst();
            if (cardHndl.isPresent()) {
                cardHandle = cardHndl.get().getCardHandle();
            } else {
                throw new ConnectorCardsException(String.format("No card handle found for card " +
                        "handle type %s", cardHandleType.getCardHandleType()));
            }
        }

        return cardHandle;
    }

    public enum CardHandleType {
        EGK("EGK"),
        HBA_Q_SIG("HBA-qSig"),
        HBA("HBA"),
        SMC_B("SMC-B"),
        HSM_B("HSM-B"),
        SMC_KT("SMC-KT"),
        KVK("KVK"),
        ZOD_2_0("ZOD_2.0"),
        UNKNOWN("UNKNOWN"),
        HBA_X("HBAx"),
        SM_B("SM-B");

        private final String cardHandleType;

        CardHandleType(String cardHandleType) {
            this.cardHandleType = cardHandleType;
        }

        public String getCardHandleType() {
            return cardHandleType;
        }
    }
}
