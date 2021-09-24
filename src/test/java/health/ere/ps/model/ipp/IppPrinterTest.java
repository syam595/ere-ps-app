package health.ere.ps.model.ipp;

import com.hp.jipp.encoding.Attribute;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class IppPrinterTest {

    IppPrinter ippPrinter = new IppPrinter();
    URI uri = new URI("www.testURI.com");
    private final List<Attribute<?>> defaultPrinterAttributes = Arrays.asList(DefaultAttributes.PRINTER_ATTRIBUTES);
    List<Attribute<?>> olist = new ArrayList(Arrays.asList("attributes-charset=utf-8", "attributes-natural-language=en-us"));
    List<Attribute<?>> jlist = new ArrayList(Arrays.asList("job-uri=/job/1", "job-state=pending(3)", "job-state-reasons=account-closed"));
    List<Attribute<?>> attributes = new ArrayList<>();
    public IppPrinterTest() throws URISyntaxException {
    }

    @Test
    public void testGetPrinterAttributes()
    {

        attributes = ippPrinter.getPrinterAttributes(uri);
        assertFalse(attributes.isEmpty());
        //System.out.println(attributes);
    }
    @Test
    public void testGetOperationAttributes()
    {
        attributes = ippPrinter.getOperationAttributes();
        assertFalse(attributes.isEmpty());
        Object[] array = attributes.toArray();
        Object[] oarray = olist.toArray();
        for (int i = 0; i < oarray.length; i++)
            assertEquals( oarray[i].toString(), array[i].toString());

    }
    @Test
    public void testGetJobAttributes()
    {
        attributes=ippPrinter.getJobAttributes(uri);
        assertFalse(attributes.isEmpty());
        Object[] array = attributes.toArray();
        Object[] jarray = jlist.toArray();
        for (int i = 0; i < jarray.length; i++)
            assertEquals( jarray[i].toString(), array[i].toString());

    }


}
